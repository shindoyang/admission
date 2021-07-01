package com.ut.user.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author chenglin
 * @creat 2019/10/30
 */
@Data
public class SpecialChildUser {
	@ApiModelProperty(value = "用户名", required = true)
	private String username;
}
