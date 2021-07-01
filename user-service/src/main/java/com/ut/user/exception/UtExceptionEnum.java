package com.ut.user.exception;

/**
 * 异常代码 枚举类
 */
public enum UtExceptionEnum {

    PARAM_NOT_NULL(1001, "请求参数不允许为空！"),
    USERNAME_ERR_MSG(1002, "用户名仅支持6-18位数字、字母和下划线，仅必须包含字母！"),
    PWD_ERR_MSG(1003, "密码须为8到20位包含大小写字母数字组成的字符,可选特殊字符 $@!%*#?&"),
    MOBILE_ERR_MSG(1004, "输入的手机号不合法！"),
    PARAM_ERROR(1005, "参数错误！"),

    //用户
    NO_DATA_AUTHORITY(1010, "无权访问指定数据！"),
    USERNAME_ALEADY_EXIST(1011, "用户名已被注册！"),
    MOBILE_USED_EXCEPTION(1012, "%s 已被其他账户绑定！"),
    USERNAME_NOT_NULL(1013, "用户名不能为空！"),
    USER_NOT_EXIXT(1014, "指定用户不存在！"),

    //应用
    APP_NOT_EXIXT(1031, "appKey不存在！"),

    //功能
    AUTHORITY_EXIXT(1051, "功能已存在！"),

    //角色
    AUTHORITYGROUP_NOT_BELONGTO_APP(1071, "角色不属于应用！"),
    AUTHORITYGROUP_NOT_CREATE(1072, "应用未创建指定角色！");

    private Integer utCode;
    private String utMsg;

    public Integer getUtCode() {
        return utCode;
    }

    public String getUtMsg() {
        return utMsg;
    }

    UtExceptionEnum(Integer utCode, String utMsg) {
        this.utCode = utCode;
        this.utMsg = utMsg;
    }

    public static String getUtMsg(Integer utCode){
        for(UtExceptionEnum exEnum : UtExceptionEnum.values()){
            if(exEnum.getUtCode() == utCode) {
                return exEnum.getUtMsg();
            }
        }
        return "";
    }


}
