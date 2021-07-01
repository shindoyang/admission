package com.ut.security.client.sms.register;

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
public class SmsRegisterAuthenticationSecurityConfig  extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    @Autowired
    private AuthenticationFailureHandler appAuthenticationFailureHandler;

    @Autowired
    private AuthenticationSuccessHandler appAuthenticationSuccessHandler;

    @Autowired
    private UserDetailsService myUserDetailService;

    @Override
    public void configure(HttpSecurity http) throws Exception {

        SmsRegisterAuthenticationFilter smsRegisterAuthenticationFilter = new SmsRegisterAuthenticationFilter();
        smsRegisterAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        smsRegisterAuthenticationFilter.setAuthenticationSuccessHandler(appAuthenticationSuccessHandler);
        smsRegisterAuthenticationFilter.setAuthenticationFailureHandler(appAuthenticationFailureHandler);

        SmsRegisterAuthenticationProvider smsRegisterAuthenticationProvider = new SmsRegisterAuthenticationProvider();
        smsRegisterAuthenticationProvider.setMyUserDetailService(myUserDetailService);

        http.authenticationProvider(smsRegisterAuthenticationProvider)
                .addFilterAfter(smsRegisterAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    }

}