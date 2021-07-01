package com.ut.security.client.username.register;

import com.ut.security.support.SpringUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * app 图形验证码注册 验证逻辑
 */
public class RegisterAuthenticationProvider implements AuthenticationProvider {

    private UserDetailsService myUserDetailService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationServiceException {

        RegisterAuthenticationToken authenticationToken = (RegisterAuthenticationToken) authentication;

        UserDetails user = myUserDetailService.loadUserByUsername((String) authenticationToken.getPrincipal());

        if (user == null) {
            throw new InternalAuthenticationServiceException("无法获取用户信息");
        }

        //校验用户名密码
        //additionalAuthenticationChecks(user, authenticationToken);

        RegisterAuthenticationToken authenticationResult = new RegisterAuthenticationToken(user, user.getPassword(), user.getAuthorities());

        authenticationResult.setDetails(authenticationToken.getDetails());

        return authenticationResult;
    }

    protected void additionalAuthenticationChecks(UserDetails userDetails, RegisterAuthenticationToken authentication) throws AuthenticationException {
        if(authentication.getCredentials() == null) {
            throw new BadCredentialsException("密码不能为空");
        } else {
            String presentedPassword = authentication.getCredentials().toString();
            PasswordEncoder passwordEncoder = SpringUtils.getBean(PasswordEncoder.class);
            if(!passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
                throw new BadCredentialsException("您输入的密码有误！");
            }
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return RegisterAuthenticationToken.class.isAssignableFrom(authentication);
    }

    public UserDetailsService getMyUserDetailService() {
        return myUserDetailService;
    }

    public void setMyUserDetailService(UserDetailsService myUserDetailService) {
        this.myUserDetailService = myUserDetailService;
    }
}