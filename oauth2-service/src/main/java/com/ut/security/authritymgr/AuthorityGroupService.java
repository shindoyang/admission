package com.ut.security.authritymgr;

import com.google.common.base.Strings;
import com.ut.security.appmgr.AppDao;
import com.ut.security.appmgr.AppService;
import com.ut.security.properties.ExceptionContants;
import com.ut.security.properties.UserConstants;
import com.ut.security.usermgr.MyUserEntity;
import com.ut.security.usermgr.MyUserRelateAuthorityGroupsDao;
import com.ut.security.usermgr.MyUserService;
import com.ut.security.vo.AuthorityGroupInVo;
import com.ut.security.vo.AuthorityVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class AuthorityGroupService {
    @Autowired
    AppDao appDao;
    @Autowired
    AppService appService;
    @Autowired
    AuthorityDao authorityDao;
    @Autowired
    MyUserService myUserService;
    @Autowired
    AuthorityGroupDao authorityGroupDao;
    @Autowired
    MyUserRelateAuthorityGroupsDao myUserRelateAuthorityGroupsDao;
    @Autowired
    AuthorityGroupRelateAuthoritiesDao authorityGroupRelateAuthoritiesDao;
    @Autowired
    AuthorityGroupRelateAuthoritiesService authorityGroupRelateAuthoritiesService;

    public AuthorityGroupService() {
    }

    /**
     * 角色初始化
     *
     * @param appKey               应用key
     * @param authorityGroupKey    角色key
     * @param authorityGroupName   角色名
     * @param authorityGroupParent 角色父节点
     * @param description          角色描述
     */
    public void initAuthGroup(String appKey, String authorityGroupKey, String authorityGroupName, String authorityGroupParent, String description) {
        if (null != authorityGroupDao.findByAppKeyAndAuthorityGroupKey(appKey, authorityGroupKey))
            return;
        AuthorityGroupEntity authGroup = new AuthorityGroupEntity();
        authGroup.setAppKey(appKey);
        authGroup.setAuthorityGroupKey(authorityGroupKey);
        authGroup.setAuthorityGroupName(authorityGroupName);
        if (!Strings.isNullOrEmpty(authorityGroupParent))
            authGroup.setAuthorityGroupParent(authorityGroupParent);
        if (!Strings.isNullOrEmpty(description))
            authGroup.setDescription(description);
        authorityGroupDao.save(authGroup);
    }

    /**
     * 根据appKey、父账户authGroupKey 获取子角色组列表
     */
    public List<AuthorityGroupEntity> getListAuthGroupByParent(String appKey, String parentAuthGroupKey) {
        return authorityGroupDao.findByAppKeyAndAuthorityGroupParent(appKey, parentAuthGroupKey);
    }

    public void paramCheck(AuthorityGroupInVo authorityGroup) throws Exception {
        if (Strings.isNullOrEmpty(authorityGroup.getAppKey()) || Strings.isNullOrEmpty(authorityGroup.getAuthorityGroupKey()))
            throw new Exception(String.format(ExceptionContants.PARAM_NOTNULL_EXCEPTION, "appKey 和 authorityGroupKey"));
        if (Strings.isNullOrEmpty(authorityGroup.getAuthorityGroupName()))
            throw new Exception(String.format(ExceptionContants.PARAM_NOTNULL_EXCEPTION, "authorityGroupName"));
        if (authorityGroup.getAuthorityGroupKey().split("_").length < 2)
            throw new Exception(String.format(ExceptionContants.PREFIX_EXCEPTION, "authorityGroupKey"));
        if (!authorityGroup.getAuthorityGroupKey().split("_")[0].equals(authorityGroup.getAppKey()))
            throw new Exception(String.format(ExceptionContants.PREFIX_NOT_MATCH_EXCEPTION, "authorityGroupKey"));
        if (!authorityGroup.getAuthorityGroupKey().matches(UserConstants.REGEX_AUTHKEY))
            throw new Exception(ExceptionContants.AUTHGROUPKEY_ERR_MSG);
        if (authorityGroup.getAuthorityGroupKey().length() > UserConstants.KEY_LENGTH)
            throw new Exception(String.format(ExceptionContants.EXCEED_LONG_EXCEPTION, "authorityGroupKey"));
        if (null != authorityDao.findByAuthorityKey(authorityGroup.getAuthorityGroupKey()))
            throw new Exception(ExceptionContants.AUTH_KEY_EXIST);
        if (null != authorityGroup.getAuthorityGroupParent())
            if (null == authorityGroupDao.findByAuthorityGroupKey(authorityGroup.getAuthorityGroupParent()))
                throw new Exception(String.format(ExceptionContants.PARENT_NODE_NOT_EXIST_EXCEPTION, "authorityGroupKey"));
    }

    /**
     * 新增角色
     */
    @PreAuthorize("hasAuthority('platform_app_addAuthorityGroup')")
    public boolean newAuthorityGroup(AuthorityGroupInVo authorityGroup) throws Exception {
        paramCheck(authorityGroup);
        if (!appService.isAppDeveloper(authorityGroup.getAppKey()))
            throw new Exception(String.format(ExceptionContants.NO_AUTHORITY_EXCEPTION, authorityGroup.getAppKey()));

        if (null != authorityGroupDao.findByAppKeyAndAuthorityGroupKey(authorityGroup.getAppKey(), authorityGroup.getAuthorityGroupKey()))
            throw new Exception(String.format(ExceptionContants.EXIST_EXCEPTION, authorityGroup.getAuthorityGroupKey()));
        AuthorityGroupEntity authorityGroupEntity = new AuthorityGroupEntity();
        BeanUtils.copyProperties(authorityGroup, authorityGroupEntity);
        return (null != authorityGroupDao.save(authorityGroupEntity));
    }

    //删除叶子角色
    @PreAuthorize("hasAuthority('platform_app_deleteAuthorityGroup')")
    @Transactional
    public boolean delAuthorityGroup(String authorityGroupKey) throws Exception {
        if (Strings.isNullOrEmpty(authorityGroupKey))
            throw new Exception(String.format(ExceptionContants.PARAM_NOTNULL_EXCEPTION, authorityGroupKey));

        String appKey = authorityGroupKey.split("_")[0];
        //检查当前角色是有叶子节点，否则不能删除
        if (authorityGroupDao.findByAppKeyAndAuthorityGroupParent(appKey, authorityGroupKey).size() > 0)
            throw new Exception(String.format(ExceptionContants.NOT_LEAF_EXCEPTION, authorityGroupKey));

        if (!appService.isAppDeveloper(appKey))
            throw new Exception(String.format(ExceptionContants.NO_AUTHORITY_EXCEPTION, appKey));

        //删除角色
        authorityGroupDao.deleteByAppKeyAndAuthorityGroupKey(appKey, authorityGroupKey);
        //删除用户角色关联关系
        myUserRelateAuthorityGroupsDao.deleteByAppKeyAndAuthorityGroupKey(appKey, authorityGroupKey);
        //删除角色功能关联关系
        authorityGroupRelateAuthoritiesDao.deleteByAppKeyAndAuthorityGroupKey(appKey, authorityGroupKey);
        return true;
    }

    /**
     * 修改角色名
     */
    @PreAuthorize("hasAuthority('platform_app_updateAuthorityGroup')")
    public boolean updateAuthorityGroupName(String authGroupKey, String newName) throws Exception {
        if (Strings.isNullOrEmpty(authGroupKey) || Strings.isNullOrEmpty(newName))
            throw new Exception(String.format(ExceptionContants.PARAM_NOTNULL_EXCEPTION, "authGroupKey 或 newName"));
        String appKey = authGroupKey.split("_")[0];
        if (!appService.isAppDeveloper(appKey))
            throw new Exception(String.format(ExceptionContants.NO_AUTHORITY_EXCEPTION, appKey));

        AuthorityGroupEntity authGroup = authorityGroupDao.findByAppKeyAndAuthorityGroupKey(appKey, authGroupKey);
        if (null == authGroup)
            throw new Exception(String.format(ExceptionContants.OBJECT_NOT_EXIST_EXCEPTION, authGroupKey));
        authGroup.setAuthorityGroupName(newName);
        return (null != authorityGroupDao.save(authGroup));
    }

    /**
     * 逻辑：
     * authIn 与 authDb 对比：
     * 1）交集不做修改
     * 2）authIn 与 authDb 的差集 新增
     * 3）authDb 与 authIn 的差集 删除
     */
    @PreAuthorize("hasAuthority('platform_app_bindAuth2AuthGroup')")
    @Transactional
    public synchronized boolean bindAuth2AuthGroup(String authGroupKey, AuthorityVo authorityVo) throws Exception {
        String appKey = authGroupKey.split("_")[0];
        if (!appService.isAppDeveloper(appKey))
            throw new Exception(String.format(ExceptionContants.NO_AUTHORITY_EXCEPTION, appKey));

        //若不指定功能，则删除
        if (null == authorityVo.getAuthorityKeys() || authorityVo.getAuthorityKeys().size() == 0) {
            authorityGroupRelateAuthoritiesService.deleteAuthGroupRelateByAuthGroupKey(appKey, authGroupKey);
            return true;
        }

        //判断角色与功能是否同一应用
        for (String key : authorityVo.getAuthorityKeys()) {
            if (!authGroupKey.split("_")[0].equals(key.split("_")[0])) {
                throw new Exception(String.format(ExceptionContants.NOT_SAME_APP_EXCEPTION, (authGroupKey + " 和 " + key)));
            }
        }

        Set<String> authIn = authorityVo.getAuthorityKeys();
        //校验上传的功能是否属于当前应用
        Set<String> wholeAuths = new HashSet<>();
        List<AuthorityEntity> authsByApp = authorityDao.findAllByAppKey(appKey);
        authsByApp.forEach(authorityEntity -> wholeAuths.add(authorityEntity.getAuthorityKey()));
        if (!wholeAuths.containsAll(authorityVo.getAuthorityKeys()))
            throw new Exception(ExceptionContants.AUTHORITY_EXCEED_EXCEPTION);

        Set<String> result = new HashSet<>();
        List<AuthorityGroupRelateAuthorities> userAuths = authorityGroupRelateAuthoritiesService.getListByAppKeyAndAuthGroupKey(appKey, authGroupKey);
        Set<String> authDb = new HashSet<>();
        userAuths.forEach((AuthorityGroupRelateAuthorities GroupRelateAuth) -> authDb.add(GroupRelateAuth.getAuthorityKey()));

        //authIn 与 authDb 的差集 新增
        result.clear();
        result.addAll(authIn);
        result.removeAll(authDb);
        result.forEach(authAdd -> authorityGroupRelateAuthoritiesService.addAuthGroupRealteAuth(new AuthorityGroupRelateAuthorities(authGroupKey, authAdd, appKey)));

        //authDb 与 authIn 的差集 删除
        result.clear();
        result.addAll(authDb);
        result.removeAll(authIn);
        result.forEach(authDel -> authorityGroupRelateAuthoritiesService.deleteAuthGroupRelateAuth(appKey, authGroupKey, authDel));
        return true;
    }

    /**
     * 获取角色名列表(Menu服务【Feign】调用)
     */
    @PreAuthorize("hasAuthority('platform_app_allAuthorityGroups')")
    public Set<String> getAuthorityGroupsByApp(String appKey) throws Exception {
        Set<String> authGroupSet = new HashSet<>();
        if (!appService.isAppDeveloper(appKey))
            return authGroupSet;
        List<AuthorityGroupEntity> authGroups = authorityGroupDao.findAllByAppKey(appKey);
        if (authGroups != null && authGroups.size() > 0)
            authGroups.forEach((AuthorityGroupEntity a) -> authGroupSet.add(a.getAuthorityGroupKey()));
        return authGroupSet;
    }

    /**
     * 获取当前应用的角色组树
     */
    @PreAuthorize("hasAuthority('platform_app_allAuthorityGroups')")
    public List<AuthorityGroupEntity> getAuthorityGroup(String appKey) throws Exception {
        if (Strings.isNullOrEmpty(appKey))
            throw new Exception(String.format(ExceptionContants.PARAM_NOTNULL_EXCEPTION, "appKey"));
        if (appService.isAppDeveloper(appKey)) {
            List<AuthorityGroupEntity> authorityGroups = authorityGroupDao.findByAppKeyAndAuthorityGroupParent(appKey, null);
            if (authorityGroups != null && authorityGroups.size() > 0) {
                for (AuthorityGroupEntity authgroup : authorityGroups) {
                    fixAuth(appKey, authgroup);
                    fixChildren(appKey, authgroup);
                }
            }
            return authorityGroups;
        }
        return null;
    }

    //获取指定应用的角色列表（平行-应用授权页使用）
//    @PreAuthorize("hasAuthority('platform_app_allAuthorityGroups')")
    public List<AuthorityGroupEntity> listAuthorityGroups(String appKey) throws Exception {
        if (Strings.isNullOrEmpty(appKey))
            throw new Exception(String.format(ExceptionContants.PARAM_NOTNULL_EXCEPTION, "appKey"));
        List<AuthorityGroupEntity> result = new ArrayList<>();
        if (appService.isAppDeveloper(appKey)) {
            getAuthGroupsByApp(appKey, result);
        } else {
            MyUserEntity curUser = myUserService.getSelf();
            if (!Strings.isNullOrEmpty(curUser.getParentUser())) {
                if (null != appDao.findByAppKeyAndDeveloper(appKey, curUser.getParentUser())) {
                    getAuthGroupsByApp(appKey, result);
                }
            }
        }
        return result;
    }

    public List<AuthorityGroupEntity> getAuthGroupsByApp(String appKey, List<AuthorityGroupEntity> result) {
        List<AuthorityGroupEntity> authorityGroups = authorityGroupDao.findByAppKeyAndAuthorityGroupParent(appKey, null);
        result.addAll(authorityGroups);
        if (authorityGroups != null && authorityGroups.size() > 0) {
            for (AuthorityGroupEntity authgroup : authorityGroups) {
                fixAuth(appKey, authgroup);
                addChildren(result, appKey, authgroup);
            }
        }
        return result;
    }

    //获取指定角色组的树
    @PreAuthorize("hasAuthority('platform_app_allAuthorityGroups')")
    public List<AuthorityGroupEntity> getAuthorityGroupsByKey(String authorityGroupKey) throws Exception {
        if (Strings.isNullOrEmpty(authorityGroupKey))
            throw new Exception(String.format(ExceptionContants.PARAM_NOTNULL_EXCEPTION, "authorityGroupKey"));
        String appKey = authorityGroupKey.split("_")[0];
        if (appService.isAppDeveloper(appKey)) {
            List<AuthorityGroupEntity> result = new ArrayList<>();
            AuthorityGroupEntity topGroup = authorityGroupDao.findByAuthorityGroupKey(authorityGroupKey);
            if (null != topGroup) {
                fixAuth(appKey, topGroup);
                List<AuthorityGroupEntity> authorityGroups = authorityGroupDao.findByAppKeyAndAuthorityGroupParent(appKey, authorityGroupKey);
                if (null != authorityGroups) {
                    for (AuthorityGroupEntity authgroup : authorityGroups) {
                        fixAuth(appKey, authgroup);
                        fixChildren(appKey, authgroup);
                    }
                    topGroup.setChildren(authorityGroups);
                }
                result.add(topGroup);
            }
            return result;
        }
        return null;
    }

    /**
     * 树结构，添加子角色信息
     */
    private void fixChildren(String appKey, AuthorityGroupEntity authgroup) {
        List<AuthorityGroupEntity> children = authorityGroupDao.findByAppKeyAndAuthorityGroupParent(appKey, authgroup.getAuthorityGroupKey());
        if (children != null && children.size() != 0) {
            authgroup.setChildren(children);
            for (AuthorityGroupEntity child : children) {
                fixAuth(appKey, child);
                fixChildren(appKey, child);
            }
        }
    }

    /**
     * 平行结构，添加子角色信息
     */
    private void addChildren(List<AuthorityGroupEntity> result, String appKey, AuthorityGroupEntity authgroup) {
        List<AuthorityGroupEntity> children = authorityGroupDao.findByAppKeyAndAuthorityGroupParent(appKey, authgroup.getAuthorityGroupKey());
        if (children != null && children.size() != 0) {
            result.addAll(children);
            for (AuthorityGroupEntity child : children) {
                fixAuth(appKey, child);
                addChildren(result, appKey, child);
            }
        }
    }

    public void fixAuth(String appKey, AuthorityGroupEntity authgroup) {
        Set<AuthorityEntity> authEntities = new HashSet<>();
        List<AuthorityGroupRelateAuthorities> userAuths = authorityGroupRelateAuthoritiesService.getListByAppKeyAndAuthGroupKey(appKey, authgroup.getAuthorityGroupKey());
        if (null != userAuths && userAuths.size() > 0) {
            userAuths.forEach((AuthorityGroupRelateAuthorities relate) -> authEntities.add(authorityDao.findByAuthorityKey(relate.getAuthorityKey())));
            authgroup.setAuthorityEntities(authEntities);
        }
    }

    /**
     * 获取应用的所有角色
     */
    public Set<String> getAppAuthGroups(String appKey) {
        Set<String> authGroupSet = new HashSet<>();
        List<AuthorityGroupEntity> authGroups = authorityGroupDao.findAllByAppKey(appKey);
        if (null == authGroups)
            return authGroupSet;
        authGroups.forEach((AuthorityGroupEntity a) -> authGroupSet.add(a.getAuthorityGroupKey()));
        return authGroupSet;
    }

    /**
     * 【Feign】获取角色绑定的所有功能
     */
    public Set<String> getAuthsByAuthGroupKeys(String appKey, String authGroupKey) {
        Set<String> auths = new HashSet<>();
        if (Strings.isNullOrEmpty(authGroupKey))
            return auths;
        auths = authorityGroupRelateAuthoritiesService.getAuthsRelateByAuthGroup(appKey, authGroupKey);
        return auths;
    }
}
