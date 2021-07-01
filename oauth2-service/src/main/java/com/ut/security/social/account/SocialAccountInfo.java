package com.ut.security.social.account;

import lombok.Data;

/**
 * @author litingting
 */
@Data
public class SocialAccountInfo {
	String socialEntityInJson;

	/**该用户的社交账号身份标识*/
	String uid;

	public SocialAccountInfo(){}

	public SocialAccountInfo(String socialEntityInJson, String uid){
		this.socialEntityInJson = socialEntityInJson;
		this.uid = uid;
	}
}
