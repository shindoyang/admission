package com.ut.security.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author chenglin
 * @creat 2019/9/20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthGroupAuthVo {
	@ApiModelProperty(value = "角色Key", required = true)
	private String authorityGroupKey;
	@ApiModelProperty(value = "功能Key列表", required = true)
	AuthorityVo authorityVo;

}
