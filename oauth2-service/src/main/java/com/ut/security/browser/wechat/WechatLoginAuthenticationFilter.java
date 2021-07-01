package com.ut.security.browser.wechat;

import com.ut.security.properties.SecurityConstants;
import com.ut.security.social.BaseWechatFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 微信过滤链
 *
 * @author litingting
 */
@Slf4j
public class WechatLoginAuthenticationFilter extends BaseWechatFilter {
    public WechatLoginAuthenticationFilter() {
        super(SecurityConstants.WECHAT_LOGIN_URL);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationServiceException {
        logger.info("===========微信登录流程(frame)===========");
        return super.attemptAuthentication(request, response);
    }

    protected void setDetails(HttpServletRequest request, WechatLoginAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }
}
