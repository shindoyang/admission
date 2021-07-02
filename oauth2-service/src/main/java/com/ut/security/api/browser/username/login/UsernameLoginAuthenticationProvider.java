package com.ut.security.api.browser.username.login;

import com.google.common.base.Strings;
import com.ut.security.constant.SecurityConstants;
import com.ut.security.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
@Slf4j
public class UsernameLoginAuthenticationProvider implements AuthenticationProvider {

    private UserDetailsService myUserDetailService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationServiceException {

        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) authentication;

        UserDetails user = myUserDetailService.loadUserByUsername((String) authenticationToken.getPrincipal());

        if (user == null) {
            throw new InternalAuthenticationServiceException("无法获取用户信息");
        }

        //校验用户名密码
        additionalAuthenticationChecks(user, authenticationToken);

        UsernamePasswordAuthenticationToken authenticationResult = new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());

        authenticationResult.setDetails(authenticationToken.getDetails());

        return authenticationResult;
    }

    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        if(authentication.getCredentials() == null) {
            throw new BadCredentialsException("密码不能为空");
        } else {
            /*String presentedPassword = authentication.getCredentials().toString();

            PasswordEncoder passwordEncoder = SpringUtils.getBean(PasswordEncoder.class);
            if(!passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
                throw new BadCredentialsException("您输入的密码有误！");
            }*/
            String principal = (String)authentication.getPrincipal();
            log.info("==> checkPassword  params = " + principal);
            boolean checkPwd = true;
            if(principal.split(",").length > 1){
                String type = principal.split(",")[0];
                if(type.equals(SecurityConstants.LOGIN_TYPE_APP_MOBILE_REGISTER) || type.equals(SecurityConstants.LOGIN_TYPE_APP_MOBILE_LOGIN)){
                    checkPwd = false;
                }
            }
            log.info("==> is need to check password = " + checkPwd);
            if(checkPwd){
                String presentedPassword = authentication.getCredentials().toString();
                PasswordEncoder passwordEncoder = SpringUtils.getBean(PasswordEncoder.class);
                if(Strings.isNullOrEmpty(userDetails.getPassword())){
                    throw new BadCredentialsException("您没有设置过密码，无法通过密码的登录！");
                }
                if (!passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
                    throw new BadCredentialsException("您输入密码有误！");//password does not match stored value
                }
            }
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    public UserDetailsService getMyUserDetailService() {
        return myUserDetailService;
    }

    public void setMyUserDetailService(UserDetailsService myUserDetailService) {
        this.myUserDetailService = myUserDetailService;
    }
}