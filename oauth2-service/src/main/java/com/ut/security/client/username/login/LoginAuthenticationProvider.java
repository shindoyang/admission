package com.ut.security.client.username.login;

import com.google.common.base.Strings;
import com.ut.security.support.SpringUtils;
import com.ut.security.utils.MD5Utils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.NoSuchAlgorithmException;

/**
 * app 用户名密码登录 验证逻辑
 */
public class LoginAuthenticationProvider implements AuthenticationProvider {

    private UserDetailsService myUserDetailService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationServiceException {

        LoginAuthenticationToken authenticationToken = (LoginAuthenticationToken) authentication;

        UserDetails user = myUserDetailService.loadUserByUsername((String) authenticationToken.getPrincipal());

        if (user == null) {
            throw new InternalAuthenticationServiceException("无法获取用户信息");
        }

        //校验用户名密码
        additionalAuthenticationChecks(user, authenticationToken);

        LoginAuthenticationToken authenticationResult = new LoginAuthenticationToken(user, user.getPassword(), user.getAuthorities());

        authenticationResult.setDetails(authenticationToken.getDetails());

        return authenticationResult;
    }

    protected void additionalAuthenticationChecks(UserDetails userDetails, LoginAuthenticationToken authentication) throws AuthenticationException {
        if(authentication.getCredentials() == null) {
            throw new BadCredentialsException("密码不能为空");
        } else {
            String presentedPassword = authentication.getCredentials().toString();
           if(Strings.isNullOrEmpty(authentication.getEncryption())) {
               PasswordEncoder passwordEncoder = SpringUtils.getBean(PasswordEncoder.class);
               if (!passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
                   throw new BadCredentialsException("您输入的密码有误！");
               }
           }else if("1".equals(authentication.getEncryption())){
               try {
                   if(!MD5Utils.getMD5(presentedPassword).equals(userDetails.getPassword()))
					   throw new BadCredentialsException("您输入的密码有误！");
               } catch (NoSuchAlgorithmException e) {
                   throw new AuthenticationServiceException(e.toString());
               }
           }else{
               throw new BadCredentialsException("暂不支持此加密类型!");
           }

        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return LoginAuthenticationToken.class.isAssignableFrom(authentication);
    }

    public UserDetailsService getMyUserDetailService() {
        return myUserDetailService;
    }

    public void setMyUserDetailService(UserDetailsService myUserDetailService) {
        this.myUserDetailService = myUserDetailService;
    }
}