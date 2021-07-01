package com.ut.security.feign;

import com.ut.security.UtFeignExceptionConfiguration;
import com.ut.security.rbac.thirdaccount.AppRelateDeveloperAccount;
import com.ut.security.rbac.thirdaccount.wechat.WechatUserEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "user-service", url = "${platform.service-url.user}",configuration = UtFeignExceptionConfiguration.class)
public interface FeignWechatService {

    @RequestMapping(value = "/public/findFirstByUnionId",  method= RequestMethod.GET)
    WechatUserEntity findFirstByUnionId(@RequestParam(value = "key") String key, @RequestParam(value = "token") String token);

    @RequestMapping(value = "/public/findFirstByOpenId",  method= RequestMethod.GET)
    WechatUserEntity findFirstByOpenId(@RequestParam(value = "key") String key, @RequestParam(value = "token") String token);

    @RequestMapping(value = "/public/getByAppKeyAndAccountType",  method= RequestMethod.GET)
    AppRelateDeveloperAccount getByAppKeyAndAccountType(@RequestParam(value = "appId") String appId, @RequestParam(value = "accountType") String accountType, @RequestParam(value = "token") String token);

    @RequestMapping(value = "/public/getAccountByAppKey",  method= RequestMethod.GET)
    AppRelateDeveloperAccount getAccountByAppKey(@RequestParam(value = "appId") String appId, @RequestParam(value = "token") String token);

    @RequestMapping(value = "/public/existSocialAccount",  method= RequestMethod.GET)
    boolean existSocialAccount(@RequestParam(value = "appId") String appId, @RequestParam(value = "openId") String openId, @RequestParam(value = "socialAccountType") Integer socialAccountType, @RequestParam(value = "token") String token);

}
