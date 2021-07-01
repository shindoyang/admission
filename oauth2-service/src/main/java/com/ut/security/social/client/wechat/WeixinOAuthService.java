package com.ut.security.social.client.wechat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;
import com.ut.security.feign.FeignWechatService;
import com.ut.security.rbac.thirdaccount.AppRelateDeveloperAccount;
import com.ut.security.rbac.thirdaccount.wechat.ThirdAccountConstant;
import com.ut.security.social.client.AbstractOAuth2Service;
import com.ut.security.support.AES_ECB_128_Service;
import com.ut.social.api.DefaultApi20;
import com.ut.social.constant.OAuthConstants;
import com.ut.social.model.OAuthRequest;
import com.ut.social.model.Response;
import com.ut.social.model.Token;
import com.ut.social.model.Verifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author litingting
 */
@Slf4j
@Component
public class WeixinOAuthService extends AbstractOAuth2Service {
    private final DefaultApi20 api;

    @Autowired
    FeignWechatService feignWechatService;
    @Autowired
    AES_ECB_128_Service aes_ecb_128_service;

    public WeixinOAuthService(@Qualifier("weixinClient") DefaultApi20 api) {
        super(api);
        this.api = api;
    }

    @Override
    public Token getAccessToken(AppRelateDeveloperAccount appRelateInfo, Verifier verifier) {
        OAuthRequest request = new OAuthRequest(api.getAccessTokenVerb(), api.getAccessTokenEndpoint());
        request.addQuerystringParameter("appid", appRelateInfo.getAppKey());
        request.addQuerystringParameter("secret", appRelateInfo.getAppSecret());
        request.addQuerystringParameter(OAuthConstants.CODE, verifier.getValue());
        if (appRelateInfo.getScope() != null) {
            request.addQuerystringParameter(OAuthConstants.SCOPE, appRelateInfo.getScope());
        }
        Response response = request.send();
        String responseBody = response.getBody();
        log.info(responseBody);
        Object result = JSON.parse(responseBody);
        return new Token(JSONPath.eval(result, "$.access_token").toString(), "", responseBody);
    }

    @Override
    public String getAuthType() {
        return ThirdAccountConstant.WECHAT;
    }

    @Override
    public AppRelateDeveloperAccount getAppRelateAccount(String appKey) {
        return feignWechatService.getAccountByAppKey(appKey, aes_ecb_128_service.getSecurityToken());
    }

    @Override
    public String getAuthorizationUrl(String appKey, String redirectUrl) {
        return api.getAuthorizationUrl(appKey, redirectUrl);
    }
}
