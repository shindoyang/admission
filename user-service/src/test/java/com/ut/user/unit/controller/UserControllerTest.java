package com.ut.user.unit.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.ut.user.UserApp;
import com.ut.user.appmgr.AppEntity;
import com.ut.user.cache.CacheSingleService;
import com.ut.user.feign.FeignSms;
import com.ut.user.questionmgr.ResetQuestionEntity;
import com.ut.user.thirdauth.SocialAccountDTO;
import com.ut.user.thirdauth.miniprogram.MiniProgramUser;
import com.ut.user.thirdauth.wechat.WechatUserDao;
import com.ut.user.thirdauth.wechat.WechatUserEntity;
import com.ut.user.unit.method.UserMethod;
import com.ut.user.usermgr.MyUserDao;
import com.ut.user.usermgr.MyUserEntity;
import com.ut.user.usermgr.MyUserExtendDao;
import com.ut.user.usermgr.MyUserExtendEntity;
import com.ut.user.util.AuthStatefulSecurityConfig;
import com.ut.user.vo.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import mockit.Mock;
import mockit.MockUp;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.NestedServletException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserApp.class,
		webEnvironment = SpringBootTest.WebEnvironment.MOCK,
		properties = {"spring.cloud.zookeeper.enabled=false", "spring.cloud.config.enabled=false", "spring.profiles.active=test"})
