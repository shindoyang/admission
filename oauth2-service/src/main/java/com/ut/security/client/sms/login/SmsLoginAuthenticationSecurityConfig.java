package com.ut.security.client.sms.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

@Component
public class SmsLoginAuthenticationSecurityConfig  extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    @Autowired
    private AuthenticationFailureHandler appAuthenticationFailureHandler;

    @Autowired
    private AuthenticationSuccessHandler appAuthenticationSuccessHandler;

    @Autowired
    private UserDetailsService myUserDetailService;

    @Override
    public void configure(HttpSecurity http) throws Exception {

        SmsLoginAuthenticationFilter smsLoginAuthenticationFilter = new SmsLoginAuthenticationFilter();
        smsLoginAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        smsLoginAuthenticationFilter.setAuthenticationSuccessHandler(appAuthenticationSuccessHandler);
        smsLoginAuthenticationFilter.setAuthenticationFailureHandler(appAuthenticationFailureHandler);

        SmsLoginAuthenticationProvider smsLoginAuthenticationProvider = new SmsLoginAuthenticationProvider();
        smsLoginAuthenticationProvider.setMyUserDetailService(myUserDetailService);

        http.authenticationProvider(smsLoginAuthenticationProvider)
                .addFilterAfter(smsLoginAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    }

}