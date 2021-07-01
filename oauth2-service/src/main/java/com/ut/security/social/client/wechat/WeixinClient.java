package com.ut.security.social.client.wechat;

import com.ut.security.feign.FeignUserService;
import com.ut.security.rbac.MyUserEntity;
import com.ut.security.support.AES_ECB_128_Service;
import com.ut.social.api.DefaultApi20;
import com.ut.social.utils.OAuthEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 与微信服务器交互的客户端（适用于PC端 及 移动端）
 * @author litingting
 */
@Service
public class WeixinClient extends DefaultApi20 {
	private static final String AUTHORIZE_URL = "https://open.weixin.qq.com/connect/qrconnect?appid=%s&redirect_uri=%s&response_type=code&state=esfadsgsad34fwdef&scope=snsapi_login#wechat_redirect";
	private static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?grant_type=authorization_code";

	@Autowired
	FeignUserService feignUserService;
	@Autowired
	AES_ECB_128_Service aes_ecb_128_service;

	@Override
	public String getAuthorizationUrl(String appKey, String redirectUrl) {
		MyUserEntity user = feignUserService.getSelf(aes_ecb_128_service.getSecurityToken());
		String encodeParam;

		if (user != null){
			encodeParam = redirectUrl + "?uKey=" +  user.getUsername();
		} else{
			encodeParam = redirectUrl;
		}
		return String.format(AUTHORIZE_URL, appKey, OAuthEncoder.encode(encodeParam));
	}

	@Override
	public String getAccessTokenEndpoint() {
		return ACCESS_TOKEN_URL;
	}
}
