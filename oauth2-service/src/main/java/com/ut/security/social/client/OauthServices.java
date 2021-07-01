package com.ut.security.social.client;

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
public class OauthServices implements ApplicationContextAware {
    private static Map<String, AbstractOAuth2Service> custonOauthBeans = new HashMap<>(1);

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, AbstractOAuth2Service> vmDataMap = applicationContext.getBeansOfType(AbstractOAuth2Service.class);
        vmDataMap.forEach((key, value) -> custonOauthBeans.put(value.getAuthType(), value));
    }

    public AbstractOAuth2Service getOAuthService(String type) throws IllegalArgumentException {
        AbstractOAuth2Service services = custonOauthBeans.get(type);
        if (services == null){
            throw new IllegalArgumentException("找不到该第三方账号对应的服务类,当前第三方账号类型==" + type);
        }
        return services;
    }
}
