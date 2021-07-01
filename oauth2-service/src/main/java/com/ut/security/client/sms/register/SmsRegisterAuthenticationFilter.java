package com.ut.security.client.sms.register;

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
 * app 短信验证码注册 过滤器
 */
@Slf4j
public class SmsRegisterAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private boolean postOnly = true;

    public SmsRegisterAuthenticationFilter() {
        super(new AntPathRequestMatcher(SecurityConstants.APP_SMS_REGISTER_URL, "POST"));
    }

    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationServiceException {
        if (postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        logger.info("content-type=" + request.getContentType());
        logger.info("===========app 短信验证码 注册(frame)===========");

        String mobile = obtainParameter(request, SecurityConstants.LOGIN_MOBILE);
        String just_phoneNum = mobile;
        if (mobile.indexOf("#") != -1)
            just_phoneNum = mobile.substring(mobile.indexOf("#") + 1);
        if (!just_phoneNum.matches(SecurityConstants.REGEX_MOBILE))
            throw new AuthenticationServiceException(SecurityConstants.MOBILE_FORMAT_ERROR);
        String messageId = obtainParameter(request, SecurityConstants.LOGIN_MESSAGEID);
        String smsCode = obtainParameter(request, SecurityConstants.LOGIN_SMSCODE);
        String appPrefix = obtainParameter(request, SecurityConstants.LOGIN_APPPREFIX);

        //校验用户是否已注册
        FeignUserService feignUserService = SpringUtils.getBean(FeignUserService.class);
        AES_ECB_128_Service aes = SpringUtils.getBean(AES_ECB_128_Service.class);
        if(feignUserService.getUserByMobile(mobile, aes.getSecurityToken()) != null)
            throw new AuthenticationServiceException(mobile + " 用户已注册！");

        //校验短信验证码
        SmsService smsService = SpringUtils.getBean(SmsService.class);
        smsService.checkSmsCode(SecurityConstants.LOGIN_TYPE_APP_MOBILE_REGISTER, mobile, messageId, smsCode, appPrefix);

        //用户注册
        MyUserEntity userByMobile = feignUserService.mobileRegister(mobile, aes.getSecurityToken());

        SmsRegisterAuthenticationToken authRequest = new SmsRegisterAuthenticationToken(userByMobile.getUsername());

        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    private String obtainParameter(HttpServletRequest request, String parameter) {
        return request.getParameter(parameter);
    }

    protected void setDetails(HttpServletRequest request, SmsRegisterAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }

}
