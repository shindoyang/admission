package com.ut.user.unit.controller;

import com.ut.user.UserApp;
import com.ut.user.appmgr.AppDao;
import com.ut.user.authritymgr.AuthorityGroupDao;
import com.ut.user.unit.method.AccountSysMethod;
import com.ut.user.usermgr.MyUserDao;
import com.ut.user.usermgr.MyUserEntity;
import com.ut.user.usermgr.MyUserExtendDao;
import com.ut.user.util.AuthStatefulSecurityConfig;
import com.ut.user.vo.UserListVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chenglin
 * @creat 2019/9/16
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserApp.class,
		webEnvironment = SpringBootTest.WebEnvironment.MOCK,
		properties = {"spring.cloud.zookeeper.enabled=false", "spring.cloud.config.enabled=false", "spring.profiles.active=test"})
@AutoConfigureMockMvc
@Rollback
@Transactional
@Import(AuthStatefulSecurityConfig.class)
public class AccountSysControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private MyUserDao myUserDao;
	@Autowired
	private MyUserExtendDao myUserExtendDao;
	@Autowired
	private AppDao appDao;
	@Autowired
	private AuthorityGroupDao authorityGroupDao;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private void registerUser(String username, String password, String mobile, String parent) throws Exception {
		MyUserEntity myUserEntity = new MyUserEntity();
		myUserEntity.setUsername(username);
		myUserEntity.setPassword(passwordEncoder.encode(password));
		myUserEntity.setMobile(mobile);
		myUserEntity.setParentUser(parent);
		myUserDao.saveAndFlush(myUserEntity);
	}

	//创建账号
