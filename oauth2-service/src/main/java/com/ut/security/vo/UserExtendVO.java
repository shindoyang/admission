package com.ut.security.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author chenglin
 * @creat 2020/2/25
 */
@Data
public class UserExtendVO {
	private String username;
//	private String loginName;
//	private String nickname;
//	private String mobile;
//	private String email;

	private String sex;
	private Date birthday;
	private String province;
	private String city;
	private String area;
	private String address;
	private String intro;
	private String logo;
}
