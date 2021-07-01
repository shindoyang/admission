package com.ut.security.client.miniprogram.login;

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

/**
 * @author litingting
 */
@Component
public class MiniProgramAuthenticationSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    @Autowired
    private AuthenticationFailureHandler appAuthenticationFailureHandler;
    @Autowired
    private AuthenticationSuccessHandler appAuthenticationSuccessHandler;

    @Autowired
    private MyUserDetailService myUserDetailService;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        MiniProgramLoginAuthenticationFilter miniProgramLoginAuthenticationFilter = new MiniProgramLoginAuthenticationFilter();
        miniProgramLoginAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        miniProgramLoginAuthenticationFilter.setAuthenticationSuccessHandler(appAuthenticationSuccessHandler);
        miniProgramLoginAuthenticationFilter.setAuthenticationFailureHandler(appAuthenticationFailureHandler);

        MiniProgramAuthenticationProvider wechatAuthenticationProvider = new MiniProgramAuthenticationProvider();
        wechatAuthenticationProvider.setMyUserDetailService(myUserDetailService);

        http.authenticationProvider(wechatAuthenticationProvider)
                .addFilterAfter(miniProgramLoginAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }
}