package com.ut.user.vo;

import lombok.Data;

import javax.persistence.Transient;
import java.util.List;

@Data
public class ChildUserAuthorityVO {
    private String username;
    private String loginName;
    private boolean activated;

    @Transient
    private List<UserAppAuthorityVO> userAppAuthorityVO;
}

