package com.ut.security.client.username.register;

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
public class RegisterAuthenticationSecurityConfig  extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    @Autowired
    private AuthenticationFailureHandler appAuthenticationFailureHandler;

    @Autowired
    private AuthenticationSuccessHandler appAuthenticationSuccessHandler;

    @Autowired
    private UserDetailsService myUserDetailService;

    @Override
    public void configure(HttpSecurity http) throws Exception {

        RegisterAuthenticationFilter registerAuthenticationFilter = new RegisterAuthenticationFilter();
        registerAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        registerAuthenticationFilter.setAuthenticationSuccessHandler(appAuthenticationSuccessHandler);
        registerAuthenticationFilter.setAuthenticationFailureHandler(appAuthenticationFailureHandler);

        RegisterAuthenticationProvider registerAuthenticationProvider = new RegisterAuthenticationProvider();
        registerAuthenticationProvider.setMyUserDetailService(myUserDetailService);

        http.authenticationProvider(registerAuthenticationProvider)
                .addFilterAfter(registerAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    }

}