package com.ut.security.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author chenglin
 * @creat 2019/9/18
 */
@Data
public class AccountVO {
    @ApiModelProperty(value = "用户名", name = "username", required = true)
    String username;
    @ApiModelProperty(value = "密码", name = "password", required = true)
    String password;
    @ApiModelProperty(value = "头像路径", name = "logo", required = true)
    String logo;
    @ApiModelProperty(value = "昵称", name = "nickname", required = true)
    String nickname;
    @ApiModelProperty(value = "手机号", name = "mobile", required = true)
    String mobile;
    @ApiModelProperty(value = "是否激活：0-禁用，1-启用 (注意：不指定默认激活)", name = "activated")
    String activated;
    String sex;

    AccountAuthorityGroupVO accountAuthorityGroupVO;
}
