package com.ut.security.social.client;

import com.alibaba.fastjson.JSON;
import com.ut.security.rbac.thirdaccount.AppRelateDeveloperAccount;
import com.ut.social.api.DefaultApi20;
import com.ut.social.constant.OAuthConstants;
import com.ut.social.model.OAuthRequest;
import com.ut.social.model.Response;
import com.ut.social.model.Token;
import com.ut.social.model.Verifier;

/**
 * @author litingting
 */
public abstract class AbstractOAuth2Service {
    private final DefaultApi20 api;

    public AbstractOAuth2Service(DefaultApi20 api) {
        this.api = api;
    }

    /**
     * {@inheritDoc}
     */
    public Token getAccessToken(AppRelateDeveloperAccount appRelateInfo, Verifier verifier) {
        OAuthRequest request = new OAuthRequest(api.getAccessTokenVerb(), api.getAccessTokenEndpoint());
        request.addQuerystringParameter(OAuthConstants.CLIENT_ID, appRelateInfo.getAppKey());
        request.addQuerystringParameter(OAuthConstants.CLIENT_SECRET, appRelateInfo.getAppSecret());
        request.addQuerystringParameter(OAuthConstants.CODE, verifier.getValue());
        request.addQuerystringParameter(OAuthConstants.REDIRECT_URI, appRelateInfo.getRedirectUrl());
        if (appRelateInfo.getScope() != null) {
            request.addQuerystringParameter(OAuthConstants.SCOPE, appRelateInfo.getScope());
        }
        Response response = request.send();
        return api.getAccessTokenExtractor().extract(response.getBody());
    }

    /**
     * {@inheritDoc}
     */
    public String getAuthorizationUrl(String appKey, String redirectUrl) {
        return api.getAuthorizationUrl(appKey, redirectUrl);
    }

    public abstract String getAuthType();

    public abstract AppRelateDeveloperAccount getAppRelateAccount(String appKey);

    /**
     * 检查微信返回的信息是否为错误信息
     */
    public void checkWechatRespError(String responseBody) {
        WechatErrorMsg errorMsg = JSON.parseObject(responseBody, WechatErrorMsg.class);
        if (errorMsg.getErrmsg() != null) {
            throw new IllegalArgumentException("微信后台报错：" + errorMsg);
        }
    }
}
