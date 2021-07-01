package com.ut.security.social;

import com.ut.security.feign.FeignUserService;
import com.ut.security.rbac.thirdaccount.AppRelateDeveloperAccount;
import com.ut.security.rbac.thirdaccount.miniprogram.MiniProgramUser;
import com.ut.security.social.account.SocialAccountInfo;
import com.ut.security.social.account.SocialAccountServiceProvider;
import com.ut.security.social.account.SocialAccountServices;
import com.ut.security.social.client.AbstractOAuth2Service;
import com.ut.security.social.client.OauthServices;
import com.ut.security.social.client.miniprogram.MiniProgramOAuthService;
import com.ut.security.support.AES_ECB_128_Service;
import com.ut.security.utils.MD5Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.UserPrincipalNotFoundException;

/**
 * 自定义社交账号登录态服务
 * @author litingting
 * @date 2019-3-29
 */
@Slf4j
@Service
public class SocialLoginService {
    @Autowired
    private SocialAccountServiceProvider socialAccountServiceProvider;
    @Autowired
    OauthServices oAuthServices;
    @Autowired
    FeignUserService feignUserService;
    @Autowired
    MiniProgramOAuthService miniProgramOAuthService;
    @Autowired
    AES_ECB_128_Service aes_ecb_128_service;

    public String createThirdAuthUrl(String appKey, String thirdAccountType, String redirectUrl) throws UserPrincipalNotFoundException {
        AbstractOAuth2Service customOauthService = oAuthServices.getOAuthService(thirdAccountType);
        return customOauthService.getAuthorizationUrl(appKey, redirectUrl);
    }

    public String getLoginKey(String loginKey) throws Exception {
        String thirdAccountInJson = feignUserService.getCache(loginKey, aes_ecb_128_service.getSecurityToken());
        if (thirdAccountInJson == null) {
            throw new Exception("第三方账号登录失败，找不到该social账号登录key " + loginKey);
        }
        return thirdAccountInJson;
    }

    /**
     * 获取微信账号的自定义身份标识
     * @return 自定义身份标识
     */
    public SocialLoginDTO getWechatUserLoginKey(String code, String appId, String socialAccountType) throws Exception {
        log.info("get wechat loginKey:code" + code + ",appId:" + appId + ",accountType=" + socialAccountType);
        SocialAccountServices accountService = socialAccountServiceProvider.getAccountService(socialAccountType);
        AppRelateDeveloperAccount appRelateDeveloperAccount = accountService.getAppRelateThirdAccount(appId);
        if (appRelateDeveloperAccount == null) {
            throw new Exception("找不到对应的appId，当前登录的社交账号类型为" + socialAccountType + " appId==" + appId);
        }

        //请求微信后台获取用户信息:{openId,sessionKey,unionId}
        SocialAccountInfo socialAccountInfo = accountService.getSocialAccount(code, appRelateDeveloperAccount);
        //以用户openId为键，缓存用户微信信息
        String loginKey = MD5Utils.getMD5(socialAccountInfo.getUid());
        feignUserService.setCacheInTime(loginKey, 600, socialAccountInfo.getSocialEntityInJson(), aes_ecb_128_service.getSecurityToken());
        log.info("md5 之后的 socialAccountKey==" + loginKey);
        // existSocialAccount 用于客户端判断用户是否已经绑定小程序
        return new SocialLoginDTO(loginKey, accountService.existSocialAccount(appId, socialAccountInfo.getUid()));
    }

    public String getWeChatOpenId(String code, String appId, String socialAccountType) throws Exception {
        log.info("get wechat loginKey:code" + code);
        SocialAccountServices accountService = socialAccountServiceProvider.getAccountService(socialAccountType);
        AppRelateDeveloperAccount appRelateDeveloperAccount = accountService.getAppRelateThirdAccount(appId);
        if (appRelateDeveloperAccount == null) {
            throw new Exception("找不到对应的appId，当前登录的社交账号类型为" + socialAccountType + " appId==" + appId);
        }

        MiniProgramUser miniProgramUser = miniProgramOAuthService.getBasicMiniProgramUserInfo(appRelateDeveloperAccount.getAppKey(), appRelateDeveloperAccount.getAppSecret(), code);
        return miniProgramUser.getOpenId();
    }

}


