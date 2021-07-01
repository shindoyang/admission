package com.ut.security.social.client.miniprogram;

import com.ut.social.api.DefaultApi20;
import org.springframework.stereotype.Service;

/**
 * @author litingting
 */
@Service
public class MiniProgramClient extends DefaultApi20 {
	private static final String CODE2SESSION_URL = "https://api.weixin.qq.com/sns/jscode2session?grant_type=authorization_code";

	@Override
	public String getAuthorizationUrl(String appKey, String redirectUrl) {
		return null;
	}

	@Override
	public String getAccessTokenEndpoint() {
		return null;
	}

	public String getCode2SessionUrl(){
		return CODE2SESSION_URL;
	}
}
