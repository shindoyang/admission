package brower.controller;

import brower.util.HttpHelper;
import com.ut.security.OAuth2ServiceApp;
import lombok.extern.slf4j.Slf4j;
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
public class AppTest {

	@Autowired
	private MockMvc mockMvc;  //伪造的mvc环境

	//默认加密方式注册--正常登录
	@Test
	public void testAppRegisterPass() throws Exception {
		/*new MockUp<ImageCodeService>() {
			@SuppressWarnings("unused")
			@Mock
			public boolean checkImgCode(String uuid, String codeVerify) {
				return true;
			}
		};*/
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		LinkedMultiValueMap<String, String> paramMp = new LinkedMultiValueMap<>();
		paramMp.add("username", "buz_admin");
		paramMp.add("password", "Admin12#$");
		paramMp.add("grant_type", "password");
		paramMp.add("scope", "read");
		paramMp.add("uuid", "1231232312dsfasd");
		paramMp.add("codeVerify", "ut123456");

		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.POST, "/oauth/register", paramMp, null, "c3NvLWdhdGV3YXk6c3NvLWdhdGV3YXktc2VjcmV0");
		Assert.assertEquals(200, mockHttpServletResponse.getStatus());

		MockHttpServletResponse mockHttpServletResponseLogin = httpHelper.request(HttpMethod.POST, "/oauth/login", paramMp, null, "c3NvLWdhdGV3YXk6c3NvLWdhdGV3YXktc2VjcmV0");
		Assert.assertEquals(200, mockHttpServletResponseLogin.getStatus());

	}

	//	密码默认方式加密注册——MD5密码登录
	@Test
	public void testAppRegisterMDLog() throws Exception {
		/*new MockUp<ImageCodeService>() {
			@SuppressWarnings("unused")
			@Mock
			public boolean checkImgCode(String uuid, String codeVerify) {
				return true;
			}
		};*/
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		LinkedMultiValueMap<String, String> paramMp = new LinkedMultiValueMap<>();
		paramMp.add("username", "buz_admin");
		paramMp.add("password", "Admin12#$");
		paramMp.add("grant_type", "password");
		paramMp.add("scope", "read");
		paramMp.add("uuid", "1231232312dsfasd");
		paramMp.add("codeVerify", "ut123456");
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.POST, "/oauth/register", paramMp, null, "c3NvLWdhdGV3YXk6c3NvLWdhdGV3YXktc2VjcmV0");
		Assert.assertEquals(200, mockHttpServletResponse.getStatus());
		paramMp.add("encryption", "1");
		MockHttpServletResponse mockHttpServletResponseLogin = httpHelper.request(HttpMethod.POST, "/oauth/login", paramMp, null, "c3NvLWdhdGV3YXk6c3NvLWdhdGV3YXktc2VjcmV0");
		Assert.assertEquals(200, mockHttpServletResponseLogin.getStatus());
		Assert.assertTrue(mockHttpServletResponseLogin.getContentAsString().contains("您输入的密码有误！"));
	}

//	错误加密类型注册
	@Test
	public void testMDRegisterUnor() throws Exception {
		/*new MockUp<ImageCodeService>() {
			@SuppressWarnings("unused")
			@Mock
			public boolean checkImgCode(String uuid, String codeVerify) {
				return true;
			}
		};*/
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		LinkedMultiValueMap<String, String> paramMp = new LinkedMultiValueMap<>();
		paramMp.add("username", "buz_admin");
		paramMp.add("password", "Admin12#$");
		paramMp.add("grant_type", "password");
		paramMp.add("scope", "read");
		paramMp.add("uuid", "1231232312dsfasd");
		paramMp.add("codeVerify", "ut123456");
		paramMp.add("encryption", "2");
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.POST, "/oauth/register", paramMp, null, "c3NvLWdhdGV3YXk6c3NvLWdhdGV3YXktc2VjcmV0");
		Assert.assertEquals(200, mockHttpServletResponse.getStatus());
		Assert.assertTrue(mockHttpServletResponse.getContentAsString().contains("暂不支持此加密类型!"));
	}

