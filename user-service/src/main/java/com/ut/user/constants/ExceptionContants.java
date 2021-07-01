package com.ut.user.constants;

public class ExceptionContants {

	public static final String APPKEY_ERR_MSG = "应用名仅限小写字母数字组成";
	public static final String AUTHKEY_ERR_MSG = "功能名仅限大小写字母数字下划线组成";
	public static final String AUTHGROUPKEY_ERR_MSG = "角色名仅限大小写字母数字下划线组成";
	public static final String PWD_ERR_MSG = "密码须为8到20位包含大小写字母数字组成的字符,可选特殊字符 $@!%*#?&";
	public static final String USERNAME_ERR_MSG = "用户名仅支持6-18位数字、字母和下划线，仅必须包含字母！";
	public static final String MOBILE_ERR_MSG = "输入的手机号不合法！";

	public static final String PARAM_NOTNULL_EXCEPTION = "%s 不能为空！";
	public static final String EXIST_EXCEPTION = "%s 已存在，请勿重复创建！";
	public static final String NO_AUTHORITY_EXCEPTION = "%s 应用不存在或您无权操作该应用！";
	public static final String PREFIX_EXCEPTION = "%s 应以 '{appKey}_' 开头！";
	public static final String PREFIX_NOT_MATCH_EXCEPTION = "appKey与 %s 前缀不一致！";
	public static final String EXCEED_LONG_EXCEPTION = "%s 的长度不能超过 " + UserConstants.KEY_LENGTH + " 个字符！";
	public static final String PARENT_NODE_NOT_EXIST_EXCEPTION = "%s 指定的父节点不存在！";
	public static final String NOT_LEAF_EXCEPTION = "%s 非叶子节点，不予许操作！";
	public static final String AUTHORITY_EXCEED_EXCEPTION = "存在功能超出权限！";
	public static final String AUTHORITYGROUP_EXCEED_EXCEPTION = "存在角色超出权限！";
	public static final String OBJECT_NOT_EXIST_EXCEPTION = "%s 未创建！";
	public static final String NOT_SAME_APP_EXCEPTION = "%s 不属于同一应用！";
	public static final String NOT_CHILDREN_EXCEPTION = "%s 用户非当前用户子用户！";

	public static final String MOBILE_HAS_BIND_EXCEPTION = "该手机号已绑定！";
	public static final String HAS_MOBILE_EXCEPTION = "您已绑定手机号，若要更换，请使用更换手机号功能！";
	public static final String MOBILE_USED_EXCEPTION = "%s 已被其他账户绑定！";
	public static final String NO_QUESTION_EXCEPTION = "您未设置过此密保问题！";
	public static final String ANSWER_ERROR_EXCEPTION = "密保答案有误！";
	public static final String QUESTION_NOT_EXIST = "新密保问题不存在！";
	public static final String VERFITY_OLD_QUESTION = "请先验证旧的密保问题答案！";
	public static final String QUESTION_SAME = "新旧密保问题不能相同！";

	public static final String PASSWORD_ALREADY_EXIST = "您已设置过密码！";
	public static final String LOGINNAME_ALREADY_EXIST = "用户登录名已经被占用！";
	public static final String USERNAME_ALEADY_EXIST = "用户名已被注册！";
	public static final String USERNAME_NOT_EXIST = "指定用户不存在！";
	public static final String AUTH_GROUP_KEY_EXIST = "已存在同名的角色Key！功能Key不能与角色Key重名！";
	public static final String AUTH_KEY_EXIST = "已存在同名的功能Key！角色Key不能与功能Key重名！";


}
