package com.ut.user.thirdauth;

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
public class SocialAccountProvider implements ApplicationContextAware {
    private static Map<String, SocialAccountService> thirdAccountBeans = new HashMap<>(1);

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, SocialAccountService> vmDataMap = applicationContext.getBeansOfType(SocialAccountService.class);
        vmDataMap.forEach((key, value) -> thirdAccountBeans.put(value.getAuthType(), value));
    }

    public SocialAccountService getAccountService(String type) throws IllegalArgumentException {
        SocialAccountService services = thirdAccountBeans.get(type);
        if (services == null){
            throw new IllegalArgumentException("输入的社交账号类型有误，当前类型值==" + type);
        }
        return services;
    }
}