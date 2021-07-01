package com.ut.user.authritymgr;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class AuthorityGroupRelateAuthorities {
    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String authorityGroupKey;
    String authorityKey;
    String appKey;

    public AuthorityGroupRelateAuthorities() {
    }

    public AuthorityGroupRelateAuthorities(String authorityGroupKey, String authorityKey, String appKey) {
        this.authorityGroupKey = authorityGroupKey;
        this.authorityKey = authorityKey;
        this.appKey = appKey;
    }
}
