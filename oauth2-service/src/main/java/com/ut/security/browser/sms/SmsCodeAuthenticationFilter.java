package com.ut.security.browser.sms;

import com.google.common.base.Strings;
import com.ut.security.feign.FeignUserService;
import com.ut.security.properties.SecurityConstants;
import com.ut.security.rbac.MyUserEntity;
import com.ut.security.support.AES_ECB_128_Service;
import com.ut.security.support.SmsService;
import com.ut.security.support.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * web-pc端 短信验证码 注册 + 登录 过滤链
 */
@Slf4j
public class SmsCodeAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	private boolean postOnly = true;

	public SmsCodeAuthenticationFilter() {
		super(new AntPathRequestMatcher(SecurityConstants.WEB_SMS_LOGIN_REGISTER_URL, "POST"));
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationServiceException {
		if (postOnly && !request.getMethod().equals("POST")) {
			throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
		}

		log.info("===========短信验证码注册/登录流程(frame)===========");

		String mobile = obtainParameter(request, SecurityConstants.LOGIN_MOBILE);
		if (Strings.isNullOrEmpty(mobile)) {
			throw new AuthenticationServiceException("mobile不能为空！");
		}
		if (mobile.indexOf("#") != -1) {
			mobile = mobile.substring(mobile.indexOf("#") + 1);
		}
		if (!mobile.matches(SecurityConstants.REGEX_MOBILE)) {
			throw new AuthenticationServiceException(SecurityConstants.MOBILE_FORMAT_ERROR);
		}
		String messageId = obtainParameter(request, SecurityConstants.LOGIN_MESSAGEID);
		String smsCode = obtainParameter(request, SecurityConstants.LOGIN_SMSCODE);
		String appPrefix = obtainParameter(request, SecurityConstants.LOGIN_APPPREFIX);

		if (Strings.isNullOrEmpty(appPrefix)||Strings.isNullOrEmpty(smsCode)||Strings.isNullOrEmpty(messageId)) {
			throw new AuthenticationServiceException("appPrefix或smsCode或messgeId不能为空！");
		}
		//验证码查验
		SmsService smsService = SpringUtils.getBean(SmsService.class);
		smsService.checkSmsCode(SecurityConstants.LOGIN_TYPE_MOBILE, mobile, messageId, smsCode, appPrefix);
		//检查用户是否注册-不存在默认注册
		FeignUserService feignUserService = SpringUtils.getBean(FeignUserService.class);
		AES_ECB_128_Service aes = SpringUtils.getBean(AES_ECB_128_Service.class);

		//获取用户主键
		MyUserEntity userByMobile = feignUserService.getUserByMobile(mobile, aes.getSecurityToken());
		if (null == userByMobile) {
			userByMobile = feignUserService.mobileRegister(mobile, aes.getSecurityToken());
		}

		SmsCodeAuthenticationToken authRequest = new SmsCodeAuthenticationToken(userByMobile.getUsername());

		// Allow subclasses to set the "details" property
		setDetails(request, authRequest);

		return this.getAuthenticationManager().authenticate(authRequest);
	}

	private String obtainParameter(HttpServletRequest request, String parameter) {
		return request.getParameter(parameter);
	}

	/**
	 * Provided so that subclasses may configure what is put into the
	 * authentication request's details property.
	 *
	 * @param request     that an authentication request is being created for
	 * @param authRequest the authentication request object that should have its details
	 *                    set
	 */
	protected void setDetails(HttpServletRequest request, SmsCodeAuthenticationToken authRequest) {
		authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
	}

}
