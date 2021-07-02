package com.ut.security.model.vo;

import lombok.Data;

/**
 * @author chenglin
 * @creat 2019/12/6
 */
@Data
public class SmsPasswordVO {
	private String mobile;
	private String appPrefix;
	private String messageId;
	private String smsCode;
	private String newPwd;
}
