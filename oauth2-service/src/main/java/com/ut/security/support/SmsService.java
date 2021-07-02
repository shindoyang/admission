package com.ut.security.support;

import com.google.common.base.Strings;
import com.ut.security.constant.SecurityConstants;
import com.ut.security.feign.FeignSmsService;
import com.ut.security.feign.FeignUserService;
import com.ut.security.utils.MD5Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SmsService {
	//编译器报错，无视。 因为这个Bean是在程序启动的时候注入的，编译器感知不到，所以报错。
	@Autowired
	FeignSmsService feignSms;

	@Autowired
	FeignUserService feignUserService;

	@Autowired
	AES_ECB_128_Service aes_ecb_128_service;

	public void checkSmsCode(String loginType, String mobile, String messageId, String smsCode, String appPrefix) throws UsernameNotFoundException {
		if (!SecurityConstants.REVIEW_USER_MOBILE.equals(mobile)) {//ios上架审查账户
			if (Strings.isNullOrEmpty(messageId) || Strings.isNullOrEmpty(appPrefix)) {
				throw new BadCredentialsException("请先获取验证码！");
			}
		}
		//校验验证码
		if (SecurityConstants.REVIEW_USER_MOBILE.equals(mobile)) {//ios上架审查账户
			if (!SecurityConstants.REVIEW_USER_MOBILE_VERIFYCODE.equals(smsCode)) {
				throw new BadCredentialsException("请输入正确的验证码！");
			}
		} else {
			if (SecurityConstants.LOGIN_TYPE_APP_MOBILE_REGISTER.equals(loginType) ||
					SecurityConstants.LOGIN_TYPE_APP_MOBILE_LOGIN.equals(loginType)) {

				boolean smsCodeRight = feignSms.compareVerifyCode(appPrefix, messageId, smsCode);
				log.info("=====>手机号： " + mobile + ", 用户验证码： " + smsCode + ", 验证码校验结果： " + smsCodeRight);
				if (!smsCodeRight) {
					throw new BadCredentialsException("验证码输入有误！");
				}
				try {
					if (!messageId.equals(feignUserService.getCache(MD5Utils.getMD5(mobile), aes_ecb_128_service.getSecurityToken()))) {
						throw new Exception(SecurityConstants.MOBILE_INCONGRUENCE);
					}
				} catch (Exception e) {
					throw new UsernameNotFoundException(SecurityConstants.MOBILE_INCONGRUENCE);
				}
			} else {//web端
				if (!checkSmsCode_prefer(mobile, messageId, smsCode, appPrefix)) {
					throw new BadCredentialsException("验证码输入有误！");
				}
			}
		}
	}

	//表单参数校验阶段
	public boolean checkSmsCode_prefer(String mobile, String messageId, String smsCode, String appPrefix) throws UsernameNotFoundException {
		if (!SecurityConstants.REVIEW_USER_MOBILE.equals(mobile)) {//ios上架审查账户
			if (Strings.isNullOrEmpty(messageId)) {
				throw new UsernameNotFoundException("请先获取验证码！");
			}
		}
		if (Strings.isNullOrEmpty(mobile)) {
			throw new UsernameNotFoundException("手机号不能为空！");
		}
		if (!mobile.matches(SecurityConstants.REGEX_MOBILE)) {
			throw new UsernameNotFoundException(SecurityConstants.MOBILE_FORMAT_ERROR);
		}

		//校验验证码
		if (SecurityConstants.REVIEW_USER_MOBILE.equals(mobile)) {//ios上架审查账户
			if (!SecurityConstants.REVIEW_USER_MOBILE_VERIFYCODE.equals(smsCode))
				return false;
		} else {
			boolean smsCodeRight = feignSms.compareVerifyCode(appPrefix, messageId, smsCode);
			if (!smsCodeRight) {
				return false;
			}
			try {
				if (!messageId.equals(feignUserService.getCache(MD5Utils.getMD5(mobile), aes_ecb_128_service.getSecurityToken()))) {
					throw new UsernameNotFoundException(SecurityConstants.MOBILE_INCONGRUENCE);
				}
			} catch (Exception e) {
				throw new UsernameNotFoundException(SecurityConstants.MOBILE_INCONGRUENCE);
			}
		}
		return true;
	}
}
