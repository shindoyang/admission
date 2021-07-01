package com.ut.user.util;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.MultiValueMap;


@Data
public class HttpHelper<RequestBodyType> {

	private   MockMvc mockMvc;

	public HttpHelper(MockMvc mockMvc) {
		this.mockMvc = mockMvc;
	}

	public   MockHttpServletResponse request(HttpMethod method, String url, MultiValueMap<String, String> paramMp, RequestBodyType requestBodyType, String token) throws Exception{
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder =
				(method == HttpMethod.GET?MockMvcRequestBuilders.get(url):(method==HttpMethod.POST?MockMvcRequestBuilders.post(url):(method==HttpMethod.PUT?MockMvcRequestBuilders.put(url):MockMvcRequestBuilders.delete(url))));
		if (paramMp != null){
			mockHttpServletRequestBuilder.params(paramMp);
		}
		if (requestBodyType != null){
			mockHttpServletRequestBuilder.content(JSON.toJSONString(requestBodyType));
		}
		if (token != null){
			mockHttpServletRequestBuilder.header("Authorization", OAuth2AccessToken.BEARER_TYPE + " " + token);
		}
		mockHttpServletRequestBuilder.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON);

		return mockMvc.perform(mockHttpServletRequestBuilder).andReturn().getResponse();
	}
}
