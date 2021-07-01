package com.ut.security.controller;

import com.ut.security.social.SocialLoginDTO;
import com.ut.security.social.SocialLoginService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.attribute.UserPrincipalNotFoundException;

/**
 * 第三方社交账号的对外接口
 * @author litingting
 *
 */
@RestController
public class SocialController {
    @Autowired
    private SocialLoginService socialService;

    @GetMapping("/public/authUrl")
    @ApiOperation(value = "获取第三方账户登录授权的url【网页版专用】")
    public String getThirdAuthUrl(String appKey, String thirdAccountType, String redirectUrl) throws UserPrincipalNotFoundException, IllegalArgumentException {
        return socialService.createThirdAuthUrl(appKey, thirdAccountType, redirectUrl);
    }

    /**
     * 以前是一个用户只能绑定一个绑定一个openId，因此解绑是单独解绑
     * 后来智厨有提了新需求：即一个用户可以绑定多了openId。但解绑的功能一直没有动过
     * getSocialEntityByKey方法，应该是为了支撑 同一用户，可以在不同的智厨应用间微信登录吧，因为多个应用时，用户的openId不一样，但unionId是同一个
     * 2020-04-06 智厨希望在现有全部解绑接口的基础上，新增一个单独解绑的接口，解绑后，未解绑的小程序不受影响。
     */
    @GetMapping("/public/wechat/getLoginKey")
    @ApiOperation(value = "获取自定义的微信用户登录态")
    public SocialLoginDTO getWechatLoginKey(String code, String appId, String socialAccountType) throws Exception {
        return socialService.getWechatUserLoginKey(code, appId, socialAccountType);
    }

    @GetMapping("/public/getOpenId")
    @ApiOperation(value = "获取openId")
    public String getWeChatOpenId(String code, String appId, String socialAccountType) throws Exception {
        return socialService.getWeChatOpenId(code, appId, socialAccountType);
    }


}
