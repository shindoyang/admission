package com.ut.security.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @auth: 陈佳攀
 * @Description:
 * @Date: Created in 17:05 2017-12-1
 */
@RestController
@Slf4j
public class LogoutController {

    @Autowired
    private ObjectMapper objectMapper;

    @ApiIgnore
    @RequestMapping("oauth/exit")
    public void exit(HttpServletRequest request, HttpServletResponse response) {
        // token can be revoked here if needed
        log.info("--> LogoutController exit() ");
        new SecurityContextLogoutHandler().logout(request, null, null);
        try {
            //sending back to client app
            if(Strings.isNullOrEmpty(request.getHeader("referer"))){
                //重定向到用户中心登录页
                response.sendRedirect("/uaa/login.html");
                //返回json数据
                /*response.setStatus(HttpStatus.OK.value());
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(objectMapper.writeValueAsString(new ResultResponse("退出成功！")));*/
            }else{
                response.sendRedirect(request.getHeader("referer"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    //原有登录无法实现登出
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpServletRequest request, HttpServletResponse response, Model model) {
        //Getting session and then invalidating it
        HttpSession session = request.getSession();
        if (session != null) {
            session.invalidate();
        }
        HandleLogOutResponse(response, request);
        return "logout";
    }

    private void HandleLogOutResponse(HttpServletResponse response, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            cookie.setMaxAge(0);
            cookie.setValue(null);
            cookie.setPath("/");
            response.addCookie(cookie);
        }
    }*/
}
