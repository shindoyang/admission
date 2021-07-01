package com.ut.security;

import com.ut.security.feign.FeignUserService;
import com.ut.security.rbac.MyAuthoritiesService;
import com.ut.security.rbac.MyUserEntity;
import com.ut.security.rbac.UnifiedLoginService;
import com.ut.security.support.AES_ECB_128_Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * @auth: 陈佳攀
 * @Description:
 * @Date: Created in 15:53 2017-11-21
 */
@Component("myUserDetailService")
@Slf4j
public class MyUserDetailService implements UserDetailsService {
	@Autowired
	FeignUserService feignUserService;
	@Autowired
	UnifiedLoginService unifiedLoginService;
	@Autowired
	MyAuthoritiesService myAuthoritiesService;
	@Autowired
	AES_ECB_128_Service aes_ecb_128_service;

	@Override
	public UserDetails loadUserByUsername(String principal) throws UsernameNotFoundException {
		log.info("MyUserDetailService.loadUserByUsername(String principal) params = " + principal);
		MyUserEntity loginUser = null;
		if (principal.split(",").length > 1)//统一入口（旧版）
			loginUser = unifiedLoginService.unifiedLogin(principal);

		if (principal.split(",").length == 1)//自定义过滤链使用
			loginUser = feignUserService.getUserByUid(principal, aes_ecb_128_service.getSecurityToken());

		if(null == loginUser)
			throw new UsernameNotFoundException(principal + " 用户未注册！");
		if (!loginUser.isActivated())
//			throw new UsernameNotFoundException((loginUser.getMobile() == null ? loginUser.getUsername() : loginUser.getMobile()) + " 该用户未激活，请联系管理员！");
			throw new UsernameNotFoundException("该账号未激活，请联系管理员！");

		return myAuthoritiesService.assemblingUserDetail(loginUser);
	}

}
