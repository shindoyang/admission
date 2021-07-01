package com.ut.security.social.account;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author litingting
 */
@Service
public class SocialAccountServiceProvider implements ApplicationContextAware {
	private static Map<String, SocialAccountServices> thirdAccountBeans = new HashMap<>(1);

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		Map<String, SocialAccountServices> vmDataMap = applicationContext.getBeansOfType(SocialAccountServices.class);
		vmDataMap.forEach((key, value) -> thirdAccountBeans.put(value.getAuthType(), value));
	}

	public SocialAccountServices getAccountService(String type) throws IllegalArgumentException {
		SocialAccountServices services = thirdAccountBeans.get(type);
		if (services == null){
			throw new IllegalArgumentException("输入的社交账号类型有误，当前类型值==" + type);
		}
		return services;
	}
}
