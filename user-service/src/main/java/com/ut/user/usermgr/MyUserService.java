package com.ut.user.usermgr;


import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.ut.user.appmgr.AppDao;
import com.ut.user.appmgr.AppEntity;
import com.ut.user.appmgr.AppService;
import com.ut.user.authritymgr.*;
import com.ut.user.cache.CacheSingleService;
import com.ut.user.constants.ExceptionContants;
import com.ut.user.constants.UserConstants;
import com.ut.user.constants.UserException;
import com.ut.user.feign.FeignSms;
import com.ut.user.questionmgr.QuestionVo;
import com.ut.user.questionmgr.ResetQuestionDao;
import com.ut.user.questionmgr.ResetQuestionEntity;
import com.ut.user.utils.MD5Utils;
import com.ut.user.utils.OkHttpUtils;
import com.ut.user.utils.StringRandom;
import com.ut.user.vo.*;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.Response;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Pattern;

import static com.ut.user.constants.UserConstants.*;


/**
 * @auth: 陈佳攀
 * @Description:
 * @Date: Created in 13:48 2017-11-18
 */
@Service
@Slf4j
public class MyUserService {
	@Value("${oauth2.login.url}")
	private String oauth2LoginUrl;

	@Autowired
	private AppDao appDao;
	@Autowired
	private MyUserDao myUserDao;
	@Autowired
	private AuthorityDao authorityDao;
	@Autowired
	private AuthorityGroupDao authorityGroupDao;
	//编译器报错，无视。 因为这个Bean是在程序启动的时候注入的，编译器感知不到，所以报错。
	@Autowired
	private FeignSms feignSms;
	@Autowired
	PasswordEncoder passwordEncoder;
	@Autowired
	CacheSingleService cacheSingleService;
	@Autowired
	private MyUserExtendDao myUserExtendDao;
	@Autowired
	private ResetQuestionDao resetQuestionDao;
	@Autowired
	private MyUserRelateQuestionDao myUserRelateQuestionDao;
	@Autowired
	private ChildAuthorityDao childAuthorityDao;

	@Autowired
	private AppService appService;
	@Autowired
	private AuthorityGroupService authorityGroupService;
	@Autowired
	private MyUserRelateAppsService myUserRelateAppsService;
	@Autowired
	private MyUserRelateAuthoritiesService myUserRelateAuthoritiesService;
	@Autowired
	private MyUserRelateAuthorityGroupsService myUserRelateAuthorityGroupsService;
	@Autowired
	private AuthorityGroupRelateAuthoritiesService authorityGroupRelateAuthoritiesService;

	/**
	 * 注册 -- 后续注册使用oauth的接口
	 * 新注册的用户没有任何权限，也不能是任何账号的子账号
	 */
	public void regUser(MyUserEntity user) throws Exception {
		user.setParentUser(null);
		if (myUserDao.findByUsername(user.getUsername()) != null) {
			throw new Exception("用户名已存在！");
		}
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		if (myUserDao.save(user) == null)
			throw new Exception("保存用户失败！");
	}

	/**
	 * 登录用户信息
	 */
	public UserVo getLoginUser() {
		MyUserEntity user = getSelf();
		log.info("当前用户是： " + user.getUsername());
		return getBasicVo(user, true);
	}

	/**
	 * 用户名查找用户--支持username/loginName/mobile搜索
	 */
	public UserVo getUser(String arg) throws Exception {
		MyUserEntity user = getUserByKeyword(arg);
		return getBasicVo(user, false);
	}

	/**
	 * 获取用户扩展信息username/loginName/mobile搜索
	 */
	public UserExtendVO getUserExtend(String username) throws Exception {
		MyUserEntity user = getUserByKeyword(username);
		MyUserExtendEntity extend = myUserExtendDao.findByUsername(user.getUsername());
		return getUserExtend(user, extend);
	}

	/**
	 * 修改用户密码
	 */
	public boolean changePswd(String oldPwd, String newPwd) throws Exception {
		MyUserEntity self = getSelf();
		if (!newPwd.matches(REGEX_PWD))
			throw new Exception(ExceptionContants.PWD_ERR_MSG);
		if (passwordEncoder.matches(oldPwd, self.getPassword())) {
			self.setPassword(passwordEncoder.encode(newPwd));
			return (null != myUserDao.saveAndFlush(self));
		}
		return false;
	}

	/**
	 * 重置用户密码--密保方式
	 */
	public boolean resetPasswordByQuestion(String questionId, String answer, String newPwd) throws Exception {
		MyUserEntity user = getSelf();
		checkQuestion(questionId, answer);
		if (!newPwd.matches(REGEX_PWD))
			throw new Exception(ExceptionContants.PWD_ERR_MSG);
		user.setPassword(passwordEncoder.encode(newPwd));
		return (null != myUserDao.saveAndFlush(user));
	}

	/**
	 * 重置用户密码--短信验证码方式；发送验证码—校验检验码接口—该接口
	 */
	public boolean resetPasswordByMobile(String messageId, String smsCode, String newPwd) throws Exception {
		if (Strings.isNullOrEmpty(messageId))
			throw new UserException(String.format(ExceptionContants.PARAM_NOTNULL_EXCEPTION, "messageId"));
		if (Strings.isNullOrEmpty(smsCode))
			throw new UserException(String.format(ExceptionContants.PARAM_NOTNULL_EXCEPTION, "smsCode"));
		if (Strings.isNullOrEmpty(newPwd))
			throw new UserException(String.format(ExceptionContants.PARAM_NOTNULL_EXCEPTION, "newPwd"));
		MyUserEntity self = getSelf();

		//校验验证码是否正确
		String codeStatusKey = MD5Utils.getMD5(messageId + smsCode);//校验验证码是否校验正确--关键字
		String result = cacheSingleService.get(codeStatusKey);
		if (Strings.isNullOrEmpty(result))
			throw new Exception("验证码已过期，重置用户密码失败！");
		if (!"true".equals(result))
			throw new Exception("验证码校验失败，重置用户密码失败！");

		//校验密码
		if (!newPwd.matches(REGEX_PWD))
			throw new Exception(ExceptionContants.PWD_ERR_MSG);
		self.setPassword(passwordEncoder.encode(newPwd));
		return (null != myUserDao.saveAndFlush(self));
	}

