package com.ut.security.browser.sms;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * 短信登录验证逻辑
 *
 * 由于短信验证码的验证在过滤器里已完成，这里直接读取用户信息即可。
 *
 */
public class SmsCodeAuthenticationProvider implements AuthenticationProvider {

    private UserDetailsService myUserDetailService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationServiceException {

        SmsCodeAuthenticationToken authenticationToken = (SmsCodeAuthenticationToken) authentication;

        UserDetails user = myUserDetailService.loadUserByUsername((String) authenticationToken.getPrincipal());

        if (user == null) {
            throw new InternalAuthenticationServiceException("无法获取用户信息");
        }

        SmsCodeAuthenticationToken authenticationResult = new SmsCodeAuthenticationToken(user, user.getAuthorities());

        authenticationResult.setDetails(authenticationToken.getDetails());

        return authenticationResult;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.security.authentication.AuthenticationProvider#
     * supports(java.lang.Class)
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return SmsCodeAuthenticationToken.class.isAssignableFrom(authentication);
    }

    public UserDetailsService getMyUserDetailService() {
        return myUserDetailService;
    }

    public void setMyUserDetailService(UserDetailsService myUserDetailService) {
        this.myUserDetailService = myUserDetailService;
    }
}