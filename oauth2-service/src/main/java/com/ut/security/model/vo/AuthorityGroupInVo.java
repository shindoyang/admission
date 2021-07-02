package com.ut.security.model.vo;

import lombok.Data;

@Data
public class AuthorityGroupInVo {
    private String appKey;
    private String authorityGroupKey;
    private String authorityGroupName;
    private String authorityGroupParent;
    private String description;

}
