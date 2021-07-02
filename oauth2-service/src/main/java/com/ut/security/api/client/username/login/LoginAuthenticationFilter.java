package com.ut.security.api.client.username.login;

import com.google.common.base.Strings;
import com.ut.security.constant.SecurityConstants;
import com.ut.security.usermgr.MyUserEntity;
import com.ut.security.usermgr.MyUserService;
import com.ut.security.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * app 用户名密码登录 过滤器
 */
@Slf4j
public class LoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private boolean postOnly = true;


    public LoginAuthenticationFilter() {
        super(new AntPathRequestMatcher(SecurityConstants.APP_FORM_LOGIN_URL, "POST"));
    }

    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationServiceException {
        if (postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        logger.debug("content-type=" + request.getContentType());
        logger.info("===========app用户名密码 登录(frame)===========");

        String username = obtainParameter(request, SecurityConstants.LOGIN_USERNAME);
        String mobile = obtainParameter(request,SecurityConstants.LOGIN_MOBILE);
        String password = obtainParameter(request, SecurityConstants.LOGIN_PASSWORD);
        String encryption = obtainParameter(request,SecurityConstants.LOGIN_ENCRYPTION);

        MyUserEntity userEntity = null;
        MyUserService myUserService = SpringUtils.getBean(MyUserService.class);

        //检查用户
        if(!Strings.isNullOrEmpty(username)) {
            userEntity = myUserService.getUserByUsername(username);
            if(null == userEntity)
                throw new AuthenticationServiceException(username + " 用户未注册！");
            if(Strings.isNullOrEmpty(userEntity.getPassword())){
                throw  new AuthenticationServiceException("该用户未设置密码！");
            }
        }else if(!Strings.isNullOrEmpty(mobile)){
            userEntity = myUserService.getUserByMobile(mobile);
            if(null == userEntity)
                throw new AuthenticationServiceException(mobile + " 用户未注册！");
            if(Strings.isNullOrEmpty(userEntity.getPassword())){
                throw  new AuthenticationServiceException("该账号未设置密码，请使用手机验证码登录！");
            }
        }else{
            throw new AuthenticationServiceException("用户名和手机号不能同时为空！");
        }

        //获取用户主键
        if(null == userEntity)
            throw new AuthenticationServiceException("获取用户信息异常！");

        LoginAuthenticationToken authRequest = new LoginAuthenticationToken(userEntity.getUsername(), password, encryption);

        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    private String obtainParameter(HttpServletRequest request, String parameter) {
        return request.getParameter(parameter);
    }

    protected void setDetails(HttpServletRequest request, LoginAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }

}
