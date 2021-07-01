package com.ut.user.oauthFeignController;

import com.ut.user.support.AES_ECB_128_Service;
import com.ut.user.thirdauth.account.AppRelateDeveloperAccount;
import com.ut.user.thirdauth.account.AppRelateDeveloperAccountDao;
import com.ut.user.thirdauth.wechat.WechatService;
import com.ut.user.thirdauth.wechat.WechatUserDao;
import com.ut.user.thirdauth.wechat.WechatUserEntity;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 以下为oauth 服务feign调用专用接口，api不允许暴露
 */
@ApiIgnore
@RestController
@RequestMapping("/public")
@Api(description="Oauth服务feign专用接口集-微信部分", tags= {"WechatFeignController"})
public class WechatFeignController {
    @Autowired
    private WechatService wechatService;
    @Autowired
    private WechatUserDao wechatUserDao;
    @Autowired
    AppRelateDeveloperAccountDao appRelateDeveloperAccountDao;
    @Autowired
    AES_ECB_128_Service aes_ecb_128_service;

    @GetMapping("/findFirstByUnionId")
    public WechatUserEntity findFirstByUnionId(String key, String token)throws Exception{
        aes_ecb_128_service.checkSecurityToken(token);
        return wechatUserDao.findFirstByUnionId(key);
    }

    @GetMapping("/findFirstByOpenId")
    public WechatUserEntity findFirstByOpenId(String key, String token)throws Exception{
        aes_ecb_128_service.checkSecurityToken(token);
        return wechatUserDao.findFirstByOpenId(key);
    }

    @GetMapping("/getByAppKeyAndAccountType")
    public AppRelateDeveloperAccount getByAppKeyAndAccountType(String appId, String accountType, String token)throws Exception{
        aes_ecb_128_service.checkSecurityToken(token);
        return appRelateDeveloperAccountDao.findFirstByAppKeyAndSocialAccountType(appId, accountType);
    }

    @GetMapping("/getAccountByAppKey")
    public AppRelateDeveloperAccount getAccountByAppKey(String appId, String token)throws Exception{
        aes_ecb_128_service.checkSecurityToken(token);
        return appRelateDeveloperAccountDao.findFirstByAppKey(appId);
    }

    /**
     * 检查用户是否绑定指定的小程序
     * 并对于存量数据，补充appId的关联关系
     */
    @GetMapping("/existSocialAccount")
    private boolean existSocialAccount(String appId, String openId, Integer socialAccountType, String token)throws Exception{
        aes_ecb_128_service.checkSecurityToken(token);
        return wechatService.existSocialAccount(appId, openId, socialAccountType);
    }

}
