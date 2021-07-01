package com.ut.security.client.wechat;

import com.ut.security.MyUserDetailService;
import com.ut.security.browser.wechat.WechatLoginAuthenticationFilter;
import com.ut.security.browser.wechat.WechatLoginAuthenticationProvider;
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
public class WechatAppLoginAuthenticationSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    @Autowired
    private AuthenticationFailureHandler appAuthenticationFailureHandler;

    @Autowired
    private AuthenticationSuccessHandler appAuthenticationSuccessHandler;

    @Autowired
    private MyUserDetailService myUserDetailService;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        WechatAppLoginAuthenticationFilter wechatLoginFilter = new WechatAppLoginAuthenticationFilter();
        wechatLoginFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        wechatLoginFilter.setAuthenticationSuccessHandler(appAuthenticationSuccessHandler);
        wechatLoginFilter.setAuthenticationFailureHandler(appAuthenticationFailureHandler);

        WechatLoginAuthenticationProvider wechatLoginProvider = new WechatLoginAuthenticationProvider();
        wechatLoginProvider.setMyUserDetailService(myUserDetailService);

        http.authenticationProvider(wechatLoginProvider)
                .addFilterAfter(wechatLoginFilter, UsernamePasswordAuthenticationFilter.class);
    }
}