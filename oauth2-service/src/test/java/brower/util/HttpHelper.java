package brower.util;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.MultiValueMap;

@Data
public class HttpHelper<RequestBody> {
	private MockMvc mockMvc;
	public HttpHelper(MockMvc mockMvc){
		this.mockMvc=mockMvc;
	}

	public MockHttpServletResponse request(HttpMethod method, String url, MultiValueMap<String, String> paraMap, RequestBody body, String token) throws  Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = (method == HttpMethod.GET?MockMvcRequestBuilders.get(url):(method==HttpMethod.POST?MockMvcRequestBuilders.post(url):MockMvcRequestBuilders.put(url)));
		if(paraMap!=null) {
			mockHttpServletRequestBuilder.params(paraMap);
		}
		if(body!=null){
			mockHttpServletRequestBuilder.content(JSON.toJSONString(body));
		}
		if(token!=null) {
			mockHttpServletRequestBuilder.accept(MediaType.APPLICATION_JSON).header("Authorization", "Basic"+ " " + token);
		}
			mockHttpServletRequestBuilder.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE);

		return  mockMvc.perform(mockHttpServletRequestBuilder).andReturn().getResponse();


	}
}
