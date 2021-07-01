package com.ut.user.unit.controller;

import com.ut.user.UserApp;
import com.ut.user.unit.method.AuthorityManageMethod;
import com.ut.user.unit.method.ControllerMethod;
import com.ut.user.usermgr.MyUserDao;
import com.ut.user.usermgr.MyUserEntity;
import com.ut.user.util.AuthStatefulSecurityConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;


@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserApp.class,
		webEnvironment = SpringBootTest.WebEnvironment.MOCK,
		properties = {"spring.cloud.zookeeper.enabled=false", "spring.cloud.config.enabled=false", "spring.profiles.active=test"})
@AutoConfigureMockMvc
@Rollback
@Transactional
@Import(AuthStatefulSecurityConfig.class)

public class AppProcessTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private MyUserDao myUserDao;
	@Autowired
	PasswordEncoder passwordEncoder;


	private void registerUser(String username, String password, String parent) throws Exception {
		MyUserEntity myUserEntity = new MyUserEntity();
		myUserEntity.setUsername(username);
		myUserEntity.setPassword(passwordEncoder.encode(password));
		myUserEntity.setParentUser(parent);
		myUserDao.saveAndFlush(myUserEntity);
	}

	@Test
	@WithMockUser(username = "testuser", password = "Admin12#$", authorities = {"platform_app_addApp", "platform_app_addAuthority", "platform_app_addAuthorityGroup"})
	public void testApp() throws Exception {

		AuthorityManageMethod authorityManageMethod = new AuthorityManageMethod(mockMvc);
		ControllerMethod controllerMethod = new ControllerMethod(mockMvc);
		registerUser("testuser", "Admin12#$", "");

		Assert.assertEquals("true", controllerMethod.creatApp("myapp", "我的应用", "我的应用").getContentAsString());

		Assert.assertEquals("true", controllerMethod.addAuthority("myapp", "myapp_order_view", "订单查看", "订单查看").getContentAsString());

		Assert.assertEquals("true", controllerMethod.addAuthorityGroup("myapp", "myapp_hr_manager", "人事经理", "人事经理").getContentAsString());

		Assert.assertEquals("true", authorityManageMethod.createChildUser("childcookie", "Admin12#$").getContentAsString());

		Assert.assertEquals("true", authorityManageMethod.bindAuthorities("childcookie", "myapp", "myapp_order_view").getContentAsString());

		Assert.assertEquals("true", authorityManageMethod.bindAuthoritiesIncrement("childcookie", "myapp",
				"myapp_order_view",0).getContentAsString());
		Assert.assertEquals("true", authorityManageMethod.bindAuthoritiesIncrement("childcookie", "myapp", "myapp_order_view",1
				).getContentAsString());

		Assert.assertEquals("true", authorityManageMethod.bindAuthorityGroups("childcookie", "myapp", "myapp_hr_manager").getContentAsString());

	}


}


