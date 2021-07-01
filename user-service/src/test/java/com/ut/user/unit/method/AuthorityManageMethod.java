package com.ut.user.unit.method;

import com.ut.user.util.HttpHelper;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;

public class AuthorityManageMethod {
	private MockMvc mockMvc;

	public AuthorityManageMethod(MockMvc mockMvc) {
		this.mockMvc = mockMvc;
	}


	//	校验用户是否有权限访问该应用
	public MockHttpServletResponse checkUserRelateApp(String appKey) throws Exception {
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		LinkedMultiValueMap<String, String> paramMp = new LinkedMultiValueMap<>();
		paramMp.add("appKey", appKey);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.GET, "/checkUserRelateApp", paramMp, "", null);
		return mockHttpServletResponse;
	}

	//	校验当前用户是否该应用的开发者
	public MockHttpServletResponse isAppDeveloper(String appKey) throws Exception {
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		LinkedMultiValueMap<String, String> paramMp = new LinkedMultiValueMap<>();
		paramMp.add("appKey", appKey);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.GET, "/isAppDeveloper", paramMp, "", null);
		return mockHttpServletResponse;
	}

	//	获取指定子账户已分配权限-用于前端修改分配角色时返显
	public MockHttpServletResponse getChildUserAuthorityGroup(String appKey, String username) throws Exception {
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		LinkedMultiValueMap<String, String> paramMp = new LinkedMultiValueMap<>();
		paramMp.add("appKey", appKey);
		paramMp.add("username", username);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.GET, "/listUserAuthorities", paramMp, "", null);
		return mockHttpServletResponse;

	}


	//	获取(所有子账户)已分配的权限
	public MockHttpServletResponse getUserAuthorities(String appKey) throws Exception {
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		LinkedMultiValueMap<String, String> paramMp = new LinkedMultiValueMap<>();
		paramMp.add("appKey", appKey);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.GET, "/pageAllAuthorities", paramMp, "", null);
		return mockHttpServletResponse;
	}

	//子账户列表Page<ChildUserAuthorityVO>
	public MockHttpServletResponse pageMyChildAccount() throws Exception {
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.GET, "/pageMyChildAccount", null, "", null);
		return mockHttpServletResponse;

	}

	//获取当前用户关联的应用列表List<AppEntity>
	public MockHttpServletResponse listMyRealteApps(String appKey, String username) throws Exception {
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		LinkedMultiValueMap<String, String> paramMp = new LinkedMultiValueMap<>();
		paramMp.add("appKey", appKey);
		paramMp.add("username", username);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.GET, "/listMyRealteApps", paramMp, "", null);
		return mockHttpServletResponse;

	}

	//获取用户权限集（功能集）ChildUserAuthorityVO
	public MockHttpServletResponse listUserAuthorities(String appKey, String username) throws Exception {
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		LinkedMultiValueMap<String, String> paramMp = new LinkedMultiValueMap<>();
		paramMp.add("appKey", appKey);
		paramMp.add("username", username);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.GET, "/listUserAuthorities", paramMp, "", null);
		return mockHttpServletResponse;
	}

	//创建当前用户的子账户
	public MockHttpServletResponse createChildUser(String username, String password) throws Exception {
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		LinkedMultiValueMap<String, String> paramMp = new LinkedMultiValueMap<>();
		paramMp.add("username", username);
		paramMp.add("password", password);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.POST, "/childUser", paramMp, "", null);
		return mockHttpServletResponse;
	}


	public MockHttpServletResponse bindAuthorities(String username, String appKey, String authorityKeys) throws Exception {

		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		LinkedMultiValueMap<String, String> paramMp = new LinkedMultiValueMap<>();
		paramMp.add("username", username);
		paramMp.add("appKey", appKey);
		paramMp.add("authorityKeys", authorityKeys);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.POST, "/userAuth", paramMp, "", null);
		return mockHttpServletResponse;
	}

	public MockHttpServletResponse bindAuthoritiesIncrement(String username, String appKey, String authorityKeys,
															Integer add)
			throws Exception {

		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		LinkedMultiValueMap<String, String> paramMp = new LinkedMultiValueMap<>();
		paramMp.add("username", username);
		paramMp.add("appKey", appKey);
		paramMp.add("authorityKeys", authorityKeys);
		paramMp.add("add", String.valueOf(add));
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.POST, "/userAuthIncrement", paramMp, "", null);
		return mockHttpServletResponse;
	}

	public MockHttpServletResponse bindAuthorityGroups(String username, String appKey, String authorityGroupKeys) throws Exception {
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		LinkedMultiValueMap<String, String> paramMp = new LinkedMultiValueMap<>();
		paramMp.add("username", username);
		paramMp.add("appKey", appKey);
		paramMp.add("authorityGroupKeys", authorityGroupKeys);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.POST, "/userAuthGroup", paramMp, "", null);
		return mockHttpServletResponse;
	}

}
