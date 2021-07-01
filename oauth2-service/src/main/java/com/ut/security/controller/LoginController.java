package com.ut.security.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.ut.security.feign.FeignUserService;
import com.ut.security.rbac.MyUserEntity;
import com.ut.security.support.AES_ECB_128_Service;
import com.ut.security.support.ResultResponse;
import com.ut.security.support.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @auth: zhangxinyue
 * @Description: 登录rest接口
 * @Date: Created in 10:05 2018-7-26
 */

@RestController
@RequestMapping("/login")
public class LoginController {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	SmsService smsService;
	@Autowired
	PasswordEncoder passwordEncoder;
	@Autowired
	FeignUserService feignUserService;
	@Autowired
	AES_ECB_128_Service aes_ecb_128_service;

	@ApiIgnore
	@RequestMapping("/checkPwdLoginParam")
	public Object checkPwdLoginParam(String username, String password) {
		logger.info("前端参数校验：/login/checkPwdLoginParam");
		MyUserEntity user = feignUserService.getUserByUsername(username, aes_ecb_128_service.getSecurityToken());
		if (null == user) {
			return JSON.toJSONString(new ResultResponse("1", username + "用户未注册！"));
		}
		if (!user.isActivated()) {
			return JSON.toJSONString(new ResultResponse("1", username + "用户已被禁用！"));
		}
		if (Strings.isNullOrEmpty(user.getPassword())) {
			return JSON.toJSONString(new ResultResponse("1", "用户没有设置过密码！"));
		}
		if (!this.passwordEncoder.matches(password, user.getPassword())) {
			return JSON.toJSONString(new ResultResponse("1", "输入密码有误！"));
		}
		return JSON.toJSONString(new ResultResponse("0", ""));
	}

	@ApiIgnore
	@RequestMapping("/checkRegisterParam")
	public Object checkRegisterParam(String username, String uuid, String codeVerify) {
		logger.info("--------------> /login/checkRegisterParam");
		MyUserEntity user = feignUserService.getUserByUsername(username, aes_ecb_128_service.getSecurityToken());
		if (null != user)
			return JSON.toJSONString(new ResultResponse("1", username + "用户已注册！"));
		//校验图形验证码
		if (!feignUserService.checkImgCodePrefer(uuid, codeVerify, aes_ecb_128_service.getSecurityToken()))
			return JSON.toJSONString(new ResultResponse("1", "验证码输入有误！"));

		return JSON.toJSONString(new ResultResponse("0", ""));
	}

	@ApiIgnore
	@RequestMapping("/checkMobileLoginParam")
	public Object checkMobileLoginParam(String mobile, String messageId, String smsCode, String appPrefix) {
		MyUserEntity myUserEntity = feignUserService.getUserByMobile(mobile, aes_ecb_128_service.getSecurityToken());
		if (null != myUserEntity && !myUserEntity.isActivated())
			return JSON.toJSONString(new ResultResponse("1", mobile + "用户已被禁用！"));
//		try {
//			if (!smsService.checkSmsCode_prefer(mobile, messageId, smsCode, appPrefix)) {
//				return JSON.toJSONString(new ResultResponse("1", "验证码输入有误！"));
//			}
//		} catch (UsernameNotFoundException e) {
//			return JSON.toJSONString(new ResultResponse("1", e.getMessage()));
//		}
//		//放入缓存
//		feignUserService.setCache(messageId, "code_right", aes_ecb_128_service.getSecurityToken());
		return JSON.toJSONString(new ResultResponse("0", ""));
	}
}
