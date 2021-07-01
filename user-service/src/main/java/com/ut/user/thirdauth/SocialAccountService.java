package com.ut.user.thirdauth;

/**
 * @author litingting
 */
public interface SocialAccountService {
    String getAuthType();

    void bindSocialAccount(String loginKey) throws Exception;

    boolean unbindThirdAccount();

    boolean unbindSocialAccountByAppId(String appId, String socialAccountType)throws Exception;

    String getOauthUserName(String socialEntityInJson) throws Exception;
}
