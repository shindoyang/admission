package com.ut.security.api;

import com.ut.security.model.vo.UserVo;
import com.ut.security.usermgr.MyUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@Api(description = "用户信息管理", tags = {"UserController"})
public class UserController {
    @Autowired
    MyUserService myUserService;

    @GetMapping("/getUserByCode")
    @ApiOperation(value = "获取用户信息")
    public UserVo getLoginUser() {
        return myUserService.getLoginUser();
    }
}
