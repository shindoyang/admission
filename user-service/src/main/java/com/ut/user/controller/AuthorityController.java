package com.ut.user.controller;

import com.ut.user.authritymgr.AuthorityEntity;
import com.ut.user.authritymgr.AuthorityService;
import com.ut.user.vo.AuthorityInVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * @auth: 陈佳攀
 * @Description:
 * @Date: Created in 10:46 2017-11-20
 */

@RestController
@Api(description="功能管理", tags= {"AuthorityController"})
public class AuthorityController {

    @Autowired
    AuthorityService authorityService;

    @PostMapping("/authority")
    @ApiOperation(value="新增功能")
    public boolean newAuthority(@RequestBody AuthorityInVo authorityInVo)throws Exception{
        return authorityService.newAuthority(authorityInVo);
    }

    @DeleteMapping("/authority")
    @ApiOperation(value="删除功能(删除关联关系)")
    public boolean delAuthority(String authorityKey) throws Exception {
        return authorityService.delAuthority(authorityKey.trim());
    }

    @GetMapping("/authorities")
    @ApiOperation(value="查询功能列表(分页-功能列表页使用)")
    public Page<AuthorityEntity> getAuthoritiesByPage(String appKey, Pageable pageable){
        return authorityService.getAuthorities(appKey.trim(), pageable);
    }

    @GetMapping("/allAuthorities")
    @ApiOperation(value="查询功能列表(平行-授权功能使用)")
    public List<AuthorityEntity> getAllAuthorities(String appKey){
        return authorityService.getAllAuthorities(appKey.trim());
    }

    @GetMapping("/authoritiesByName")
    @ApiOperation(value="模糊查询功能列表(平行-授权功能使用)")
    public List<AuthorityEntity> getAuthoritiesByName(String appKey, String keyword)throws Exception{
        return authorityService.getAuthoritiesByName(appKey.trim(), keyword.trim());
    }

    @GetMapping("/getAuthoritiesByApp")
    @ApiOperation(value="查询所有功能名(Menu服务【Feign】调用)")
    public Set<String> getAuthoritiesByApp(String appKey){
        return authorityService.getAuthoritiesByApp(appKey.trim());
    }

}