	/**
	 * 修改用户昵称
	 */
	public boolean changeNickname(String nickname) throws Exception {
		if (Strings.isNullOrEmpty(nickname))
			throw new Exception("nickname 不能为空");
		nickname = nickname.trim();
		if (nickname.length() > UserConstants.FIFTY_LENGTH)
			throw new Exception("昵称不能超过50个字符");
		MyUserEntity user = getSelf();
		user.setNickname(nickname);
		return (null != myUserDao.saveAndFlush(user));
	}

	/**
	 * 新增用户扩展信息
	 */
	public boolean newUserExtend(MyUserExtendEntity myUserExtendEntity) throws Exception {
		MyUserEntity user = getSelf();
		MyUserExtendEntity extend = myUserExtendDao.findByUsername(user.getUsername());
		if (null == extend) {
			extend = new MyUserExtendEntity();
			extend.setUsername(user.getUsername());
		}

		if (null != myUserExtendEntity.getBirthday())
			extend.setBirthday(myUserExtendEntity.getBirthday());
		if (null != myUserExtendEntity.getLogo())
			extend.setLogo(myUserExtendEntity.getLogo());
		if (null != myUserExtendEntity.getSex()) {
			if (!("0".equals(myUserExtendEntity.getSex()) || "1".equals(myUserExtendEntity.getSex())))
				throw new Exception("请输入数字 0 或数字 1 !");
			extend.setSex(myUserExtendEntity.getSex());
		}

		if (null != myUserExtendEntity.getIntro())
			extend.setIntro(myUserExtendEntity.getIntro());
		if (null != myUserExtendEntity.getProvince()) {
			if (!isInteger(myUserExtendEntity.getProvince()))
				throw new Exception("应输入省的代号!");
			extend.setProvince(myUserExtendEntity.getProvince());
		}
		if (null != myUserExtendEntity.getCity()) {
			if (!isInteger(myUserExtendEntity.getCity()))
				throw new Exception("应输入市的代号!");
			extend.setCity(myUserExtendEntity.getCity());
		}
		if (null != myUserExtendEntity.getArea()) {
			if (!isInteger(myUserExtendEntity.getArea()))
				throw new Exception("应输入区的代号!");
			extend.setArea(myUserExtendEntity.getArea());
		}
		if (null != myUserExtendEntity.getAddress())
			extend.setAddress(myUserExtendEntity.getAddress());
		return (null != myUserExtendDao.saveAndFlush(extend));
	}

	/**
	 * 获取密保问题列表
	 */
	public List<ResetQuestionEntity> questionList() {
		return resetQuestionDao.findAll();
	}

	/**
	 * 获取用户已设置的密保问题
	 */
	public List<QuestionVo> myQuestion() throws Exception {
		List<QuestionVo> questionVos = new ArrayList<QuestionVo>();
		String userName = getLoginUsername();
		List<MyUserRelateQuestion> myUserRelateQuestions = myUserRelateQuestionDao.findAllByUserName(userName);
		if (myUserRelateQuestions == null)
			throw new Exception("未设置密保问题！");
		for (MyUserRelateQuestion myUserRelateQuestion : myUserRelateQuestions) {
			ResetQuestionEntity question = resetQuestionDao.getOne(Long.parseLong(myUserRelateQuestion.questionId));
			QuestionVo vo = new QuestionVo();
			BeanUtils.copyProperties(question, vo);
			questionVos.add(vo);
		}
		return questionVos;
	}

	/**
	 * 设置密保答案
	 */
	public boolean questionAnswer(String questionId, String answer) throws Exception {
		if (Strings.isNullOrEmpty(questionId) || Strings.isNullOrEmpty(answer))
			throw new Exception("questionId 或 answer 不能为空！");
		if (!resetQuestionDao.findById(Long.parseLong(questionId)).isPresent())
			throw new Exception("密保问题不存在！");
		if (answer.length() > FIFTY_LENGTH)
			throw new Exception("答案长度不能超过50！");
		String userName = getLoginUsername();
		List<MyUserRelateQuestion> myUserRelateQuestions = myUserRelateQuestionDao.findAllByUserName(userName);
		if (null != myUserRelateQuestions && myUserRelateQuestions.size() > 0) {
			for (MyUserRelateQuestion relate : myUserRelateQuestions) {
				if (questionId.equals(relate.getQuestionId()))
					throw new Exception("您已设置过该密保答案");
			}
		}
		MyUserRelateQuestion myUserRelateQuestion = new MyUserRelateQuestion();
		myUserRelateQuestion.setUserName(userName);
		myUserRelateQuestion.setQuestionId(questionId);
		myUserRelateQuestion.setAnswer(MD5Utils.getMD5(answer));

		return (null != myUserRelateQuestionDao.save(myUserRelateQuestion));

	}

	/**
	 * 校验密保答案
	 * <p>
	 * 1、检查用户是否已设置密保，有则继续
	 * 2、校验请求密保是否用户设置的密保，是则继续
	 * 3、校验密保答案，正确则继续
	 * 4、以 用户名+密保id 作md5串作为key，redis缓存结果
	 * 5、响应true,false
	 */
	public boolean checkQuestion(String questionId, String answer) throws Exception {
		if (Strings.isNullOrEmpty(questionId) || Strings.isNullOrEmpty(answer))
			throw new Exception(String.format(ExceptionContants.PARAM_NOTNULL_EXCEPTION, questionId + " 或 " + answer));
		String userName = getLoginUsername();
		MyUserRelateQuestion myUserRelateQuestion = myUserRelateQuestionDao.findByUserNameAndQuestionId(userName, questionId);
		if (myUserRelateQuestion == null)
			throw new Exception(ExceptionContants.NO_QUESTION_EXCEPTION);
		if (!MD5Utils.getMD5(answer).equals(myUserRelateQuestion.answer))
			throw new Exception(ExceptionContants.ANSWER_ERROR_EXCEPTION);
		//redis缓存
		String redisKey = MD5Utils.getMD5(userName + questionId);
		cacheSingleService.setex(redisKey, UserConstants.TRUE);
		return true;
	}

