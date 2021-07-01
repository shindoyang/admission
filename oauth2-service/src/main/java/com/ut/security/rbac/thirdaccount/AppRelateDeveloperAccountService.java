package com.ut.security.rbac.thirdaccount;

import com.ut.security.feign.FeignWechatService;
import com.ut.security.support.AES_ECB_128_Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author litingting
 */
@Service
public class AppRelateDeveloperAccountService {
    @Autowired
    FeignWechatService feignWechatService;
    @Autowired
    AES_ECB_128_Service aes_ecb_128_service;

    public AppRelateDeveloperAccount getDevelopAccount(String appId, String accountType){
        return feignWechatService.getByAppKeyAndAccountType(appId, accountType, aes_ecb_128_service.getSecurityToken());
    }
}
