package com.ut.user.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AccountAuthorityGroupVO {
    @ApiModelProperty(value = "应用key", name = "appKey", required = true)
    String appKey;
    @ApiModelProperty(value = "角色key", name = "authorityGroupKey", required = true)
    String authorityGroupKey;
    @ApiModelProperty(value = "角色名(新增、修改时无须指定)", name = "authorityGroupName")
    String authorityGroupName;

}
