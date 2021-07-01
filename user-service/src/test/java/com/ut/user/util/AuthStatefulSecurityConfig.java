package com.ut.user.util;

import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

public class AuthStatefulSecurityConfig extends ResourceServerConfigurerAdapter {
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) {
		resources.stateless(false);
	}
}
