package com.ut.user.usermgr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @auth: 陈佳攀
 * @Description:
 * @Date: Created in 17:09 2017-11-17
 */
@Service
public class MyUserRelateAuthorityGroupsService{
    @Autowired
    MyUserRelateAuthorityGroupsDao myUserRelateAuthorityGroupsDao;

    public void init(String appKey, String username, String authGroupKey){
        if (null == myUserRelateAuthorityGroupsDao.findByAppKeyAndUsernameAndAuthorityGroupKey(appKey, username, authGroupKey)) {
            MyUserRelateAuthorityGroups relate = new MyUserRelateAuthorityGroups();
            relate.setAppKey(appKey);
            relate.setUsername(username);
            relate.setAuthorityGroupKey(authGroupKey);
            myUserRelateAuthorityGroupsDao.save(relate);
        }
    }

    public List<MyUserRelateAuthorityGroups> getUserRelateAuthGroupsByApp(String username, String appKey){
        return myUserRelateAuthorityGroupsDao.findByUsernameAndAppKey(username, appKey);
    }

    public MyUserRelateAuthorityGroups addAuthGroupRelate(MyUserRelateAuthorityGroups myUserRelateAuthorityGroups){
        return myUserRelateAuthorityGroupsDao.save(myUserRelateAuthorityGroups);
    }

    public int deleteAuthGroupRelate(String username, String authGroupKey, String appKey){
        return myUserRelateAuthorityGroupsDao.deleteByUsernameAndAuthorityGroupKeyAndAppKey(username, authGroupKey, appKey);
    }

    public int deleteAuthGroupRelateByUsername(String appKey, String username){
        return myUserRelateAuthorityGroupsDao.deleteByAppKeyAndUsername(appKey, username);
    }

}
