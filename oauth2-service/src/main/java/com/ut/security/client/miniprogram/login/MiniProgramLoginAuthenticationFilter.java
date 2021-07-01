package com.ut.security.client.miniprogram.login;

import com.ut.security.feign.FeignUserService;
import com.ut.security.properties.SecurityConstants;
import com.ut.security.rbac.thirdaccount.wechat.ThirdAccountConstant;
import com.ut.security.rbac.MyUserEntity;
import com.ut.security.social.SocialLoginService;
import com.ut.security.social.account.SocialAccountServiceProvider;
import com.ut.security.social.account.SocialAccountServices;
import com.ut.security.support.AES_ECB_128_Service;
import com.ut.security.support.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 小程序登录过滤链
 * @author litingting
 */
@Slf4j
public class MiniProgramLoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	public MiniProgramLoginAuthenticationFilter() {
		super(new AntPathRequestMatcher(SecurityConstants.MINIPROGRAM_LOGIN_URL, "POST"));
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationServiceException {
		logger.info("===========小程序授权登录流程(frame)===========");

		String loginKey = request.getParameter(SecurityConstants.SOCIAL_ACCOUNT_KEY);
		log.info("小程序 socialAccountKey" + loginKey);

		SocialLoginService socialService = SpringUtils.getBean(SocialLoginService.class);
		String socialAccountInJson ;
		try {
			socialAccountInJson = socialService.getLoginKey(loginKey);
		} catch (Exception e) {
			throw new AuthenticationServiceException("无法找到自定义登录key，请查看是否过期或有效，key=" + loginKey);
		}

		SocialAccountServiceProvider socialAccountServiceProvider = SpringUtils.getBean(SocialAccountServiceProvider.class);
		SocialAccountServices socialAccountService = socialAccountServiceProvider.getAccountService(ThirdAccountConstant.MINI_PROGRAM);
		String oauthUser = socialAccountService.getOauthUserName(socialAccountInJson);
		if (oauthUser == null){
			throw new AuthenticationServiceException("该微信用户未在系统中注册，请先注册");
		}

		//获取用户主键
		FeignUserService feignUserService = SpringUtils.getBean(FeignUserService.class);
		AES_ECB_128_Service aes = SpringUtils.getBean(AES_ECB_128_Service.class);
		MyUserEntity userByUsername = feignUserService.getUserByUsername(oauthUser, aes.getSecurityToken());
		if(null == userByUsername)
			throw new AuthenticationServiceException("获取用户信息异常！");

		MiniProgramAuthenticationToken authRequest = new MiniProgramAuthenticationToken(userByUsername.getUsername());
		setDetails(request, authRequest);
		return this.getAuthenticationManager().authenticate(authRequest);
	}

	protected void setDetails(HttpServletRequest request, MiniProgramAuthenticationToken authRequest) {
		authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
	}
}