//  注册——错误加密登录
	@Test
	public void testAppRegisterMDUnnormal() throws Exception {

		/*new MockUp<ImageCodeService>() {
			@SuppressWarnings("unused")
			@Mock
			public boolean checkImgCode(String uuid, String codeVerify) {
				return true;
			}
		};*/
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		LinkedMultiValueMap<String, String> paramMp = new LinkedMultiValueMap<>();
		paramMp.add("username", "buz_admin");
		paramMp.add("password", "Admin12#$");
		paramMp.add("grant_type", "password");
		paramMp.add("scope", "read");
		paramMp.add("uuid", "1231232312dsfasd");
		paramMp.add("codeVerify", "ut123456");
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.POST, "/oauth/register", paramMp, null, "c3NvLWdhdGV3YXk6c3NvLWdhdGV3YXktc2VjcmV0");
		Assert.assertEquals(200, mockHttpServletResponse.getStatus());

		LinkedMultiValueMap<String, String> paramMpLogin = new LinkedMultiValueMap<>();
		paramMpLogin.add("username", "buz_admin");
		paramMpLogin.add("password", "Admin12#$");
		paramMpLogin.add("grant_type", "password");
		paramMpLogin.add("scope", "read");
		paramMpLogin.add("encryption", "2");

		MockHttpServletResponse mockHttpServletResponseLogin = httpHelper.request(HttpMethod.POST, "/oauth/login", paramMpLogin, null, "c3NvLWdhdGV3YXk6c3NvLWdhdGV3YXktc2VjcmV0");
		Assert.assertEquals(200, mockHttpServletResponseLogin.getStatus());
		System.out.println(mockHttpServletResponseLogin.getContentAsString());
		Assert.assertTrue(mockHttpServletResponseLogin.getContentAsString().contains("暂不支持此加密类型!"));

	}

	//	MD5加密注册—默认加密登录
	@Test
	public void testAppRegisterMDReg() throws Exception {
		/*new MockUp<ImageCodeService>() {
			@SuppressWarnings("unused")
			@Mock
			public boolean checkImgCode(String uuid, String codeVerify) {
				return true;
			}
		};*/
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		LinkedMultiValueMap<String, String> paramMp = new LinkedMultiValueMap<>();
		paramMp.add("username", "buz_admin");
		paramMp.add("password", "Admin12#$");
		paramMp.add("grant_type", "password");
		paramMp.add("scope", "read");
		paramMp.add("uuid", "1231232312dsfasd");
		paramMp.add("codeVerify", "ut123456");
		paramMp.add("encryption", "1");
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.POST, "/oauth/register", paramMp, null, "c3NvLWdhdGV3YXk6c3NvLWdhdGV3YXktc2VjcmV0");
		Assert.assertEquals(200, mockHttpServletResponse.getStatus());
		LinkedMultiValueMap<String, String> paramMpLogin = new LinkedMultiValueMap<>();
		paramMpLogin.add("username", "buz_admin");
		paramMpLogin.add("password", "Admin12#$");
		paramMpLogin.add("grant_type", "password");
		paramMpLogin.add("scope", "read");

		MockHttpServletResponse mockHttpServletResponseLogin = httpHelper.request(HttpMethod.POST, "/oauth/login", paramMpLogin, null, "c3NvLWdhdGV3YXk6c3NvLWdhdGV3YXktc2VjcmV0");

		Assert.assertEquals(200, mockHttpServletResponseLogin.getStatus());
		Assert.assertTrue(mockHttpServletResponseLogin.getContentAsString().contains("您输入的密码有误！"));
	}

	//	默认加密注册——md5登录
	@Test
	public void testAppRegisterMDUnLogin() throws Exception {
		/*new MockUp<ImageCodeService>() {
			@SuppressWarnings("unused")
			@Mock
			public boolean checkImgCode(String uuid, String codeVerify) {
				return true;
			}
		};*/
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		LinkedMultiValueMap<String, String> paramMp = new LinkedMultiValueMap<>();
		paramMp.add("username", "buz_admin");
		paramMp.add("password", "Admin12#$");
		paramMp.add("grant_type", "password");
		paramMp.add("scope", "read");
		paramMp.add("uuid", "1231232312dsfasd");
		paramMp.add("codeVerify", "ut123456");

		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.POST, "/oauth/register", paramMp, null, "c3NvLWdhdGV3YXk6c3NvLWdhdGV3YXktc2VjcmV0");
		Assert.assertEquals(200, mockHttpServletResponse.getStatus());
		paramMp.add("encryption", "1");
		MockHttpServletResponse mockHttpServletResponseLogin = httpHelper.request(HttpMethod.POST, "/oauth/login", paramMp, null, "c3NvLWdhdGV3YXk6c3NvLWdhdGV3YXktc2VjcmV0");
		Assert.assertEquals(200, mockHttpServletResponseLogin.getStatus());
		Assert.assertTrue(mockHttpServletResponseLogin.getContentAsString().contains("您输入的密码有误！"));
	}

	//	md5注册——md5加密
	@Test
	public void testAppRegisterMDNorLogin() throws Exception {
		/*new MockUp<ImageCodeService>() {
			@SuppressWarnings("unused")
			@Mock
			public boolean checkImgCode(String uuid, String codeVerify) {
				return true;
			}
		};*/
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		LinkedMultiValueMap<String, String> paramMp = new LinkedMultiValueMap<>();
		paramMp.add("username", "buz_admin");
		paramMp.add("password", "Admin12#$");
		paramMp.add("grant_type", "password");
		paramMp.add("scope", "read");
		paramMp.add("uuid", "1231232312dsfasd");
		paramMp.add("codeVerify", "ut123456");
		paramMp.add("encryption", "1");

		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.POST, "/oauth/register", paramMp, null, "c3NvLWdhdGV3YXk6c3NvLWdhdGV3YXktc2VjcmV0");
		Assert.assertEquals(200, mockHttpServletResponse.getStatus());
		MockHttpServletResponse mockHttpServletResponseLogin = httpHelper.request(HttpMethod.POST, "/oauth/login", paramMp, null, "c3NvLWdhdGV3YXk6c3NvLWdhdGV3YXktc2VjcmV0");
		Assert.assertEquals(200, mockHttpServletResponseLogin.getStatus());
	}

	//未注册登录
	@Test
	public void testAppRegisterMD() throws Exception {
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		LinkedMultiValueMap<String, String> paramMp = new LinkedMultiValueMap<>();
		paramMp.add("username", "buz_admin");
		paramMp.add("password", "Admin12#$");
		paramMp.add("grant_type", "password");
		paramMp.add("scope", "read");
		paramMp.add("encryption", "1");
		MockHttpServletResponse mockHttpServletResponseLogin = httpHelper.request(HttpMethod.POST, "/oauth/login", paramMp, null, "c3NvLWdhdGV3YXk6c3NvLWdhdGV3YXktc2VjcmV0");
		Assert.assertEquals(200, mockHttpServletResponseLogin.getStatus());
		Assert.assertTrue(mockHttpServletResponseLogin.getContentAsString().contains("用户未注册！"));
	}
}