	/**
	 * 修改密保问题
	 */
	public boolean resetQuestionAnswer(String oldQuestionId, String newQuestionId, String answer) throws Exception {
		if (Strings.isNullOrEmpty(oldQuestionId) || Strings.isNullOrEmpty(newQuestionId) || Strings.isNullOrEmpty(answer))
			throw new Exception(String.format(ExceptionContants.PARAM_NOTNULL_EXCEPTION, oldQuestionId + " 或 " + newQuestionId + " 或 " + answer));
		if (oldQuestionId.equals(newQuestionId))
			throw new Exception(ExceptionContants.QUESTION_SAME);
		if(answer.length()>UserConstants.FIFTY_LENGTH)
			throw new Exception("密保答案长度不能超过50！");
		String userName = getLoginUsername();
		MyUserRelateQuestion myUserRelateQuestion = myUserRelateQuestionDao.findByUserNameAndQuestionId(userName, oldQuestionId);
		if (null == myUserRelateQuestion)
			throw new Exception(ExceptionContants.NO_QUESTION_EXCEPTION);
		String redisKey = MD5Utils.getMD5(userName + oldQuestionId);
		String redisValue = cacheSingleService.get(redisKey);
		if (redisValue.equals(UserConstants.TRUE)) {
			if (!resetQuestionDao.findById(Long.parseLong(newQuestionId)).isPresent())
				throw new Exception(ExceptionContants.QUESTION_NOT_EXIST);
			myUserRelateQuestion.setQuestionId(newQuestionId);
			myUserRelateQuestion.setAnswer(MD5Utils.getMD5(answer));
			return (null != myUserRelateQuestionDao.save(myUserRelateQuestion));
		}
		return false;
	}

	/**
	 * 绑定电话号码
	 */
	public boolean bindMobile(String appPrefix, String messageId, String smsCode, String phoneNo) throws Exception {
		if (Strings.isNullOrEmpty(phoneNo) || Strings.isNullOrEmpty(appPrefix) || Strings.isNullOrEmpty(messageId) || Strings.isNullOrEmpty(smsCode))
			throw new UserException(String.format(ExceptionContants.PARAM_NOTNULL_EXCEPTION, "phoneNo、appPrefix、messageId、smsCode"));
		String just_phoneNum = getSymbolRemoveNo(phoneNo);
		if (!checkIsMobile(just_phoneNum))
			throw new Exception("输入的手机号码不合法");
		MyUserEntity user = getSelf();
		if (!Strings.isNullOrEmpty(user.getMobile()))
			throw new UserException(ExceptionContants.HAS_MOBILE_EXCEPTION);
		if (phoneNo.equals(user.getMobile()))
			throw new UserException(ExceptionContants.MOBILE_HAS_BIND_EXCEPTION);
		if (null == myUserDao.findByMobileAndAccountSystemKey(phoneNo, user.getAccountSystemKey())) {
			String redisKey = MD5Utils.getMD5(phoneNo);
			if (!messageId.equals(cacheSingleService.get(redisKey)))
				throw new Exception("绑定手机号与获取验证码手机号不一致！");
			if (feignSms.compareVerifyCode(appPrefix, messageId, smsCode)) {
				user.setMobile(phoneNo);
				return (null != myUserDao.save(user));
			} else {
				throw new RuntimeException("验证码错误!");
			}
		} else {
			throw new UserException(String.format(ExceptionContants.MOBILE_USED_EXCEPTION, phoneNo));
		}
	}

	/**
	 * 绑定电话号码-待弃用
	 */
	@Deprecated
	public boolean bindPhoneNo(String phoneNo) throws Exception {
		if (Strings.isNullOrEmpty(phoneNo))
			throw new Exception("phoneNo不能为空！");
		if (null != myUserDao.findByMobileAndAccountSystemKey(phoneNo,getSelf().getAccountSystemKey()))
			throw new Exception(phoneNo + "已被其他账户绑定！");
		String phoneNoWithoutSymbol = getSymbolRemoveNo(phoneNo);
		if (!checkIsMobile(phoneNo) && !checkIsMobile(phoneNoWithoutSymbol))
			throw new Exception("手机号码不正确");
		MyUserEntity user = getSelf();
		user.setMobile(phoneNo);
		return (null != myUserDao.save(user));
	}

	/**
	 * 更换已绑定的手机号
	 */
	public boolean changeMobile(SmsCodeVo smsCodeVo) throws Exception {
		MyUserEntity loginUser = null;
		if (null != smsCodeVo) {
			if (Strings.isNullOrEmpty(smsCodeVo.getOldSmsCode().getMessageId()))
				throw new Exception("请先获取旧手机的验证码！");
			if (Strings.isNullOrEmpty(smsCodeVo.getNewSmsCode().getMessageId()))
				throw new Exception("请先获取新手机的验证码！");
			if (Strings.isNullOrEmpty(smsCodeVo.getOldSmsCode().getPhoneNo()) || Strings.isNullOrEmpty(smsCodeVo.getOldSmsCode().getAppPrefix()) || Strings.isNullOrEmpty(smsCodeVo.getOldSmsCode().getSmsCode()))
				throw new Exception("旧手机号相关参数不能为空！");
			if (Strings.isNullOrEmpty(smsCodeVo.getNewSmsCode().getPhoneNo()) || Strings.isNullOrEmpty(smsCodeVo.getNewSmsCode().getAppPrefix()) || Strings.isNullOrEmpty(smsCodeVo.getNewSmsCode().getSmsCode()))
				throw new Exception("新手机号相关参数不能为空！");
			String oldMobile = getSymbolRemoveNo(smsCodeVo.getOldSmsCode().getPhoneNo());
			String newMobile = getSymbolRemoveNo(smsCodeVo.getNewSmsCode().getPhoneNo());
			if (!checkIsMobile(oldMobile) || !checkIsMobile(newMobile))
				throw new Exception("输入的手机号不正确");
			loginUser = getSelf();
			if (!loginUser.getMobile().equals(smsCodeVo.getOldSmsCode().getPhoneNo()))
				throw new Exception("旧手机号与当前用户绑定的手机号不一致！");
			if (null != myUserDao.findByMobileAndAccountSystemKey(smsCodeVo.getNewSmsCode().getPhoneNo(),loginUser.getAccountSystemKey()))
				throw new Exception(newMobile + " 已被绑定使用！");
			String redisKey = MD5Utils.getMD5(smsCodeVo.getOldSmsCode().getMessageId() + smsCodeVo.getOldSmsCode().getSmsCode());
			String redisValue = cacheSingleService.get(redisKey);
			if (Strings.isNullOrEmpty(redisValue))
				throw new Exception("旧手机验证码输入错误！");
			if ((redisValue.equals("true") && feignSms.compareVerifyCode(smsCodeVo.getNewSmsCode().getAppPrefix(), smsCodeVo.getNewSmsCode().getMessageId(), smsCodeVo.getNewSmsCode().getSmsCode()))) {
				loginUser.setMobile(smsCodeVo.getNewSmsCode().getPhoneNo());
				return (null != myUserDao.save(loginUser));
			}
		}
		return false;
	}

