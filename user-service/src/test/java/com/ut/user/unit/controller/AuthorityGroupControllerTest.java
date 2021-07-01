package com.ut.user.unit.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.ut.user.UserApp;
import com.ut.user.authritymgr.AuthorityGroupEntity;
import com.ut.user.unit.method.AuthorityGroupMethod;
import com.ut.user.unit.method.ControllerMethod;
import com.ut.user.util.AuthStatefulSecurityConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
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
@SpringBootTest(classes=UserApp.class,
		webEnvironment = SpringBootTest.WebEnvironment.MOCK,
		properties = {"spring.cloud.zookeeper.enabled=false", "spring.cloud.config.enabled=false", "spring.profiles.active=test"})
@AutoConfigureMockMvc
@Rollback
@Transactional
@Import(AuthStatefulSecurityConfig.class)
public class AuthorityGroupControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Test
	@WithMockUser(username="testuser",password = "pswd",authorities = {"platform_app_addApp","platform_app_addAuthorityGroup","platform_app_updateAuthorityGroup","platform_app_allAuthorityGroups","platform_app_addAuthority","platform_app_bindAuth2AuthGroup","platform_app_deleteAuthorityGroup"})
	public void testGroup() throws Exception {

		AuthorityGroupMethod authorityGroupMethod = new AuthorityGroupMethod(mockMvc);
		ControllerMethod controllerMethod = new ControllerMethod(mockMvc);

		Assert.assertEquals("true",controllerMethod.creatApp("mybuz", "商业云", "商业云").getContentAsString());

		Assert.assertEquals("true",controllerMethod.addAuthority("mybuz", "mybuz_order_view", "订单查看", "订单查看").getContentAsString());

		Assert.assertEquals("true",controllerMethod.addAuthorityGroup("mybuz", "mybuz_hr_manager", "人事经理", "人事经理").getContentAsString());

		Assert.assertEquals("true", authorityGroupMethod.updateAuthGroupName( "mybuz_hr_manager","大堂经理").getContentAsString());//修改角色名

		Assert.assertEquals(1,((Collection)JSON.parseObject(authorityGroupMethod.getAuthorityGroup("mybuz").getContentAsString(), new TypeReference<List<AuthorityGroupEntity>>(){})).size());

		Assert.assertEquals(1, ((Collection)JSON.parseObject(authorityGroupMethod.getAuthGroupTreeByAuthGroupKey( "mybuz_hr_manager").getContentAsString(), new TypeReference<List<AuthorityGroupEntity>>() {})).size());//获取指定节点的角色树

		Assert.assertEquals(1, ((Collection)JSON.parseObject(authorityGroupMethod.listAuthorityGroups("mybuz").getContentAsString(), new TypeReference<List<AuthorityGroupEntity>>(){})).size());

		Assert.assertEquals(1, ((Collection)JSON.parseObject(authorityGroupMethod.getAuthGroupTreeByAuthGroupKey( "mybuz_hr_manager").getContentAsString(), new TypeReference<List<AuthorityGroupEntity>>() {})).size());

		Assert.assertEquals(1, ((Collection)JSON.parseObject(authorityGroupMethod.getAuthorityGroupsByApp( "mybuz").getContentAsString(), new TypeReference<Set<String>>() {})).size());

		Assert.assertEquals(200, authorityGroupMethod.bindAuth2AuthGroup("mybuz_hr_manager", "mybuz_order_view").getStatus());

		Assert.assertEquals(1, ((Collection)JSON.parseObject(authorityGroupMethod.getAuthsByAuthGroupKeys( "mybuz","mybuz_hr_manager").getContentAsString(), new TypeReference<Set<String>>() {})).size());

		Assert.assertEquals(200, authorityGroupMethod.delAuthorityGroup( "mybuz_hr_manager").getStatus());
		//删除角色后，查询角色是否存在


	}



}