package com.ut.security.client.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ut.security.support.ResultResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * app端失败处理接口
 */
@Component("appAuthenticationFailureHandler")
public class AppAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        logger.info("登录失败");
        String loginTyep = request.getParameter("type");
        int httpStatus = HttpStatus.UNAUTHORIZED.value();
        if(exception instanceof BadCredentialsException ||
                exception instanceof AuthenticationServiceException ||
                exception instanceof UsernameNotFoundException){
            logger.info("失败的原因：" + exception.getMessage());
            httpStatus = HttpStatus.OK.value();
        }
        response.setStatus(httpStatus);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(new ResultResponse(exception.getMessage())));
    }
}
