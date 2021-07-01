package com.ut.user.unit.method;

import com.ut.user.appmgr.AppEntity;
import com.ut.user.authritymgr.AuthorityEntity;
import com.ut.user.authritymgr.AuthorityGroupEntity;
import com.ut.user.util.HttpHelper;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

public class ControllerMethod {
	private MockMvc mockMvc;
	public ControllerMethod(MockMvc mockMvc){
		this.mockMvc = mockMvc;
	}


	//新增应用
	public MockHttpServletResponse  creatApp(String appKey, String decription, String name) throws Exception {
		AppEntity appEntity = new AppEntity();
		appEntity.setAppKey(appKey);
		appEntity.setDescription(decription);
		appEntity.setName(name);
		HttpHelper<AppEntity> httpHelper = new HttpHelper<>(mockMvc);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.POST, "/app/app", null, appEntity, null);
		return mockHttpServletResponse;
	}

	//新增功能
	public MockHttpServletResponse addAuthority(String appKey,String authorityKey,String description,String name)throws  Exception{
		AuthorityEntity authorityEntity = new AuthorityEntity();
		authorityEntity.setAppKey(appKey);
		authorityEntity.setAuthorityKey(authorityKey);
		authorityEntity.setDescription(description);
		authorityEntity.setName(name);
		HttpHelper<AuthorityEntity> httpHelper = new HttpHelper<>(mockMvc);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.POST,"/authority",null,authorityEntity, null);
		return mockHttpServletResponse;

	}
	//新增角色
	public MockHttpServletResponse addAuthorityGroup(String appKey,String authorityGroupKey,String authorityGroupName,String description) throws Exception {
		AuthorityGroupEntity authorityGroupEntity = new AuthorityGroupEntity();
		authorityGroupEntity.setAppKey(appKey);
		authorityGroupEntity.setAuthorityGroupKey(authorityGroupKey);
		authorityGroupEntity.setAuthorityGroupName(authorityGroupName);
		authorityGroupEntity.setDescription(description);
		HttpHelper<AuthorityGroupEntity> httpHelper = new HttpHelper<>(mockMvc);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.POST,"/authorityGroup",null,authorityGroupEntity,null);
		return mockHttpServletResponse;
	}


}
