package com.ut.user.unit.method;

import com.ut.user.util.HttpHelper;
import com.ut.user.vo.AppVo;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;

public class AppMethod {
	private MockMvc mockMvc;
	public AppMethod(MockMvc mockMvc){
		this.mockMvc = mockMvc;
	}

	//获取全部应用列表List<AppEntity>
	public MockHttpServletResponse getAllApps() throws  Exception{
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.GET, "/app/allApps", null, "",null);
		return mockHttpServletResponse;
	}

	//分页获取应用列表JSONObject
	public MockHttpServletResponse pageGetApps() throws  Exception{
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.GET, "/app/apps", null, "",null);
		return mockHttpServletResponse;
	}

	//根据appKey获取应用信息AppEntity
	public MockHttpServletResponse getAppInfo(String appKey) throws Exception{
		LinkedMultiValueMap<String, String> paramMp = new LinkedMultiValueMap<>();
		paramMp.add("appKey",appKey);
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.GET, "/app/app", paramMp, "",null);
		return mockHttpServletResponse;
	}

	// 修改应用信息
	public MockHttpServletResponse updateApp(AppVo appVo) throws  Exception{
		HttpHelper<AppVo> httpHelper = new HttpHelper<>(mockMvc);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.PUT, "/app/app",null , appVo,null);
		return mockHttpServletResponse;

	}
	//修改应用状态
	public MockHttpServletResponse   updateAppStatus(String appKey,String status) throws Exception {
		LinkedMultiValueMap<String, String> paramMp = new LinkedMultiValueMap<>();
		paramMp.add("appKey", appKey);
		paramMp.add("status", status);
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.PUT, "/app/appStatus", paramMp, "", null);
		return mockHttpServletResponse;
	}

	public MockHttpServletResponse pagePlatformApps() throws  Exception{
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.GET, "/app/pagePlatformApps", null, "",null);
		return mockHttpServletResponse;
	}
	public MockHttpServletResponse listPlatformApps() throws Exception{
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.GET, "/app/listPlatformApps", null, "",null);
		return mockHttpServletResponse;
	}

}
