package com.ut.user.usermgr;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class MyUserRelateAuthorityGroups {
    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name="user_uid")
    String username;
    String authorityGroupKey;
    String appKey;

    public MyUserRelateAuthorityGroups() {
    }

    public MyUserRelateAuthorityGroups(String username, String authorityGroupKey, String appKey) {
        this.username = username;
        this.authorityGroupKey = authorityGroupKey;
        this.appKey = appKey;
    }
}
