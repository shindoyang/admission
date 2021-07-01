package com.ut.user.oauthFeignController;

import com.ut.user.cache.CacheSingleService;
import com.ut.user.support.AES_ECB_128_Service;
import com.ut.user.support.ImageCodeService;
import com.ut.user.usermgr.MyUserEntity;
import com.ut.user.usermgr.MyUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * 以下为oauth 服务feign调用专用接口，api不允许暴露
 */
@ApiIgnore
@RestController
@RequestMapping("/public")
@Api(description="Oauth服务feign专用接口集-用户部分", tags= {"OauthFeignController"})
public class UserFeignController {

    @Autowired
    MyUserService myUserService;
    @Autowired
    ImageCodeService imageCodeService;
    @Autowired
    CacheSingleService cacheSingleService;
    @Autowired
    AES_ECB_128_Service aes_ecb_128_service;

    //=================新增用户、更新用户信息=================
    @PostMapping("/mobileRegister")
    @ApiOperation(value = "手机号注册用户")
    public MyUserEntity mobileRegister(String mobile, String token)throws Exception{
        aes_ecb_128_service.checkSecurityToken(token);
        return myUserService.mobileRegister(mobile);
    }

    @PostMapping("/usernameRegister")
    @ApiOperation(value = "用户名密码注册")
    public MyUserEntity usernameRegister(String username, String password, String token)throws Exception{
        aes_ecb_128_service.checkSecurityToken(token);
        return myUserService.usernameRegister(username, password);
    }

    //=================获取用户信息=================
    @GetMapping("/getSelf")
    @ApiOperation(value = "获取当前用户信息")
    public MyUserEntity getSelf(String token)throws Exception{
        aes_ecb_128_service.checkSecurityToken(token);
        return myUserService.getSelf();
    }

    @GetMapping("/getUserByUid")
    @ApiOperation(value = "根据主键获取用户信息")
    public MyUserEntity getUserByUid(String uid, String token)throws Exception{
        aes_ecb_128_service.checkSecurityToken(token);
        return myUserService.getUserByUid(uid);
    }

    @GetMapping("/getUserByMobile")
    @ApiOperation(value = "根据手机号获取用户信息")
    public MyUserEntity getUserByMobile(String mobile, String token)throws Exception{
        aes_ecb_128_service.checkSecurityToken(token);
        return myUserService.getUserByMobile(mobile);
    }

    @GetMapping("/getUserByUsername")
    @ApiOperation(value = "根据登录名获取用户信息")
    public MyUserEntity getUserByUsername(String username, String token)throws Exception{
        aes_ecb_128_service.checkSecurityToken(token);
        return myUserService.getUserByUsername(username);
    }

    @GetMapping("/getUserByUsernameOrLoginName")
    @ApiOperation(value = "根据用户名或登录名获取用户信息")
    public MyUserEntity getUserByUsernameOrLoginName(String name, String token)throws Exception{
        aes_ecb_128_service.checkSecurityToken(token);
        return myUserService.getUserByUsernameOrLoginName(name);
    }

    @GetMapping("/getGrantedAuthorities")
    @ApiOperation(value = "获取用户所有权限")
    public List<String> getGrantedAuthorities(String userName, String token)throws Exception{
        aes_ecb_128_service.checkSecurityToken(token);
        return myUserService.getGrantedAuthorities(userName);
    }

    //=================检查工具类=================
    @GetMapping("/checkImgCode")
    @ApiOperation(value = "校验图片验证码")
    public boolean checkImgCode(String uuid, String codeVerify, String token)throws Exception{
        aes_ecb_128_service.checkSecurityToken(token);
        return imageCodeService.checkImgCode(uuid, codeVerify);
    }

    @GetMapping("/checkImgCodePrefer")
    @ApiOperation(value = "校验图片验证码--检查参数阶段")
    public boolean checkImgCodePrefer(String uuid, String codeVerify, String token)throws Exception{
        aes_ecb_128_service.checkSecurityToken(token);
        return imageCodeService.checkImgCode_prefer(uuid, codeVerify);
    }

    //=================缓存工具类=================
    @PostMapping("/setCache")
    @ApiOperation(value = "设置缓存")
    public void setCache(String key, String value, String token)throws Exception{
        aes_ecb_128_service.checkSecurityToken(token);
        cacheSingleService.setex(key, value);
    }

    @PostMapping("/setCacheInTime")
    @ApiOperation(value = "自定义失效时间设置缓存")
    public void setCacheInTime(String key, int seconds, String value, String token)throws Exception{
        aes_ecb_128_service.checkSecurityToken(token);
        cacheSingleService.setex(key, seconds, value);
    }

    @GetMapping("/getCache")
    @ApiOperation(value = "获取缓存")
    public String getCache(String key, String token)throws Exception{
        aes_ecb_128_service.checkSecurityToken(token);
        return cacheSingleService.get(key);
    }

}
