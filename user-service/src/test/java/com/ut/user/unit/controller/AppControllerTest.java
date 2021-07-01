package com.ut.user.unit.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.ut.user.UserApp;
import com.ut.user.appmgr.AppEntity;
import com.ut.user.unit.method.AppMethod;
import com.ut.user.unit.method.ControllerMethod;
import com.ut.user.usermgr.MyUserDao;
import com.ut.user.usermgr.MyUserEntity;
import com.ut.user.util.AuthStatefulSecurityConfig;
import com.ut.user.vo.AppVo;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;


/**
 * @Author: 程霖
 * @Description:
 * @Date: Create in 2019/3/8
 * @Modified By:
 * @Modified Date:
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserApp.class,
		webEnvironment = SpringBootTest.WebEnvironment.MOCK,
		properties = {"spring.cloud.zookeeper.enabled=false", "spring.cloud.config.enabled=false", "spring.profiles.active=test"})
@AutoConfigureMockMvc
@Rollback
@Transactional
@Import(AuthStatefulSecurityConfig.class)
public class AppControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private MyUserDao myUserDao;

	private void registerUser(String username, String password, String parent) throws Exception {
		MyUserEntity myUserEntity = new MyUserEntity();
		myUserEntity.setUsername(username);
		myUserEntity.setPassword(passwordEncoder.encode(password));
		myUserEntity.setParentUser(parent);
		myUserDao.saveAndFlush(myUserEntity);
	}

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	//	未登录新增应用
	@Test
	public void testAddAppUnLogin() throws Exception {
		ControllerMethod controllerMethod = new ControllerMethod(mockMvc);
		Assert.assertEquals(401, controllerMethod.creatApp("buz", "商业云", "商业云").getStatus());
	}

	//	已登录但无权限新增应用
	@Test
	@WithMockUser(username = "testuser", password = "pswd")
	public void testAddAppOutAuth() throws Exception {
		ControllerMethod controllerMethod = new ControllerMethod(mockMvc);
		Assert.assertEquals(403, controllerMethod.creatApp("mybuz", "商业云", "商业云").getStatus());
	}

	//	正常新增应用
	@Test
	@WithMockUser(username = "testuser", password = "pswd", authorities = {"platform_app_addApp"})
	public void testAddApp() throws Exception {
		ControllerMethod controllerMethod = new ControllerMethod(mockMvc);
		MockHttpServletResponse mockHttpServletResponse = controllerMethod.creatApp("mybuz", "商业云", "商业云");
		Assert.assertEquals(200, mockHttpServletResponse.getStatus());
		Assert.assertEquals("true", mockHttpServletResponse.getContentAsString());
	}
	// 参数为空新增应用

	@Test
	@WithMockUser(username = "testuser", password = "pswd", authorities = {"platform_app_addApp"})
	public void testAddAppEmpty() throws Exception {
		ControllerMethod controllerMethod = new ControllerMethod(mockMvc);
		thrown.expect(Exception.class);
		thrown.expectMessage(("应用key和应用名 不能为空！"));
		controllerMethod.creatApp(null, null, null);
	}

	@Test
	@WithMockUser(username = "testuser", password = "pswd", authorities = {"platform_app_addApp"})
	public void testAddAppIllegal() throws Exception {
		ControllerMethod controllerMethod = new ControllerMethod(mockMvc);
		thrown.expect(Exception.class);
		thrown.expectMessage(("应用名仅限小写字母数字组成"));
		controllerMethod.creatApp("785三", "商业云", "商业云");
	}

	@Test
	@WithMockUser(username = "testuser", password = "pswd", authorities = {"platform_app_addApp"})
	public void testAddAppAlreadyExist() throws Exception {
		ControllerMethod controllerMethod = new ControllerMethod(mockMvc);
		controllerMethod.creatApp("mybuz", "商业云", "商业云");
		thrown.expect(Exception.class);
		thrown.expectMessage(("mybuz应用 已存在，请勿重复创建！"));
		controllerMethod.creatApp("mybuz", "商业云", "商业云");
	}


	//根据AppKey获取应用信息
	@Test
	@WithMockUser(username = "testuser", password = "pswd", authorities = {"platform_app_addApp", "platform_app_getApp"})
	public void testGetAppInfo() throws Exception {
		AppMethod appMethod = new AppMethod(mockMvc);
		ControllerMethod controllerMethod = new ControllerMethod(mockMvc);
		Assert.assertEquals(200, controllerMethod.creatApp("mybuz", "商业云", "商业云").getStatus());
		MockHttpServletResponse response = appMethod.getAppInfo("mybuz");
		Assert.assertEquals(200, response.getStatus());
		AppEntity appEntity = new ObjectMapper().readValue(response.getContentAsString(), AppEntity.class);
		Assert.assertEquals("商业云", appEntity.getName());

	}

	@Test
	@WithMockUser(username = "testuser", password = "pswd", authorities = {"platform_app_addApp", "platform_app_allApps"})
	public void testPageGetApps() throws Exception {
		AppMethod appMethod = new AppMethod(mockMvc);
		ControllerMethod controllerMethod = new ControllerMethod(mockMvc);
		Assert.assertEquals("true", controllerMethod.creatApp("mybuz", "商业云", "商业云").getContentAsString());
		MockHttpServletResponse response = appMethod.pageGetApps();
		Assert.assertEquals(200, response.getStatus());
		Assert.assertEquals(1, ((JSONObject) JSONObject.parseObject(response.getContentAsString())).get("totalElements"));
	}

	@Test
	@WithMockUser(username = "platform_admin", password = "pswd", authorities = {"platform_app_addApp", "platform_pagePlatformApps"})
	public void testPagePlatformApps() throws Exception {
		AppMethod appMethod = new AppMethod(mockMvc);
		ControllerMethod controllerMethod = new ControllerMethod(mockMvc);
		Assert.assertEquals("true", controllerMethod.creatApp("mybuz", "商业云", "商业云").getContentAsString());
		MockHttpServletResponse response = appMethod.pagePlatformApps();
		Assert.assertEquals(200, response.getStatus());
		Assert.assertEquals(2, ((JSONObject) JSONObject.parseObject(response.getContentAsString())).get("totalElements"));
	}

	@Test
	@WithMockUser(username = "platform_admin", password = "pswd", authorities = {"platform_app_addApp", "platform_listPlatformApps"})
	public void testListPlatformApps() throws Exception {
		AppMethod appMethod = new AppMethod(mockMvc);
		ControllerMethod controllerMethod = new ControllerMethod(mockMvc);
		Assert.assertEquals("true", controllerMethod.creatApp("mybuz", "商业云", "商业云").getContentAsString());
		MockHttpServletResponse response = appMethod.listPlatformApps();
		Assert.assertEquals(200, response.getStatus());
		Assert.assertEquals(2, ((Collection) JSON.parseObject(response.getContentAsString(), new TypeReference<List<AppEntity>>() {
		})).size());
	}

	//修改应用信息
	@Test
	@WithMockUser(username = "platform_admin", password = "pswd", authorities = {"platform_app_addApp", "platform_listPlatformApps", "platform_app_updateApp"})
	public void testUpdateApp() throws Exception {
		AppMethod appMethod = new AppMethod(mockMvc);
		ControllerMethod controllerMethod = new ControllerMethod(mockMvc);
		Assert.assertEquals("true", controllerMethod.creatApp("mybuz", "商业云", "商业云").getContentAsString());
		AppVo appVo = new AppVo();
		appVo.setAppKey("mybuz");
		appVo.setName("商业云平台");
		appVo.setDescription("商业云平台");
		MockHttpServletResponse response = appMethod.updateApp(appVo);
		Assert.assertEquals(200, response.getStatus());
		Assert.assertEquals("true", response.getContentAsString());
		List<AppEntity> appEntities = JSON.parseObject(appMethod.listPlatformApps().getContentAsString(), new TypeReference<List<AppEntity>>() {
		});
		boolean confirm = false;
		for (AppEntity appEntity : appEntities) {
			if ("商业云平台".equals(appEntity.getName())) {
				confirm = true;
			}
		}
		Assert.assertTrue(confirm);
	}

	@Test
	@WithMockUser(username = "platform_admin", password = "pswd", authorities = {"platform_app_addApp", "platform_listPlatformApps", "platform_app_updateApp"})
	public void testUpdateAppUnNormal() throws Exception {
		AppMethod appMethod = new AppMethod(mockMvc);
		ControllerMethod controllerMethod = new ControllerMethod(mockMvc);
		Assert.assertEquals("true", controllerMethod.creatApp("mybuz", "商业云", "商业云").getContentAsString());
		thrown.expect(Exception.class);
		AppVo appVo = new AppVo();
		MockHttpServletResponse response = appMethod.updateApp(appVo);
	}

	//	无权限获取已激活应用列表
	@Test
	@WithMockUser(username = "testuser", password = "pswd")
	public void testGetAllAppsOutAuth() throws Exception {
		registerUser("testuser", "Admin12#$", "");
		AppMethod appMethod = new AppMethod(mockMvc);
		MockHttpServletResponse mockHttpServletResponse = appMethod.getAllApps();
		Assert.assertEquals(403, mockHttpServletResponse.getStatus());
	}


	//  未新增应用，获取已激活应用列表
	@Test
	@WithMockUser(username = "testuser", password = "pswd", authorities = {"platform_app_allApps"})
	public void testGetAllAppsOutAdd() throws Exception {
		registerUser("testuser", "Admin12#$", "");
		AppMethod appMethod = new AppMethod(mockMvc);
		MockHttpServletResponse mockHttpServletResponse = appMethod.getAllApps();
		Assert.assertEquals(200, mockHttpServletResponse.getStatus());
		Assert.assertEquals(0, JSONObject.parseArray(mockHttpServletResponse.getContentAsString(), AppEntity.class).size());
	}


	//	获取已激活应用列表
	@Test
	@WithMockUser(username = "testuser", password = "pswd", authorities = {"platform_app_addApp", "platform_app_allApps"})
	public void testGetAllApps() throws Exception {
		registerUser("testuser", "Admin12#$", "");
		AppMethod appMethod = new AppMethod(mockMvc);
		ControllerMethod controllerMethod = new ControllerMethod(mockMvc);
		Assert.assertEquals(200, controllerMethod.creatApp("mybuz", "商业云", "商业云").getStatus());
		MockHttpServletResponse mockHttpServletResponse = appMethod.getAllApps();
		Assert.assertEquals(200, mockHttpServletResponse.getStatus());
		Assert.assertEquals(1, JSONObject.parseArray(mockHttpServletResponse.getContentAsString(), AppEntity.class).size());
	}

	//新增应用——将应用状态设为false——获取应用列表元素个数为0
	@Test
	@WithMockUser(username = "testuser", password = "pswd", authorities = {"platform_app_addApp", "platform_app_allApps", "platform_app_changeAppStatus"})
	public void testGetAllAppsUpdateStatus() throws Exception {
		registerUser("testuser", "Admin12#$", "");
		AppMethod appMethod = new AppMethod(mockMvc);
		ControllerMethod controllerMethod = new ControllerMethod(mockMvc);
		Assert.assertEquals(200, controllerMethod.creatApp("mybuz", "商业云", "商业云").getStatus());
		Assert.assertEquals(200, appMethod.updateAppStatus("mybuz", "false").getStatus());
		MockHttpServletResponse mockHttpServletResponse = appMethod.getAllApps();
		Assert.assertEquals(200, mockHttpServletResponse.getStatus());
		Assert.assertEquals(0, JSONObject.parseArray(mockHttpServletResponse.getContentAsString(), AppEntity.class).size());
	}


	//新增一个应用—无权限修改应用状态为false—获取已激活应用列表元素个数为1
	@Test
	@WithMockUser(username = "testuser", password = "pswd", authorities = {"platform_app_addApp", "platform_app_allApps"})
	public void testGetAllAppsOutUpdateStatusAuth() throws Exception {
		registerUser("testuser", "Admin12#$", "");
		AppMethod appMethod = new AppMethod(mockMvc);
		ControllerMethod controllerMethod = new ControllerMethod(mockMvc);
		Assert.assertEquals(200, controllerMethod.creatApp("mybuz", "商业云", "商业云").getStatus());
		Assert.assertEquals(403, appMethod.updateAppStatus("mybuz", "false").getStatus());
		MockHttpServletResponse mockHttpServletResponse = appMethod.getAllApps();
		Assert.assertEquals(200, mockHttpServletResponse.getStatus());
		Assert.assertEquals(1, JSONObject.parseArray(mockHttpServletResponse.getContentAsString(), AppEntity.class).size());
	}
}