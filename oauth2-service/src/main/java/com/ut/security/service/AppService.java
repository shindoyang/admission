package com.ut.security.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AppService {


	@PreAuthorize("hasAuthority('platform_app_addApp')")
	public void createApp() throws Exception {
		getSelf();
		System.out.println("有权操作！");
	}

	public String getSelf() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			return null;
		}
		Object principal = authentication.getPrincipal();
		if (principal == null || principal.equals("anonymousUser")) {
			return null;
		}
		String username;
		if (principal instanceof UserDetails) {
			username = ((UserDetails) principal).getUsername();
		} else {
			username = principal.toString();
		}
		return username;
	}


}
