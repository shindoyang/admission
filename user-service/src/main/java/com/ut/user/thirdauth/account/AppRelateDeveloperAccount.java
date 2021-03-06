package com.ut.user.thirdauth.account;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Size;

/**
 * @auth: litingting
 * @Description: 各应用的第三方开发平台的账号信息
 * @Date: 2019/3/21
 */

@Entity
@Data
public class AppRelateDeveloperAccount {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	/** 第三方账号的类型 */
	private String socialAccountType;

	/** 应用名称*/
	private String appName;

	/** 在第三方账号的appKey */
	@Size(max = 100)
	private String appKey;

	/** 应用在第三方中的应用密钥*/
	@Size(max = 200)
	private String appSecret;

	/** 换取accessToken时要带上的重定向url*/
	@Transient
	private String redirectUrl;

	/**授权范围*/
	@Transient
	private String scope;

	public AppRelateDeveloperAccount(){}

	public AppRelateDeveloperAccount(String appKey, String appSecret, String appName, String accountType){
		this.appKey = appKey;
		this.appSecret = appSecret;
		this.appName = appName;
		this.socialAccountType = accountType;
	}
}
