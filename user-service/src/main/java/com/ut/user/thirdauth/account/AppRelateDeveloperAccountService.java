package com.ut.user.thirdauth.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author litingting
 */
@Service
public class AppRelateDeveloperAccountService {
    @Autowired
    AppRelateDeveloperAccountDao appRelateDeveloperAccountDao;

    public AppRelateDeveloperAccount getDevelopAccount(String appId, String accountType){
        return appRelateDeveloperAccountDao.findFirstByAppKeyAndSocialAccountType(appId, accountType);
    }

    public void saveDevelopAccount(AppRelateDeveloperAccount appRelateDeveloperAccount){
        appRelateDeveloperAccountDao.save(appRelateDeveloperAccount);
    }

    public AppRelateDeveloperAccount getDevelopeAccountByAppKey(String appKey) {
        return appRelateDeveloperAccountDao.findFirstByAppKey(appKey);
    }
}
