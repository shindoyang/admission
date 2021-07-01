package com.ut.security.rbac.thirdaccount.wechat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;
import com.google.common.base.Strings;
import com.ut.security.feign.FeignUserService;
import com.ut.security.feign.FeignWechatService;
import com.ut.security.properties.SocialConstants;
import com.ut.security.rbac.thirdaccount.AppRelateDeveloperAccount;
import com.ut.security.rbac.thirdaccount.AppRelateDeveloperAccountService;
import com.ut.security.social.account.SocialAccountInfo;
import com.ut.security.social.account.SocialAccountServices;
import com.ut.security.social.client.AbstractOAuth2Service;
import com.ut.security.social.client.OauthServices;
import com.ut.security.support.AES_ECB_128_Service;
import com.ut.security.support.SpringUtils;
import com.ut.social.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @author litingting
 * @date 2019-3-29
 */
@Slf4j
@Service
public class WechatUserService implements SocialAccountServices {
	private static final String USER_INFO_URL = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s";
	@Autowired
	AppRelateDeveloperAccountService appRelateDeveloperAccountService;
	@Autowired
	FeignUserService feignUserService;
	@Autowired
	FeignWechatService feignWechatService;
	@Autowired
	AES_ECB_128_Service aes_ecb_128_service;

	@Override
	public String getAuthType() {
		return ThirdAccountConstant.WECHAT;
	}

	/**
	 * 多条件查询社交账户信息
	 * 2019/11/26支持绑定同一组织下的多个小程序绑定
	 */
	public WechatUserEntity getSocialEntityByKey(String key) {
		if (!Strings.isNullOrEmpty(key)) {
			WechatUserEntity wechatUser = feignWechatService.findFirstByOpenId(key, aes_ecb_128_service.getSecurityToken());
			if (null == wechatUser) {
				return feignWechatService.findFirstByUnionId(key, aes_ecb_128_service.getSecurityToken());
			}
			return wechatUser;
		}
		return null;
	}

	public WechatUserEntity getWechatEntityByToken(Token accessToken) {
		String authUserInfo = getRawUserInfoByToken(accessToken);
		log.info("【raw微信用户信息】" + authUserInfo);
		WechatUserEntity wechatUsr = JSON.parseObject(authUserInfo, WechatUserEntity.class);
		return wechatUsr;
	}

	public String getRawUserInfoByToken(Token accessToken) {
		Object result = JSON.parse(accessToken.getRawResponse());
		String openId = JSONPath.eval(result, "$.openid").toString();
		String userInfoUrl = String.format(USER_INFO_URL, accessToken.getToken(), openId);

		OAuthRequest request = new OAuthRequest(Verb.GET, userInfoUrl);
		Response response = request.send();
		return response.getBody();
	}

	@Override
	public AppRelateDeveloperAccount getAppRelateThirdAccount(String appId) {
		return appRelateDeveloperAccountService.getDevelopAccount(appId, ThirdAccountConstant.WECHAT);
	}

	@Override
	public String getOauthUserName(String socialEntityInJson) {
		WechatUserEntity socialRawEntity = JSON.parseObject(socialEntityInJson, WechatUserEntity.class);

		WechatUserEntity currentUser = getSocialEntityByKey(socialRawEntity.getOpenId());
		if (currentUser == null && socialRawEntity.getOpenId() != null) {
			currentUser = getSocialEntityByKey(socialRawEntity.getOpenId());
		}
		return currentUser != null ? currentUser.getOauthUserName() : null;
	}

	@Override
	public SocialAccountInfo getSocialAccount(String code, AppRelateDeveloperAccount appRelateDeveloperAccount) throws Exception {
		//获取access_token
		OauthServices oAuthServices = SpringUtils.getBean(OauthServices.class);
		AbstractOAuth2Service oAuthService = oAuthServices.getOAuthService(ThirdAccountConstant.WECHAT);
		Token accessToken = oAuthService.getAccessToken(appRelateDeveloperAccount, new Verifier(code));
		log.info("accessToken=" + accessToken);

		WechatUserService wechatUserService = SpringUtils.getBean(WechatUserService.class);

		//根据access_token获取用户信息
		WechatUserEntity wechatUser = wechatUserService.getWechatEntityByToken(accessToken);
		wechatUser.setAppId(appRelateDeveloperAccount.getAppKey());
		log.info("微信--给微信返回的用户信息添加对应的appId后{}", wechatUser);
		return new SocialAccountInfo(JSON.toJSONString(wechatUser), wechatUser.getUnionId() != null ? wechatUser.getUnionId() : wechatUser.getOpenId());
	}

	@Override
	public boolean existSocialAccount(String uid) {
		return getSocialEntityByKey(uid) != null;
	}

	/**
	 * 检查用户是否已与该小程序绑定
	 * 该逻辑为了支撑单独解绑小程序
	 * 2021/4/7
	 */
	@Override
	public boolean existSocialAccount(String appId, String openId){
		if (!Strings.isNullOrEmpty(appId) && !Strings.isNullOrEmpty(openId)) {
			return feignWechatService.existSocialAccount(appId, openId, SocialConstants.WECHAT_TYPE_VALUE ,aes_ecb_128_service.getSecurityToken());
		}
		return false;
	}
}
