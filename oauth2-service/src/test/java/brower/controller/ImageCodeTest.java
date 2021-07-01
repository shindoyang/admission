package brower.controller;

import brower.util.HttpHelper;
import com.ut.security.OAuth2ServiceApp;
import com.ut.security.support.ResultResponse;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;


/**
 * created by chenglin 2019/4/15
 */

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = OAuth2ServiceApp.class,
		webEnvironment = SpringBootTest.WebEnvironment.MOCK,
		properties = {"spring.cloud.zookeeper.enabled=false", "spring.cloud.config.enabled=false", "spring.profiles.active=test"})
@AutoConfigureMockMvc
@Rollback
public class ImageCodeTest {

    @Autowired
	private MockMvc mockMvc;  //伪造的mvc环境
	//注册用户
	@Test
	public void testRegisty() throws Exception {
		/*new MockUp<ImageCodeService>() {
			@SuppressWarnings("unused")
			@Mock
			public boolean checkImgCode(String uuid, String codeVerify) {
				return true;
			}
		};*/
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		LinkedMultiValueMap<String,String> paramMp = new LinkedMultiValueMap<>();
		paramMp.add("username","ut123456");
		paramMp.add("password","Admin12#$");
		paramMp.add("uuid","1231232312dsfasd");
		paramMp.add("codeVerify","ut123456");
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.POST,"/authentication/register",paramMp,"", null);
		int statusRegister = mockHttpServletResponse.getStatus();
		Assert.assertEquals(200, statusRegister);

	}
	@Test
	public  void  testWebLogin() throws  Exception{
		testRegisty();// 注册
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		LinkedMultiValueMap<String,String> paramMp = new LinkedMultiValueMap<>();
		paramMp.add("username","ut123456");
		paramMp.add("password","Admin12#$");
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.POST,"/login/checkPwdLoginParam",paramMp,"", "");
		int statusLogin = mockHttpServletResponse.getStatus();
		Assert.assertEquals(200, statusLogin);
		ResultResponse resultResponse = new ResultResponse();
		resultResponse = new ObjectMapper().readValue(mockHttpServletResponse.getContentAsString(), ResultResponse.class);
		Assert.assertEquals("0",resultResponse.getCode());

	}
}




