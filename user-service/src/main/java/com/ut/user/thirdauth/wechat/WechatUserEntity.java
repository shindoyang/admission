package com.ut.user.thirdauth.wechat;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 微信用户信息
 * @author litingting
 */
@Entity
@Table
@Data
public class WechatUserEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;

	/**用户中心的用户名*/
	private String oauthUserName;

	/** 微信昵称 */
	private String nickname;

	/** openId 对应的appId */
	private String appId;

	/** appId 对应的社交账户类型 */
	private Integer socialAccountType;

	/** 普通用户的标识，对当前开发者帐号唯一 */
	private String openId;

	private Byte sex = 0;

	private String language;

	private String city;

	private String province;

	private String country;

	/** 微信头像链接*/
	private String headimgurl;

	/**微信的用户统一标识。针对一个微信开放平台帐号下的应用，
	 * 同一用户的unionid是唯一的。
	 * */
	private String unionId;

	private Date createTime;

	private Date updateTime;

	public WechatUserEntity(){}

	public WechatUserEntity(String openId, String unionId, String oauthUserName){
		this.openId = openId;
		this.unionId = unionId;
		this.oauthUserName = oauthUserName;
	}

	@Override
	public String toString(){
		return "昵称= " + nickname + "\n openId=" + openId + "\n appId=" + appId
				+ "\n unionId=" + unionId + "\n headimgurl=" + headimgurl;
	}
}
