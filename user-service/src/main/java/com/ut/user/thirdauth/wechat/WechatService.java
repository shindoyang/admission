package com.ut.user.thirdauth.wechat;

import com.alibaba.fastjson.JSON;
import com.ut.user.constants.SocialAccountTypeEnum;
import com.ut.user.constants.ThirdAccountConstant;
import com.ut.user.thirdauth.SocialAccountService;
import com.ut.user.thirdauth.SocialHelper;
import com.ut.user.thirdauth.account.AppRelateDeveloperAccountService;
import com.ut.user.usermgr.MyUserEntity;
import com.ut.user.usermgr.MyUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 微信用户绑定的相关接口
 * @author litingting
 */
@Service
@Slf4j
public class WechatService implements SocialAccountService {
    @Autowired
    SocialHelper socialHelper;
    @Autowired
    MyUserService userService;
    @Autowired
    WechatUserDao wechatUserDao;
    @Autowired
    AppRelateDeveloperAccountService appRelateDevAccountService;

    @Override
    public String getAuthType() {
        return ThirdAccountConstant.WECHAT;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindSocialAccount(String loginKey) throws Exception {
        log.info("=====微信绑定====loginKey=：{}", loginKey);
        String socialAccountInJson = socialHelper.getLoginKey(loginKey);

        WechatUserEntity wechatUser = JSON.parseObject(socialAccountInJson, WechatUserEntity.class);
        log.info("待绑定的微信账号信息: {}" + wechatUser);

        //检查待绑定社交账号是否已经绑定
        String username = userService.getLoginUsername();
        WechatUserEntity wechatUserInDB = wechatUserDao.findByAppIdAndOpenIdAndSocialAccountType(wechatUser.getAppId(), wechatUser.getOpenId(), wechatUser.getSocialAccountType());
        if (null != wechatUserInDB) {
            if (!username.equals(wechatUserInDB.getOauthUserName())) {
                throw new Exception("微信账号已被其他用户绑定");
            }
            if(username.equals(wechatUserInDB.getOauthUserName())) {
                throw new Exception("你已经绑定过微信啦！");
            }
        }

        //检查用户是否已经绑定过同类型的社交账号
        List<WechatUserEntity> bindedAccountList = wechatUserDao.findByOauthUserNameAndSocialAccountType(username, wechatUser.getSocialAccountType());
        if(bindedAccountList.size() <= 0){//第一次绑定该社交账号类型，需修改用户信息表对应的社交账号状态
            MyUserEntity user = userService.getUserByKeyword(username);
            user.setWechat(true);
            userService.updateUser(user);
        }

        //插入绑定信息表
        WechatUserEntity bindEntity = new WechatUserEntity();
        bindEntity.setAppId(wechatUser.getAppId());
        bindEntity.setOpenId(wechatUser.getOpenId());
        bindEntity.setUnionId(wechatUser.getUnionId());
        bindEntity.setSocialAccountType(wechatUser.getSocialAccountType());
        bindEntity.setOauthUserName(username);
        bindEntity.setCreateTime(new Date());
        wechatUserDao.save(bindEntity);

    }

    /** 保存微信账户 */
    @Transactional(rollbackFor = Exception.class)
    public void saveWechatUser(WechatUserEntity wechatUser) {
        WechatUserEntity wechatUserEntity = new WechatUserEntity();
        BeanUtils.copyProperties(wechatUser, wechatUserEntity);
        wechatUserDao.save(wechatUserEntity);
    }

    /** 解绑微信账号
     * */
    @Override
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    public boolean unbindThirdAccount(){
        MyUserEntity loginUser = userService.getSelf();
        if (loginUser != null){
            // 更新用户表中的微信绑定状态
            loginUser.setWechat(false);
            userService.updateUser(loginUser);
            wechatUserDao.deleteByOauthUserName(userService.getLoginUsername());
            return true;
        }
        return false;
    }

    /**
     * 解绑指定微信账号
     */
    @Override
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    public boolean unbindSocialAccountByAppId(String appId, String socialAccountType)throws Exception {
        MyUserEntity loginUser = userService.getSelf();
        Integer int_socialAccountType = SocialAccountTypeEnum.getTypeValue(socialAccountType);
        //删除绑定关系表
        int deleteResult = wechatUserDao.deleteByAppIdAndOauthUserNameAndSocialAccountType(appId, loginUser.getUsername(), int_socialAccountType);
        if(deleteResult > 1){
            throw new Exception("解绑微信的时候超过了两条，请检查数据");
        }

        //检查用户是否已经解绑全部微信
        List<WechatUserEntity> bindedAccountList = wechatUserDao.findByOauthUserNameAndSocialAccountType(loginUser.getUsername(), int_socialAccountType);
        if(bindedAccountList.size() <= 0){//如果已经全部解绑，需修改用户信息表对应的社交账号状态
            loginUser.setWechat(false);
            userService.updateUser(loginUser);
        }
        return true;
    }

    /**
     * 多条件查询社交账户信息
     */
    public WechatUserEntity getSocialEntityByKey(String key) throws Exception {
        if (StringUtils.isEmpty(key)){
            throw new Exception("微信/小程序用户的openId为空");
        }
        WechatUserEntity wechatUser = wechatUserDao.findFirstByUnionId(key);
        if(null == wechatUser){
            return wechatUserDao.findFirstByOpenId(key);
        }
        return wechatUser;
    }

    @Override
    public String getOauthUserName(String socialEntityInJson) throws Exception {
        WechatUserEntity socialRawEntity = JSON.parseObject(socialEntityInJson, WechatUserEntity.class);

        WechatUserEntity currentUser = getSocialEntityByKey(socialRawEntity.getOpenId());
        if (currentUser == null && socialRawEntity.getUnionId() != null){
            currentUser = getSocialEntityByKey(socialRawEntity.getUnionId());
        }
        return currentUser != null ? currentUser.getOauthUserName() : null ;
    }

    /**
     * 检查用户是否绑定指定的小程序
     * 并对于存量数据，补充appId的关联关系
     */
    public boolean existSocialAccount(String appId, String openId, Integer socialAccountType){
        //检查该openId是否已经有存量数据
        WechatUserEntity wechatUserEntity = wechatUserDao.findByAppIdAndOpenIdAndSocialAccountType(appId, openId, socialAccountType);
        if(null != wechatUserEntity){
            log.info("通过 appId + openId 找到微信用户： {}", wechatUserEntity);
            return true;
        }

        //更新存量数据，补充appId和socialAccountType
        wechatUserEntity = wechatUserDao.findFirstByOpenId(openId);
        if(null != wechatUserEntity){
            log.info("通过 openId 找到微信用户： {} ", wechatUserEntity);
            wechatUserEntity.setAppId(appId);
            wechatUserEntity.setSocialAccountType(socialAccountType);
            wechatUserEntity.setUpdateTime(new Date());
            wechatUserDao.save(wechatUserEntity);
            return true;
        }
        return false;
    }
}
