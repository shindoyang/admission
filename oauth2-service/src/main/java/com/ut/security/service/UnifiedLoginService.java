package com.ut.security.service;

import com.ut.security.constant.SecurityConstants;
import com.ut.security.usermgr.MyUserEntity;
import com.ut.security.usermgr.MyUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 统一入口 --- 1.3.6以前版本接口
 * 自1.3.7后已通过自定义过滤链的形式单独提供接口
 */
@Service
@Slf4j
public class UnifiedLoginService {
    @Autowired
    private SmsService smsService;
    @Autowired
    private MyUserService myUserService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AES_ECB_128_Service aes_ecb_128_service;
    /**
     * 后续改造注意兼容
     * 旧的统一登录接口，兼容：用户密码注册、用户密码登录、手机验证码注册、手机验证码登录
     * 目前仅餐饮优食汇app在用 用户密码登录，验证码登录，验证码注册
     */
    public MyUserEntity unifiedLogin(String principal){
        MyUserEntity loginUser = null;
        String loginType = principal.split(",")[0];
        log.info("登录类型 = " + loginType + " ,进入旧统一登录接口逻辑 : UnifiedLoginService.unifiedLogin() 入参为： " + principal);
        //app验证码注册、登录
        if (SecurityConstants.LOGIN_TYPE_APP_MOBILE_REGISTER.equals(loginType) ||
                SecurityConstants.LOGIN_TYPE_APP_MOBILE_LOGIN.equals(loginType)) {
            String mobile = principal.split(",")[1];//mobile
            String messageId = principal.split(",")[2];
            String smsCode = principal.split(",")[3];
            String appPrefix = principal.split(",")[4];

            loginUser = myUserService.getUserByMobile(mobile);

            if (SecurityConstants.LOGIN_TYPE_APP_MOBILE_REGISTER.equals(loginType) && null != loginUser)
                throw new BadCredentialsException(mobile + " 用户已注册！");

            if (SecurityConstants.LOGIN_TYPE_APP_MOBILE_LOGIN.equals(loginType) && null == loginUser)
                throw new BadCredentialsException(mobile + " 用户未注册！");

            smsService.checkSmsCode(loginType, mobile, messageId, smsCode, appPrefix);

            if (SecurityConstants.LOGIN_TYPE_APP_MOBILE_REGISTER.equals(loginType))
                loginUser = myUserService.mobileRegister(mobile);
        }
        return loginUser;
    }
}
