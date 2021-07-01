package com.ut.security.client.miniprogram.register;

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
public class MiniProgramRegisterAuthenticationSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    @Autowired
    private AuthenticationFailureHandler appAuthenticationFailureHandler;
    @Autowired
    private AuthenticationSuccessHandler appAuthenticationSuccessHandler;

    @Autowired
    private MyUserDetailService myUserDetailService;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        MiniProgramRegisterAuthenticationFilter miniProgramRegisterAuthenticationFilter= new MiniProgramRegisterAuthenticationFilter();
        miniProgramRegisterAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        miniProgramRegisterAuthenticationFilter.setAuthenticationSuccessHandler(appAuthenticationSuccessHandler);
        miniProgramRegisterAuthenticationFilter.setAuthenticationFailureHandler(appAuthenticationFailureHandler);

        MiniProgramRegisterAuthenticationProvider wechatAuthenticationProvider = new MiniProgramRegisterAuthenticationProvider();
        wechatAuthenticationProvider.setMyUserDetailService(myUserDetailService);

        http.authenticationProvider(wechatAuthenticationProvider)
                .addFilterAfter(miniProgramRegisterAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }
}