package com.ut.security.model.vo;

import lombok.Data;

@Data
public class UserVo {
    private String username;
    private String loginName;
    private String nickname;
    private String mobile;
    private String email;
    private String sex;

    private String logo;
    private Boolean passwordFlag = false;

    public UserVo() {
    }

}
