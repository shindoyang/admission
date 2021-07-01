package com.ut.user.unit.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.ut.user.UserApp;
import com.ut.user.appmgr.AppDao;
import com.ut.user.appmgr.AppEntity;
import com.ut.user.authritymgr.AuthorityGroupDao;
import com.ut.user.authritymgr.AuthorityGroupEntity;
import com.ut.user.unit.method.AuthorityManageMethod;
import com.ut.user.unit.method.ControllerMethod;
import com.ut.user.usermgr.*;
import com.ut.user.util.AuthStatefulSecurityConfig;
import com.ut.user.vo.ChildUserAuthorityVO;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;
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
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserApp.class,
		webEnvironment = SpringBootTest.WebEnvironment.MOCK,
		properties = {"spring.cloud.zookeeper.enabled=false", "spring.cloud.config.enabled=false", "spring.profiles.active=test"})
@AutoConfigureMockMvc
@Rollback
@Transactional
@Import(AuthStatefulSecurityConfig.class)
public class AuthorityManageContrllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	PasswordEncoder passwordEncoder;
	@Autowired
	MyUserDao myUserDao;
	@Autowired
	AppDao appDao;
	@Autowired
	MyUserRelateAppsDao myUserRelateAppsDao;
	@Autowired
	MyUserRelateAuthoritiesDao myUserRelateAuthoritiesDao;
	@Autowired
	AuthorityGroupDao authorityGroupDao;

	private  void registerUser(String username, String password, String parent) throws Exception {
		MyUserEntity myUserEntity = new MyUserEntity();
		myUserEntity.setUsername(username);
		myUserEntity.setPassword(passwordEncoder.encode(password));
		myUserEntity.setParentUser(parent);
		myUserDao.saveAndFlush(myUserEntity);
	}

	@Test
	@WithMockUser(username = "testuser", password = "Admin12#$", authorities = {"platform_app_addApp", "platform_app_addAuthority", "platform_app_addAuthorityGroup", "platform_app_pageAllAuthorities"})
	//模拟以developer登陆，不存数据库
	public void testManage() throws Exception {
		registerUser("testuser", "Admin12#$", "parentuser");
		AuthorityManageMethod authorityManageMethod = new AuthorityManageMethod(mockMvc);

		ControllerMethod controllerMethod  = new ControllerMethod(mockMvc);

		Assert.assertEquals("true",controllerMethod.creatApp("mybuz", "商业云", "商业云").getContentAsString());

		Assert.assertEquals("true",controllerMethod.addAuthority("mybuz", "mybuz_order_view", "订单查看", "订单查看").getContentAsString());

		Assert.assertEquals("true",controllerMethod.addAuthorityGroup("mybuz", "mybuz_hr_manager", "人事经理", "人事经理").getContentAsString());

		Assert.assertEquals("true",controllerMethod.addAuthority("mybuz", "mybuz_order_add", "订单增加", "订单增加").getContentAsString());

		Assert.assertEquals("true",controllerMethod.addAuthorityGroup("mybuz", "mybuz_hr_employ", "人事职员", "人事职员").getContentAsString());


		Assert.assertEquals("true", authorityManageMethod.checkUserRelateApp("mybuz").getContentAsString());

		Assert.assertEquals("true", authorityManageMethod.bindAuthorities("testuser", "mybuz", "mybuz_order_view").getContentAsString());

		Assert.assertEquals("true", authorityManageMethod.bindAuthoritiesIncrement("testuser", "mybuz",
				"mybuz_order_view",0)
				.getContentAsString());
		Assert.assertEquals("true", authorityManageMethod.bindAuthoritiesIncrement("testuser", "mybuz",
				"mybuz_order_view",1)
				.getContentAsString());

		Assert.assertEquals("true", authorityManageMethod.bindAuthorityGroups("testuser", "mybuz", "mybuz_hr_manager").getContentAsString());

		Assert.assertEquals("true", authorityManageMethod.createChildUser("testchild", "Admin12#$").getContentAsString());

		Assert.assertEquals(1, ((JSONObject) JSON.parseObject( authorityManageMethod.getUserAuthorities("mybuz").getContentAsString())).get("totalElements"));//分页问题

		Assert.assertEquals("true", authorityManageMethod.isAppDeveloper("mybuz").getContentAsString());

		Assert.assertEquals(1, ((JSONObject)JSON.parseObject(authorityManageMethod.pageMyChildAccount().getContentAsString())).get("totalElements"));

		Assert.assertEquals(1, ((List<AppEntity>) JSON.parseObject(authorityManageMethod.listMyRealteApps("mybuz", "testuser").getContentAsString(), new TypeReference<List<AppEntity>>(){})).size());

		Assert.assertEquals("testuser", ((ChildUserAuthorityVO)new ObjectMapper().readValue(authorityManageMethod.listUserAuthorities("mybuz", "testuser").getContentAsString(), ChildUserAuthorityVO.class)).getUsername());

	}

	//为已注册的用户分配开发者的角色
	@Test
	@WithMockUser(username = "platform_admin", password = "Admin12#$")
	public void testDevAuth()throws Exception{
		registerUser("platform_admin","Admin12#$","");
		AuthorityManageMethod authorityManageMethod = new AuthorityManageMethod(mockMvc);

		registerUser("testdev","Admin12#$","platform_admin");
		AppEntity  appEntity = new AppEntity();
		appEntity.setAppKey("platform");
		appEntity.setDeveloper("基础平台");
		appEntity.setName("基础平台");
		appEntity.setDeveloper("platform_admin");
		appDao.saveAndFlush(appEntity);

		AuthorityGroupEntity authorityGroupEntity = new AuthorityGroupEntity();
		authorityGroupEntity.setAppKey("platform");
		authorityGroupEntity.setAuthorityGroupKey("platform_developer");
		authorityGroupEntity.setAuthorityGroupName("开发者");
		authorityGroupDao.saveAndFlush(authorityGroupEntity);

		Assert.assertEquals("true",authorityManageMethod.bindAuthorityGroups("testdev","platform","platform_developer").getContentAsString());

	}




}
