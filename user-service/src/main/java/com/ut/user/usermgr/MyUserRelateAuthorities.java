package com.ut.user.usermgr;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class MyUserRelateAuthorities {
    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name="user_uid")
    String username;
    String authorityKey;
    String appKey;

    public MyUserRelateAuthorities() {
    }

    public MyUserRelateAuthorities(String username, String authorityKey) {
        this.username = username;
        this.authorityKey = authorityKey;
    }

    public MyUserRelateAuthorities(String username, String authorityKey, String appKey) {
        this.username = username;
        this.authorityKey = authorityKey;
        this.appKey = appKey;
    }
}
