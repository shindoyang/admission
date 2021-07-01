package com.ut.user.unit.method;

import com.ut.user.util.HttpHelper;
import com.ut.user.vo.UserListVo;
import com.ut.user.vo.UserStatusVo;
import com.ut.user.vo.UsernamePswdVo;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

/**
 * @author chenglin
 * @creat 2019/9/16
 */
public class AccountSysMethod {
	private MockMvc mockMvc;
	public AccountSysMethod(MockMvc mockMvc){
		this.mockMvc = mockMvc;
	}
	//获取登录用户信息
	/*public MockHttpServletResponse creatAccount(AccountVo accountVo) throws Exception {
		HttpHelper<AccountVo> httpHelper = new HttpHelper<>(mockMvc);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.POST, "/accountAdmin/creatAccount", null, accountVo, null);
		return mockHttpServletResponse;
	}*/

	//修改指定用户密码
	public MockHttpServletResponse updateUserPassword(UsernamePswdVo usernamePswdVo) throws Exception {
		HttpHelper<UsernamePswdVo> httpHelper = new HttpHelper<>(mockMvc);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.POST, "/accountAdmin/updateUserPassword", null, usernamePswdVo, null);
		return mockHttpServletResponse;
	}

	//修改指定用户状态
	public MockHttpServletResponse updateUserStatus(UserStatusVo userStatusVo) throws Exception {
		HttpHelper<UserStatusVo> httpHelper = new HttpHelper<>(mockMvc);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.POST, "/accountAdmin/updateUserStatus", null, userStatusVo, null);
		return mockHttpServletResponse;
	}
	//批量获取用户信息
	public MockHttpServletResponse getUserInfoInBulk(UserListVo userListVo) throws Exception {
		HttpHelper<UserListVo> httpHelper = new HttpHelper<>(mockMvc);
		MockHttpServletResponse mockHttpServletResponse = httpHelper.request(HttpMethod.GET, "/accountAdmin/getUserInfoInBulk", null, userListVo, null);
		return mockHttpServletResponse;
	}

}
