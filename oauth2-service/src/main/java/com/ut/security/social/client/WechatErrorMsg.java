package com.ut.security.social.client;

import lombok.Data;

/** 用来接收微信服务器的报错信息
 * @author litingting
 * */
@Data
public class WechatErrorMsg {
    int errcode;
    String errmsg;
}
