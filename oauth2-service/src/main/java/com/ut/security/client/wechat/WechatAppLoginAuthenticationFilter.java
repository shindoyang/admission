package com.ut.security.client.wechat;

import com.ut.security.properties.SecurityConstants;
import com.ut.security.social.BaseWechatFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 微信过滤链
 */
@Slf4j
public class WechatAppLoginAuthenticationFilter extends BaseWechatFilter{

    public WechatAppLoginAuthenticationFilter() {
        super(SecurityConstants.WECHAT_APP_LOGIN_URL);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationServiceException {
        logger.info("===========APP端微信登录流程(frame)===========");
        return super.attemptAuthentication(request, response);
    }
}
