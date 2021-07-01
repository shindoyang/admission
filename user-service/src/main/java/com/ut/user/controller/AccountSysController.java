package com.ut.user.controller;

import com.ut.user.accountSysMgr.AccountSysService;
import com.ut.user.usermgr.MyUserRelateAuthorityGroups;
import com.ut.user.vo.AccountVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author chenglin
 * @creat 2019/9/16
 *
 * 以下接口为餐饮特供接口，
 * 1.0版本临时只给餐饮所属的开发者授权，2.0版本上线时移植到餐饮体系的所属开发者
 */
@RestController
@RequestMapping("/accountAdmin")
@Api(description = "账户管理(餐饮管理员特供)", tags = {"AccountSysController"})
public class AccountSysController {
	@Autowired
	private AccountSysService accountSysService;

	@PostMapping("/creatAccount")
	@ApiOperation(value = "创建账号")
	public void creatAccount(@RequestBody @Validated AccountVO accountVO) throws Exception {
		accountSysService.creatAccount(accountVO);
	}

	@PostMapping("/updateUserInfo")
	@ApiOperation(value = "修改指定用户信息/修改密码")
	public void updateAccountUser(@RequestBody @Validated AccountVO accountVO) throws Exception {
		accountSysService.updateAccountInfo(accountVO);
	}

	@GetMapping("/listUserInfo")
	@ApiOperation(value = "批量获取用户信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "appKey", value = "应用key,选填，若不获取用户角色可不填"),
			@ApiImplicitParam(name = "usernameList", value = "用户列表，必填", required = true)
	})
	public List<AccountVO> listUserInfo(String appKey, @RequestParam(value = "usernameList") List<String> usernameList) throws Exception {
		return accountSysService.listUserInfo(appKey, usernameList);
	}

	@GetMapping("/queryUserByAuthGroup")
	@ApiOperation(value = "获取拥有指定角色的用户列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "appKey", value = "应用key,必填", required = true),
			@ApiImplicitParam(name = "authGroupKey", value = "角色key，必填", required = true)
	})
	public Page<MyUserRelateAuthorityGroups> queryUserByAuthGroup(@RequestParam(required = true) String appKey, @RequestParam(required = true) String authGroupKey, Pageable pageable) throws Exception {
		return accountSysService.queryUserByAuthGroup(appKey, authGroupKey, pageable);
	}


}
