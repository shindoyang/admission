package com.ut.security.authorize;

import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.stereotype.Component;

/**
 * 核心模块的授权配置提供器，安全模块涉及的url的授权配置在这里。
 */
@Component
@Order(Integer.MIN_VALUE)
public class UtAuthorizeConfigProvider implements AuthorizeConfigProvider {

	@Override
	public boolean config(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry config) {
//		config.antMatchers("/login/**").permitAll();

		/*if (StringUtils.isNotBlank(securityProperties.getBrowser().getSignOutUrl())) {
			config.antMatchers(securityProperties.getBrowser().getSignOutUrl()).permitAll();
		}*/
		return false;
	}

}
