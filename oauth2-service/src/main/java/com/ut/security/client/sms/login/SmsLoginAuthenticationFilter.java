package com.ut.security.client.sms.login;

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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * app 短信验证码登录 过滤器
 */
@Slf4j
public class SmsLoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private boolean postOnly = true;

    public SmsLoginAuthenticationFilter() {
        super(new AntPathRequestMatcher(SecurityConstants.APP_SMS_LOGIN_URL, "POST"));
    }

    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationServiceException {
        if (postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        logger.info("content-type=" + request.getContentType());
        logger.info("===========app短信验证码 登录(frame)===========");

        String mobile = obtainParameter(request, SecurityConstants.LOGIN_MOBILE);
        String just_phoneNum = mobile;
        if (mobile.indexOf("#") != -1)
            just_phoneNum = mobile.substring(mobile.indexOf("#") + 1);
        if (!just_phoneNum.matches(SecurityConstants.REGEX_MOBILE))
            throw new AuthenticationServiceException(SecurityConstants.MOBILE_FORMAT_ERROR);
        String messageId = obtainParameter(request, SecurityConstants.LOGIN_MESSAGEID);
        String smsCode = obtainParameter(request, SecurityConstants.LOGIN_SMSCODE);
        String appPrefix = obtainParameter(request, SecurityConstants.LOGIN_APPPREFIX);

        //校验用户
        FeignUserService feignUserService = SpringUtils.getBean(FeignUserService.class);
        AES_ECB_128_Service aes = SpringUtils.getBean(AES_ECB_128_Service.class);
        MyUserEntity userByMobile = feignUserService.getUserByMobile(mobile, aes.getSecurityToken());
        if(null == userByMobile)
            throw new AuthenticationServiceException(mobile + "用户未注册！");

        //校验短信验证码
        SmsService smsService = SpringUtils.getBean(SmsService.class);
        smsService.checkSmsCode(SecurityConstants.LOGIN_TYPE_APP_MOBILE_LOGIN, mobile, messageId, smsCode, appPrefix);

        SmsLoginAuthenticationToken authRequest = new SmsLoginAuthenticationToken(userByMobile.getUsername());

        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    private String obtainParameter(HttpServletRequest request, String parameter) {
        return request.getParameter(parameter).trim();
    }

    protected void setDetails(HttpServletRequest request, SmsLoginAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }

}
