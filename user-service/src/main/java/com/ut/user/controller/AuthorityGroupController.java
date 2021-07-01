package com.ut.user.controller;

import com.ut.user.authritymgr.AuthorityGroupEntity;
import com.ut.user.authritymgr.AuthorityGroupService;
import com.ut.user.vo.AuthGroupAuthVo;
import com.ut.user.vo.AuthorityGroupInVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * @auth: 陈佳攀
 * @Description:
 * @Date: Created in 10:46 2018-11-20
 */

@RestController
@Api(description="角色管理", tags= {"AuthorityGroupController"})
public class AuthorityGroupController {

    @Autowired
    AuthorityGroupService authorityGroupService;

    @PostMapping("/authorityGroup")
    @ApiOperation(value="新增角色")
    public boolean newAuthorityGroup(@RequestBody AuthorityGroupInVo authorityGroup)throws Exception{
        return authorityGroupService.newAuthorityGroup(authorityGroup);
    }

    @DeleteMapping("/authorityGroup")
    @ApiOperation(value="删除叶子角色")
    public boolean delAuthorityGroup(String authorityGroupKey)throws Exception{
        return authorityGroupService.delAuthorityGroup(authorityGroupKey.trim());
    }

    @PutMapping("/authorityGroup")
    @ApiOperation(value="修改角色名")
    public boolean updateAuthorityGroupName(String authGroupKey, String newName)throws Exception{
        return authorityGroupService.updateAuthorityGroupName(authGroupKey.trim(), newName.trim());
    }

    @PostMapping("/bindAuth2AuthGroup")
    @ApiOperation(value="角色绑定功能")
    public boolean bindAuth2AuthGroup(@RequestBody AuthGroupAuthVo authGroupAuthVo) throws Exception {
        return authorityGroupService.bindAuth2AuthGroup(authGroupAuthVo.getAuthorityGroupKey(), authGroupAuthVo.getAuthorityVo());
    }

    @GetMapping("/authorityGroup")
    @ApiOperation(value="获取角色树")
    public List<AuthorityGroupEntity> getAuthorityGroup(String appKey)throws Exception {
        return authorityGroupService.getAuthorityGroup(appKey.trim());
    }

    @GetMapping("/listAuthorityGroups")
    @ApiOperation(value="获取角色列表（平行-应用授权页使用）")
    public List<AuthorityGroupEntity> listAuthorityGroups(String appKey)throws Exception {
        return authorityGroupService.listAuthorityGroups(appKey.trim());
    }

    @GetMapping("/authorityGroupsByKey")
    @ApiOperation(value="获取指定节点角色树")
    public List<AuthorityGroupEntity> getAuthorityGroupsByKey(String authorityGroupKey)throws Exception {
        return authorityGroupService.getAuthorityGroupsByKey(authorityGroupKey.trim());
    }

    @GetMapping("/getAuthorityGroupsByApp")
    @ApiOperation(value="获取角色名列表(Menu服务【Feign】调用)")
    public Set<String> getAuthorityGroupsByApp(String appKey)throws Exception {
        return authorityGroupService.getAuthorityGroupsByApp(appKey.trim());
    }

    @GetMapping("getAuthsByAuthGroupKeys")
    @ApiOperation(value = "获取角色绑定的功能【Feign】")
    public Set<String> getAuthsByAuthGroupKeys(String appKey, String authGroupKey){
        return authorityGroupService.getAuthsByAuthGroupKeys(appKey.trim(), authGroupKey.trim());
    }

}
