package com.ut.security.usermgr;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class MyUserRelateApps {
    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "user_uid")
    String username;
    String appKey;
    @Transient
    String appName;

    public MyUserRelateApps() {
    }

    public MyUserRelateApps(String username, String appKey) {
        this.username = username;
        this.appKey = appKey;
    }
}