	public void setLoginName(String loginName) throws Exception {
		if (Strings.isNullOrEmpty(loginName))
			throw new Exception(String.format(ExceptionContants.PARAM_NOTNULL_EXCEPTION, "loginName"));
		if (!loginName.matches(UserConstants.REGEX_USERNAME))
			throw new Exception(ExceptionContants.USERNAME_ERR_MSG);
		MyUserEntity curUser = getSelf();
		if (curUser.getLoginName() != null && curUser.getLoginName().length() != 0) {
			throw new Exception("您已经设置过登录名！");
		}
		if (null != myUserDao.findByLoginNameAndAccountSystemKey(loginName,curUser.getAccountSystemKey()))
			throw new Exception(ExceptionContants.LOGINNAME_ALREADY_EXIST);
		curUser.setLoginName(loginName);
		myUserDao.save(curUser);
	}

	/**
	 * 设置密码
	 */
	public boolean setPassword(String password, String messageId, String smsCode) throws Exception {
		if (Strings.isNullOrEmpty(password))
			throw new Exception(String.format(ExceptionContants.PARAM_NOTNULL_EXCEPTION, "password"));
		if (Strings.isNullOrEmpty(messageId))
			throw new Exception(String.format(ExceptionContants.PARAM_NOTNULL_EXCEPTION, "messageId"));
		if (Strings.isNullOrEmpty(smsCode))
			throw new Exception(String.format(ExceptionContants.PARAM_NOTNULL_EXCEPTION, "smsCode"));
		if (!password.matches(UserConstants.REGEX_PWD))
			throw new Exception(ExceptionContants.PWD_ERR_MSG);

		//校验验证码是否属于当前手机号
		MyUserEntity curUser = getSelf();
		if (null == curUser.getMobile())
			throw new Exception("请先绑定手机号！");
		String checkSameMobileKey = MD5Utils.getMD5(curUser.getMobile());//校验是否同一手机号--关键字
		String cacheMessageId = cacheSingleService.get(checkSameMobileKey);
		if (null == cacheMessageId)
			throw new Exception("请先获取验证码！");
		if (!messageId.equals(cacheMessageId))
			throw new Exception("非法验证码！");

		//校验验证码是否正确
		String codeStatusKey = MD5Utils.getMD5(messageId + smsCode);//校验验证码是否校验正确--关键字
		String result = cacheSingleService.get(codeStatusKey);
		if (Strings.isNullOrEmpty(result))
			throw new Exception("验证码已过期，设置登录名密码失败！");
		if (!"true".equals(result))
			throw new Exception("验证码校验失败，设置登录名密码失败！");

		MyUserEntity user = getSelf();
		if (!Strings.isNullOrEmpty(user.getPassword()))
			throw new Exception(ExceptionContants.PASSWORD_ALREADY_EXIST);
		user.setPassword(passwordEncoder.encode(password));
		return (null != myUserDao.save(user));
	}

