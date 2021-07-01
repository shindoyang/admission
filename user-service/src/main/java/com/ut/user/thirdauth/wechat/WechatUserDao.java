package com.ut.user.thirdauth.wechat;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WechatUserDao extends JpaRepository<WechatUserEntity, Integer> {
	WechatUserEntity findByOauthUserName(String oauthUserName);
	WechatUserEntity findFirstByOpenId(String openId);
    WechatUserEntity findFirstByUnionId(String unionId);
    void deleteByOauthUserName(String oauthUserName);
    int deleteByAppIdAndOauthUserNameAndSocialAccountType(String appId, String oauthUsername, int socialAccountType);
	WechatUserEntity findByAppIdAndOpenIdAndSocialAccountType(String appId, String openId, int socialAccountType);
	List<WechatUserEntity> findByOauthUserNameAndSocialAccountType(String oauthUsername, int socialAccountType);
}
