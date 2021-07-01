package com.ut.user.unit.method;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.ut.user.appmgr.AppEntity;
import com.ut.user.authritymgr.AuthorityEntity;
import com.ut.user.util.HttpHelper;
import com.ut.user.util.Response;
import org.junit.Assert;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;

import java.util.List;
import java.util.Set;

public class AuthorityMethod {

	private MockMvc mockMvc;
	public AuthorityMethod(MockMvc mockMvc){
		this.mockMvc = mockMvc;
	}

	//查询指定应用的所有功能 List<AuthorityEntity>
	public MockHttpServletResponse getAuthsByAppKey(String appKey) throws  Exception{
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		LinkedMultiValueMap<String,String> paramMp = new LinkedMultiValueMap<>();
		paramMp.add("appKey",appKey);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.GET,"/allAuthorities",paramMp,"",null);
		return mockHttpServletResponse;

	}

	//分页查询功能列表Page<AuthorityEntity>
	public  MockHttpServletResponse getAuthPage(String appKey) throws  Exception{
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		LinkedMultiValueMap<String,String> paramMp = new LinkedMultiValueMap<>();
		paramMp.add("appKey",appKey);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.GET,"/authorities",paramMp,"", null);
		return mockHttpServletResponse;
	}

	//根据功能名模糊查询指定应用的功能列表List<AuthorityEntity

	public  MockHttpServletResponse  getAuthoritiesByName(String appKey,String keyword)  throws  Exception{

		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		LinkedMultiValueMap<String,String> paramMp = new LinkedMultiValueMap<>();
		paramMp.add("appKey",appKey);
		paramMp.add("keyword",keyword);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.GET,"/authoritiesByName",paramMp,"",null);
		return mockHttpServletResponse;

	}

	//	通过应用查询功能列表Set<String>
	public MockHttpServletResponse getAuthoritiesByApp(String appKey) throws  Exception{
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		LinkedMultiValueMap<String,String> paramMp = new LinkedMultiValueMap<>();
		paramMp.add("appKey",appKey);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.GET,"/getAuthoritiesByApp",paramMp,"",null);
		return mockHttpServletResponse;

	}
	//删除功能
	public MockHttpServletResponse  deleteAuth(String authorityKey) throws  Exception{
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		LinkedMultiValueMap<String,String> paramMp = new LinkedMultiValueMap<>();
		paramMp.add("authorityKey",authorityKey);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.DELETE,"/authority",paramMp,"",null);
		return mockHttpServletResponse;
	}
}
