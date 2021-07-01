package com.ut.security.support;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringUtils implements ApplicationContextAware {

	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		if (SpringUtils.applicationContext == null) {
			SpringUtils.applicationContext = applicationContext;
		}
	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	//根据name
	public static Object getBean(String name) {
		return getApplicationContext().getBean(name);
	}

	//根据类型
	public static <T> T getBean(Class<T> clazz) {
		return getApplicationContext().getBean(clazz);
	}

	public static String getParam(String paramKey) {
		return getApplicationContext().getEnvironment().getProperty(paramKey);
	}

	public static <T> T getBean(String name, Class<T> clazz) {
		return getApplicationContext().getBean(name, clazz);
	}

	// 获取当前环境
	public static String getActiveProfile() {
		return getApplicationContext().getEnvironment().getActiveProfiles()[0];
	}
}
