package com.ut.user.unit.method;

import com.ut.user.util.HttpHelper;
import com.ut.user.vo.AuthGroupAuthVo;
import com.ut.user.vo.AuthorityVo;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;

import java.util.HashSet;
import java.util.Set;

public class AuthorityGroupMethod {
	private MockMvc mockMvc;
	public AuthorityGroupMethod(MockMvc mockMvc){
		this.mockMvc = mockMvc;
	}

	//修改角色名
	public MockHttpServletResponse updateAuthGroupName(String authGroupKey, String newName) throws Exception {
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		LinkedMultiValueMap<String, String> paramMp = new LinkedMultiValueMap<>();
		paramMp.add("authGroupKey", authGroupKey);
		paramMp.add("newName", newName);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.PUT, "/authorityGroup", paramMp, "", null);
		return mockHttpServletResponse;
	}
// 获取指定角色的树结构List<AuthorityGroupEntity>

	public MockHttpServletResponse getAuthGroupTreeByAuthGroupKey(String authorityGroupKey) throws Exception {
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		LinkedMultiValueMap<String, String> paramMp = new LinkedMultiValueMap<>();
		paramMp.add("authorityGroupKey", authorityGroupKey);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.GET, "/authorityGroupsByKey", paramMp, "", null);
		return mockHttpServletResponse;
	}
//获取指定应用的角色树List<AuthorityGroupEntity>

	public MockHttpServletResponse getAuthorityGroup(String appKey) throws Exception {
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		LinkedMultiValueMap<String, String> paramMp = new LinkedMultiValueMap<>();
		paramMp.add("appKey", appKey);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.GET, "/authorityGroup", paramMp, "", null);
		return mockHttpServletResponse;
	}

	//获取应用的角色列表Set<String>
	public MockHttpServletResponse getAuthorityGroupsByApp(String appKey) throws Exception {
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		LinkedMultiValueMap<String, String> paramMp = new LinkedMultiValueMap<>();
		paramMp.add("appKey", appKey);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.GET, "/getAuthorityGroupsByApp", paramMp, "", null);
		return mockHttpServletResponse;
	}

	//角色绑定功能
	public MockHttpServletResponse bindAuth2AuthGroup(String authorityGroupKey,String authorityKey) throws  Exception{
		HttpHelper<AuthGroupAuthVo> httpHelper = new HttpHelper<>(mockMvc);
		AuthGroupAuthVo authGroupAuthVo = new AuthGroupAuthVo();
		AuthorityVo authorityVo = new AuthorityVo();
		Set<String> auths = new HashSet<>();
		auths.add(authorityKey);
		authorityVo.setAuthorityKeys(auths);
		authGroupAuthVo.setAuthorityVo(authorityVo);
		authGroupAuthVo.setAuthorityGroupKey(authorityGroupKey);

		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.POST, "/bindAuth2AuthGroup", null, authGroupAuthVo, null);
		return mockHttpServletResponse;
	}
	//获取角色绑定的功能列表Set<String>
	public  MockHttpServletResponse getAuthsByAuthGroupKeys(String appKey, String authGroupKey) throws Exception{
		HttpHelper<AuthorityVo> httpHelper = new HttpHelper<>(mockMvc);
		LinkedMultiValueMap<String, String> paramMp = new LinkedMultiValueMap<>();
		paramMp.add("appKey", appKey);
		paramMp.add("authGroupKey",authGroupKey);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.GET, "/getAuthsByAuthGroupKeys", paramMp, null, null);
		return mockHttpServletResponse;
	}

	//删除角色
	public MockHttpServletResponse delAuthorityGroup(String authorityGroupKey) throws Exception{
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		LinkedMultiValueMap<String, String> paramMp = new LinkedMultiValueMap<>();
		paramMp.add("authorityGroupKey", authorityGroupKey);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.DELETE,"/authorityGroup", paramMp, "", null);
		return mockHttpServletResponse;
	}
	//List<AuthorityGroupEntity>
	public MockHttpServletResponse listAuthorityGroups(String appKey)throws Exception {
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		LinkedMultiValueMap<String, String> paramMp = new LinkedMultiValueMap<>();
		paramMp.add("appKey", appKey);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.GET,"/listAuthorityGroups", paramMp, "", null);
		return mockHttpServletResponse;
	}
}
