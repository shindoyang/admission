package com.ut.security.browser.username.register;

import com.ut.security.browser.authentication.BrowserLoginAuthSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

@Component
public class ImageCodeAuthenticationSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    @Autowired
    private AuthenticationFailureHandler browserAuthenticationFailureHandler;
    @Autowired
    private BrowserLoginAuthSuccessHandler browserLoginAuthSuccessHandler;

    @Autowired
    private UserDetailsService myUserDetailService;

    @Override
    public void configure(HttpSecurity http) throws Exception {

        ImageCodeAuthenticationFilter codeVerifyAuthenticationFilter = new ImageCodeAuthenticationFilter();
        codeVerifyAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        codeVerifyAuthenticationFilter.setAuthenticationSuccessHandler(browserLoginAuthSuccessHandler);
        codeVerifyAuthenticationFilter.setAuthenticationFailureHandler(browserAuthenticationFailureHandler);

        ImageCodeAuthenticationProvider codeVerifyAuthenticationProvider = new ImageCodeAuthenticationProvider();
        codeVerifyAuthenticationProvider.setMyUserDetailService(myUserDetailService);

        http.authenticationProvider(codeVerifyAuthenticationProvider)
                .addFilterAfter(codeVerifyAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    }

}