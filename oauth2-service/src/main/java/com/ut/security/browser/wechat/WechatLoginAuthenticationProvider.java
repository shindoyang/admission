package com.ut.security.browser.wechat;

import com.ut.security.MyUserDetailService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public class WechatLoginAuthenticationProvider implements AuthenticationProvider {

    private MyUserDetailService myUserDetailService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationServiceException {

        WechatLoginAuthenticationToken authenticationToken = (WechatLoginAuthenticationToken) authentication;

        UserDetails user = myUserDetailService.loadUserByUsername((String) authenticationToken.getPrincipal());

        if (user == null) {
            throw new InternalAuthenticationServiceException("无法获取用户信息");
        }

        WechatLoginAuthenticationToken authenticationResult = new WechatLoginAuthenticationToken(user, user.getAuthorities());

        authenticationResult.setDetails(authenticationToken.getDetails());

        return authenticationResult;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return WechatLoginAuthenticationToken.class.isAssignableFrom(authentication);
    }

    public UserDetailsService getMyUserDetailService() {
        return myUserDetailService;
    }

    public void setMyUserDetailService(MyUserDetailService myUserDetailService) {
        this.myUserDetailService = myUserDetailService;
    }
}