	/**
	 * 从认证信息中获取当前登录用户的名称
	 */
	public String getLoginUsername() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username;
		if (principal instanceof UserDetails) {
			username = ((UserDetails) principal).getUsername();
		} else {
			username = principal.toString();
		}
		return username;
	}

	/**
	 * 检查字符串是否电话号码
	 */
	public boolean checkIsMobile(String args) {
		if (args.matches(REGEX_MOBILE))
			return true;
		return false;
	}

	/**
	 * 针对物联应用传入的phoneNO，“key#18727344793”
	 *
	 * @return
	 */
	public String getSymbolRemoveNo(String args) {
		if (args.indexOf("#") != -1) {
			args = args.substring(args.indexOf("#") + 1);
		}
		return args;
	}

	public MyUserEntity getSelf() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			return null;
		}
		Object principal = authentication.getPrincipal();
		if (principal == null || principal.equals("anonymousUser")) {
			return null;
		}
		String username;
		if (principal instanceof UserDetails) {
			username = ((UserDetails) principal).getUsername();
		} else {
			username = principal.toString();
		}
		MyUserEntity user = myUserDao.findByUsername(username);
		return user;
	}

	/**
	 * 获取当前用户的所有权限
	 */
	public Set<String> getLoginUserAuthorities() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>) auth.getAuthorities();
		Set<String> roles = new HashSet<>();
		authorities.forEach((GrantedAuthority g) -> roles.add(g.getAuthority()));
		return roles;
	}

	/**
	 * 通过username 或者loginName 查找用户
	 */
	public MyUserEntity getByUsernameOrLoginName(String arg) throws Exception {
		MyUserEntity user = myUserDao.findByUsername(arg);
		if (null == user) {
			user = myUserDao.findByLoginNameAndAccountSystemKey(arg,DEFAULT_SYSTEM_KEY);
			if (user == null)
				throw new Exception(String.format(ExceptionContants.OBJECT_NOT_EXIST_EXCEPTION, arg));
		}
		return user;
	}

	/**
	 * 封装响应对象
	 */
	public UserVo getBasicVo(MyUserEntity user, boolean encryMobile) {
		UserVo vo = new UserVo();
		if (!Strings.isNullOrEmpty(user.getMobile())) {
			String tel = "";
			if (encryMobile) {
				tel = user.getMobile();
			} else {
				tel = user.getMobile();
			}
			vo.setMobile(tel);
		}
		if (null != user.getUsername())
			vo.setUsername(user.getUsername());
		if (null != user.getLoginName())
			vo.setLoginName(user.getLoginName());
		if (null != user.getNickname())
			vo.setNickname(user.getNickname());
		if (null != user.getEmail())
			vo.setEmail(user.getEmail());

		MyUserExtendEntity extend = myUserExtendDao.findByUsername(user.getUsername());
		if (null != extend) {
			if (!Strings.isNullOrEmpty(extend.getLogo())){
				vo.setLogo(extend.getLogo());
			}
			if (!Strings.isNullOrEmpty(extend.getSex())){
				vo.setSex(extend.getSex());
			}
		}
		if (null == user.getPassword() || user.getPassword().length() == 0) {
			vo.setPasswordFlag(false);
		} else {
			vo.setPasswordFlag(true);
		}

		return vo;
	}

	/**
	 * 封装用户扩展信息
	 */
	public UserExtendVO getUserExtend(MyUserEntity user, MyUserExtendEntity extend) {
		UserExtendVO vo = new UserExtendVO();

		if (extend == null) {
			vo.setUsername(user.getUsername());
			return vo;
		}
		vo.setUsername(extend.getUsername());
		if (null != extend.getSex()) {
			vo.setSex(extend.getSex());
		}
		if (null != extend.getBirthday()) {
			vo.setBirthday(extend.getBirthday());
		}
		if (null != extend.getProvince()) {
			vo.setProvince(extend.getProvince());
		}
		if (null != extend.getCity()) {
			vo.setCity(extend.getCity());
		}
		if (null != extend.getArea()) {
			vo.setArea(extend.getArea());
		}
		if (null != extend.getAddress()) {
			vo.setAddress(extend.getAddress());
		}
		if (null != extend.getIntro()) {
			vo.setIntro(extend.getIntro());
		}
		if (null != extend.getLogo()) {
			vo.setLogo(extend.getLogo());
		}
		return vo;
	}

	public static boolean isInteger(String str) {
		Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
		return pattern.matcher(str).matches();
	}

	/**
	 * 根据username/loginname/mobile查找用户
	 *
	 * @param keyword
	 * @return
	 */
	public MyUserEntity getUserByKeyword(String keyword) throws Exception {
		if (Strings.isNullOrEmpty(keyword))
			throw new Exception("参数不能为空！");
		MyUserEntity user = null;
		if (checkIsMobile(keyword)) {
			user = myUserDao.findByMobileAndAccountSystemKey(keyword,DEFAULT_SYSTEM_KEY);
		} else {
			user = myUserDao.findByUsername(keyword);
			if (null == user)
				user = myUserDao.findByLoginNameAndAccountSystemKey(keyword,DEFAULT_SYSTEM_KEY);
		}
		if (user == null)
			throw new Exception("该用户不存在！");
		return user;
	}

	public MyUserEntity updateUser(MyUserEntity myUserEntity) {
		return myUserDao.save(myUserEntity);
	}

	/**
	 * 检查是否平台管理员
	 */
	public boolean isPlatFormAdmin() {
		Set<String> myAuthorities = getLoginUserAuthorities();
		//平台超级管理员--最高权限
		if (myAuthorities.contains(UserConstants.PLATFORM_ADMIN))
			return true;
		return false;
	}

	/**
	 * 获取指定用户的子账户
	 */
	public Page<MyUserEntity> pageChildUser(String username, Pageable pageable) {
		return myUserDao.findAllByParentUser(username, pageable);
	}

	@Transactional
	@PreAuthorize("hasAuthority('platform_user_batchRegister')")
	public boolean batchRegister(List<BatchUserVo> batchUserVos) throws Exception {
		if (batchUserVos.size() == 0)
			throw new Exception("输入用户名和密码为空");
		for (BatchUserVo batchUserVo : batchUserVos) {
			MyUserEntity myUserEntity = new MyUserEntity();
			if (Strings.isNullOrEmpty(batchUserVo.getUsername()))
				throw new Exception("用户名不能为空");
			if (Strings.isNullOrEmpty(batchUserVo.getPassword()))
				throw new Exception("密码不能为空");
			if (!batchUserVo.getUsername().matches(REGEX_USERNAME))
				throw new Exception(batchUserVo.getUsername() + "用户名支持数字字母下划线，且不小于8位");
			if (!batchUserVo.getPassword().matches(REGEX_PWD))
				throw new Exception(batchUserVo.getPassword() + "密码必须同时包含数字、大小写字母;特殊符号仅支持#$&");
			myUserEntity.setUsername(batchUserVo.getUsername());
			myUserEntity.setLoginName(batchUserVo.getUsername());
			if(Strings.isNullOrEmpty(getSelf().getAccountSystemKey())){
				myUserEntity.setAccountSystemKey(DEFAULT_SYSTEM_KEY);
			} else {
				myUserEntity.setAccountSystemKey(getSelf().getAccountSystemKey());
			}
			myUserEntity.setPassword(passwordEncoder.encode(batchUserVo.getPassword()));
			if (null == myUserDao.save(myUserEntity))
				return false;
		}
		return true;
	}

	public BindStatuVO getBindStatus() throws Exception {
		MyUserEntity user = getSelf();
		if (user == null) {
			throw new Exception("无法获取当前用户信息，可能该用户为匿名用户，或是认证信息为空");
		}
		return new BindStatuVO(user.getWechat(), user.getWeibo(), user.getQq());
	}

	/**
	 * 重置用户密码
	 */
	@PreAuthorize("hasAuthority('platform_admin')")
	public String resetPassword(String username) throws Exception {
		if (Strings.isNullOrEmpty(username))
			throw new Exception(String.format(ExceptionContants.PARAM_NOTNULL_EXCEPTION, "username"));
		MyUserEntity user = getByUsernameOrLoginName(username);
		if (user == null)
			throw new Exception(username + " 用户不存在！");
		String randomPwd = StringRandom.getRandom();
		user.setPassword(passwordEncoder.encode(randomPwd));

		if (null == myUserDao.save(user))
			throw new Exception("重置密码失败！");
		return randomPwd;
	}


	/**
	 * 修改子用户状态
	 */
	public boolean changeUserStatus(String username, boolean activated) throws Exception {
		String curUsername = getLoginUsername();
		MyUserEntity user = getUserByKeyword(username);
		if (!curUsername.equals(user.getParentUser()))
			throw new Exception(username + "不是您的子账户，无权操作！");
		user.setActivated(activated);
		return (null != myUserDao.save(user));
	}


