package com.ut.user.support;

import com.ut.user.cache.CacheSingleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ImageCodeService {

    @Autowired
    private CacheSingleService cacheSingleService;

    /**
     * 校验验证码
     */
    public boolean checkImgCode(String uuid, String codeVerify){
        String cacheCode = cacheSingleService.get(uuid);
        if(!codeVerify.equalsIgnoreCase(cacheCode))
            return false;
        return true;
    }

    /**
     * 校验验证码--前置
     */
    public boolean checkImgCode_prefer(String uuid, String codeVerify){
        String cacheCode = cacheSingleService.get(uuid);
        if(!codeVerify.equalsIgnoreCase(cacheCode))
            return false;
        return true;
    }
}
