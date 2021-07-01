package com.ut.security.client.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ut.security.support.RequestUtils;
import com.ut.security.support.ResultResponse;
import com.ut.security.support.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidScopeException;
import org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * APP环境下认证成功处理器
 */
@Component("appAuthenticationSuccessHandler")
@Order(Integer.MIN_VALUE)
public class AppAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ClientDetailsService clientDetailsService;

	@SuppressWarnings("unchecked")
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		logger.info("登录成功");
		response.setContentType("application/json;charset=UTF-8");

		String header = request.getHeader("Authorization");

		if (header == null || !header.startsWith("Basic ")) {
			throw new UnapprovedClientAuthenticationException("请求头中无client信息");
		}

		String[] tokens = extractAndDecodeHeader(header, response);
		assert tokens.length == 2;

		String clientId = tokens[0];
		String clientSecret = tokens[1];

		//校验client_id 和 client_secret
		ClientDetails clientDetails = getClientDetails(response, clientId, clientSecret);
        Map requestParamMap = RequestUtils.getRequestParamMap(request);
        String grantType = (String)requestParamMap.get("grant_type");

        TokenRequest tokenRequest = new TokenRequest(requestParamMap, clientId, clientDetails.getScope(), grantType);
		
		OAuth2Request oAuth2Request = tokenRequest.createOAuth2Request(clientDetails);
		
		OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(oAuth2Request, authentication);

		AuthorizationServerTokenServices authorizationServerTokenServices = SpringUtils.getBean(AuthorizationServerTokenServices.class);

		OAuth2AccessToken token = authorizationServerTokenServices.createAccessToken(oAuth2Authentication);

		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().write(objectMapper.writeValueAsString(token));

	}

	/**
	 * 解析请求头，获取client_id、client_secret
	 */
	private String[] extractAndDecodeHeader(String header, HttpServletResponse response) throws IOException {

		byte[] base64Token = header.substring(6).getBytes("UTF-8");
		byte[] decoded;
		try {
			decoded = Base64.decode(base64Token);
		} catch (IllegalArgumentException e) {
            response.getWriter().write(objectMapper.writeValueAsString(new ResultResponse("解析请求头 authentication 异常！")));//Failed to decode basic authentication token
            return null;
		}

		String token = new String(decoded, "UTF-8");

		int delim = token.indexOf(":");

		if (delim == -1) {
            response.getWriter().write(objectMapper.writeValueAsString(new ResultResponse("请求头 authentication 格式不符合要求")));//Invalid basic authentication token
            return null;
		}
		return new String[] { token.substring(0, delim), token.substring(delim + 1) };
	}

	/**
	 * 获取clientDetails，并校验client_id和client_secret
	 */
	private ClientDetails getClientDetails(HttpServletResponse response, String clientId, String clientSecret) throws IOException {
		ClientDetails clientDetails;
		try {
			clientDetails = clientDetailsService.loadClientByClientId(clientId);
		} catch (NoSuchClientException e) {
			response.getWriter().write(objectMapper.writeValueAsString(new ResultResponse("clientId对应的配置信息不存在:" + clientId)));
			return null;
		}
		PasswordEncoder passwordEncoder = SpringUtils.getBean(PasswordEncoder.class);
		if(!passwordEncoder.matches(clientSecret, clientDetails.getClientSecret())) {
			response.getWriter().write(objectMapper.writeValueAsString(new ResultResponse("clientSecret不匹配:" + clientSecret)));
			return null;
		}
		return clientDetails;
	}

	/**
	 * 校验scope
	 */
	private void validateScope(HttpServletResponse response, Set<String> requestScopes, Set<String> clientScopes) {
		if(clientScopes != null && !clientScopes.isEmpty()) {
			Iterator var3 = requestScopes.iterator();

			while(var3.hasNext()) {
				String scope = (String)var3.next();
				if(!clientScopes.contains(scope)) {
					throw new InvalidScopeException("Invalid scope: " + scope, clientScopes);
				}
			}
		}

		if(requestScopes.isEmpty()) {
			throw new InvalidScopeException("Empty scope (either the client or the sms is not allowed the requested scopes)");
		}
	}

	/*public static void main(String[] args) {
		String clientAndSecret = "sso-gateway:sso-gateway-secret";
		clientAndSecret = "Basic "+ java.util.Base64.getEncoder().encodeToString(clientAndSecret.getBytes());
		System.out.println("clientAndSecret:" + clientAndSecret);
	}*/

}
