package com.ut.user.controller;

import com.ut.user.appmgr.AppEntity;
import com.ut.user.appmgr.AppService;
import com.ut.user.assignAuthmgr.AuthorityManagerService;
import com.ut.user.vo.AuthorityGroupVo;
import com.ut.user.vo.AuthorityVo;
import com.ut.user.vo.ChildUserAuthorityVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Api(description="权限分配管理", tags= {"AuthorityManageController"})
public class AuthorityManageController {

    @Autowired
    AppService appService;
    @Autowired
    AuthorityManagerService authorityManagerService;

    @PostMapping("/childUser")
    @ApiOperation(value = "创建子账户")
    public boolean createChildUser(String username, String password) throws Exception {
        return authorityManagerService.createChildUser(username, password);
    }

    @PostMapping("/userAuth")
    @ApiOperation(value = "分配功能")
    public boolean bindAuthorities(String username, String appKey, AuthorityVo authorityVo) throws Exception {

        synchronized (username.intern()){
            return authorityManagerService.bindAuthorities(username.trim(), appKey.trim(), authorityVo);
        }
    }

    @PostMapping("/userAuthIncrement")
    @ApiOperation(value = "分配功能（增量操作）")
    public boolean bindAuthoritiesIncrement(String username, String appKey, AuthorityVo authorityVo,Integer add) throws
            Exception {

        synchronized (username.intern()){
            return authorityManagerService.bindAuthorities(username.trim(), appKey.trim(), authorityVo,add);
        }
    }

    @PostMapping("/userAuthGroup")
    @ApiOperation(value = "分配角色")
    public boolean bindAuthorityGroups(String username, String appKey, AuthorityGroupVo authorityGroupVo) throws Exception {

        synchronized (username.intern()){
            return authorityManagerService.bindAuthorityGroups(username.trim(), appKey.trim(), authorityGroupVo);
        }
    }

    @GetMapping("/pageAllAuthorities")
    @ApiOperation(value = "获取指定应用已授权的用户列表(分页-应用管理授权页使用)")
    public Page<ChildUserAuthorityVO> pageAllAuthorities(String appKey, Pageable pageable) throws Exception {
        return authorityManagerService.pageAllAuthorities(appKey, pageable);
    }

    @GetMapping("/pageMyChildAccount")
    @ApiOperation(value = "子账户列表(分页-子账户管理授权页使用)")
    public Page<ChildUserAuthorityVO> pageMyChildAccount(Pageable pageable) throws Exception {
        return authorityManagerService.pageMyChildAccount(pageable);
    }

    @GetMapping("/listMyRealteApps")
    @ApiOperation(value = "我关联的应用列表(平行-子账户管理使用)")
    public List<AppEntity> listMyRealteApps() {
        return authorityManagerService.listMyRealteApps();
    }

    @GetMapping("/listUserAuthorities")
    @ApiOperation(value = "获取用户的权限集-(授权页编辑时比对使用)")
    public ChildUserAuthorityVO listUserAuthorities(String appKey, String username) throws Exception {
        return authorityManagerService.listUserAuthorities(appKey, username);
    }

    @GetMapping("/isAppDeveloper")
    @ApiOperation(value = "【Feign】是否应用开发者")
    public boolean isAppDeveloper(String appKey) {
        return appService.isAppDeveloper(appKey);
    }

    @GetMapping("/checkUserRelateApp")
    @ApiOperation(value = "【Feign】是否有权限访问应用", notes = "appKey为必填项，username为选填项，若未指定username，则校验当前用户是否有权访问指定应用，若指定username，则校验指定用户是否有权访问指定应用，含开发者判断")
    public boolean checkUserRelateApp(@RequestParam(required = true) String appKey, String username) throws Exception {
        return authorityManagerService.checkUserRelateApp(appKey, username);
    }
}