//	正常
	/*@Test
	@WithMockUser(username = "testuser", password = "Admin12#$",authorities = {"platform_account_systemKey"})
	public void testCreatAccount() throws Exception {
		AccountSysMethod accountSysMethod = new AccountSysMethod(mockMvc);
		AccountVo accountVo = new AccountVo();
		accountVo.setUsername("platform_cook");
		accountVo.setPassword("Ut123456");
		Assert.assertEquals(200, accountSysMethod.creatAccount(accountVo).getStatus());
	}

	//商家账号
	@Test
	@WithMockUser(username = "testuser", password = "Admin12#$",authorities = {"platform_account_systemKey"})
	public void testCreatAccountVo() throws Exception {
		AccountSysMethod accountSysMethod = new AccountSysMethod(mockMvc);
		AccountVo accountVo = new AccountVo();
		accountVo.setUsername("platform_cook");
		accountVo.setPassword("Ut123456");
		accountVo.setNickname("餐饮");
		accountVo.setMobile("18819495594");
		accountVo.setLogo("http://www.baidulogo.com");
		Assert.assertEquals(200, accountSysMethod.creatAccount(accountVo).getStatus());
		Assert.assertEquals("http://www.baidulogo.com",myUserExtendDao.findByUsername("platform_cook").getLogo());
		Assert.assertEquals("餐饮",myUserDao.findByUsername("platform_cook").getNickname());
		Assert.assertEquals("18819495594",myUserDao.findByUsername("platform_cook").getMobile());
	}
	//员工账号
	@Test
	@WithMockUser(username = "testuser", password = "Admin12#$",authorities = {"platform_account_systemKey"})
	public void testCreatAccountInfoVo() throws Exception {
		AppEntity appEntity = new AppEntity();
		appEntity.setAppKey("cheng");
		appEntity.setDeveloper("userLin");
		appDao.save(appEntity);
		AuthorityGroupEntity  authorityGroupEntity = new AuthorityGroupEntity();
		authorityGroupEntity.setAppKey("cheng");
		authorityGroupEntity.setAuthorityGroupKey("cheng_manage");
		authorityGroupDao.save(authorityGroupEntity);
		AccountSysMethod accountSysMethod = new AccountSysMethod(mockMvc);
		AccountVo accountVo = new AccountVo();
		accountVo.setUsername("platform_cook");
		accountVo.setPassword("Ut123456");
		accountVo.setNickname("餐饮");
		accountVo.setMobile("18819495594");
		accountVo.setLogo("http://www.baidulogo.com");
		AuthorityGroupInVo authGroupEntity  = new AuthorityGroupInVo();
		authGroupEntity.setAppKey("cheng");
		authGroupEntity.setAuthorityGroupKey("cheng_manage");
		accountVo.setAuthorityGroupInVo(authGroupEntity);
		Assert.assertEquals(200, accountSysMethod.creatAccount(accountVo).getStatus());
		Assert.assertEquals("http://www.baidulogo.com",myUserExtendDao.findByUsername("platform_cook").getLogo());
		Assert.assertEquals("餐饮",myUserDao.findByUsername("platform_cook").getNickname());
		Assert.assertEquals("18819495594",myUserDao.findByUsername("platform_cook").getMobile());
		Assert.assertEquals(1,authorityGroupDao.findAllByAppKey("cheng").size());
	}

	//异常——appKey和authorityKey不能为空
	@Test
	@WithMockUser(username = "testuser", password = "Admin12#$",authorities = {"platform_account_systemKey"})
	public void testCreatAccountInfoVo1() throws Exception {
		AppEntity appEntity = new AppEntity();
		appEntity.setAppKey("cheng");
		appEntity.setDeveloper("userLin");
		appDao.save(appEntity);
		AuthorityGroupEntity  authorityGroupEntity = new AuthorityGroupEntity();
		authorityGroupEntity.setAppKey("cheng");
		authorityGroupEntity.setAuthorityGroupKey("cheng_manage");
		authorityGroupDao.save(authorityGroupEntity);
		AccountSysMethod accountSysMethod = new AccountSysMethod(mockMvc);
		AccountVo accountVo = new AccountVo();
		accountVo.setUsername("platform_cook");
		accountVo.setPassword("Ut123456");
		accountVo.setNickname("餐饮");
		accountVo.setMobile("18819495594");
		accountVo.setLogo("http://www.baidulogo.com");
		AuthorityGroupInVo authGroupEntity  = new AuthorityGroupInVo();
		accountVo.setAuthorityGroupInVo(authGroupEntity);
		thrown.expectMessage("appKey 和 authorityGroupKey 不能为空！");
		accountSysMethod.creatAccount(accountVo);


	}


//异常——无权限
	@Test
	@WithMockUser(username = "testuser", password = "Admin12#$")
	public void testResetQuestionUnAuth() throws Exception {
		AccountSysMethod accountSysMethod = new AccountSysMethod(mockMvc);
		AccountVo accountVo = new AccountVo();
		accountVo.setUsername("platform_cook");
		accountVo.setPassword("Ut123456");
		Assert.assertEquals(403, accountSysMethod.creatAccount(accountVo).getStatus());
	}
//	异常-用户名为空
@Test
@WithMockUser(username = "testuser", password = "Admin12#$", authorities = {"platform_account_systemKey"})
public void testResetQuestionEmpty1() throws Exception {
	AccountSysMethod accountSysMethod = new AccountSysMethod(mockMvc);
	AccountVo accountVo = new AccountVo();
	accountVo.setUsername("");
	accountVo.setPassword("Ut123456");
	thrown.expectMessage("用户名或密码不能为空！");
	accountSysMethod.creatAccount(accountVo);
}
	//	异常-密码为空
	@Test
	@WithMockUser(username = "testuser", password = "Admin12#$", authorities = {"platform_account_systemKey"})
	public void testResetQuestionEmpty2() throws Exception {
		AccountSysMethod accountSysMethod = new AccountSysMethod(mockMvc);
		AccountVo accountVo = new AccountVo();
		accountVo.setUsername("platform_cook");
		accountVo.setPassword("");
		thrown.expectMessage("用户名或密码不能为空！");
		accountSysMethod.creatAccount(accountVo);
	}
	//	异常-用户名不合法
	@Test
	@WithMockUser(username = "testuser", password = "Admin12#$", authorities = {"platform_account_systemKey"})
	public void testResetQuestionNameIlleage() throws Exception {
		AccountSysMethod accountSysMethod = new AccountSysMethod(mockMvc);
		AccountVo accountVo = new AccountVo();
		accountVo.setUsername("w45");
		accountVo.setPassword("Ut123456");
		thrown.expectMessage(ExceptionContants.USERNAME_ERR_MSG);
		accountSysMethod.creatAccount(accountVo);
	}
	//	异常-密码不合法
	@Test
	@WithMockUser(username = "testuser", password = "Admin12#$", authorities = {"platform_account_systemKey"})
	public void testResetQuestionPswdIlleage() throws Exception {
		AccountSysMethod accountSysMethod = new AccountSysMethod(mockMvc);
		AccountVo accountVo = new AccountVo();
		accountVo.setUsername("platform_cook");
		accountVo.setPassword("123456");
		thrown.expectMessage(ExceptionContants.PWD_ERR_MSG);
		accountSysMethod.creatAccount(accountVo);
	}

	//	异常-用户名已被占用
	@Test
	@WithMockUser(username = "testuser", password = "Admin12#$", authorities = {"platform_account_systemKey"})
	public void testResetQuestionUsernameExist() throws Exception {
		registerUser("testuser","Ut123456","","");
		AccountSysMethod accountSysMethod = new AccountSysMethod(mockMvc);
		AccountVo accountVo = new AccountVo();
		accountVo.setUsername("testuser");
		accountVo.setPassword("Ut123456");
		thrown.expectMessage(ExceptionContants.USERNAME_ALEADY_EXIST);
		accountSysMethod.creatAccount(accountVo);
	}
	//	异常-手机号不合法
	@Test
	@WithMockUser(username = "testuser", password = "Admin12#$", authorities = {"platform_account_systemKey"})
	public void testResetQuestionMobileIlleage() throws Exception {
		AccountSysMethod accountSysMethod = new AccountSysMethod(mockMvc);
		AccountVo accountVo = new AccountVo();
		accountVo.setUsername("platform_cook");
		accountVo.setPassword("Ut123456");
		accountVo.setMobile("188194994");
		thrown.expectMessage(ExceptionContants.MOBILE_ERR_MSG);
		accountSysMethod.creatAccount(accountVo);
	}
	//	异常-手机号已被占用
	@Test
	@WithMockUser(username = "testuser", password = "Admin12#$", authorities = {"platform_account_systemKey"})
	public void testResetQuestionMobileExist() throws Exception {
		registerUser("testuser","Ut123456","18819495594","");
		AccountSysMethod accountSysMethod = new AccountSysMethod(mockMvc);
		AccountVo accountVo = new AccountVo();
		accountVo.setUsername("platform_cook");
		accountVo.setPassword("Ut123456");
		accountVo.setMobile("18819495594");
		thrown.expectMessage(String.format(ExceptionContants.MOBILE_USED_EXCEPTION,"18819495594"));
		accountSysMethod.creatAccount(accountVo);
	}*/

