package com.ut.security.service;

import com.google.common.base.Strings;
import com.ut.security.dao.AppDao;
import com.ut.security.model.AppEntity;
import com.ut.security.properties.ExceptionContants;
import com.ut.security.properties.UserConstants;
import com.ut.security.usermgr.MyUserDao;
import com.ut.security.usermgr.MyUserEntity;
import com.ut.security.usermgr.MyUserService;
import com.ut.security.vo.AppVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class AppService {
	@Autowired
	private AppDao appDao;
	@Autowired
	private MyUserService myUserService;
	@Autowired
	private MyUserDao myUserDao;

	/**
	 * 初始数据
	 */
	public void initApp(String appKey, String developer, String name, String description) {
		if (null != appDao.findByAppKeyAndDeveloper(appKey, developer))
			return;
		AppEntity appEntity = new AppEntity();
		appEntity.setAppKey(appKey);
		appEntity.setDeveloper(developer);
		appEntity.setName(name);
		appEntity.setDescription(description);
		appDao.save(appEntity);
	}

	@PreAuthorize("hasAuthority('platform_app_addApp')")
	@Transactional
	public boolean createApp(AppVo appVo) throws Exception {
		if (null == appVo || Strings.isNullOrEmpty(appVo.getAppKey()) || Strings.isNullOrEmpty(appVo.getName()))
			throw new Exception(String.format(ExceptionContants.PARAM_NOTNULL_EXCEPTION, "应用key和应用名"));
		if (!appVo.getAppKey().matches(UserConstants.REGEX_APPKEY))
			throw new Exception(ExceptionContants.APPKEY_ERR_MSG);
		if (null != appDao.findByAppKey(appVo.getAppKey()))
			throw new Exception(String.format(ExceptionContants.EXIST_EXCEPTION, appVo.getAppKey() + "应用"));
		if (appDao.findByName(appVo.getName()).size() > 0)
			throw new Exception(String.format(ExceptionContants.EXIST_EXCEPTION, appVo.getName() + "应用"));

		AppEntity appEntity = new AppEntity();
		BeanUtils.copyProperties(appVo, appEntity);
		appEntity.setDeveloper(myUserService.getLoginUsername());
		return (null != appDao.save(appEntity));
	}

	@PreAuthorize("hasAuthority('platform_app_getApp')")
	public AppEntity getAppInfo(String appKey) throws Exception {
		if (Strings.isNullOrEmpty(appKey))
			throw new Exception(String.format(ExceptionContants.PARAM_NOTNULL_EXCEPTION, "appKey"));
		if (isAppDeveloper(appKey))
			return appDao.findByAppKeyAndStatus(appKey, true);
		return null;
	}

	@PreAuthorize("hasAuthority('platform_app_allApps')")
	public Page<AppEntity> pageGetApps(Pageable pageable) {
		if (myUserService.isPlatFormAdmin())
			return appDao.findAll(pageable);

		//判断是否有父账号
		Optional<String> parentUsername = myUserService.getParentUsername();
		if (parentUsername.isPresent()) {
			return appDao.findByDeveloperIn(Arrays.asList(parentUsername.get(), myUserService.getLoginUsername()), pageable);
		}

		return appDao.findByDeveloper(myUserService.getLoginUsername(), pageable);
	}

	@PreAuthorize("hasAuthority('platform_app_allApps')")
	public List<AppEntity> getAllApps() {
		return getAllMyApps();
	}

	@PreAuthorize("hasAuthority('platform_app_updateApp')")
	public boolean updateApp(AppVo appVo) throws Exception {
		if (null == appVo || Strings.isNullOrEmpty(appVo.getAppKey()))
			throw new Exception(String.format(ExceptionContants.PARAM_NOTNULL_EXCEPTION, "appKey"));
		if (!isAppDeveloper(appVo.getAppKey()))
			throw new Exception(String.format(ExceptionContants.NO_AUTHORITY_EXCEPTION, appVo.getAppKey()));
		AppEntity app = appDao.findByAppKeyAndStatus(appVo.getAppKey(), true);

		if (!Strings.isNullOrEmpty(appVo.getName()) || !Strings.isNullOrEmpty(appVo.getDescription())) {
			if (!Strings.isNullOrEmpty(appVo.getName())) {
				if (appDao.findByName(appVo.getName()).size() > 0 && !appVo.getName().equals(appDao.findByAppKey(appVo.getAppKey()).getName()))
					throw new Exception(String.format(ExceptionContants.EXIST_EXCEPTION, appVo.getName() + "应用"));
				app.setName(appVo.getName());
			}
			if (!Strings.isNullOrEmpty(appVo.getDescription()))
				app.setDescription(appVo.getDescription());
			return null != appDao.save(app);
		}
		return true;
	}

	@Transactional
	@PreAuthorize("hasAuthority('platform_app_changeAppStatus')")
	public boolean updateAppStatus(String appKey, boolean status) throws Exception {
		if (Strings.isNullOrEmpty(appKey))
			throw new Exception(String.format(ExceptionContants.PARAM_NOTNULL_EXCEPTION, "appKey"));
		AppEntity app = null;
		if (myUserService.isPlatFormAdmin()) {
			app = appDao.findByAppKey(appKey);
		} else {
			app = appDao.findByAppKeyAndDeveloper(appKey, myUserService.getLoginUsername());
		}
		if (null == app)
			throw new Exception("应用不存在，或者没有权限修改应用状态");
		app.setStatus(status);
		return (null != appDao.save(app));
	}

	/**
	 * 判断当前用户是否应用开发者
	 */
	public boolean isAppDeveloper(String appKey) {
		if (checkAppAlive(appKey)) {
			//平台管理员
			if (myUserService.isPlatFormAdmin())
				return true;
			//应用创建者
			if (null != appDao.findByAppKeyAndDeveloperAndStatus(appKey, myUserService.getLoginUsername(), true))
				return true;
			//是否应用创建者子账户
			AppEntity appEntity = appDao.findByAppKeyAndStatus(appKey, true);
			MyUserEntity byUsernameAndParentUser = myUserDao.findByUsernameAndParentUser(myUserService.getLoginUsername(), appEntity.getDeveloper());
			if (null != byUsernameAndParentUser)
				return true;
		}
		return false;
	}

	/**
	 * 判断指定用户是否应用开发者
	 */
	public boolean checkTargetUserIsAppDeveloper(String appKey, String username) {
		if (checkAppAlive(appKey)) {
			//平台管理员
			if (UserConstants.PLATFORM_ADMIN.equals(username))
				return true;
			//应用创建者
			if (null != appDao.findByAppKeyAndDeveloperAndStatus(appKey, username, true))
				return true;
			//是否应用创建者子账户
			AppEntity appEntity = appDao.findByAppKeyAndStatus(appKey, true);
			MyUserEntity byUsernameAndParentUser = myUserDao.findByUsernameAndParentUser(username, appEntity.getDeveloper());
			if (null != byUsernameAndParentUser)
				return true;
		}
		return false;
	}

	/**
	 * 检查应用是否启动状态
	 */
	public boolean checkAppAlive(String appKey) {
		return (null != appDao.findByAppKeyAndStatus(appKey, true));
	}

	/**
	 * 检查用户是否有权操作应用
	 */
	@Deprecated
	public void hasAuthToManageApp(String appKey) throws Exception {
		if (!isAppDeveloper(appKey))
			throw new Exception("当前用户无权操作" + appKey + "应用！");
	}

	/**
	 * 获取已激活应用列表
	 */
	public List<AppEntity> getAllMyApps() {
		if (myUserService.isPlatFormAdmin())
			return appDao.findAllByStatus(true);
		List<AppEntity> result = appDao.findAllByDeveloperAndStatus(myUserService.getLoginUsername(), true);
		MyUserEntity curUser = myUserService.getSelf();
		if (!Strings.isNullOrEmpty(curUser.getParentUser()))
			result.addAll(appDao.findAllByDeveloperAndStatus(curUser.getParentUser(), true));
		return result;
	}

	@PreAuthorize("hasAuthority('platform_pagePlatformApps')")
	public Page<AppEntity> pagePlatformApps(Pageable pageable) {
		return appDao.findByDeveloper(UserConstants.PLATFORM_ADMIN, pageable);
	}

	@PreAuthorize("hasAuthority('platform_listPlatformApps')")
	public List<AppEntity> listPlatformApps() {
		return appDao.findAllByDeveloperAndStatus(UserConstants.PLATFORM_ADMIN, true);
	}

}
