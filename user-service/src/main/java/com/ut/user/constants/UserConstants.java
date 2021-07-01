package com.ut.user.constants;

public class UserConstants {
    //电话号码校验正则
    public static final String REGEX_MOBILE = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";

   //  public static final String REGEX_PWD =  "^(?![0-9A-Z]+$)(?![0-9A-Z$#&]+$)(?![0-9a-z]+$)(?![0-9a-z$#&]+$)(?![a-zA-Z]+$)(?![a-zA-Z$#&]+$)[0-9A-Za-z$#&]{8,20}$";

    //密码校验规则: 8到20个字符，至少1个大写字母，1个小写字母和1个数字,可包含特殊字符（$@$!%*#?&）：
    public static final String REGEX_PWD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{8,20}$";
    //用户名命名规则: 6-18个字符，允许大小写字母数字下划线且必须包含字母
    public static final String REGEX_USERNAME =  "^(?=.*[a-zA-Z])[a-zA-Z0-9_]{6,18}$";
    public static final int FIFTY_LENGTH = 50;

    //"^[A-Za-z0-9_]{3,100}$";

    //角色、角色组命名规则: 允许大小写字母数字下划线

    public static final String REGEX_AUTHKEY = "^[A-Za-z0-9_]+$";
    //应用命名规则: 允许小写字母数字
    public static final String REGEX_APPKEY = "^[a-z0-9]+$";
    //基础平台-超级管理员
    public static final String PLATFORM_ADMIN = "platform_admin";
    //应用开发者 -角色
    public static final String APP_DEVELOPER = "developer";
    //平台应用名
    public static final String PLATFORM_APPKEY = "platform";
    //应用权限分配者 -角色
    public static final String APP_ASSIGN_AUTHORITY = "authority_assign";
    //角色、角色组 主键长度
    public static final int KEY_LENGTH = 150;

    //分配功能 权限
    public static final String BIND_AUTH_TO_USER = "platform_app_bindAuth2User";
    //分配角色 权限
    public static final String BIND_AUTHGROUP_TO_USER = "platform_app_bindAuthGroup2User";

    public static final String TRUE = "true";
    public static final String GRANT_TYPE = "password";
    public static final String SCOPE = "read";

    public static final String DEFAULT_SYSTEM_KEY = "cook";

    //=================================兼容oauth逻辑===================================
    public static final String LOGIN_TYPE_USERNAMEPASSWORD = "password";//账户密码登录
    public static final String LOGIN_TYPE_IMAGECODE = "imageCode";//图片验证码注册
    public static final String LOGIN_TYPE_MOBILE = "mobile";//手机号登录-web端
    public static final String LOGIN_TYPE_APP_MOBILE_REGISTER = "mobileRegister";//app验证码注册
    public static final String LOGIN_TYPE_APP_MOBILE_LOGIN = "mobileLogin";//app验证码登录
    public static final String LOGIN_TYPE_WECHAT = "wechat";//微信授权码登录

    public static final String LOGIN_TYPE_KEY = "type";// 登陆类型

    //ios上架审查账户
    public static final String REVIEW_USER_MOBILE = "18866666688";
    public static final String REVIEW_USER_MOBILE_VERIFYCODE = "551388";

    public static final String MOBILE_FORMAT_ERROR = "手机号码格式不正确！";
    public static final String MOBILE_INCONGRUENCE = "当前手机号与获取验证码手机号不一致！";
}

