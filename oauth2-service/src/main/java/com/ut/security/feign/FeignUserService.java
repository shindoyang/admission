package com.ut.security.feign;

import com.ut.security.config.UtFeignExceptionConfiguration;
import com.ut.security.usermgr.MyUserEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "user-service", url = "${platform.service-url.user}",configuration = UtFeignExceptionConfiguration.class)
public interface FeignUserService {

    //=================新增用户、更新用户信息=================
    @RequestMapping(value = "/public/usernameRegister",  method= RequestMethod.POST)
    MyUserEntity usernameRegister(@RequestParam(value = "username") String username, @RequestParam(value = "password") String password, @RequestParam(value = "token") String token);

    @RequestMapping(value = "/public/mobileRegister",  method= RequestMethod.POST)
    MyUserEntity mobileRegister(@RequestParam(value = "mobile") String mobile, @RequestParam(value = "token") String token);

    //=================获取用户信息=================
    @RequestMapping(value = "/public/getSelf",  method= RequestMethod.GET)
    MyUserEntity getSelf(@RequestParam(value = "token") String token);

    @RequestMapping(value = "/public/getUserByUid",  method= RequestMethod.GET)
    MyUserEntity getUserByUid(@RequestParam(value = "uid") String uid, @RequestParam(value = "token") String token);

    @RequestMapping(value = "/public/getUserByMobile",  method= RequestMethod.GET)
    MyUserEntity getUserByMobile(@RequestParam(value = "mobile") String mobile, @RequestParam(value = "token") String token);

    @RequestMapping(value = "/public/getUserByUsername",  method= RequestMethod.GET)
    MyUserEntity getUserByUsername(@RequestParam(value = "username") String username, @RequestParam(value = "token") String token);

    @RequestMapping(value = "/public/getUserByUsernameOrLoginName",  method= RequestMethod.GET)
    MyUserEntity getUserByUsernameOrLoginName(@RequestParam(value = "name") String name, @RequestParam(value = "token") String token);

    @RequestMapping(value = "/public/getGrantedAuthorities",  method= RequestMethod.GET)
    List<String> getGrantedAuthorities(@RequestParam(value = "userName") String userName, @RequestParam(value = "token") String token);

    //=================检查工具类=================
    @RequestMapping(value = "/public/checkImgCode",  method= RequestMethod.GET)
    boolean checkImgCode(@RequestParam(value = "uuid") String uuid, @RequestParam(value = "codeVerify") String codeVerify, @RequestParam(value = "token") String token);

    @RequestMapping(value = "/public/checkImgCodePrefer",  method= RequestMethod.GET)
    boolean checkImgCodePrefer(@RequestParam(value = "uuid") String uuid, @RequestParam(value = "codeVerify") String codeVerify, @RequestParam(value = "token") String token);

    //=================缓存工具类=================
    @RequestMapping(value = "/public/setCache",  method= RequestMethod.POST)
    void setCache(@RequestParam(value = "key") String key, @RequestParam(value = "value") String value, @RequestParam(value = "token") String token);

    @RequestMapping(value = "/public/setCacheInTime",  method= RequestMethod.POST)
    void setCacheInTime(@RequestParam(value = "key") String key, @RequestParam(value = "seconds") int seconds, @RequestParam(value = "value") String value, @RequestParam(value = "token") String token);

    @RequestMapping(value = "/public/getCache",  method= RequestMethod.GET)
    String getCache(@RequestParam(value = "key") String key, @RequestParam(value = "token") String token);
}
