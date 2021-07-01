package com.ut.user.thirdauth;

import com.ut.user.constants.ThirdAccountConstant;
import com.ut.user.thirdauth.account.AppRelateDeveloperAccount;
import com.ut.user.thirdauth.account.AppRelateDeveloperAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * 初始化微信数据
 * @author litingting
 */
@Service
public class InitWechatDataService {
    @Autowired
    AppRelateDeveloperAccountService appRelateDeveloperAccountService;

    @PostConstruct
    public void init() {
        //用户中心微信PC端
        initDevelopAccount(ThirdAccountConstant.WECAHT_WEB_APPKEY, ThirdAccountConstant.WECHAT_WEB_SECRET, "用户中心PC端", ThirdAccountConstant.WECHAT);
        // 用户中心微信移动端
        initDevelopAccount(ThirdAccountConstant.WECHAT_MOBILE_USER_APPKEY, ThirdAccountConstant.WECHAT_MOBILE_USER_SECRET, "用户中心移动端", ThirdAccountConstant.WECHAT_MOBILE);
        //用户中心小程序
        initDevelopAccount(ThirdAccountConstant.MINIP_USER_APPKEY, ThirdAccountConstant.MINIP_USER_SECRET, "用户中心小程序", ThirdAccountConstant.MINI_PROGRAM);
        //餐饮应用小程序
        initDevelopAccount(ThirdAccountConstant.COOK_APPKEY, ThirdAccountConstant.COOK_SECRET, "餐饮应用小程序", ThirdAccountConstant.MINI_PROGRAM);
    }

    private void initDevelopAccount(String appKey, String secret, String appName, String accountType){
        if (appRelateDeveloperAccountService.getDevelopeAccountByAppKey(appKey) == null) {
            appRelateDeveloperAccountService.saveDevelopAccount(new AppRelateDeveloperAccount(appKey, secret, appName, accountType));
        }
    }
}
