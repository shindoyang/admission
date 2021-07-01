package com.ut.user.vo;

import lombok.Data;

@Data
public class RegisterUserVo {
    private String username;
    private String password;
    private String uuid;
    private String userVerifyCode;
}
