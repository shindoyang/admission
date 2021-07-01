package com.ut.security.rbac.thirdaccount.wechat;

import com.ut.security.properties.SocialConstants;
import lombok.Data;

/**
 * 微信用户信息
 * @author litingting
 */
@Data
public class WechatUserEntity {
	Long id;

	Integer socialAccountType = SocialConstants.WECHAT_TYPE_VALUE;

	/**用户中心的用户名*/
	private String oauthUserName;

	/** 微信昵称 */
	private String nickname;

	/** openId 对应的appId */
	private String appId;

	/** 普通用户的标识，对当前开发者帐号唯一 */
	private String openId;

	private Byte sex;

	private String city;

	private String province;

	private String country;

	/** 微信头像链接*/
	private String headimgurl;

	/**微信的用户统一标识。针对一个微信开放平台帐号下的应用，
	 * 同一用户的unionid是唯一的。
	 * */
	private String unionId;

	public WechatUserEntity(){}

	@Override
	public String toString(){
		return "昵称= " + nickname + "\n openId=" + openId+ "\n appId=" + appId
				+ "\n unionId=" + unionId + "\n headimgurl=" + headimgurl + " , socialAccountType=" + socialAccountType;
	}
}
