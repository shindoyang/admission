package com.ut.security.model.vo;

import com.ut.security.model.AuthorityEntity;
import com.ut.security.model.AuthorityGroupEntity;
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
