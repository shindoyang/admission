package com.ut.security;

import com.google.common.base.Strings;
import com.ut.security.support.AES_ECB_128_Service;
import com.ut.security.usermgr.MyUserEntity;
import com.ut.security.usermgr.MyUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.HashMap;
import java.util.Map;

public class CustomTokenEnhancer implements TokenEnhancer {

    @Autowired
    MyUserService myUserService;
    @Autowired
    AES_ECB_128_Service aes_ecb_128_service;

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        final Map<String, Object> additionalInfo = new HashMap<>();

//        additionalInfo.put("customInfo", "some_stuff_here");
        // 注意添加的额外信息，最好不要和已有的json对象中的key重名，容易出现错误
        //additionalInfo.put("authorities", user.getAuthorities());
        MyUserEntity userByUid = myUserService.getUserByUid(user.getUsername());
        additionalInfo.put("username",userByUid.getLoginName());
        additionalInfo.put("accountSystemKey",userByUid.getAccountSystemKey());
        if(!Strings.isNullOrEmpty(userByUid.getParentUser())){
            additionalInfo.put("parentUser", userByUid.getParentUser());
        }

        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);

        return accessToken;
    }
}

