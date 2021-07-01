package com.ut.security.client.sms.register;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * app 短信验证码注册 验证逻辑
 */
public class SmsRegisterAuthenticationProvider implements AuthenticationProvider {

    private UserDetailsService myUserDetailService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationServiceException {

        SmsRegisterAuthenticationToken authenticationToken = (SmsRegisterAuthenticationToken) authentication;

        UserDetails user = myUserDetailService.loadUserByUsername((String) authenticationToken.getPrincipal());

        if (user == null) {
            throw new InternalAuthenticationServiceException("无法获取用户信息");
        }

        SmsRegisterAuthenticationToken authenticationResult = new SmsRegisterAuthenticationToken(user, user.getAuthorities());

        authenticationResult.setDetails(authenticationToken.getDetails());

        return authenticationResult;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return SmsRegisterAuthenticationToken.class.isAssignableFrom(authentication);
    }

    public UserDetailsService getMyUserDetailService() {
        return myUserDetailService;
    }

    public void setMyUserDetailService(UserDetailsService myUserDetailService) {
        this.myUserDetailService = myUserDetailService;
    }
}