package com.ut.user.thirdauth.account;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AppRelateDeveloperAccountDao extends JpaRepository<AppRelateDeveloperAccount, Long> {
    AppRelateDeveloperAccount findFirstByAppKey(String appKey);
	AppRelateDeveloperAccount findFirstByAppKeyAndSocialAccountType(String appId, String type);
}
