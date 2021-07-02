package com.ut.security.usermgr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MyUserRelateAuthoritiesService {
    @Autowired
    MyUserRelateAuthoritiesDao myUserRelateAuthoritiesDao;

    public List<MyUserRelateAuthorities> getUserRelateAuthsByApp(String username, String appKey) {
        return myUserRelateAuthoritiesDao.findByUsernameAndAppKey(username, appKey);
    }

    public MyUserRelateAuthorities addAuthRelate(MyUserRelateAuthorities myUserRelateAuthorities) {
        return myUserRelateAuthoritiesDao.save(myUserRelateAuthorities);
    }

    public int deleteAuthRelate(String username, String authKey, String appKey) {
        return myUserRelateAuthoritiesDao.deleteByUsernameAndAuthorityKeyAndAppKey(username, authKey, appKey);
    }

    /**
     * 删除用户功能关联关系
     */
    public int deleteAuthRelateByAuthKey(String appKey, String authKey) {
        return myUserRelateAuthoritiesDao.deleteByAppKeyAndAuthorityKey(appKey, authKey);
    }

    /**
     * 删除用户所关联的所有功能
     */
    public int deleteAuthRelateByUser(String appKey, String username) {
        return myUserRelateAuthoritiesDao.deleteByAppKeyAndUsername(appKey, username);
    }
}
