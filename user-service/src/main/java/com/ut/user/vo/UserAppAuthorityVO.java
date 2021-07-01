package com.ut.user.vo;

import com.ut.user.authritymgr.AuthorityEntity;
import com.ut.user.authritymgr.AuthorityGroupEntity;
import lombok.Data;

import javax.persistence.Transient;
import java.util.List;
import java.util.Set;

@Data
public class UserAppAuthorityVO {
    private String appKey;
    private String appName;

    @Transient
    private List<AuthorityEntity> authorities;
    @Transient
    private List<AuthorityGroupEntity> authorityGroups;

}
