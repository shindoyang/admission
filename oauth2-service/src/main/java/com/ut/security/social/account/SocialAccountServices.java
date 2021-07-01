package com.ut.security.social.account;


import com.ut.security.rbac.thirdaccount.AppRelateDeveloperAccount;

/**
 * @author litingting
 * @date 2019-3-29
 */
public interface SocialAccountServices {
	String getAuthType();

	AppRelateDeveloperAccount getAppRelateThirdAccount(String appId);

	String getOauthUserName(String socialEntityInJson);

	SocialAccountInfo getSocialAccount(String code, AppRelateDeveloperAccount appRelateDeveloperAccount) throws Exception;

	boolean existSocialAccount(String uid);

	boolean existSocialAccount(String appId, String openId);
}
