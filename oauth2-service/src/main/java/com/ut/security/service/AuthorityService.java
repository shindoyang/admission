package com.ut.security.service;

import com.google.common.base.Strings;
import com.ut.security.constant.ExceptionContants;
import com.ut.security.constant.UserConstants;
import com.ut.security.dao.AuthorityDao;
import com.ut.security.dao.AuthorityGroupDao;
import com.ut.security.model.AuthorityEntity;
import com.ut.security.model.AuthorityGroupRelateAuthorities;
import com.ut.security.model.vo.AuthorityInVo;
import com.ut.security.usermgr.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @auth: 陈佳攀
 * @Description:
 * @Date: Created in 10:35 2017-11-20
 */
@Service
public class AuthorityService {
    @Autowired
    AppService appService;
    @Autowired
    AuthorityDao authorityDao;
    @Autowired
    AuthorityGroupDao authorityGroupDao;
    @Autowired
    MyUserService myUserService;
    @Autowired
    MyUserRelateAuthoritiesDao myUserRelateAuthoritiesDao;
    @Autowired
    MyUserRelateAuthorityGroupsDao myUserRelateAuthorityGroupsDao;
    @Autowired
    MyUserRelateAuthoritiesService myUserRelateAuthoritiesService;
    @Autowired
    AuthorityGroupRelateAuthoritiesService authorityGroupRelateAuthoritiesService;

    public AuthorityService() {
    }

    /**
     * 功能初始化
     *
     * @param appKey       应用key
     * @param authorityKey 功能key
     * @param name         功能名
     * @param description  功能描述
     */
    public void initAuthority(String appKey, String authorityKey, String name, String description) {
        if (null != authorityDao.findByAppKeyAndAuthorityKey(appKey, authorityKey))
            return;
        AuthorityEntity auth = new AuthorityEntity();
        auth.setAppKey(appKey);
        auth.setAuthorityKey(authorityKey);
        auth.setName(name);
        auth.setDescription(description);
        authorityDao.save(auth);
    }

    void initRole(String authKey, String parent, String name) {
        AuthorityEntity au = authorityDao.findByAuthorityKey(authKey);
        if (au == null) {
            au = new AuthorityEntity();
            au.setAuthorityKey(authKey);
            au.setName(name);
            au.setParent(parent);
            authorityDao.save(au);
        }
    }

    @PostConstruct
    public void init() {
        initRole("platform_admin", null, "框架管理员");
        initRole("developer", "platform_admin", "开发者");
        initRole("assign_authority", "developer", "权限分配者");

        //下述角色应由"platform_admin创建，目前初始化时置入
        initRole("iot_admin", "platform_admin", "设备云管理员");
        initRole("buz_admin", "platform_admin", "商业云管理员");
        initRole("setl_admin", "platform_admin", "结算云管理员");
        initRole("cook_admin", "platform_admin", "餐饮应用管理员");
    }

    public void paramCheck(AuthorityInVo auth) throws Exception {
        if (Strings.isNullOrEmpty(auth.getAppKey()) || Strings.isNullOrEmpty(auth.getAuthorityKey()))
            throw new Exception(String.format(ExceptionContants.PARAM_NOTNULL_EXCEPTION, "appKey、authorityKey"));
        if (Strings.isNullOrEmpty(auth.getName()))
            throw new Exception(String.format(ExceptionContants.PARAM_NOTNULL_EXCEPTION, "name"));
        if (auth.getAuthorityKey().split("_").length < 2)
            throw new Exception(String.format(ExceptionContants.PREFIX_EXCEPTION, "authorityKey"));
        if (!auth.getAuthorityKey().split("_")[0].equals(auth.getAppKey()))
            throw new Exception(String.format(ExceptionContants.PREFIX_NOT_MATCH_EXCEPTION, "authorityKey"));
        if (!auth.getAuthorityKey().matches(UserConstants.REGEX_AUTHKEY))
            throw new Exception(ExceptionContants.AUTHKEY_ERR_MSG);
        if (auth.getAuthorityKey().length() > UserConstants.KEY_LENGTH)
            throw new Exception(String.format(ExceptionContants.EXCEED_LONG_EXCEPTION, "authorityKey"));
        if (null != authorityGroupDao.findByAuthorityGroupKey(auth.getAuthorityKey()))
            throw new Exception(ExceptionContants.AUTH_GROUP_KEY_EXIST);
    }

