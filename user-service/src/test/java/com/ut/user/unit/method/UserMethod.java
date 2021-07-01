package com.ut.user.unit.method;

import com.ut.user.thirdauth.SocialAccountDTO;
import com.ut.user.usermgr.MyUserExtendEntity;
import com.ut.user.util.HttpHelper;
import com.ut.user.util.Response;
import com.ut.user.vo.BatchUserVo;
import com.ut.user.vo.PasswordVo;
import com.ut.user.vo.SmsCodeVo;
import com.ut.user.vo.UsernamePswdVo;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;

import java.util.List;

public class UserMethod {
	private MockMvc mockMvc;
	public UserMethod(MockMvc mockMvc){
		this.mockMvc = mockMvc;
	}
	//获取登录用户信息
	public MockHttpServletResponse getLoginUser() throws Exception {
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.GET, "/user/curUser", null, "", null);
		return mockHttpServletResponse;
	}

	//账户名查找账户
	public MockHttpServletResponse getUser(String arg) throws Exception {
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		LinkedMultiValueMap<String, String> paramMp = new LinkedMultiValueMap<>();
		paramMp.add("arg", arg);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.GET, "/user/user", paramMp, "", null);
		return  mockHttpServletResponse;
	}

	//获取用户扩展信息
	public MockHttpServletResponse getUserExtend(String username) throws Exception {
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		LinkedMultiValueMap<String, String> paramMp = new LinkedMultiValueMap<>();
		paramMp.add("username", username);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.GET, "/user/userExtend", paramMp, "", null);
		return mockHttpServletResponse;
	}

	public MockHttpServletResponse newUserExtend(MyUserExtendEntity myUserExtendEntity) throws Exception{
		HttpHelper<MyUserExtendEntity> httpHelper = new HttpHelper<>(mockMvc);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.POST, "/user/userExtend", null, myUserExtendEntity, null);
		return mockHttpServletResponse;
	}

	//修改用户昵称
	public MockHttpServletResponse updateNickname(String nickname) throws Exception {
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		LinkedMultiValueMap<String, String> paramMp = new LinkedMultiValueMap<>();
		paramMp.add("nickname", nickname);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.POST, "/user/nickname", paramMp, "", null);
		return mockHttpServletResponse;
	}

	//	设置用户登录名
	public MockHttpServletResponse setLoginName(String loginName) throws Exception {
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		LinkedMultiValueMap<String, String> paramMp = new LinkedMultiValueMap<>();
		paramMp.add("loginName", loginName);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.POST, "/user/setLoginName", paramMp, "", null);
		return mockHttpServletResponse;
	}

	//修改用户密码
	public MockHttpServletResponse changePswd(String oldPwd, String newPwd) throws Exception {
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		LinkedMultiValueMap<String, String> paramMp = new LinkedMultiValueMap<>();
		paramMp.add("oldPwd", oldPwd);
		paramMp.add("newPwd", newPwd);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.POST, "/user/password", paramMp, "", null);
		int status = mockHttpServletResponse.getStatus();
		Response result = new Response(status,mockHttpServletResponse.getContentAsString());
		return mockHttpServletResponse;
	}
//	List<QuestionVo>
	public MockHttpServletResponse myQuestion() throws Exception{
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.GET, "/user/myQuestion", null, "", null);
		return mockHttpServletResponse;
	}


	//获取系统密保问题列表
	public MockHttpServletResponse questionList() throws Exception {
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.GET, "/user/questionList", null, "", null);
		return mockHttpServletResponse;
	}

	//设置密保问题答案
	public MockHttpServletResponse questionAnswer(String questionId, String answer) throws Exception {
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		LinkedMultiValueMap<String, String> paramMp = new LinkedMultiValueMap<>();
		paramMp.add("questionId", questionId);
		paramMp.add("answer", answer);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.POST, "/user/questionAnswer", paramMp, "", null);
		return mockHttpServletResponse;
	}


	//校验密保问题答案
	public MockHttpServletResponse checkQuestion(String questionId, String answer) throws Exception {
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		LinkedMultiValueMap<String, String> paramMp = new LinkedMultiValueMap<>();
		paramMp.add("questionId", questionId);
		paramMp.add("answer", answer);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.POST, "/user/checkQuestion", paramMp, "", null);
		return  mockHttpServletResponse;
	}

