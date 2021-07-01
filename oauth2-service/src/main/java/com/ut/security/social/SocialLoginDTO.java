package com.ut.security.social;

import lombok.Data;

/**
 * 当前登录的用户社交账号信息
 * @author litingting
 */
@Data
public class SocialLoginDTO {
	String socialAccountKey;
	Boolean exist;

	public SocialLoginDTO(){}

	public SocialLoginDTO(String socialAccountKey, boolean exist){
		this.socialAccountKey = socialAccountKey;
		this.exist = exist;
	}
}
