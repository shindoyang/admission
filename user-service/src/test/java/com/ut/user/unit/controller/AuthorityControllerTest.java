package com.ut.user.unit.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.ut.user.UserApp;
import com.ut.user.authritymgr.AuthorityEntity;
import com.ut.user.unit.method.AuthorityMethod;
import com.ut.user.unit.method.ControllerMethod;
import com.ut.user.util.AuthStatefulSecurityConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Set;


@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserApp.class,
		webEnvironment = SpringBootTest.WebEnvironment.MOCK,
		properties = {"spring.cloud.zookeeper.enabled=false", "spring.cloud.config.enabled=false", "spring.profiles.active=test"})
@AutoConfigureMockMvc
@Rollback
@Transactional
@Import(AuthStatefulSecurityConfig.class)
public class AuthorityControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	//无权限新增应用
	@Test
	@WithMockUser(username = "testuser", password = "pswd")
	public void testCreatAppWithoutRole() throws Exception {
		ControllerMethod controllerMethod = new ControllerMethod(mockMvc);
		Assert.assertEquals(403, controllerMethod.creatApp("testapp", "testapp des", "ceshi app").getStatus());
	}

//无权限查询指定应用的所有功能
	@Test
	@WithMockUser(username= "testuser",password = "pswd",authorities = {"platform_app_addApp", "platform_app_addAuthority"})
	public void testGetAuthsByAppKey() throws Exception{
		ControllerMethod controllerMethod = new ControllerMethod(mockMvc);
		AuthorityMethod authorityMethod = new AuthorityMethod(mockMvc);

		Assert.assertEquals("true", controllerMethod.creatApp("testapp", "测试应用", "测试应用").getContentAsString());

		Assert.assertEquals("true", controllerMethod.addAuthority("testapp", "testapp_order_view", "订单查看", "订单查看").getContentAsString());

		Assert.assertEquals(403, (authorityMethod.getAuthsByAppKey("testapp").getStatus()));
	}

	//无权限查看应用
	@Test
	@WithMockUser(username= "testuser",password = "pswd",authorities = {"platform_app_addApp", "platform_app_addAuthority"})
	public  void testGetAuthPage() throws Exception{
		ControllerMethod controllerMethod = new ControllerMethod(mockMvc);
		AuthorityMethod authorityMethod = new AuthorityMethod(mockMvc);

		Assert.assertEquals("true", controllerMethod.creatApp("testapp", "测试应用", "测试应用").getContentAsString());

		Assert.assertEquals("true", controllerMethod.addAuthority("testapp", "testapp_order_view", "订单查看", "订单查看").getContentAsString());
		Assert.assertEquals(403, authorityMethod.getAuthPage("testapp").getStatus());

	}
	// dev新增——查看——删除——查看
	@Test
	@WithMockUser(username = "testuser", password = "pswd", authorities = {"platform_app_addApp", "platform_app_addAuthority", "platform_app_allAuthorities", "platform_app_deleteAuthority"})
	//模拟以developer登陆，不存数据库
	public void testAuthority() throws Exception {

		ControllerMethod controllerMethod = new ControllerMethod(mockMvc);
		AuthorityMethod authorityMethod = new AuthorityMethod(mockMvc);

		Assert.assertEquals("true", controllerMethod.creatApp("testapp", "测试应用", "测试应用").getContentAsString());

		Assert.assertEquals("true", controllerMethod.addAuthority("testapp", "testapp_order_view", "订单查看", "订单查看").getContentAsString());

		Assert.assertEquals(1, ((Collection) JSON.parseObject(authorityMethod.getAuthsByAppKey("testapp").getContentAsString(), new TypeReference<List<AuthorityEntity>>() {
		})).size());

		Assert.assertEquals(200, authorityMethod.getAuthPage("testapp").getStatus());

		Assert.assertEquals(1, ((Collection) JSON.parseObject(authorityMethod.getAuthsByAppKey("testapp").getContentAsString(), new TypeReference<List<AuthorityEntity>>() {
		})).size());//分页查询应用

		Assert.assertEquals(1, ((Collection) JSON.parseObject(authorityMethod.getAuthoritiesByName("testapp", "订单").getContentAsString(), new TypeReference<List<AuthorityEntity>>() {
		})).size());

		Assert.assertEquals(1, ((Collection) JSON.parseObject(authorityMethod.getAuthoritiesByApp("testapp").getContentAsString(), new TypeReference<Set<String>>() {
		})).size());

		Assert.assertEquals(200, authorityMethod.deleteAuth("testapp_order_view").getStatus());

		Assert.assertEquals(0, ((Collection) JSON.parseObject(authorityMethod.getAuthsByAppKey("testapp").getContentAsString(), new TypeReference<List<AuthorityEntity>>() {
		})).size());//验证功能已被删除
	}
}
