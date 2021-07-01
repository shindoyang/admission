package com.ut.user.thirdauth.miniprogram;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotBlank;

/**
 * 小程序基本用户信息
 * @author litingting
 */
@Data
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class MiniProgramUser {
	Integer socialAccountType;
	String appId;
	@NotBlank(message = "小程序的opendId不能为空")
	String openId;
	String sessionKey;
	String unionId;

	public MiniProgramUser(@NotBlank(message = "小程序的opendId不能为空") String openId, String sessionKey, String unionId) {
		this.openId = openId;
		this.sessionKey = sessionKey;
		this.unionId = unionId;
	}

	@Override
	public String toString(){
		return "appId=" + appId + "openId=" + openId + " , sessionKey=" + sessionKey + " , unionId=" + unionId + " , socialAccountType=" + socialAccountType ;
	}
}
