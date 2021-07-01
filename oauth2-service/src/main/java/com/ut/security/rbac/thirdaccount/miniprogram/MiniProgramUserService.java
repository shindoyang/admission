package com.ut.security.rbac.thirdaccount.miniprogram;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.ut.security.feign.FeignWechatService;
import com.ut.security.properties.SocialConstants;
import com.ut.security.rbac.thirdaccount.AppRelateDeveloperAccount;
import com.ut.security.rbac.thirdaccount.wechat.ThirdAccountConstant;
import com.ut.security.rbac.thirdaccount.wechat.WechatUserEntity;
import com.ut.security.rbac.thirdaccount.wechat.WechatUserService;
import com.ut.security.social.account.SocialAccountInfo;
import com.ut.security.social.account.SocialAccountServices;
import com.ut.security.social.client.miniprogram.MiniProgramOAuthService;
import com.ut.security.support.AES_ECB_128_Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author litingting
 * @date 2019-3-29
 */
@Slf4j
@Service
public class MiniProgramUserService implements SocialAccountServices {
    @Autowired
    MiniProgramOAuthService miniProgramOAuthService;
    @Autowired
    WechatUserService wechatUserService;
	@Autowired
	FeignWechatService feignWechatService;
	@Autowired
	AES_ECB_128_Service aes_ecb_128_service;

    @Override
    public String getAuthType() {
        return ThirdAccountConstant.MINI_PROGRAM;
    }

    @Override
    public AppRelateDeveloperAccount getAppRelateThirdAccount(String appId) {
        return miniProgramOAuthService.getAppRelateAccount(appId);
    }

	@Override
	public String getOauthUserName(String socialEntityInJson) {
		MiniProgramUser miniProgramUser = JSON.parseObject(socialEntityInJson, MiniProgramUser.class);
		WechatUserEntity wechatUser = wechatUserService.getSocialEntityByKey(miniProgramUser.getOpenId());
		if (wechatUser == null) {
			if (!Strings.isNullOrEmpty(miniProgramUser.getUnionId())) {
				wechatUser = wechatUserService.getSocialEntityByKey(miniProgramUser.getUnionId());
			}
		}
		return wechatUser != null ? wechatUser.getOauthUserName() : null;
	}

    @Override
    public SocialAccountInfo getSocialAccount(String code, AppRelateDeveloperAccount appRelateDeveloperAccount) throws Exception {
        MiniProgramUser miniProgramUser = miniProgramOAuthService.getBasicMiniProgramUserInfo(appRelateDeveloperAccount.getAppKey(), appRelateDeveloperAccount.getAppSecret(), code);
        if (miniProgramUser.getOpenId() == null) {
            throw new Exception("无法获取使用该小程序的用户信息，请查看传入的code是否有效或正确,code==" + code);
        }
        miniProgramUser.setAppId(appRelateDeveloperAccount.getAppKey());
        log.info("小程序--给微信返回的用户信息添加对应的appId后{}", miniProgramUser);
        return new SocialAccountInfo(JSON.toJSONString(miniProgramUser), miniProgramUser.getOpenId());
    }

    @Override
    public boolean existSocialAccount(String uid) {
        return wechatUserService.getSocialEntityByKey(uid) != null;
    }

	@Override
	public boolean existSocialAccount(String appId, String openId){
		if (!Strings.isNullOrEmpty(appId) && !Strings.isNullOrEmpty(openId)) {
			return feignWechatService.existSocialAccount(appId, openId, SocialConstants.MINIPROGRAM_TYPE_VALUE, aes_ecb_128_service.getSecurityToken());
		}
		return false;
	}
}
