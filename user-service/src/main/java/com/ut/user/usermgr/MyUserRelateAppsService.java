package com.ut.user.usermgr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MyUserRelateAppsService {

    @Autowired
    private MyUserRelateAppsDao myUserRelateAppsDao;

    public List<MyUserRelateApps> getAppsRelateByUsername(String username){
        return myUserRelateAppsDao.findByUsername(username);
    }

    /**
     * 用户与应用绑定
     */
    public void userBindApp(String username, String appKey){
        if(null == myUserRelateAppsDao.findByUsernameAndAppKey(username, appKey))
            myUserRelateAppsDao.save(new MyUserRelateApps(username, appKey));
    }

    public MyUserRelateApps getOneByUsernameAndAppKey(String username, String appKey){
        return myUserRelateAppsDao.findByUsernameAndAppKey(username, appKey);
    }

    /**
     * 用户应用关联关系初始化
     * @param appKey 应用key
     * @param username 用户名
     */
    public void initUserRelateApp(String appKey, String username){
        if (null != myUserRelateAppsDao.findByUsernameAndAppKey(username, appKey))
            return ;
        MyUserRelateApps relate = new MyUserRelateApps();
        relate.setAppKey(appKey);
        relate.setUsername(username);
        myUserRelateAppsDao.save(relate);
    }
}
