package com.ut.security.social.client.miniprogram;

import com.alibaba.fastjson.JSON;
import com.ut.security.rbac.thirdaccount.AppRelateDeveloperAccount;
import com.ut.security.rbac.thirdaccount.AppRelateDeveloperAccountService;
import com.ut.security.rbac.thirdaccount.miniprogram.MiniProgramUser;
import com.ut.security.rbac.thirdaccount.wechat.ThirdAccountConstant;
import com.ut.security.social.client.AbstractOAuth2Service;
import com.ut.social.constant.OAuthConstants;
import com.ut.social.model.OAuthRequest;
import com.ut.social.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author litingting
 */
@Slf4j
@Component
public class MiniProgramOAuthService extends AbstractOAuth2Service {
    private final MiniProgramClient api;

    @Autowired
    private AppRelateDeveloperAccountService appRelateDeveloperAccountService;

    public MiniProgramOAuthService(MiniProgramClient api) {
        super(api);
        this.api = api;
    }

    public MiniProgramUser getBasicMiniProgramUserInfo(String appKey, String appSecret, String code) {
        OAuthRequest request = new OAuthRequest(api.getAccessTokenVerb(), api.getCode2SessionUrl());
        request.addQuerystringParameter("appid", appKey);
        request.addQuerystringParameter("secret", appSecret);
        request.addQuerystringParameter(OAuthConstants.JS_CODE, code);
        Response response = request.send();
        String responseBody = response.getBody();
        log.info("请求微信获取用户信息结果responseBody： {}", responseBody);
        checkWechatRespError(responseBody);
        MiniProgramUser miniProgramUser = JSON.parseObject(responseBody, MiniProgramUser.class);
        log.info("请求微信响应的用户信息： {}", miniProgramUser);
        return miniProgramUser;
    }

    @Override
    public String getAuthType() {
        return ThirdAccountConstant.MINI_PROGRAM;
    }

    @Override
    public AppRelateDeveloperAccount getAppRelateAccount(String appKey) {
        return appRelateDeveloperAccountService.getDevelopAccount(appKey, ThirdAccountConstant.MINI_PROGRAM);
    }

    @Override
    public String getAuthorizationUrl(String appKey, String redirectUrl) {
        return api.getAuthorizationUrl(appKey, redirectUrl);
    }
}