//	//######修改用户密码
//	//正常
//	@Test
//	@WithMockUser(username = "testuser", password = "Admin12#$", authorities = {"platform_account_systemKey"})
//	public void updateUserPassword() throws Exception{
//		registerUser("testuser","Ut123456","18819495594","");
//		AccountSysMethod accountSysMethod = new AccountSysMethod(mockMvc);
//		UsernamePswdVo usernamePswdVo = new UsernamePswdVo();
//		usernamePswdVo.setUsername("testuser");
//		usernamePswdVo.setNewPassword("Ut1234567");
//		Assert.assertEquals(200,accountSysMethod.updateUserPassword(usernamePswdVo).getStatus());
//	}
////	异常-无权限
//@Test
//@WithMockUser(username = "testuser", password = "Admin12#$")
//public void updateUserPasswordUnAuth() throws Exception{
//	registerUser("testuser","Ut123456","18819495594","");
//	AccountSysMethod accountSysMethod = new AccountSysMethod(mockMvc);
//	UsernamePswdVo usernamePswdVo = new UsernamePswdVo();
//	usernamePswdVo.setUsername("testuser");
//	usernamePswdVo.setNewPassword("Ut1234567");
//	Assert.assertEquals(403,accountSysMethod.updateUserPassword(usernamePswdVo).getStatus());
//}
//
//	//异常——用户名为空
//	@Test
//	@WithMockUser(username = "testuser", password = "Admin12#$", authorities = {"platform_account_systemKey"})
//	public void updateUserPasswordEmpty1() throws Exception{
//		registerUser("testuser","Ut123456","18819495594","");
//		AccountSysMethod accountSysMethod = new AccountSysMethod(mockMvc);
//		UsernamePswdVo usernamePswdVo = new UsernamePswdVo();
//		usernamePswdVo.setUsername("");
//		usernamePswdVo.setNewPassword("Ut1234567");
//		thrown.expectMessage(String.format(ExceptionContants.PARAM_NOTNULL_EXCEPTION, "username或newPassword"));
//		accountSysMethod.updateUserPassword(usernamePswdVo);
//	}
//	//异常——密码为空
//	@Test
//	@WithMockUser(username = "testuser", password = "Admin12#$", authorities = {"platform_account_systemKey"})
//	public void updateUserPasswordEmpty2() throws Exception{
//		registerUser("testuser","Ut123456","18819495594","");
//		AccountSysMethod accountSysMethod = new AccountSysMethod(mockMvc);
//		UsernamePswdVo usernamePswdVo = new UsernamePswdVo();
//		usernamePswdVo.setUsername("testuser");
//		usernamePswdVo.setNewPassword("");
//		thrown.expectMessage(String.format(ExceptionContants.PARAM_NOTNULL_EXCEPTION, "username或newPassword"));
//	     accountSysMethod.updateUserPassword(usernamePswdVo);
//	}
//
//	//异常 -用户不存在
//	@Test
//	@WithMockUser(username = "testuser", password = "Admin12#$", authorities = {"platform_account_systemKey"})
//	public void updateUserPasswordNotExist() throws Exception{
//		AccountSysMethod accountSysMethod = new AccountSysMethod(mockMvc);
//		UsernamePswdVo usernamePswdVo = new UsernamePswdVo();
//		usernamePswdVo.setUsername("testuser");
//		usernamePswdVo.setNewPassword("Ut1234567");
//		 thrown.expectMessage(ExceptionContants.USERNAME_NOT_EXIST);
//		accountSysMethod.updateUserPassword(usernamePswdVo);
//	}
//
//
////	修改用户状态
////	正常
//@Test
//@WithMockUser(username = "testuser", password = "Admin12#$", authorities = {"platform_account_systemKey"})
//public void updateUserStatus() throws Exception{
//	registerUser("testuser","Ut123456","18819495594","");
//	AccountSysMethod accountSysMethod = new AccountSysMethod(mockMvc);
//	UserStatusVo userStatusVo = new UserStatusVo();
//	userStatusVo.setUsername("testuser");
//	userStatusVo.setActivated(true);
//	Assert.assertEquals(200,accountSysMethod.updateUserStatus(userStatusVo).getStatus());
//}
//	//	异常-无权限
//	@Test
//	@WithMockUser(username = "testuser", password = "Admin12#$")
//	public void updateUserStatusUnAuth() throws Exception{
//		registerUser("testuser","Ut123456","18819495594","");
//		AccountSysMethod accountSysMethod = new AccountSysMethod(mockMvc);
//		UserStatusVo userStatusVo = new UserStatusVo();
//		userStatusVo.setUsername("testuser");
//		userStatusVo.setActivated(true);
//		Assert.assertEquals(403,accountSysMethod.updateUserStatus(userStatusVo).getStatus());
//	}
//	//异常 -用户名为空
//	@Test
//	@WithMockUser(username = "testuser", password = "Admin12#$", authorities = {"platform_account_systemKey"})
//	public void updateUserStatusEmpty() throws Exception{
//		AccountSysMethod accountSysMethod = new AccountSysMethod(mockMvc);
//		UserStatusVo userStatusVo = new UserStatusVo();
//		userStatusVo.setUsername("");
//		userStatusVo.setActivated(true);
//		thrown.expectMessage(String.format(ExceptionContants.PARAM_NOTNULL_EXCEPTION,"username"));
//		accountSysMethod.updateUserStatus(userStatusVo);
//	}
//	//异常 -用户不存在
//	@Test
//	@WithMockUser(username = "testuser", password = "Admin12#$", authorities = {"platform_account_systemKey"})
//	public void updateUserStatusNotExist() throws Exception{
//		AccountSysMethod accountSysMethod = new AccountSysMethod(mockMvc);
//		UserStatusVo userStatusVo = new UserStatusVo();
//		userStatusVo.setUsername("testuser");
//		userStatusVo.setActivated(false);
//		thrown.expectMessage(ExceptionContants.USERNAME_NOT_EXIST);
//		accountSysMethod.updateUserStatus(userStatusVo);
//	}
//

	//批量获取用户信息
	@Test
	@WithMockUser(username = "testuser", password = "Admin12#$", authorities = {"platform_account_systemKey"})
	public void getUserInfoInBulk() throws Exception{
		registerUser("testuser","Ut123456","18819495594","");
		registerUser("platform_cook","Ut123456","","");
		AccountSysMethod accountSysMethod = new AccountSysMethod(mockMvc);
		UserListVo userListVo = new UserListVo();
		List<String> usernames = new ArrayList<>();
		usernames.add("testuser");
		usernames.add("platform_cook");
		userListVo.setUsernameList(usernames);
		Assert.assertEquals(200,accountSysMethod.getUserInfoInBulk(userListVo).getStatus());
	}

}
