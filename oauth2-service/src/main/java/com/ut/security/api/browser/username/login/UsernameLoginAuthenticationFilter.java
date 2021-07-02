package com.ut.security.api.browser.username.login;


import com.google.common.base.Strings;
import com.ut.security.constant.SecurityConstants;
import com.ut.security.service.AES_ECB_128_Service;
import com.ut.security.usermgr.MyUserEntity;
import com.ut.security.usermgr.MyUserService;
import com.ut.security.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @auth: zhangxinyue
 * @Description: 自定义登陆filter
 * 支持登录方式有：账号密码注册/登录、短信验证码、微信授权码
 * @Date: Created in 10:05 2018-7-4
 * <p>
 * 注意：旧的统一登录接口
 */
@Slf4j
public class UsernameLoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
	private boolean postOnly = true;

	public UsernameLoginAuthenticationFilter() {
		super(new AntPathRequestMatcher(SecurityConstants.WEB_FORM_LOGIN_URL, "POST"));
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		if (postOnly && !request.getMethod().equals("POST")) {
			throw new AuthenticationServiceException(
					"Authentication method not supported: " + request.getMethod());
		}
		logger.info("===========web 端用户名密码 登录入口===========");
		String username = obtainParameter(request, SecurityConstants.LOGIN_USERNAME);//用户名或登录名
		String password = obtainParameter(request, SecurityConstants.LOGIN_PASSWORD);//密码
		if (Strings.isNullOrEmpty(username) || Strings.isNullOrEmpty(password)) {
			throw new AuthenticationServiceException("username或password不能为空！");
		}

		MyUserService myUserService = SpringUtils.getBean(MyUserService.class);
		AES_ECB_128_Service aes = SpringUtils.getBean(AES_ECB_128_Service.class);
		MyUserEntity userByUsernameOrLoginName = myUserService.getUserByUsername(username);
		if (null == userByUsernameOrLoginName) {
			throw new AuthenticationServiceException(username + " 用户未注册！");
		}
		if (Strings.isNullOrEmpty(userByUsernameOrLoginName.getPassword())) {
			throw new AuthenticationServiceException(username + " 用户没有设置过密码！");
		}

		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(userByUsernameOrLoginName.getUsername(), password);
		setDetails(request, authRequest);
		return this.getAuthenticationManager().authenticate(authRequest);
	}

	private void setDetails(HttpServletRequest request,
							AbstractAuthenticationToken authRequest) {
		authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
	}

	private String obtainParameter(HttpServletRequest request, String paramKey) {
		String paramValue = request.getParameter(paramKey);
		return paramValue;
	}
}
