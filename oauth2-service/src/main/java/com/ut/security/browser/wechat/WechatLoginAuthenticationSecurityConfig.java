package com.ut.security.browser.wechat;

import com.ut.security.MyUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

@Component
public class WechatLoginAuthenticationSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    @Autowired
    private AuthenticationFailureHandler browserAuthenticationFailureHandler;
    @Autowired
    private AuthenticationSuccessHandler browserLoginAuthSuccessHandler;

    @Autowired
    private MyUserDetailService myUserDetailService;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        WechatLoginAuthenticationFilter wechatLoginFilter = new WechatLoginAuthenticationFilter();
        wechatLoginFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        wechatLoginFilter.setAuthenticationSuccessHandler(browserLoginAuthSuccessHandler);
        wechatLoginFilter.setAuthenticationFailureHandler(browserAuthenticationFailureHandler);

        WechatLoginAuthenticationProvider wechatLoginProvider = new WechatLoginAuthenticationProvider();
        wechatLoginProvider.setMyUserDetailService(myUserDetailService);

        http.authenticationProvider(wechatLoginProvider)
                .addFilterAfter(wechatLoginFilter, UsernamePasswordAuthenticationFilter.class);
    }

}