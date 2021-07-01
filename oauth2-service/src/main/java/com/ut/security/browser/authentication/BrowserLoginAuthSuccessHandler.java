package com.ut.security.browser.authentication;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ut.security.support.ResultResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * PC端登陆成功处理
 */
@Component("browserLoginAuthSuccessHandler")
@Slf4j
public class BrowserLoginAuthSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	private RequestCache requestCache = new HttpSessionRequestCache();

	@Value("${login.success.redirectUrl}")
	private String loginSuccessRedirectUrl;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		log.info("登录成功");
		boolean backJson = false;
		SavedRequest savedRequest = this.requestCache.getRequest(request, response);
		if (savedRequest != null&&!savedRequest.getRedirectUrl().contains("/exit")) {
			String targetUrlParameter = this.getTargetUrlParameter();
			if (!this.isAlwaysUseDefaultTargetUrl() && (targetUrlParameter == null || !StringUtils.hasText(request.getParameter(targetUrlParameter)))) {
				this.clearAuthenticationAttributes(request);
				String targetUrl = savedRequest.getRedirectUrl();
				this.getRedirectStrategy().sendRedirect(request, response, targetUrl);
			} else {
				this.requestCache.removeRequest(request, response);
				super.onAuthenticationSuccess(request, response, authentication);
			}
		} else {
//			String activeProfile = SpringUtils.getActiveProfile();
			log.info("redirectUrl : " + loginSuccessRedirectUrl);
			response.sendRedirect(loginSuccessRedirectUrl);
		}
		if (backJson) {
			response.setContentType("application/json;charset=UTF-8");
			response.getWriter().append(JSON.toJSONString(new ResultResponse("0", JSONObject.parseObject(JSON.toJSONString(authentication.getPrincipal())).get("username") + " 欢迎访问用户中心！")));
		}
	}

}