package com.ut.security.rbac.thirdaccount.miniprogram;

import com.ut.security.properties.SocialConstants;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 小程序基本用户信息
 * @author litingting
 */
@Data
@Slf4j
public class MiniProgramUser {
	Integer socialAccountType = SocialConstants.MINIPROGRAM_TYPE_VALUE;
	String appId;
	String openId;
	String sessionKey;
	String unionId;

	@Override
	public String toString(){
		return "appId=" + appId + " , openId=" + openId + " , sessionKey=" + sessionKey + " , unionId=" + unionId + " , socialAccountType=" + socialAccountType;
	}
}
