package com.ut.security.client.miniprogram.login;

import com.ut.security.MyUserDetailService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @author litingting
 */
public class MiniProgramAuthenticationProvider implements AuthenticationProvider {

    private MyUserDetailService myUserDetailService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationServiceException {

        MiniProgramAuthenticationToken authenticationToken = (MiniProgramAuthenticationToken) authentication;

        UserDetails user = myUserDetailService.loadUserByUsername((String) authenticationToken.getPrincipal());

        if (user == null) {
            throw new InternalAuthenticationServiceException("无法获取用户信息");
        }

        MiniProgramAuthenticationToken authenticationResult = new MiniProgramAuthenticationToken(user, user.getAuthorities());

        authenticationResult.setDetails(authenticationToken.getDetails());

        return authenticationResult;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return MiniProgramAuthenticationToken.class.isAssignableFrom(authentication);
    }

    public UserDetailsService getMyUserDetailService() {
        return myUserDetailService;
    }

    public void setMyUserDetailService(MyUserDetailService myUserDetailService) {
        this.myUserDetailService = myUserDetailService;
    }
}