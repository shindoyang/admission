package com.ut.security.model.vo;

import lombok.Data;

@Data
public class RegisterUserVo {
    private String username;
    private String password;
    private String uuid;
    private String userVerifyCode;
}
