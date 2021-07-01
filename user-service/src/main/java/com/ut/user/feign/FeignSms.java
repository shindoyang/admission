package com.ut.user.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "util-service", url = "${platform.service-url.util}")
public interface FeignSms {
    @RequestMapping(value = "/public/compareVerifyCode",  method= RequestMethod.PUT)
    boolean compareVerifyCode(@RequestParam(value = "appPrefix") String appPrefix, @RequestParam(value = "messageId") String messageId, @RequestParam(value = "userVerifyCode") String smsCode);

    @RequestMapping(value = "/public/sendVerifyCode",  method= RequestMethod.PUT)
    String sendVerifyCode(@RequestParam(value = "appPrefix") String appPrefix, @RequestParam(value = "mobile") String mobile);
}