//    /**
//     * 修改子用户密码
//     */
//    public void changeUserPassword(UsernamePswdVo usernamePswdVo)throws Exception{
//        if(Strings.isNullOrEmpty(usernamePswdVo.getUsername())||Strings.isNullOrEmpty(usernamePswdVo.getNewPassword()))
//            throw  new Exception("用户名或者新密码不能为空！");
//        String username= usernamePswdVo.getUsername().trim();
//        String password = usernamePswdVo.getNewPassword().trim();
//        String curUsername = getLoginUsername();
//        MyUserEntity user = getUserByKeyword(username);
//        if(!curUsername.equals(user.getParentUser()))
//            throw new Exception(username + "不是您的子账户，无权操作！");
//        user.setPassword(password);
//        myUserDao.save(user);
//    }

	/**
	 * =====================================以下为oauth 服务feign调用专用方法=====================================
	 */

	public void initUser(String username, String password, String nickName, String mobile, String email) {
		if (myUserDao.findByUsername(username) == null) {
			MyUserEntity user = new MyUserEntity();
			user.setUsername(username);
			user.setLoginName(username);
			user.setAccountSystemKey(UserConstants.DEFAULT_SYSTEM_KEY);
			if (!Strings.isNullOrEmpty(password))
				user.setPassword(passwordEncoder.encode(password));
			if (!Strings.isNullOrEmpty(mobile))
				user.setMobile(mobile);
			if (!Strings.isNullOrEmpty(email))
				user.setEmail(email);
			if (!Strings.isNullOrEmpty(nickName))
				user.setNickname(nickName);
			myUserDao.save(user);
		}
	}

	/**
	 * 用户名密码注册
	 */
	public MyUserEntity usernameRegister(String keyword, String password) {
		MyUserEntity user = new MyUserEntity();
		user.setUsername(keyword);
		user.setPassword(password);//入参已经过passwordEncoder.encode(password)
		user.setLoginName(keyword);
		user.setAccountSystemKey(DEFAULT_SYSTEM_KEY);
		return myUserDao.save(user);
	}

	/**
	 * 手机渠道注册
	 */
	public MyUserEntity mobileRegister(String mobile) {
		MyUserEntity user = new MyUserEntity();
		String username = getRandomUsername();
		user.setUsername(username);
		user.setLoginName(username);
		user.setMobile(mobile);
		user.setAccountSystemKey(DEFAULT_SYSTEM_KEY);
		myUserDao.save(user);
		return user;
	}

	public String getRandomUsername() {
		String randomName = getStringRandom(10);
		while (true) {
			if (null != myUserDao.findByUsername(randomName)) {
				getRandomUsername();
			} else {
				break;
			}
		}
		return randomName;
	}

	//生成随机用户名，数字和字母组成,
	public String getStringRandom(int length) {
		String val = "";
		Random random = new Random();
		//参数length，表示生成几位随机数
		for (int i = 0; i < length; i++) {
			String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
			//输出字母还是数字
			if ("char".equalsIgnoreCase(charOrNum)) {
				//输出是大写字母还是小写字母
				int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
				val += (char) (random.nextInt(26) + temp);
			} else if ("num".equalsIgnoreCase(charOrNum)) {
				val += String.valueOf(random.nextInt(10));
			}
		}
		return val;
	}

	/**
	 * 通过手机号和体系获取用户信息
	 */
	public MyUserEntity getUserByMobile(String mobile) {
		return myUserDao.findByMobileAndAccountSystemKey(mobile, DEFAULT_SYSTEM_KEY);
	}

	/**
	 * 通过用户名和体系获取用户信息
	 */
	public MyUserEntity getUserByUsername(String username) {
		return myUserDao.findByLoginNameAndAccountSystemKey(username,DEFAULT_SYSTEM_KEY);
	}

	/**
	 * 通过用户主键获取用户信息
	 */
	public MyUserEntity getUserByUid(String userId) throws UsernameNotFoundException {
		MyUserEntity user = myUserDao.findByUsername(userId);
		return user;
	}


	/**
	 * 通过用户名、登录名获取用户信息
	 */
	public MyUserEntity getUserByUsernameOrLoginName(String name) {
		MyUserEntity user = myUserDao.findByUsername(name);
		if (null == user)
			user = myUserDao.findByLoginNameAndAccountSystemKey(name,DEFAULT_SYSTEM_KEY);
		return user;
	}

	/**
	 * 获取已经分配给用户的所有应用的功能、角色权限
	 */
	public List<String> getGrantedAuthorities(String userName) throws Exception {
//        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
		List<String> grantedAuthorities = new ArrayList<>();
		//获取用户以授权的app 功能、角色集
		//若ChildAuthorityEntity允许使用父账户权限，则取父账户权限
		ChildAuthorityEntity childAuthorityEntity = childAuthorityDao.findByUsername(userName);
		if (null != childAuthorityEntity && childAuthorityEntity.isAuthUp() && null != childAuthorityEntity.getParentUser()) {
			userName = childAuthorityEntity.getParentUser();
			if (null == myUserDao.findByUsername(userName))
				throw new Exception(userName + "账户不存在！");
		}
		getUserAuths(userName, grantedAuthorities);
		return grantedAuthorities;
	}

	/**
	 * 获取权限集
	 */
	private void getUserAuths(String userName, List<String> grantedAuthorities) {
		List<MyUserRelateApps> apps = myUserRelateAppsService.getAppsRelateByUsername(userName);
		for (MyUserRelateApps userApp : apps) {
			if (appService.checkAppAlive(userApp.getAppKey())) {//检查应用是否启用
				//获取用户绑定的功能
				List<MyUserRelateAuthorities> userAuths = myUserRelateAuthoritiesService.getUserRelateAuthsByApp(userName, userApp.getAppKey());
				if (null != userAuths && userAuths.size() > 0) {
					for (MyUserRelateAuthorities userAuth : userAuths) {
//                        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(userAuth.getAuthorityKey());
						grantedAuthorities.add(userAuth.getAuthorityKey());
					}
				}
				//获取用户绑定的角色，其子角色及其绑定的功能
				getMyRelateAuthGroups(userApp.getAppKey(), userName, grantedAuthorities);
			}
		}

		//获取开发者所创建应用的 功能、角色集
		List<AppEntity> myApps = appDao.findAllByDeveloperAndStatus(userName, true);
		if (null != myApps && myApps.size() > 0) {
			for (AppEntity app : myApps) {
				List<AuthorityEntity> appAuths = authorityDao.findAllByAppKey(app.getAppKey());
				if (null != appAuths && appAuths.size() > 0) {
					for (AuthorityEntity auth : appAuths) {
						grantedAuthorities.add(auth.getAuthorityKey());
					}
				}

				List<AuthorityGroupEntity> appAuthgroups = authorityGroupDao.findAllByAppKey(app.getAppKey());
				if (null != appAuthgroups && appAuthgroups.size() > 0) {
					for (AuthorityGroupEntity authGroup : appAuthgroups) {
						grantedAuthorities.add(authGroup.getAuthorityGroupKey());
					}
				}
			}

		}
	}

	/**
	 * 获取用户绑定的角色，其子角色及其绑定的功能
	 */
	public void getMyRelateAuthGroups(String appKey, String username, List<String> grantedAuthorities) {
		List<MyUserRelateAuthorityGroups> relateGroups = myUserRelateAuthorityGroupsService.getUserRelateAuthGroupsByApp(username, appKey);
		if (null != relateGroups && relateGroups.size() > 0) {
			for (MyUserRelateAuthorityGroups relateGroup : relateGroups) {
				//自身绑定的角色
//                GrantedAuthority grantedAuthGroup = new SimpleGrantedAuthority(relateGroup.getAuthorityGroupKey());
				grantedAuthorities.add(relateGroup.getAuthorityGroupKey());
				//自身角色所绑定的功能
				List<AuthorityGroupRelateAuthorities> groupRelateAuths = authorityGroupRelateAuthoritiesService.getListByAppKeyAndAuthGroupKey(appKey, relateGroup.getAuthorityGroupKey());
				for (AuthorityGroupRelateAuthorities relate : groupRelateAuths) {
//                    GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(relate.getAuthorityKey());
					grantedAuthorities.add(relate.getAuthorityKey());
				}
				//子角色及其绑定的功能
				getChildrenAuthGroup(appKey, relateGroup.getAuthorityGroupKey(), grantedAuthorities);
			}
		}
	}

	/**
	 * 获取子角色组
	 */
	private void getChildrenAuthGroup(String appKey, String authGroupKey, List<String> grantedAuthorities) {
		List<AuthorityGroupEntity> children = authorityGroupService.getListAuthGroupByParent(appKey, authGroupKey);
		if (children != null && children.size() > 0) {
			for (AuthorityGroupEntity child : children) {
//                GrantedAuthority grantedAuthGroup = new SimpleGrantedAuthority(child.getAuthorityGroupKey());
				grantedAuthorities.add(child.getAuthorityGroupKey());
				getChildrenAuthGroup(appKey, child.getAuthorityGroupKey(), grantedAuthorities);

				//角色组绑定的角色
				List<AuthorityGroupRelateAuthorities> groupRelateAuths = authorityGroupRelateAuthoritiesService.getListByAppKeyAndAuthGroupKey(appKey, child.getAuthorityGroupKey());
				for (AuthorityGroupRelateAuthorities relate : groupRelateAuths) {
//                    GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(relate.getAuthorityKey());
					grantedAuthorities.add(relate.getAuthorityKey());
				}
			}
		}
	}

	/**
	 * 创建指定账户的子账户
	 */
	@Transactional
	@PreAuthorize("hasAuthority('platform_user_createSpecifiedChild')")
	public String createSpecifiedChild(String username) throws Exception {
		MyUserEntity pUser = myUserDao.findByUsername(username);
		if (null == pUser)
			throw new Exception(username + "用户名不存在！");
		if (!Strings.isNullOrEmpty(pUser.getParentUser()))
			throw new Exception("当前账户是" + pUser.getParentUser() + "的子账户，不允许再建子账户！");
		MyUserEntity user = new MyUserEntity();
		String childUsername = getStringRandom(8);
		user.setUsername(childUsername);
		user.setLoginName(childUsername);
		user.setAccountSystemKey(UserConstants.DEFAULT_SYSTEM_KEY);
		user.setParentUser(username);
		user.setPassword(passwordEncoder.encode(MD5Utils.getMD5(childUsername)));
		myUserDao.save(user);
		ChildAuthorityEntity childAuthorityEntity = new ChildAuthorityEntity();
		childAuthorityEntity.setUsername(user.getUsername());
		childAuthorityEntity.setParentUser(user.getParentUser());
		childAuthorityEntity.setAuthUp(true);
		childAuthorityDao.save(childAuthorityEntity);
		return user.getUsername();
	}

	/**
	 * 创建当前用户的随机子账户
	 */
	@Transactional
	@PreAuthorize("hasAuthority('platform_user_creatRandomChild')")
	public String creatRandomChild() throws Exception {
		MyUserEntity curUser = getSelf();
		if (!Strings.isNullOrEmpty(curUser.getParentUser()))
			throw new Exception("当前账户是" + curUser.getParentUser() + "的子账户，不允许再建子账户！");
		MyUserEntity user = new MyUserEntity();
		String username = getStringRandom(8);
		user.setUsername(username);
		user.setLoginName(username);
		user.setAccountSystemKey(UserConstants.DEFAULT_SYSTEM_KEY);
		user.setParentUser(curUser.getUsername());
		user.setPassword(passwordEncoder.encode(MD5Utils.getMD5(username)));
		myUserDao.save(user);
		ChildAuthorityEntity childAuthorityEntity = new ChildAuthorityEntity();
		childAuthorityEntity.setUsername(username);
		childAuthorityEntity.setParentUser(user.getParentUser());
		childAuthorityEntity.setAuthUp(true);
		childAuthorityDao.save(childAuthorityEntity);
		return user.getUsername();
	}

	/**
	 * 获取子账户token
	 */
	@PreAuthorize("hasAuthority('platform_user_getToken')")
	public String getToken(String username) throws Exception {
		return getTokenByUserNamePwdLogin(username, MD5Utils.getMD5(username));
	}

	/**
	 * 忘记密码公开接口
	 */
	public void forgetPassword(SmsPasswordVO smsPasswordVO) throws Exception {
		if (Strings.isNullOrEmpty(smsPasswordVO.getAppPrefix()))
			throw new Exception("appPrefix不能为空！");
		if (Strings.isNullOrEmpty(smsPasswordVO.getMessageId()))
			throw new Exception("messageId不能为空！");
		if (Strings.isNullOrEmpty(smsPasswordVO.getMobile()))
			throw new Exception("mobile不能为空！");
		if (Strings.isNullOrEmpty(smsPasswordVO.getSmsCode()))
			throw new Exception("smsCode不能为空！");
		if (Strings.isNullOrEmpty(smsPasswordVO.getNewPwd()))
			throw new Exception("newPwd不能为空！");
		String mobile = smsPasswordVO.getMobile().trim();
		String newPwd = smsPasswordVO.getNewPwd().trim();
		MyUserEntity self = myUserDao.findByMobileAndAccountSystemKey(mobile,DEFAULT_SYSTEM_KEY);
		if (null == self)
			throw new Exception(mobile + "：用户未注册，请先注册！");

		//校验验证码是否属于当前手机号
		String checkSameMobileKey = MD5Utils.getMD5(mobile);//校验是否同一手机号--关键字
		String cacheMessageId = cacheSingleService.get(checkSameMobileKey);
		if (null == cacheMessageId)
			throw new Exception("请先获取验证码！");
		if (!smsPasswordVO.getMessageId().trim().equals(cacheMessageId))
			throw new Exception("非法验证码！");

		//校验验证码是否正确
		if (!feignSms.compareVerifyCode(smsPasswordVO.getAppPrefix().trim(), smsPasswordVO.getMessageId().trim(), smsPasswordVO.getSmsCode().trim()))
			throw new Exception("验证码错误!");

		//修改密码
		if (!newPwd.matches(REGEX_PWD))
			throw new Exception(ExceptionContants.PWD_ERR_MSG);
		self.setPassword(passwordEncoder.encode(newPwd));
		myUserDao.saveAndFlush(self);
	}

	/**
	 * token转换
	 * 第三方系统，若不对接用户中心，使用本接口转换优特云token；为了后续区分第三方系统用户，使用的账户体系的概念
	 */
	@Transactional
	@PreAuthorize("hasAuthority('platform_user_checkAndRegister')")
	public String checkAndRegister(String username) throws Exception {
		//入参校验
		if (Strings.isNullOrEmpty(username))
			throw new Exception(String.format(ExceptionContants.PARAM_NOTNULL_EXCEPTION, "username"));

		//检查用户是否已经注册
		MyUserEntity targetUser = myUserDao.findByUsername(username);
		if (null == targetUser) {//新用户直接入库
			targetUser = new MyUserEntity();
			targetUser.setUsername(username);
			targetUser.setLoginName(username);
			targetUser.setAccountSystemKey(UserConstants.DEFAULT_SYSTEM_KEY);
			targetUser.setPassword(passwordEncoder.encode(MD5Utils.getMD5(username)));
			myUserDao.saveAndFlush(targetUser);
		}
		return username;
		//检查用户是否有这些权限，没有则添加
	}

	/**
	 * 用户名密码登录，获取用户token
	 */
	private String getTokenByUserNamePwdLogin(String username, String pwd) throws Exception {
		FormBody.Builder builder = new FormBody.Builder();
		builder.add("username", username);
		builder.add("password", pwd);
		builder.add("grant_type", GRANT_TYPE);
		builder.add("scope", SCOPE);
		FormBody formBody = builder.build();
		Response response = OkHttpUtils.postForm(oauth2LoginUrl, formBody);
		String responseStr = response.body().string();
		if (null == JSONObject.parseObject(responseStr).get("access_token")) {
			if (null != JSONObject.parseObject(responseStr).get("msg"))
				throw new Exception(JSONObject.parseObject(responseStr).get("msg").toString());
			throw new Exception(responseStr);
		}
		return JSONObject.parseObject(responseStr).get("access_token").toString();
	}


    /**
     * 从token中获取parentName
     */
    public Optional<String> getParentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object details = authentication.getDetails();
        if(details instanceof OAuth2AuthenticationDetails){
            Object decodedDetails = ((OAuth2AuthenticationDetails) details).getDecodedDetails();
            if(decodedDetails instanceof Map){
                Object parentUser = ((Map) decodedDetails).get("parentUser");
                if(parentUser !=null){
                    return Optional.of((String) parentUser);
                }
            }
        }
        return Optional.empty();
    }

}
