package com.ut.user.controller;

import com.ut.user.cache.CacheSingleService;
import com.ut.user.usermgr.MyUserService;
import com.ut.user.vo.SmsPasswordVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @auth: 陈佳攀
 * @Description:
 * @Date: Created in 15:09 2018-3-26
 */
//@Deprecated
@RestController
@RequestMapping("/public")
@Api(description="公共管理", tags= {"PublicController"})
public class PublicController {

    @Autowired
    MyUserService myUserService;

    @Autowired
    CacheSingleService cacheSingleService;


    @PostMapping("/forgetPassword")
    @ApiOperation(value = "通过手机号找回密码-忘记密码")
    public void forgetPassword(@RequestBody SmsPasswordVO smsPasswordVo) throws Exception {
        myUserService.forgetPassword(smsPasswordVo);
    }

//    @Deprecated
//    @PostMapping("/register")
//    @ApiOperation(value="用户注册--弃用--请调用新接口")
//    public void register(@RequestBody RegisterUserVo registerUserVo) throws Exception {
//        //if (doVerify(request.getSession(), verifyCode))
//        if (doVerifyUUID(registerUserVo)){
//            MyUserEntity user = new MyUserEntity();
//            user.setUsername(registerUserVo.getUsername());
//            user.setPassword(registerUserVo.getPassword());
//            myUserService.regUser(user);
//        }else{
//            throw new Exception("verify code error");
//        }
//    }
//
//    private boolean doVerify(HttpSession session, String code) {
//        String oldCode = (String)session.getAttribute("verification-code");
//        if (oldCode == null) {
//            return false;
//        }
//        if (oldCode.equalsIgnoreCase(code)) {
//            session.removeAttribute("verification-code");
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    private boolean doVerifyUUID(RegisterUserVo registerUserVo)throws Exception {
//        if(Strings.isNullOrEmpty(registerUserVo.getUuid()))
//            throw new Exception("uuid 不能为空！");
//        if(Strings.isNullOrEmpty(registerUserVo.getUserVerifyCode()))
//            throw new Exception("验证码不能为空！");
//        if(Strings.isNullOrEmpty(registerUserVo.getUsername()))
//            throw new Exception("用户名不能为空！");
//        if(Strings.isNullOrEmpty(registerUserVo.getPassword()))
//            throw new Exception("密码不能为空！");
//        String oldCode = cacheSingleService.get(registerUserVo.getUuid());
//        if (oldCode == null) {
//            throw new Exception("验证码已过期，请重新获取验证码！");
//        }
//        if (oldCode.equalsIgnoreCase(registerUserVo.getUserVerifyCode())) {
//            return true;
//        } else {
//            return false;
//        }
//    }
}
