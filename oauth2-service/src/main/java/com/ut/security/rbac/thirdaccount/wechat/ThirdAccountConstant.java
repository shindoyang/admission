package com.ut.security.rbac.thirdaccount.wechat;

/**
 * @author litingting
 */
public class ThirdAccountConstant {
    /**
     * 第三方账户的默认绑定状态（0 为未绑定，1为绑定）
     */
    public static final byte UNBIND = 0;

    public static final byte BIND = 1;

    public static final String WECHAT = "wechat";
    public static final String WECHAT_MOBILE = "wechat_mobile";
    public static final String MINI_PROGRAM = "miniProgram";
    //用户中心小程序的appKey及secret
    public static final String MINIP_USER_APPKEY = "wxea9fb6bc3421122f";
    public static final String MINIP_USER_SECRET = "1180a42cccabb13ef7d2c255272a8d35";
    //用户中心移动版的appKey及secret
    public static final String WECHAT_MOBILE_USER_APPKEY = "wxed6a6a64f6a3b59a";
    public static final String WECHAT_MOBILE_USER_SECRET = "4720efd0dd52f3c2425a3f1462a036c8";
    //餐饮应用小程序的appKey及secret
    public static final String COOK_APPKEY = "wxbadaa2dc40450e9d";
    public static final String COOK_SECRET = "06067371d379bc65048838e730698b52";
    //钱包小程序的appKey及secret
    public static final String WALLET_APPKEY = "wx73414bbc7f2d813b";
    public static final String WALLET_SECRET = "9711efa6569cbbcd066098434bd814e1";
}
