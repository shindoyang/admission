package com.ut.security.service;

import com.ut.security.dao.AuthorityGroupRelateAuthoritiesDao;
import com.ut.security.model.AuthorityGroupRelateAuthorities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @auth: 陈佳攀
 * @Description:
 * @Date: Created in 10:34 2017-11-20
 */
@Service
public class AuthorityGroupRelateAuthoritiesService {
    @Autowired
    private AuthorityGroupRelateAuthoritiesDao authorityGroupRelateAuthoritiesDao;

    /**
     * 开发者角色 绑定功能 数据初始化
     *
     * @param appKey            应用key
     * @param authorityGroupKey 角色key
     * @param authorityKey      功能key
     */
    public void initAuthGroupRealteAuth(String appKey, String authorityGroupKey, String authorityKey) {
        if (null != authorityGroupRelateAuthoritiesDao.findByAppKeyAndAuthorityGroupKeyAndAuthorityKey(appKey, authorityGroupKey, authorityKey))
            return;
        AuthorityGroupRelateAuthorities relate = new AuthorityGroupRelateAuthorities();
        relate.setAppKey(appKey);
        relate.setAuthorityGroupKey(authorityGroupKey);
        relate.setAuthorityKey(authorityKey);
        authorityGroupRelateAuthoritiesDao.save(relate);
    }

    public List<AuthorityGroupRelateAuthorities> getListByAppKeyAndAuthGroupKey(String appKey, String authGroupKey) {
        return authorityGroupRelateAuthoritiesDao.findByAppKeyAndAuthorityGroupKey(appKey, authGroupKey);
    }

    public AuthorityGroupRelateAuthorities addAuthGroupRealteAuth(AuthorityGroupRelateAuthorities authorityGroupRelateAuthorities) {
        return authorityGroupRelateAuthoritiesDao.save(authorityGroupRelateAuthorities);
    }

    public int deleteAuthGroupRelateAuth(String appKey, String authGroupKey, String authDel) {
        return authorityGroupRelateAuthoritiesDao.deleteByAppKeyAndAuthorityGroupKeyAndAuthorityKey(appKey, authGroupKey, authDel);

    }

    /**
     * 根据功能删除角色功能关联关系
     */
    public int deleteAuthRelateByAuthKey(String appKey, String authKey) {
        return authorityGroupRelateAuthoritiesDao.deleteByAppKeyAndAuthorityKey(appKey, authKey);
    }

    /**
     * 根据角色删除角色功能关联关系
     */
    public int deleteAuthGroupRelateByAuthGroupKey(String appKey, String authGroupKey) {
        return authorityGroupRelateAuthoritiesDao.deleteByAppKeyAndAuthorityGroupKey(appKey, authGroupKey);
    }

    /**
     * 根据角色组获取其绑定的所有的角色
     */
    public Set<String> getAuthsRelateByAuthGroup(String appKey, String authGroupKey) {
        Set<String> auths = new HashSet<>();
        String[] groupKeys = authGroupKey.split(",");
        Set<AuthorityGroupRelateAuthorities> relateAuthsSet = authorityGroupRelateAuthoritiesDao.findByAppKeyAndAuthorityGroupKeyIn(appKey, groupKeys);
        relateAuthsSet.forEach(relateAuth -> auths.add(relateAuth.getAuthorityKey()));
        return auths;
    }

}
