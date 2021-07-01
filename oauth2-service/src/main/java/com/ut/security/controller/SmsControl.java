package com.ut.security.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ut.security.feign.FeignSmsService;
import com.ut.security.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.HashMap;
import java.util.Map;

/**
 * 提供前端界面调用，获取验证码
 */
@RestController
@RequestMapping("/public")
public class SmsControl {

    @Autowired
    FeignSmsService feignSms;
    @Autowired
    AppService appService;

    @ApiIgnore
    @PutMapping("sendVerifyCode")
    public String sendVerifyCode(String appPrefix, String mobile) throws Exception {
        Map map = new HashMap();
        try {
            String back = feignSms.sendVerifyCode(appPrefix, mobile);
            JSONObject jsonObject = JSON.parseObject(back);
            map.put("code", 0);
            map.put("msg", jsonObject.get("messageId"));
        } catch (Exception e) {
            //{"timestamp":"2019-07-19T08:15:46.065+0000","status":500,"error":"Internal Server Error","message":"Request processing failed; nested exception is java.lang.Exception: 验证码发送过于频繁,请于38 秒 后重试","path":"/public/sendVerifyCode"}
            String message = e.getMessage();
            String content = message.substring(message.indexOf("{\"timestamp\""), message.length());
            JSONObject jsonObject = JSONObject.parseObject(content);
            String wholdMsg = jsonObject.get("message").toString();
            String errorMsg = wholdMsg.substring(wholdMsg.lastIndexOf(":") + 1);
            map.put("code", 1);
            map.put("msg", errorMsg);
        }
        return JSONObject.toJSON(map).toString();
    }

    @GetMapping("test")
    public void test(String appPrefix, String mobile) throws Exception {
        appService.createApp();
    }
}
