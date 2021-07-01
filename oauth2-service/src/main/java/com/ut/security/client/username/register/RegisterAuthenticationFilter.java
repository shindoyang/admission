package com.ut.security.client.username.register;

import com.ut.security.feign.FeignUserService;
import com.ut.security.properties.SecurityConstants;
import com.ut.security.rbac.MyUserEntity;
import com.ut.security.support.AES_ECB_128_Service;
import com.ut.security.support.PasswordService;
import com.ut.security.support.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * app 用户名密码注册 过滤器
 */
@Slf4j
public class RegisterAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private boolean postOnly = true;

    public RegisterAuthenticationFilter() {
        super(new AntPathRequestMatcher(SecurityConstants.APP_FORM_REGISTER_URL, "POST"));
    }

    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationServiceException {
        if (postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        logger.info("content-type=" + request.getContentType());
        logger.info("===========app用户名密码 注册(frame)===========");

        String username = obtainParameter(request, SecurityConstants.LOGIN_USERNAME);
        String password = obtainParameter(request, SecurityConstants.LOGIN_PASSWORD);
        String encryption = obtainParameter(request,SecurityConstants.LOGIN_ENCRYPTION);
        String uuid = obtainParameter(request, SecurityConstants.LOGIN_UUID);
        String codeVerify = obtainParameter(request, SecurityConstants.LOGIN_CODEVERIFY);

        if(!username.matches(SecurityConstants.REGEX_USERNAME))
            throw new AuthenticationServiceException(SecurityConstants.USERNAME_ERR_MSG);

        if(!password.matches(SecurityConstants.REGEX_PWD))
            throw new AuthenticationServiceException(SecurityConstants.PWD_ERR_MSG);

        FeignUserService feignUserService = SpringUtils.getBean(FeignUserService.class);
        AES_ECB_128_Service aes = SpringUtils.getBean(AES_ECB_128_Service.class);
        //图形验证码校验
        if(!feignUserService.checkImgCode(uuid, codeVerify, aes.getSecurityToken()))
            throw new AuthenticationServiceException("验证码输入有误！");

        //校验用户是否已注册
        MyUserEntity myUserEntity = feignUserService.getUserByUsername(username, aes.getSecurityToken());
        if(null != myUserEntity)
            throw new AuthenticationServiceException(username + " 用户已注册！");

        //用户注册
        PasswordService passwordService = SpringUtils.getBean(PasswordService.class);
        password = passwordService.encryPassword(password, encryption);
        myUserEntity = feignUserService.usernameRegister(username, password, aes.getSecurityToken());

        RegisterAuthenticationToken authRequest = new RegisterAuthenticationToken(myUserEntity.getUsername(), password);

        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    private String obtainParameter(HttpServletRequest request, String parameter) {
        return request.getParameter(parameter);
    }

    protected void setDetails(HttpServletRequest request, RegisterAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }

}
