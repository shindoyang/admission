package com.ut.security.constant;

/**
 * 系统参量
 */
public interface SecurityConstants {

    //密码校验规则: 8到20个字符，至少1个大写字母，1个小写字母和1个数字,可包含特殊字符（$@$!%*#?&）：
    String REGEX_PWD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{8,20}$";
    //用户名命名规则: 允许大小写字母数字下划线
    String REGEX_USERNAME = "^[A-Za-z0-9_]{3,100}$";
    //电话号码校验正则
    String REGEX_MOBILE = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";

    String USERNAME_ERR_MSG = "用户名仅支持3位以上大小写字母数字或下划线";
    String PWD_ERR_MSG = "密码须为8到20位包含大小写字母数字组成的字符,可选特殊字符 $@!%*#?&";
    String MOBILE_FORMAT_ERROR = "手机号码格式不正确！";
    String MOBILE_INCONGRUENCE = "当前手机号与获取验证码手机号不一致！";

    String SITE_WIDE_SECRET = "my-secret-salt";

    String LOGIN_TYPE_MOBILE = "mobile";//手机号登录-web端
    String LOGIN_TYPE_APP_MOBILE_REGISTER = "mobileRegister";//app验证码注册
    String LOGIN_TYPE_APP_MOBILE_LOGIN = "mobileLogin";//app验证码登录

    //账户密码登录入参
    String LOGIN_USERNAME = "username";
    String LOGIN_PASSWORD = "password";

	//加密方式入参
	String LOGIN_ENCRYPTION= "encryption";

    //图片验证码注册入参
    String LOGIN_UUID = "uuid";
    String LOGIN_CODEVERIFY = "codeVerify";

    //手机号登录入参
    String LOGIN_MOBILE = "mobile";
    String LOGIN_MESSAGEID = "messageId";
    String LOGIN_SMSCODE = "smsCode";
    String LOGIN_APPPREFIX = "appPrefix";

	/**
	 * 某用户的社交账号标志
	 */
	String SOCIAL_ACCOUNT_KEY = "socialAccountKey";
	String ENCRYPTED_DATA = "encryptedData";
	String IV = "iv";

	//ios上架审查账户
	String REVIEW_USER_MOBILE = "18866666688";
	String REVIEW_USER_MOBILE_VERIFYCODE = "551388";

    /**
     * 当请求需要身份认证时，默认跳转的url
     *
     */
    String DEFAULT_UNAUTHENTICATION_URL = "/authentication/require";
    /**
     * 默认的用户名密码登录请求处理url
     */
    String WEB_FORM_LOGIN_URL = "/authentication/form";//pc 端用户名密码登录前置入口
    String WEB_FORM_REGISTER_URL = "/authentication/register";//pc 端用户名密码注册入口
    String WEB_SMS_LOGIN_REGISTER_URL = "/authentication/mobile";//pc 短信验证码登录/注册接口
    String APP_FORM_LOGIN_URL = "/oauth/login";//app 用户名密码登录
    String APP_FORM_REGISTER_URL = "/oauth/register";//app 用户名密码注册
    String APP_SMS_LOGIN_URL = "/oauth/sms_login";//app 短信验证码登录
    String APP_SMS_REGISTER_URL = "/oauth/sms_register";//app 短信验证码注册
    
	String WECHAT_LOGIN_URL = "/wechat/login";//微信登录
	String WECHAT_APP_LOGIN_URL = "/wechat/app_login";//微信APP端登录
	String MINIPROGRAM_LOGIN_URL = "/miniprogram/login";//小程序登录
	String MINIPROGRAM_REGISTER_URL = "/miniprogram/register";//小程序登录
    /**
     * 默认登录页面
     *
     */
    String DEFAULT_LOGIN_PAGE_URL = "/login.html";
}
