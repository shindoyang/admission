package com.ut.user.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author chenglin
 * @creat 2019/9/10
 */
@Data
public class UsernamePswdVo {
	private String username;
	private String newPassword;
}
