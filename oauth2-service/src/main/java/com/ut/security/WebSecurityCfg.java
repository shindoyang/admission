package com.ut.security;

import com.ut.security.authorize.AuthorizeConfigManager;
import com.ut.security.browser.username.login.UsernameLoginAuthenticationFilter;
import com.ut.security.browser.username.login.UsernameLoginAuthenticationProvider;
import com.ut.security.browser.username.register.ImageCodeAuthenticationSecurityConfig;
import com.ut.security.browser.sms.SmsCodeAuthenticationSecurityConfig;
import com.ut.security.browser.wechat.WechatLoginAuthenticationSecurityConfig;
import com.ut.security.client.miniprogram.login.MiniProgramAuthenticationSecurityConfig;
import com.ut.security.client.miniprogram.register.MiniProgramRegisterAuthenticationSecurityConfig;
import com.ut.security.client.sms.login.SmsLoginAuthenticationSecurityConfig;
import com.ut.security.client.sms.register.SmsRegisterAuthenticationSecurityConfig;
import com.ut.security.client.username.login.LoginAuthenticationSecurityConfig;
import com.ut.security.client.username.register.RegisterAuthenticationSecurityConfig;
import com.ut.security.client.wechat.WechatAppLoginAuthenticationSecurityConfig;
import com.ut.security.properties.SecurityConstants;
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
	private SmsCodeAuthenticationSecurityConfig smsCodeAuthenticationSecurityConfig;
	@Autowired
	private ImageCodeAuthenticationSecurityConfig imageCodeAuthenticationSecurityConfig;
	@Autowired
	private LoginAuthenticationSecurityConfig loginAuthenticationSecurityConfig;
	@Autowired
	private RegisterAuthenticationSecurityConfig registerAuthenticationSecurityConfig;
	@Autowired
	private SmsLoginAuthenticationSecurityConfig smsLoginAuthenticationSecurityConfig;
	@Autowired
	private SmsRegisterAuthenticationSecurityConfig smsRegisterAuthenticationSecurityConfig;

	@Autowired
	private WechatLoginAuthenticationSecurityConfig wechatLoginSecurityConfig;
	@Autowired
	private WechatAppLoginAuthenticationSecurityConfig wechatAppLoginAuthenticationSecurityConfig;
	@Autowired
	private MiniProgramRegisterAuthenticationSecurityConfig miniProgramRegisterAuthenticationSecurityConfig;
	@Autowired
	private MiniProgramAuthenticationSecurityConfig miniProgramAuthenticationSecurityConfig;
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
				.apply(imageCodeAuthenticationSecurityConfig)//pc 图形验证码注册并登录
				.and()
				.apply(smsCodeAuthenticationSecurityConfig)//pc 短信验证码注册并登录
				.and()
				.apply(loginAuthenticationSecurityConfig)//app 用户名密码登录
				.and()
				.apply(registerAuthenticationSecurityConfig)//app 用户名密码注册并登录
				.and()
				.apply(smsRegisterAuthenticationSecurityConfig)//app 短信验证码注册并登录
				.and()
				.apply(smsLoginAuthenticationSecurityConfig)//app 短信验证码登录
				.and()
				.apply(wechatLoginSecurityConfig)		//PC端微信登录
				.and()
				.apply(wechatAppLoginAuthenticationSecurityConfig)	//移动端微信登录
				.and()
				.apply(miniProgramRegisterAuthenticationSecurityConfig)	//小程序注册
				.and()
				.apply(miniProgramAuthenticationSecurityConfig)	//小程序登录
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