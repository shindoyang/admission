package com.ut.security.feign;

import com.ut.security.config.UtFeignExceptionConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "util-service", url = "${platform.service-url.util}",configuration = UtFeignExceptionConfiguration.class)
public interface FeignSmsService {
    @RequestMapping(value = "/public/compareVerifyCode",  method= RequestMethod.PUT)
    boolean compareVerifyCode(@RequestParam(value = "appPrefix") String appPrefix, @RequestParam(value = "messageId") String messageId, @RequestParam(value = "userVerifyCode") String smsCode);

    @RequestMapping(value = "/public/sendVerifyCode",  method= RequestMethod.PUT)
    String sendVerifyCode(@RequestParam(value = "appPrefix") String appPrefix, @RequestParam(value = "mobile") String mobile);

}
