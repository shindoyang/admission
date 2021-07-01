package com.ut.security.browser.username.register;

import com.google.common.base.Strings;
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
 * 用户名密码 注册过滤器
 */
@Slf4j
public class ImageCodeAuthenticationFilter extends AbstractAuthenticationProcessingFilter{

    private boolean postOnly = true;

    public ImageCodeAuthenticationFilter() {
        super(new AntPathRequestMatcher(SecurityConstants.WEB_FORM_REGISTER_URL, "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationServiceException {
        if (postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        logger.info("===========web 端用户名密码 注册入口===========");

        String username = obtainParameter(request, SecurityConstants.LOGIN_USERNAME);
        String password = obtainParameter(request, SecurityConstants.LOGIN_PASSWORD);
        String codeVerify = obtainParameter(request, SecurityConstants.LOGIN_CODEVERIFY);
        String uuid = obtainParameter(request, SecurityConstants.LOGIN_UUID);
        if(Strings.isNullOrEmpty(username)||Strings.isNullOrEmpty(password)||Strings.isNullOrEmpty(codeVerify)||Strings.isNullOrEmpty(uuid)){
            throw new AuthenticationServiceException("username或password或codeVerify或uuid都不能为空！");
        }
        if(!username.matches(SecurityConstants.REGEX_USERNAME)) {
            throw new AuthenticationServiceException(SecurityConstants.USERNAME_ERR_MSG);
        }
        if(!password.matches(SecurityConstants.REGEX_PWD)) {
            throw new AuthenticationServiceException(SecurityConstants.PWD_ERR_MSG);
        }



        //校验图形验证码
        FeignUserService feignUserService = SpringUtils.getBean(FeignUserService.class);
        AES_ECB_128_Service aes = SpringUtils.getBean(AES_ECB_128_Service.class);
        if(!feignUserService.checkImgCode(uuid, codeVerify, aes.getSecurityToken()))
            throw new AuthenticationServiceException("验证码输入有误！");

        //用户注册
        MyUserEntity userByUsername = feignUserService.getUserByUsername(username, aes.getSecurityToken());
        if(null != userByUsername)
            throw new AuthenticationServiceException(username + " 用户已注册！");
        PasswordService passwordService = SpringUtils.getBean(PasswordService.class);
        password = passwordService.encryPassword(password, null);
        userByUsername = feignUserService.usernameRegister(username,password, aes.getSecurityToken());

        ImageCodeAuthenticationToken authRequest = new ImageCodeAuthenticationToken(userByUsername.getUsername());
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
     * @param request
     *            that an authentication request is being created for
     * @param authRequest
     *            the authentication request object that should have its details
     *            set
     */
    protected void setDetails(HttpServletRequest request, ImageCodeAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }

}
