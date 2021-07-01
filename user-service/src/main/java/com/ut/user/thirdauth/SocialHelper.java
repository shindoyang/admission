package com.ut.user.thirdauth;

import com.ut.user.cache.ICacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SocialHelper {
    @Autowired
    ICacheService cacheService;

    public String getLoginKey(String loginKey) throws Exception {
        String thirdAccountInJson = cacheService.get(loginKey);
        if (thirdAccountInJson == null){
            throw new Exception("第三方账号登录失败，找不到该social账号登录key " + loginKey);
        }
        return thirdAccountInJson;
    }
}
