package com.ut.user.assignAuthmgr;

import com.google.common.base.Strings;
import com.ut.user.appmgr.AppDao;
import com.ut.user.appmgr.AppEntity;
import com.ut.user.appmgr.AppService;
import com.ut.user.authritymgr.*;
import com.ut.user.constants.ExceptionContants;
import com.ut.user.constants.UserConstants;
import com.ut.user.exception.UtException;
import com.ut.user.exception.UtExceptionEnum;
import com.ut.user.usermgr.*;
import com.ut.user.utils.PageUtils;
import com.ut.user.vo.AuthorityGroupVo;
import com.ut.user.vo.AuthorityVo;
import com.ut.user.vo.ChildUserAuthorityVO;
import com.ut.user.vo.UserAppAuthorityVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AuthorityManagerService {
	@Autowired
	private AppDao appDao;
	@Autowired
	private MyUserDao myUserDao;
	@Autowired
	MyUserService myUserService;
	@Autowired
	private AppService appService;
	@Autowired
	AuthorityDao authorityDao;
	@Autowired
	PasswordEncoder passwordEncoder;
	@Autowired
	private AuthorityGroupDao authorityGroupDao;
	@Autowired
	private MyUserRelateAppsDao myUserRelateAppsDao;
	@Autowired
	private AuthorityGroupRelateAuthoritiesDao authorityGroupRelateAuthoritiesDao;
	@Autowired
	private MyUserRelateAppsService myUserRelateAppsService;
	@Autowired
	private MyUserRelateAuthoritiesService myUserRelateAuthoritiesService;
	@Autowired
	private MyUserRelateAuthorityGroupsService myUserRelateAuthorityGroupsService;

	/**
	 * 分配功能
	 */
	@Transactional(rollbackOn = Exception.class)
	public boolean bindAuthorities(String username, String appKey, AuthorityVo authorityVo) throws Exception {
		AbstractDistributeAuthority distribute = new DistributeAuthorityImpl();
		return distribute.distributeAuthorities(username, appKey, authorityVo.getAuthorityKeys());
	}

	/**
	 * 分配功能（增量修改数据）
	 */
	@Transactional(rollbackOn = Exception.class)
	public boolean bindAuthorities(String username, String appKey, AuthorityVo authorityVo,Integer add) throws
			Exception {
		if(CollectionUtils.isEmpty(authorityVo.getAuthorityKeys())){
			throw new UtException(UtExceptionEnum.PARAM_ERROR, "authorityKeys不能为空");
		}

		AbstractDistributeAuthority distribute;
		if(add==1){
		 	distribute= new DistributeIncreasinglyAuthorityImpl();
		}else if(add==0){
			distribute= new DistributeDecreasinglyAuthorityImpl();
		}else{
			throw new UtException(UtExceptionEnum.PARAM_ERROR, "add只能是0或1");
		}
		return distribute.distributeAuthorities(username, appKey, authorityVo.getAuthorityKeys());
	}

	/**
	 * 分配角色
	 */
	@Transactional(rollbackOn = Exception.class)
	public boolean bindAuthorityGroups(String username, String appKey, AuthorityGroupVo authorityGroupVo) throws
			Exception {
		AbstractDistributeAuthority distribute = new DistributeAuthorityGroupImpl();
		return distribute.distributeAuthorities(username, appKey, authorityGroupVo.getAuthorityGroupKeys());
	}

	/**
	 * 创建子账户
	 */
	public boolean createChildUser(String username, String password) throws Exception {
		if (Strings.isNullOrEmpty(username) || Strings.isNullOrEmpty(password))
			throw new Exception(String.format(ExceptionContants.PARAM_NOTNULL_EXCEPTION, "username, password"));
		if (!username.matches(UserConstants.REGEX_USERNAME))
			throw new Exception(ExceptionContants.USERNAME_ERR_MSG);
		if (!password.matches(UserConstants.REGEX_PWD))
			throw new Exception(ExceptionContants.PWD_ERR_MSG);
		username = username.trim();
		password = password.trim();
		if (null != myUserDao.findByUsername(username))
			throw new Exception(String.format(ExceptionContants.EXIST_EXCEPTION, username));
		MyUserEntity user = new MyUserEntity();
		user.setUsername(username);
		user.setLoginName(username);
		user.setAccountSystemKey(UserConstants.DEFAULT_SYSTEM_KEY);
		user.setParentUser(myUserService.getLoginUsername());
		user.setPassword(passwordEncoder.encode(password));
		return (null != myUserDao.save(user));
	}

	/**
	 * 获取指定应用已授权的用户列表--应用开发者分配
	 */
	@PreAuthorize("hasAuthority('platform_app_pageAllAuthorities')")
	public Page<ChildUserAuthorityVO> pageAllAuthorities(String appKey, Pageable pageable) throws Exception {
		List<ChildUserAuthorityVO> result = new ArrayList<>();
		//已获得该应用权限的用户
		Page<MyUserRelateApps> relateApps = myUserRelateAppsDao.findByAppKey(appKey, pageable);
		for (MyUserRelateApps relate : relateApps) {
			MyUserEntity user = myUserService.getByUsernameOrLoginName(relate.getUsername());
			ChildUserAuthorityVO userVO = new ChildUserAuthorityVO();
			BeanUtils.copyProperties(user, userVO);
			List<UserAppAuthorityVO> back = new ArrayList<>();
			back.add(getUserAppAuthorities(appKey, user.getUsername()));
			userVO.setUserAppAuthorityVO(back);
			result.add(userVO);
		}

		//封装分页对象返回
		PageUtils<ChildUserAuthorityVO> pageUtils = new PageUtils();
		return pageUtils.pageUserBasicVos(relateApps, result);
	}

	/**
	 * 子账户列表--子账户分配
	 */
	public Page<ChildUserAuthorityVO> pageMyChildAccount(Pageable pageable) throws Exception {
		List<ChildUserAuthorityVO> result = new ArrayList<>();
		//我的子账户
		Page<MyUserEntity> childUsers = myUserService.pageChildUser(myUserService.getLoginUsername(), pageable);
		for (MyUserEntity user : childUsers) {
			ChildUserAuthorityVO vo = getchildrenAuthority(user);
			result.add(vo);
		}

		//封装分页对象返回
		PageUtils<ChildUserAuthorityVO> pageUtils = new PageUtils();
		return pageUtils.pageUserBasicVos(childUsers, result);
	}

	/**
	 * 获取用户的权限集-(授权页编辑时比对使用)
	 */
	public ChildUserAuthorityVO listUserAuthorities(String appKey, String username) throws Exception {
		if (Strings.isNullOrEmpty(appKey) || Strings.isNullOrEmpty(username))
			throw new Exception(String.format(ExceptionContants.PARAM_NOTNULL_EXCEPTION, appKey + "和" + username));
		MyUserEntity user = myUserService.getByUsernameOrLoginName(username);
		ChildUserAuthorityVO userVO = new ChildUserAuthorityVO();
		BeanUtils.copyProperties(user, userVO);
		List<UserAppAuthorityVO> back = new ArrayList<>();
		back.add(getUserAppAuthorities(appKey, user.getUsername()));
		userVO.setUserAppAuthorityVO(back);
		return userVO;
	}

	/**
	 * 我关联的应用列表(平行-子账户管理使用)
	 */
	public List<AppEntity> listMyRealteApps() {
		List<AppEntity> result = new ArrayList<>();
		List<AppEntity> myCreatApps = appDao.findAllByDeveloperAndStatus(myUserService.getLoginUsername(), true);
		if (myCreatApps.size() > 0)
			result.addAll(myCreatApps);
		Set<String> myApps = new HashSet<>();
		myCreatApps.forEach((AppEntity app) -> myApps.add(app.getAppKey()));
		List<MyUserRelateApps> relateApps = myUserRelateAppsDao.findByUsername(myUserService.getLoginUsername());
		if (relateApps.size() > 0) {
			for (MyUserRelateApps relate : relateApps) {
				if (myApps.contains(relate.getAppKey()))
					continue;
				AppEntity appEntity = appDao.findByAppKey(relate.getAppKey());
				result.add(appEntity);
			}
		}
		return result;
	}

	/**
	 * 获取子账户权限集
	 */
	private ChildUserAuthorityVO getchildrenAuthority(MyUserEntity user) {
		ChildUserAuthorityVO userVO = new ChildUserAuthorityVO();
		BeanUtils.copyProperties(user, userVO);
		List<MyUserRelateApps> relateApps = myUserRelateAppsDao.findByUsername(user.getUsername());
		List<UserAppAuthorityVO> back = new ArrayList<>();
		if (relateApps.size() > 0) {
			for (MyUserRelateApps myUserRelateApps : relateApps) {
				UserAppAuthorityVO userAppAuthorityVO = getUserAppAuthorities(myUserRelateApps.getAppKey(), user.getUsername());
				back.add(userAppAuthorityVO);
			}
		}
		userVO.setUserAppAuthorityVO(back);
		return userVO;
	}

	/**
	 * 获取用户已分配的功能、角色
	 */
	private UserAppAuthorityVO getUserAppAuthorities(String appKey, String username) {
		UserAppAuthorityVO appAuthorityVO = new UserAppAuthorityVO();
		AppEntity byAppKey = appDao.findByAppKey(appKey);
		appAuthorityVO.setAppKey(appKey);
		appAuthorityVO.setAppName(byAppKey.getName());
		//应用开发者
		if (null != appDao.findByAppKeyAndDeveloperAndStatus(appKey, username, true)) {
			appAuthorityVO.setAuthorities(authorityDao.findAllByAppKey(appKey));
			appAuthorityVO.setAuthorityGroups(authorityGroupDao.findAllByAppKey(appKey));
			return appAuthorityVO;
		}

		//获取当前用户已分配的功能
		List<MyUserRelateAuthorities> relateAuths = myUserRelateAuthoritiesService.getUserRelateAuthsByApp(username, appKey);
		List<AuthorityEntity> authList = new ArrayList<>();
		if (relateAuths.size() > 0) {
			for (MyUserRelateAuthorities authRelate : relateAuths) {
				AuthorityEntity auth = authorityDao.findByAuthorityKey(authRelate.getAuthorityKey());
				authList.add(auth);
			}
		}


		//获取当前用户已分配的角色
		List<MyUserRelateAuthorityGroups> userRelateAuthGroupsByApp = myUserRelateAuthorityGroupsService.getUserRelateAuthGroupsByApp(username, appKey);
		List<AuthorityGroupEntity> authGroupSet = new ArrayList<>();
		if (userRelateAuthGroupsByApp.size() > 0) {
			for (MyUserRelateAuthorityGroups authGroupRelate : userRelateAuthGroupsByApp) {
				AuthorityGroupEntity authGroup = authorityGroupDao.findByAuthorityGroupKey(authGroupRelate.getAuthorityGroupKey());
				authGroupSet.add(authGroup);
			}
		}
		appAuthorityVO.setAuthorityGroups(authGroupSet);

		//将角色绑定的功能加入功能列表中
		for (AuthorityGroupEntity authGroup : authGroupSet) {
			String groupKey = authGroup.getAuthorityGroupKey();
			List<AuthorityGroupRelateAuthorities> relateAuthorities = authorityGroupRelateAuthoritiesDao.findByAppKeyAndAuthorityGroupKey(appKey, groupKey);
			for (AuthorityGroupRelateAuthorities relateAuthority : relateAuthorities) {
				authList.add(authorityDao.findByAuthorityKey(relateAuthority.getAuthorityKey()));
			}
		}

		//清除重复的功能
		HashSet authSet = new HashSet(authList);
		authList.clear();
		authList.addAll(authSet);
		appAuthorityVO.setAuthorities(authList);
		return appAuthorityVO;
	}

	/**
	 * menu服务__Feign调用--应用方调用
	 */
	public boolean checkUserRelateApp(String appKey, String username) throws Exception {
		if (Strings.isNullOrEmpty(appKey))
			throw new Exception("appKey 不能为空！");
		if (Strings.isNullOrEmpty(username))
			return checkCurrentUserRelateApp(appKey);
		if (!Strings.isNullOrEmpty(username))
			return checkTargetUserRelateApp(appKey, username);
		return false;
	}

	/**
	 * 检查当前用户是否有权访问该应用
	 */
	private boolean checkCurrentUserRelateApp(String appKey) {
		if (appService.isAppDeveloper(appKey))
			return true;
		return (null != myUserRelateAppsService.getOneByUsernameAndAppKey(myUserService.getLoginUsername(), appKey));
	}

	/**
	 * 检查当前指定用户是否有权访问该应用
	 */
	private boolean checkTargetUserRelateApp(String appKey, String username) {
		if (appService.checkTargetUserIsAppDeveloper(appKey, username))
			return true;
		return (null != myUserRelateAppsService.getOneByUsernameAndAppKey(username, appKey));
	}


}
