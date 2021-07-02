package com.ut.security.config;

import com.ut.security.api.browser.username.login.UsernameLoginAuthenticationFilter;
import com.ut.security.api.browser.username.login.UsernameLoginAuthenticationProvider;
import com.ut.security.api.client.username.login.LoginAuthenticationSecurityConfig;
import com.ut.security.config.authorize.AuthorizeConfigManager;
import com.ut.security.constant.SecurityConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


/**
 * @auth: 陈佳攀
 * @Description:
 * @Date: Created in 10:40 2018-4-11
 */
@Configuration
public class WebSecurityCfg extends WebSecurityConfigurerAdapter {

	@Autowired
	private AuthenticationFailureHandler browserAuthenticationFailureHandler;
	@Autowired
	private AuthenticationSuccessHandler browserLoginAuthSuccessHandler;
	@Autowired
	private LoginAuthenticationSecurityConfig loginAuthenticationSecurityConfig;
	@Autowired
	private AuthorizeConfigManager authorizeConfigManager;
	@Autowired
	private UserDetailsService myUserDetailService;

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.addFilterAt(getMyLoginAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
				.formLogin()
				.loginPage(SecurityConstants.DEFAULT_LOGIN_PAGE_URL)
				.loginProcessingUrl(SecurityConstants.WEB_FORM_LOGIN_URL)
				.successHandler(browserLoginAuthSuccessHandler)
				.failureHandler(browserAuthenticationFailureHandler)
				.and()
				.apply(loginAuthenticationSecurityConfig)//app 用户名密码登录
				.and()
				.authorizeRequests()
				.antMatchers(
						"/login/**",
						"/authUrl/**",
						"/local/**",
						"/authentication/**",
						"/**/login.html",
						"/**/index.html",
						"/**/mobileLogin.html",
						"/**/register.html",
						"/**/transition.html",
						"**/public/**",
						"/public/**",
						"/**/*.js",
						"/**/*.css",
						"/**/*.jpg",
						"/**/*.png",
						"/**/*.woff2",
						"/druid/**",
						"/Adsxo1PDi5.txt"
				)
				.permitAll()
				.anyRequest().authenticated()
				.and()
				.csrf().disable();

		authorizeConfigManager.config(http.authorizeRequests());
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new StandardPasswordEncoder(SecurityConstants.SITE_WIDE_SECRET);
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	/**
	 * ---------------------------------以下部分为了兼容旧接口--------------------------------
	 */
	@Bean
	public UsernameLoginAuthenticationFilter getMyLoginAuthenticationFilter() {
		UsernameLoginAuthenticationFilter filter = new UsernameLoginAuthenticationFilter();
		try {
			filter.setAuthenticationManager(this.authenticationManagerBean());
		} catch (Exception e) {
			e.printStackTrace();
		}
		filter.setAuthenticationSuccessHandler(browserLoginAuthSuccessHandler);
		filter.setAuthenticationFailureHandler(browserAuthenticationFailureHandler);
		return filter;
	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) {
		UsernameLoginAuthenticationProvider provider = new UsernameLoginAuthenticationProvider();
		provider.setMyUserDetailService(myUserDetailService);
		auth.authenticationProvider(provider);
	}

}
