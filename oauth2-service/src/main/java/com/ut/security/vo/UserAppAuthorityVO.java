package com.ut.security.vo;

import com.ut.security.authritymgr.AuthorityEntity;
import com.ut.security.authritymgr.AuthorityGroupEntity;
import lombok.Data;

import javax.persistence.Transient;
import java.util.List;

@Data
public class UserAppAuthorityVO {
    private String appKey;
    private String appName;

    @Transient
    private List<AuthorityEntity> authorities;
    @Transient
    private List<AuthorityGroupEntity> authorityGroups;

}
