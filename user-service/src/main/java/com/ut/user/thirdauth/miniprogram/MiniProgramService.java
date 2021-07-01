package com.ut.user.thirdauth.miniprogram;

import com.alibaba.fastjson.JSON;
import com.ut.user.constants.SocialAccountTypeEnum;
import com.ut.user.constants.ThirdAccountConstant;
import com.ut.user.thirdauth.SocialAccountService;
import com.ut.user.thirdauth.SocialHelper;
import com.ut.user.thirdauth.wechat.WechatService;
import com.ut.user.thirdauth.wechat.WechatUserDao;
import com.ut.user.thirdauth.wechat.WechatUserEntity;
import com.ut.user.usermgr.MyUserDao;
import com.ut.user.usermgr.MyUserEntity;
import com.ut.user.usermgr.MyUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 小程序用户的相关接口
 *
 * @author litingting
 */
@Slf4j
@Service
public class MiniProgramService implements SocialAccountService {
	@Autowired
	SocialHelper socialHelper;
	@Autowired
	MyUserService userService;
	@Autowired
	WechatService wechatService;
	@Autowired
	WechatUserDao wechatUserDao;
	@Autowired
	MyUserDao myUserDao;

	Lock lock = new ReentrantLock();

	@Override
	public String getAuthType() {
		return ThirdAccountConstant.MINI_PROGRAM;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void bindSocialAccount(String loginKey) throws Exception {
		log.info("=======开始绑定小程序账号=======loginKey=：{}", loginKey);
		String socialAccountInJson = socialHelper.getLoginKey(loginKey);

		MiniProgramUser miniProgramUser = JSON.parseObject(socialAccountInJson, MiniProgramUser.class);
		log.info("待绑定的小程序账号信息: {}", miniProgramUser);
		String username = null;


		try {
			lock.lock();
			//检查待绑定社交账号是否已经绑定
			username = userService.getLoginUsername();
			WechatUserEntity wechatUserInDB = wechatUserDao.findByAppIdAndOpenIdAndSocialAccountType(miniProgramUser.getAppId(), miniProgramUser.getOpenId(), miniProgramUser.getSocialAccountType());
			if (null != wechatUserInDB) {
				if (!username.equals(wechatUserInDB.getOauthUserName())) {
					throw new Exception("小程序账号已被其他用户绑定");
				}
				if (username.equals(wechatUserInDB.getOauthUserName())) {
					throw new Exception("你已经绑定过小程序啦！");
				}
			}

			//插入绑定信息表
			WechatUserEntity bindEntity = new WechatUserEntity();
			bindEntity.setAppId(miniProgramUser.getAppId());
			bindEntity.setOpenId(miniProgramUser.getOpenId());
			bindEntity.setUnionId(miniProgramUser.getUnionId());
			bindEntity.setSocialAccountType(miniProgramUser.getSocialAccountType());
			bindEntity.setOauthUserName(username);
			bindEntity.setCreateTime(new Date());
			wechatUserDao.save(bindEntity);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}

		//检查用户是否已经绑定过同类型的社交账号
		List<WechatUserEntity> bindedAccountList = wechatUserDao.findByOauthUserNameAndSocialAccountType(username, miniProgramUser.getSocialAccountType());
		if (bindedAccountList.size() == 1) {//第一次绑定该社交账号类型，需修改用户信息表对应的社交账号状态
			log.info("第一次绑定该社交账号类型，修改用户表类型状态 ");
			MyUserEntity user = userService.getUserByKeyword(username);
			user.setMiniProgram(true);
			myUserDao.save(user);
		}
	}

	/**
	 * 解绑小程序
	 */
	@Override
	@Modifying
	@Transactional(rollbackFor = Exception.class)
	public boolean unbindThirdAccount() {
		MyUserEntity loginUser = userService.getSelf();
		if (loginUser != null) {
			// 更新用户表中的小程序绑定状态
			loginUser.setMiniProgram(false);
			userService.updateUser(loginUser);
			wechatUserDao.deleteByOauthUserName(userService.getLoginUsername());//全部解绑
			return true;
		}
		return false;
	}

	/**
	 * 解绑指定小程序
	 */
	@Override
	@Modifying
	@Transactional(rollbackFor = Exception.class)
	public boolean unbindSocialAccountByAppId(String appId, String socialAccountType)throws Exception {
		log.info("小程序单独解绑接口： appId = {}, socialAccountType = {}", appId, socialAccountType);
		MyUserEntity loginUser = userService.getSelf();
		Integer int_socialAccountType = SocialAccountTypeEnum.getTypeValue(socialAccountType);
		log.info("当前用户 : {}, 转换后的类型 socialAccountType = {}", loginUser.getUsername(), int_socialAccountType);
		//删除绑定关系表
		int deleteResult = wechatUserDao.deleteByAppIdAndOauthUserNameAndSocialAccountType(appId, loginUser.getUsername(), int_socialAccountType);
		/*if(deleteResult > 1){
			throw new Exception("解绑小程序的时候超过了两条，请检查数据");
		}*/

		log.info("解绑成功！deleteResult = {} ", deleteResult);

		//检查用户是否已经解绑全部小程序
		List<WechatUserEntity> bindedAccountList = wechatUserDao.findByOauthUserNameAndSocialAccountType(loginUser.getUsername(), int_socialAccountType);
		if (bindedAccountList.size() <= 0) {//如果已经全部解绑，需修改用户信息表对应的社交账号状态
			log.info("已解绑该类型下全部绑定关系，修改用户表类型状态 ");
			loginUser.setMiniProgram(false);
			myUserDao.save(loginUser);
		}
		return true;
	}

	@Override
	public String getOauthUserName(String socialEntityInJson) throws Exception {
		MiniProgramUser miniProgramUser = JSON.parseObject(socialEntityInJson, MiniProgramUser.class);
		return getUserName(miniProgramUser.getOpenId(), miniProgramUser.getUnionId());
	}

	private String getUserName(String openId, String unionId) throws Exception {
		// 先拿openId查一次，查不到再用unionId查一次
		WechatUserEntity wechatUser = wechatService.getSocialEntityByKey(openId);
		if (wechatUser == null) {
			if (StringUtils.isEmpty(unionId)) {
				return null;
			}
			if ((wechatUser = wechatService.getSocialEntityByKey(unionId)) == null) {
				return null;
			}
		}

		if ((userService.getUserByKeyword(wechatUser.getOauthUserName()) == null))
			throw new Exception("用户表数据异常，无法查找到用户名" + wechatUser.getOauthUserName());

		return wechatUser.getOauthUserName();
	}
}
