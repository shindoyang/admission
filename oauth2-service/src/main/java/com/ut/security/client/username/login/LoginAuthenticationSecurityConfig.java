package com.ut.security.client.username.login;

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
public class LoginAuthenticationSecurityConfig  extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    @Autowired
    private AuthenticationFailureHandler appAuthenticationFailureHandler;

    @Autowired
    private AuthenticationSuccessHandler appAuthenticationSuccessHandler;

    @Autowired
    private UserDetailsService myUserDetailService;

    @Override
    public void configure(HttpSecurity http) throws Exception {

        LoginAuthenticationFilter loginAuthenticationFilter = new LoginAuthenticationFilter();
        loginAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        loginAuthenticationFilter.setAuthenticationSuccessHandler(appAuthenticationSuccessHandler);
        loginAuthenticationFilter.setAuthenticationFailureHandler(appAuthenticationFailureHandler);

        LoginAuthenticationProvider loginAuthenticationProvider = new LoginAuthenticationProvider();
        loginAuthenticationProvider.setMyUserDetailService(myUserDetailService);

        http.authenticationProvider(loginAuthenticationProvider)
                .addFilterAfter(loginAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    }

}