@AutoConfigureMockMvc
@Rollback
@Transactional
@Import(AuthStatefulSecurityConfig.class)
public class UserControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	PasswordEncoder passwordEncoder;
	@Autowired
	MyUserDao myUserDao;
	@Autowired
	MyUserExtendDao myUserExtendDao;
	@Autowired
	WechatUserDao wechatUserDao;
	@MockBean
	FeignSms feignSms;


	@Data
	class TimedObject {
		long period;
		String value;
		Date cur = new Date();

		public TimedObject(long period, String value) {
			this.period = period;
			this.value = value;
		}
	}

	static HashMap<String, TimedObject> mp = new HashMap<>();

	@Before
	public void setUp() {
		new MockUp<CacheSingleService>() {
			@Mock
			public String setex(String key, String value) {
				mp.put(key, new TimedObject(120, value));
				return value;
			}

			@Mock
			public String get(String key) {
				TimedObject object = mp.get(key);
				if (new Date().getTime() - object.getCur().getTime() < object.period) {
					return object.value;
				} else {
					mp.remove(key);
					return null;
				}
			}
		};
		Mockito.when(feignSms.compareVerifyCode(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenAnswer(new Answer<Boolean>() {
			@Override
			public Boolean answer(InvocationOnMock invocation) throws Throwable {
				return true;
			}
		});
	}

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private void registerUser(String username, String password, String mobile, String parent) throws Exception {
		MyUserEntity myUserEntity = new MyUserEntity();
		myUserEntity.setUsername(username);
		if(!Strings.isNullOrEmpty(password))
		myUserEntity.setPassword(passwordEncoder.encode(password));
		myUserEntity.setMobile(mobile);
		myUserEntity.setParentUser(parent);
		myUserDao.saveAndFlush(myUserEntity);
	}


	@Test
	@WithMockUser(username = "testuser", password = "Admin12#$")
	public void testResetQuestion() throws Exception {
		registerUser("testuser", "Admin12#$", "", "");
		UserMethod userMethod = new UserMethod(mockMvc);
		Assert.assertEquals("true", userMethod.questionAnswer("1", "chenglin").getContentAsString());
		Assert.assertEquals("true", userMethod.checkQuestion("1", "chenglin").getContentAsString());
		Assert.assertEquals(200, userMethod.myQuestion().getStatus());
		Assert.assertEquals("true", userMethod.resetQuestionAnswer("1", "3", "暨南大学").getContentAsString());
	}


	// 未登录获取登录用户
	@Test
	public void testGetLoginUserUnLogin() throws Exception {
		UserMethod userMethod = new UserMethod(mockMvc);
		Assert.assertEquals(401, userMethod.getLoginUser().getStatus());
	}


//	//mock feigns的另外一种方法
//	@Test
//	@WithMockUser(username = "testuser", password = "Admin12#$")
//	public void testBindMobile(@Capturing final FeignSms feignSms) throws Exception {
//		registerUser("testuser", "Admin12#$", null, null);
//		UserMethod userMethod = new UserMethod(mockMvc);
//		new Expectations() {
//			{
//				feignSms.compareVerifyCode(anyString, anyString, anyString);
//				result = true;
//			}
//		};
//		Assert.assertEquals(200, userMethod.bindMobile("test", "32525524", "3243", "18819495594").getStatus());
//	}

	//	已登录获取登录用户
	@Test
	@WithMockUser(username = "testuser", password = "Admin12#$")
	public void testNewUserEntity() throws Exception {
		registerUser("testuser", "Admin12#$", "", "");
		UserMethod userMethod = new UserMethod(mockMvc);
		MyUserExtendEntity myUserExtendEntity = new MyUserExtendEntity();
		myUserExtendEntity.setAddress("珠海");
		MockHttpServletResponse response = userMethod.newUserExtend(myUserExtendEntity);
		Assert.assertEquals(200, response.getStatus());
	}


	@Test
	@WithMockUser(username = "testuser", password = "Admin12#$")
	public void testUpdateNickname() throws Exception {
		registerUser("testuser", "Admin12#$", "", "");
		UserMethod userMethod = new UserMethod(mockMvc);
		MockHttpServletResponse response = userMethod.updateNickname("测试用户");
		Assert.assertEquals(200, response.getStatus());
	}

	//	账户名查找账户(支持username/loginName/mobile)
	@Test
	@WithMockUser(username = "testuser", password = "Admin12#$")
	public void testGetUserWithUsername() throws Exception {
		registerUser("testuser", "Admin12#$", "", "");
		UserMethod userMethod = new UserMethod(mockMvc);
		MockHttpServletResponse response = userMethod.getUser("testuser");
		Assert.assertEquals(200, response.getStatus());
	}

	//获取用户扩展信息(支持username/loginname/mobile)-没有设置过用户扩展信息
	@Test
	@WithMockUser(username = "testuser", password = "Admin12#$")
	public void testGetUserExtendNoPassword() throws Exception {
		registerUser("testuser", "", "18819495594", "");
		UserMethod userMethod = new UserMethod(mockMvc);
		MockHttpServletResponse response = userMethod.getUserExtend("testuser");
		Assert.assertEquals(200, response.getStatus());
		Assert.assertEquals("testuser",new ObjectMapper().readValue(response.getContentAsString(), UserExtendVO.class).getUsername());
	}
	//获取用户扩展信息-已经设置过用户扩展信息
	@Test
	@WithMockUser(username = "testuser", password = "Admin12#$")
	public void testGetUserExtendAll() throws Exception {
		registerUser("testuser", "Admin12#$", "", "");
		UserMethod userMethod = new UserMethod(mockMvc);
		MyUserExtendEntity myUserExtendEntity = new MyUserExtendEntity();
		myUserExtendEntity.setAddress("珠海");
		Assert.assertEquals(200, userMethod.newUserExtend(myUserExtendEntity).getStatus());
		MockHttpServletResponse response = userMethod.getUserExtend("testuser");
		Assert.assertEquals(200, response.getStatus());
		Assert.assertEquals("珠海",new ObjectMapper().readValue(response.getContentAsString(), UserExtendVO.class).getAddress());
	}


	//账户名查找账户+获取用户扩展信息(设置登录名—通过登录名查找账户）
	@Test
	@WithMockUser(username = "testuser", password = "Admin12#$")
	public void testGetUserWithLoginName() throws Exception {
		registerUser("testuser", "Admin12#$", "", "");
		UserMethod userMethod = new UserMethod(mockMvc);
		Assert.assertEquals(200, userMethod.setLoginName("myuser").getStatus());
		MockHttpServletResponse responseUser = userMethod.getUser("myuser");
		Assert.assertEquals(200, responseUser.getStatus());
		Assert.assertEquals("testuser", new ObjectMapper().readValue(responseUser.getContentAsString(), UserVo.class).getUsername());
		MockHttpServletResponse responseUserExend = userMethod.getUserExtend("myuser");
		Assert.assertEquals(200, responseUserExend.getStatus());
		Assert.assertEquals("testuser", new ObjectMapper().readValue(responseUserExend.getContentAsString(), UserExtendVO.class).getUsername());
	}

	//账户名查找账户+获取用户扩展信息(设置手机号—通过手机号查找账户）


	@Test
	@WithMockUser(username = "testuser", password = "Admin12#$")
	public void testGetUserByMobile() throws Exception {
		registerUser("testuser", "Admin12#$", null, null);
		UserMethod userMethod = new UserMethod(mockMvc);
		new MockUp<CacheSingleService>() {
			@Mock
			public String get(String key) {
				return "32525524";
			}
		};
		Assert.assertEquals(200, userMethod.bindMobile("test", "32525524", "3243", "18819495594").getStatus());
		MockHttpServletResponse responseUser = userMethod.getUser("18819495594");
		Assert.assertEquals(200, responseUser.getStatus());
		Assert.assertEquals("testuser", new ObjectMapper().readValue(responseUser.getContentAsString(), UserVo.class).getUsername());
		MockHttpServletResponse responseUserExend = userMethod.getUserExtend("18819495594");
		Assert.assertEquals(200, responseUserExend.getStatus());
		Assert.assertEquals("testuser", new ObjectMapper().readValue(responseUserExend.getContentAsString(), UserExtendVO.class).getUsername());
	}

	//	设置登录名为空
	@Test
	@WithMockUser(username = "testuser", password = "Admin12#$")
	public void testSetLoginNameEmpty() throws Exception {
		registerUser("testuser", "Admin12#$", "", "");
		UserMethod userMethod = new UserMethod(mockMvc);
		thrown.expect(Exception.class);
		thrown.expectMessage(("不能为空"));
		userMethod.setLoginName("").getStatus();
	}


//重复设置手机号,

	@Test
	@WithMockUser(username = "testuser", password = "Admin12#$")
	public void testSetMobileAgain() throws Exception {
		registerUser("testuser", "Admin12#$", "18765834321", "");
		UserMethod userMethod = new UserMethod(mockMvc);
		thrown.expect(NestedServletException.class);
    	thrown.expectMessage("Request processing failed; nested exception is com.ut.user.constants.UserException");
		userMethod.bindMobile("test", "2545425455", "543", "18819495594").getStatus();
	}

	@Test()
	@WithMockUser(username = "testuser", password = "Admin12#$")
	public void testSetMobileSame() throws Exception {
		registerUser("testuser", "Admin12#$", "18765834321", "");
		UserMethod userMethod = new UserMethod(mockMvc);
		thrown.expect(NestedServletException.class);
		thrown.expectMessage("Request processing failed; nested exception is com.ut.user.constants.UserException");
		userMethod.bindMobile("test", "2545425455", "543", "18765834321").getStatus();
	}

	@Test
	@WithMockUser(username = "testuser", password = "Admin12#$")
	public void testSetMobileAppPrefixEmpty() throws Exception {
		registerUser("testuser", "Admin12#$", "", "");
		UserMethod userMethod = new UserMethod(mockMvc);
		thrown.expect(NestedServletException.class);
		thrown.expectMessage("Request processing failed; nested exception is com.ut.user.constants.UserException");
		userMethod.bindMobile("", "2545425455", "543", "18819495594").getStatus();
	}


	@Test
	@WithMockUser(username = "testuser", password = "Admin12#$")
	public void testSetMobileAppSmsCodeEmpty() throws Exception {
		registerUser("testuser", "Admin12#$", "", "");
		UserMethod userMethod = new UserMethod(mockMvc);
		thrown.expect(NestedServletException.class);
		thrown.expectMessage("Request processing failed; nested exception is com.ut.user.constants.UserException");
		userMethod.bindMobile("test", "2545425455", "", "18819495594").getStatus();
	}

	@Test
	@WithMockUser(username = "testuser", password = "Admin12#$")
	public void testSetMobileAppPhoneNoEmpty() throws Exception {
		registerUser("testuser", "Admin12#$", "", "");
		UserMethod userMethod = new UserMethod(mockMvc);
		thrown.expect(NestedServletException.class);
		thrown.expectMessage("Request processing failed; nested exception is com.ut.user.constants.UserException");
		userMethod.bindMobile("test", "2545425455", "543", "").getStatus();
	}

	//	绑定手机号(待弃用)
	@Test
	@WithMockUser(username = "testuser", password = "Admin12#$")
	public void testBindPhoneNo() throws Exception {
		registerUser("testuser", "Admin12#$", null, null);
		UserMethod userMethod = new UserMethod(mockMvc);
		MockHttpServletResponse response = userMethod.bindPhoneNo("18819495594");
		Assert.assertEquals(200, response.getStatus());
		Assert.assertEquals("true", response.getContentAsString());
	}

	//	更换绑定的手机号
	@Test
	@WithMockUser(username = "testuser", password = "Admin12#$")
	public void testChangeMobile() throws Exception {
		registerUser("testuser", "Admin12#$", "18475934354", null);
		UserMethod userMethod = new UserMethod(mockMvc);
		SmsCodeVo smsCodeVo = new SmsCodeVo();
		SmsVo smsVoOld = new SmsVo();
		smsVoOld.setAppPrefix("appKey");
		smsVoOld.setMessageId("43534");
		smsVoOld.setSmsCode("4535");
		smsVoOld.setPhoneNo("18475934354");

		SmsVo smsVoNew = new SmsVo();
		smsVoNew.setAppPrefix("appKey");
		smsVoNew.setMessageId("345363");
		smsVoNew.setSmsCode("3545");
		smsVoNew.setPhoneNo("18473242443");
		smsCodeVo.setOldSmsCode(smsVoOld);
		smsCodeVo.setNewSmsCode(smsVoNew);

		new MockUp<CacheSingleService>() {
			@Mock
			public String get(String key) {
				return "true";
			}
		};
		Assert.assertEquals("true", userMethod.changeMobile(smsCodeVo).getResponse());
	}


	@Test
	@WithMockUser(username = "testuser", password = "Admin12#$")
	public void testSetMobileAppPhoneNoIllegel() throws Exception {
		registerUser("testuser", "Admin12#$", null, "");
		UserMethod userMethod = new UserMethod(mockMvc);
		MockHttpServletResponse responseQueList = userMethod.questionList();
		Assert.assertEquals(200, responseQueList.getStatus());

	}
	//##修改密码相关


	//	获取系统密保问题列表—设置密保问题——校验密保问题—获取已设置的密保问题—修改密保问题—密保问题修改密码
	@Test
	@WithMockUser(username = "testuser", password = "Admin12#$")
	public void testChangePasswordByQue() throws Exception {
		registerUser("testuser", "Admin12#$", "", "");
		UserMethod userMethod = new UserMethod(mockMvc);
		MockHttpServletResponse responseQueList = userMethod.questionList();
		Assert.assertEquals(200, responseQueList.getStatus());
		Assert.assertEquals(3, JSONObject.parseArray(responseQueList.getContentAsString(), ResetQuestionEntity.class).size());
		Assert.assertEquals("true", userMethod.questionAnswer("1", "chenglin").getContentAsString());
		Assert.assertEquals("true", userMethod.checkQuestion("1", "chenglin").getContentAsString());
		Assert.assertEquals(200, userMethod.myQuestion().getStatus());
		Assert.assertEquals("true", userMethod.resetQuestionAnswer("1", "3", "暨南大学").getContentAsString());
		Assert.assertEquals("true", userMethod.resetPasswordByQuestion("3", "暨南大学", "Admin12345#$").getContentAsString());
	}

	@Test
	@WithMockUser(username = "testuser", password = "Admin12#$")
	public void testChangePassword() throws Exception {
		registerUser("testuser", "Admin12#$", "", "");
		UserMethod userMethod = new UserMethod(mockMvc);
		MockHttpServletResponse response = userMethod.changePswd("Admin12#$", "Admin12345#$");
		Assert.assertEquals(200, response.getStatus());
		Assert.assertEquals("true", response.getContentAsString());
	}

	@Test
	@WithMockUser(username = "testuser", password = "Admin12#$")
	public void testChangePasswordWithWrongOldPassword() throws Exception {
		registerUser("testuser", "Admin12#$", "", "");
		UserMethod userMethod = new UserMethod(mockMvc);
		MockHttpServletResponse response = userMethod.changePswd("Admin", "Admin12345#$");
		Assert.assertEquals(200, response.getStatus());
		Assert.assertEquals("false", response.getContentAsString());
	}

	@Test
	@WithMockUser(username = "testuser", password = "Admin12#$")
	public void testChangePasswordWithNewPasswordIllegal() throws Exception {
		registerUser("testuser", "Admin12#$", "", "");
		UserMethod userMethod = new UserMethod(mockMvc);
		thrown.expect(Exception.class);
		thrown.expectMessage(("密码须为8到20位包含大小写字母数字组成的字符,可选特殊字符 $@!%*#?&"));
		MockHttpServletResponse response = userMethod.changePswd("Admin12#$", "dmin");
	}

	//修改用户密码——手机验证码修改密码——重置密码
	//通过手机重置用户密码
	@Test
	@WithMockUser(username = "testuser", password = "Admin12#$")
	public void testChangePasswordByMobile() throws Exception {
		registerUser("testuser", "Admin12#$", "", "");
		UserMethod userMethod = new UserMethod(mockMvc);
		new MockUp<CacheSingleService>() {
			@Mock
			public String get(String key) {
				return "true";
			}
		};
		MockHttpServletResponse response = userMethod.resetPasswordByMobile("324224", "2433", "Admin12345#$");
		Assert.assertEquals(200, response.getStatus());
		Assert.assertEquals("true", response.getContentAsString());
	}

	//通过手机重置用户密码-新密码不合法
	@Test
	@WithMockUser(username = "testuser", password = "Admin12#$")
	public void testChangePasswordIllegal() throws Exception {
		registerUser("testuser", "Admin12#$", "", "");
		UserMethod userMethod = new UserMethod(mockMvc);
		new MockUp<CacheSingleService>() {
			@Mock
			public String get(String key) {
				return "true";
			}
		};
		thrown.expectMessage(("密码须为8到20位包含大小写字母数字组成的字符,可选特殊字符 $@!%*#?&"));
		MockHttpServletResponse response = userMethod.resetPasswordByMobile("324224 ", "2433", "Admin");
	}


	//
//	无权限重置用户密码
	@Test
	@WithMockUser(username = "testuser", password = "Admin12#$")
	public void testResetPasswordOutAuth() throws Exception {
		registerUser("testuser", "Admin12#$", "", "");
		UserMethod userMethod = new UserMethod(mockMvc);
		MockHttpServletResponse response = userMethod.resetPassword("testuser");
		Assert.assertEquals(403, response.getStatus());
	}

	//	重置用户密码
	@Test
	@WithMockUser(username = "testuser", password = "Admin12#$", authorities = {"platform_admin"})
	public void testResetPassword() throws Exception {
		registerUser("testuser", "Admin12#$", "", "");
		UserMethod userMethod = new UserMethod(mockMvc);
		MockHttpServletResponse response = userMethod.resetPassword("testuser");
		Assert.assertEquals(200, response.getStatus());
	}

	//	获取父账户名
	@Test
	@WithMockUser(username = "testuser", password = "Admin12#$", authorities = {"platform_admin"})
	public void testGetMyParentUsername() throws Exception {
		registerUser("testuser", "Admin12#$", null, "parentuser");
		UserMethod userMethod = new UserMethod(mockMvc);
		MockHttpServletResponse response = userMethod.getMyParentUsername();
		Assert.assertEquals(200, response.getStatus());
		Assert.assertEquals("parentuser", response.getContentAsString());
	}

	//修改子账户用户状态
	@Test
	@WithMockUser(username = "testuser", password = "Admin12#$", authorities = {"platform_admin"})
	public void testChangeUserStatus() throws Exception {
		registerUser("testuser", "Admin12#$", null, null);
		registerUser("childuser", "Admin12#$", null, "testuser");
		UserMethod userMethod = new UserMethod(mockMvc);
		MockHttpServletResponse response = userMethod.changeUserStatus("childuser", "false");
		Assert.assertEquals(200, response.getStatus());
		Assert.assertEquals("true", response.getContentAsString());
	}

	//修改非子账户用户状态
	@Test
	@WithMockUser(username = "testuser", password = "Admin12#$", authorities = {"platform_admin"})
	public void testChangeUserStatusUnNormal() throws Exception {
		registerUser("testuser", "Admin12#$", null, null);
		UserMethod userMethod = new UserMethod(mockMvc);
		thrown.expect(Exception.class);
		thrown.expectMessage(("不是您的子账户，无权操作！"));
		userMethod.changeUserStatus("testuser", "false");
	}

	@Test
	@WithMockUser(username = "testuser", password = "Admin12#$")
	public void testBatchRegistyOutAuth() throws Exception {
		registerUser("testuser", "Admin12#$", null, null);
		UserMethod userMethod = new UserMethod(mockMvc);
		List<BatchUserVo> bulkUserVos = new ArrayList<>();
		BatchUserVo batchUserVo = new BatchUserVo();
		batchUserVo.setUsername("myone");
		batchUserVo.setPassword("Admin12#$");
		bulkUserVos.add(batchUserVo);
		Assert.assertEquals(403, userMethod.batchRegister(bulkUserVos).getStatus());
	}

	@Test
	@WithMockUser(username = "testuser", password = "Admin12#$", authorities = {"platform_user_batchRegister"})
	public void testBatchRegisty() throws Exception {
		registerUser("testuser", "Admin12#$", null, null);
		UserMethod userMethod = new UserMethod(mockMvc);
		List<BatchUserVo> bulkUserVos = new ArrayList<>();
		BatchUserVo batchUserVo = new BatchUserVo();
		batchUserVo.setUsername("mytesttest");
		batchUserVo.setPassword("Admin12#$");
		bulkUserVos.add(batchUserVo);
		Assert.assertEquals(200, userMethod.batchRegister(bulkUserVos).getStatus());

	}

	@Test
	@WithMockUser(username = "testuser", password = "Admin12#$")
	public void testBindSocialAccoutWithWrongType() throws Exception {
		registerUser("testuser", "Admin12#$", null, null);
		UserMethod userMethod = new UserMethod(mockMvc);
		SocialAccountDTO accountDTO = new SocialAccountDTO();
		accountDTO.setSocialAccountKey("34");
		accountDTO.setSocialAccountType("345");
		thrown.expect(Exception.class);
		//thrown.expectMessage(("不是您的子账户，无权操作！"));
		userMethod.bindSocialAccount(accountDTO);
	}

	//在缓存中找不到loginKey，期望抛异常
	@Test
	@WithMockUser(username = "testuser", password = "Admin12#$")
	public void testBindMiniProgramWithWrongLoginKey() throws Exception {
		registerUser("testuser", "Admin12#$", null, null);
		UserMethod userMethod = new UserMethod(mockMvc);
		SocialAccountDTO accountDTO = new SocialAccountDTO();
		accountDTO.setSocialAccountKey("test");
		accountDTO.setSocialAccountType("miniProgram");
		thrown.expect(Exception.class);
		userMethod.bindSocialAccount(accountDTO);
	}

	//正常绑定小程序
	@Test
	@WithMockUser(username = "testuser", password = "Admin12#$")
	public void testBindMiniProgram() throws Exception {
		registerUser("testuser", "Admin12#$", null, null);
		UserMethod userMethod = new UserMethod(mockMvc);
		new MockUp<CacheSingleService>() {
			@Mock
			public String get(String key) {
				return JSON.toJSONString(new MiniProgramUser("open1029404", "iwhgoahg", "2qgiwgnsg"));
			}
		};
		SocialAccountDTO accountDTO = new SocialAccountDTO();
		accountDTO.setSocialAccountKey("test");
		accountDTO.setSocialAccountType("miniProgram");
		userMethod.bindSocialAccount(accountDTO);
	}

//	小程序已被其他用户绑定
	@Test
	@WithMockUser(username = "testuser", password = "Admin12#$")
	public void testBindMiniProgramByOther() throws Exception {
		registerUser("testuser", "Admin12#$", null, null);
		WechatUserEntity wechatUserEntity = new WechatUserEntity();
		wechatUserEntity.setOauthUserName("otheruser");
		wechatUserEntity.setOpenId("open1029404");
		wechatUserDao.save(wechatUserEntity);
		UserMethod userMethod = new UserMethod(mockMvc);
		new MockUp<CacheSingleService>() {
			@Mock
			public String get(String key) {
				return JSON.toJSONString(new MiniProgramUser("open1029404", "iwhgoahg", "2qgiwgnsg"));
			}
		};
		thrown.expect(Exception.class);
		thrown.expectMessage("小程序账号已被其他用户绑定");
		SocialAccountDTO accountDTO = new SocialAccountDTO();
		accountDTO.setSocialAccountKey("test");
		accountDTO.setSocialAccountType("miniProgram");
		userMethod.bindSocialAccount(accountDTO);
	}

	//自己曾经绑定过小程序，直接
	@Test
	@WithMockUser(username = "testuser", password = "Admin12#$")
	public void testBindMiniProgramBySelf() throws Exception {
		registerUser("testuser", "Admin12#$", null, null);
		WechatUserEntity wechatUserEntity = new WechatUserEntity();
		wechatUserEntity.setOauthUserName("testuser");
		wechatUserEntity.setOpenId("open1029404");
		wechatUserDao.save(wechatUserEntity);
		UserMethod userMethod = new UserMethod(mockMvc);
		new MockUp<CacheSingleService>() {
			@Mock
			public String get(String key) {
				return JSON.toJSONString(new MiniProgramUser("open1029404", "iwhgoahg", "2qgiwgnsg"));
			}
		};
		SocialAccountDTO accountDTO = new SocialAccountDTO();
		accountDTO.setSocialAccountKey("test");
		accountDTO.setSocialAccountType("miniProgram");
		userMethod.bindSocialAccount(accountDTO);
	}
}









