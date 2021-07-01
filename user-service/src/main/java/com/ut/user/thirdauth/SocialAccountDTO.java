package com.ut.user.thirdauth;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 绑定社交账号的输入数据
 * @author litingting
 */
@Data
public class SocialAccountDTO {
    /**用户社交账号的身份标识，绑定时使用*/
    @NotNull(message="通过该标识，可获取该用户社交账号的信息")
    String socialAccountKey;

    /**社交账号类型*/
    @NotNull(message = "要绑定的社交账号类型不能为空")
    String socialAccountType;
}