//重置用户密码
	public MockHttpServletResponse resetPassword(String username)throws Exception{
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		LinkedMultiValueMap<String, String> paramMp = new LinkedMultiValueMap<>();
		paramMp.add("username", username);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.POST, "/user/resetPassword", paramMp, "", null);
		return mockHttpServletResponse;

	}
	//修改密保问题
	public MockHttpServletResponse resetQuestionAnswer(String oldQuestionId, String newQuestionId, String answer) throws Exception {
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		LinkedMultiValueMap<String, String> paramMp = new LinkedMultiValueMap<>();
		paramMp.add("oldQuestionId", oldQuestionId);
		paramMp.add("newQuestionId", newQuestionId);
		paramMp.add("answer", answer);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.POST, "/user/resetQuestionAnswer", paramMp, "", null);
		return mockHttpServletResponse;
	}


	//重置用户密码-通过密保问题方式
	public MockHttpServletResponse resetPasswordByQuestion(String questionId, String answer, String newPwd) throws Exception {
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		LinkedMultiValueMap<String, String> paramMp = new LinkedMultiValueMap<>();
		paramMp.add("questionId", questionId);
		paramMp.add("answer", answer);
		paramMp.add("newPwd", newPwd);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.POST, "/user/resetPasswordByQuestion", paramMp, "", null);
		return mockHttpServletResponse;
	}


	//重置用户密码——通过手机验证码方式
	public MockHttpServletResponse resetPasswordByMobile(String messageId, String smsCode, String newPwd) throws Exception {
		HttpHelper<PasswordVo> httpHelper = new HttpHelper<>(mockMvc);
		PasswordVo passwordVo = new PasswordVo();
		passwordVo.setMessageId(messageId);
		passwordVo.setSmsCode(smsCode);
		passwordVo.setNewPwd(newPwd);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.POST, "/user/resetPasswordByMobile", null, passwordVo, null);
		return mockHttpServletResponse;
	}
	//	绑定手机号【新接口  用户密码注册账户使用】
	public Response bindMobile(String appPrefix, String messageId, String smsCode, String phoneNo)throws Exception{
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		LinkedMultiValueMap<String, String> paramMp = new LinkedMultiValueMap<>();
		paramMp.add("appPrefix", appPrefix);
		paramMp.add("messageId", messageId);
		paramMp.add("smsCode", smsCode);
		paramMp.add("phoneNo", phoneNo);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.PUT, "/user/bindMobile", paramMp, "", null);
		int status = mockHttpServletResponse.getStatus();
		Response result = new Response(status,mockHttpServletResponse.getContentAsString());
		return result;
	}
//	绑定手机号【餐饮组用于更换手机，待弃用】
	public MockHttpServletResponse bindPhoneNo(String phoneNo)throws Exception{
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		LinkedMultiValueMap<String, String> paramMp = new LinkedMultiValueMap<>();
		paramMp.add("phoneNo", phoneNo);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.PUT, "/user/bindPhoneNo", paramMp, "", null);
		return mockHttpServletResponse;
	}
	//解绑手机号
	public Response unBindMobile(String appPrefix, String messageId, String smsCode, String phoneNo) throws Exception{
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		LinkedMultiValueMap<String, String> paramMp = new LinkedMultiValueMap<>();
		paramMp.add("appPrefix", appPrefix);
		paramMp.add("messageId", messageId);
		paramMp.add("smsCode", smsCode);
		paramMp.add("phoneNo", phoneNo);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.PUT, "/user/unBindMobile", paramMp, "", null);
		int status = mockHttpServletResponse.getStatus();
		Response result = new Response(status,mockHttpServletResponse.getContentAsString());
		return result;
	}
	//更换已绑定手机号
	public Response changeMobile(SmsCodeVo smsCodeVo)throws Exception{
		HttpHelper<SmsCodeVo> httpHelper = new HttpHelper<>(mockMvc);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.PUT, "/user/changeMobile", null, smsCodeVo, null);
		int status = mockHttpServletResponse.getStatus();
		Response result = new Response(status,mockHttpServletResponse.getContentAsString());
		return result;
	}
	public Response batchRegister(List<BatchUserVo> bulkUserVos) throws Exception{
		HttpHelper<List<BatchUserVo>> httpHelper = new HttpHelper<>(mockMvc);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.POST, "/user/batchRegister", null, bulkUserVos, null);
		int status = mockHttpServletResponse.getStatus();
		Response result = new Response(status,mockHttpServletResponse.getContentAsString());
		return result;
	}
	//获得父账户名
	public MockHttpServletResponse getMyParentUsername() throws Exception{
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.GET, "/user/getParentUsername", null, null, null);
		return mockHttpServletResponse;
	}
	public MockHttpServletResponse changeUserStatus(String username, String activated)  throws  Exception{
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		LinkedMultiValueMap<String, String> paramMp = new LinkedMultiValueMap<>();
		paramMp.add("username", username);
		paramMp.add("activated", activated);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.POST, "/user/changeUserStatus", paramMp, "", null);
		return mockHttpServletResponse;
	}
	public MockHttpServletResponse bindSocialAccount(SocialAccountDTO socialAccountDTO) throws Exception{
		HttpHelper<SocialAccountDTO> httpHelper = new HttpHelper<>(mockMvc);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.PUT, "/user/bindSocialAccount", null, socialAccountDTO, null);
		return mockHttpServletResponse;
	}
	public MockHttpServletResponse getBindStatus() throws Exception {
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.GET, "/user/bindStatus", null, null, null);
		return mockHttpServletResponse;
	}
	public MockHttpServletResponse unbindThirdAccount(String socialAccountType) throws  Exception{
		HttpHelper<String> httpHelper = new HttpHelper<>(mockMvc);
		LinkedMultiValueMap<String, String> paramMp = new LinkedMultiValueMap<>();
		paramMp.add("socialAccountType", socialAccountType);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.DELETE, "/user/unbindSocialAccount", paramMp, "", null);
		return mockHttpServletResponse;
	}

	public MockHttpServletResponse changeUserPassword(UsernamePswdVo usernamePswdVo) throws Exception {
		HttpHelper<UsernamePswdVo> httpHelper = new HttpHelper<>(mockMvc);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.POST, "/user/changeUserPassword", null, usernamePswdVo, null);
		return mockHttpServletResponse;
	}
}