    @PreAuthorize("hasAuthority('platform_app_addAuthority')")
    public boolean newAuthority(AuthorityInVo auth) throws Exception {
        paramCheck(auth);
        if (!appService.isAppDeveloper(auth.getAppKey()))
            throw new Exception(String.format(ExceptionContants.NO_AUTHORITY_EXCEPTION, auth.getAppKey()));

        if (null != authorityDao.findByAppKeyAndAuthorityKey(auth.getAppKey(), auth.getAuthorityKey()))//校验角色名是否重复
            throw new Exception(String.format(ExceptionContants.EXIST_EXCEPTION, auth.getAuthorityKey()));
        AuthorityEntity authorityEntity = new AuthorityEntity();
        BeanUtils.copyProperties(auth, authorityEntity);
        return (null != authorityDao.save(authorityEntity));
    }

    @Transactional
    @PreAuthorize("hasAuthority('platform_app_deleteAuthority')")
    public boolean delAuthority(String authorityKey) throws Exception {
        if (Strings.isNullOrEmpty(authorityKey))
            throw new Exception(String.format(ExceptionContants.PARAM_NOTNULL_EXCEPTION, authorityKey));
        String appKey = authorityKey.split("_")[0];
        if (!appService.isAppDeveloper(appKey))
            throw new Exception(String.format(ExceptionContants.NO_AUTHORITY_EXCEPTION, appKey));
        if (null != authorityDao.findByAppKeyAndAuthorityKey(appKey, authorityKey)) {
            //删除角色
            authorityDao.deleteByAppKeyAndAuthorityKey(appKey, authorityKey);
            //删除用户角色关联关系
            myUserRelateAuthoritiesService.deleteAuthRelateByAuthKey(appKey, authorityKey);
            //删除角色组角色关联关系
            authorityGroupRelateAuthoritiesService.deleteAuthRelateByAuthKey(appKey, authorityKey);
        }
        return true;
    }

    @PreAuthorize("hasAuthority('platform_app_allAuthorities')")
    public Page<AuthorityEntity> getAuthorities(String appKey, Pageable pageable) {
        if (appService.isAppDeveloper(appKey))
            return authorityDao.findByAppKey(appKey, pageable);
        return null;
    }

    @PreAuthorize("hasAuthority('platform_app_allAuthorities')")
    public List<AuthorityEntity> getAllAuthorities(String appKey) {
        if (appService.isAppDeveloper(appKey))
            return authorityDao.findAllByAppKey(appKey);
        List<AuthorityEntity> result = new ArrayList<>();
        List<MyUserRelateAuthorities> bindAuths = myUserRelateAuthoritiesDao.findByUsernameAndAppKey(myUserService.getLoginUsername(), appKey);
        if (bindAuths.size() > 0) {
            for (MyUserRelateAuthorities relate : bindAuths) {
                result.add(authorityDao.findByAuthorityKey(relate.getAuthorityKey()));
            }
        }
        List<MyUserRelateAuthorityGroups> bindAuthGroups = myUserRelateAuthorityGroupsDao.findByUsernameAndAppKey(myUserService.getLoginUsername(), appKey);
        if (bindAuthGroups.size() > 0) {
            for (MyUserRelateAuthorityGroups relate : bindAuthGroups) {
                List<AuthorityGroupRelateAuthorities> auths = authorityGroupRelateAuthoritiesService.getListByAppKeyAndAuthGroupKey(appKey, relate.getAuthorityGroupKey());
                if (auths.size() > 0) {
                    for (AuthorityGroupRelateAuthorities groupRelate : auths) {
                        result.add(authorityDao.findByAuthorityKey(groupRelate.getAuthorityKey()));
                    }

                }
            }
        }
        return result;
    }

    @PreAuthorize("hasAuthority('platform_app_allAuthorities')")
    public List<AuthorityEntity> getAuthoritiesByName(String appKey, String keyword) throws Exception {
        if (Strings.isNullOrEmpty(appKey) || Strings.isNullOrEmpty(keyword))
            throw new Exception(String.format(ExceptionContants.PARAM_NOTNULL_EXCEPTION, "appkey 和 keyword"));
        keyword = "%" + keyword + "%";
        if (appService.isAppDeveloper(appKey))
            return authorityDao.findByAppKeyAndNameLike(appKey, keyword);
        return null;
    }

    @PreAuthorize("hasAuthority('platform_app_allAuthorities')")
    public Set<String> getAuthoritiesByApp(String appKey) {
        if (!appService.isAppDeveloper(appKey))
            return new HashSet<>();
        return getAppAuths(appKey);
    }

    /**
     * 获取应用配置的所有功能
     */
    public Set<String> getAppAuths(String appKey) {
        Set<String> authSet = new HashSet<>();
        List<AuthorityEntity> auths = authorityDao.findAllByAppKey(appKey);
        if (null == auths)
            return authSet;
        auths.forEach((AuthorityEntity a) -> authSet.add(a.getAuthorityKey()));
        return authSet;
    }

}
