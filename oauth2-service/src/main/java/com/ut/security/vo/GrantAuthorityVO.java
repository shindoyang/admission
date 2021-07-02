package com.ut.security.vo;

import lombok.Data;

import java.util.Set;

@Data
public class GrantAuthorityVO {
    //    Set<GrantedAuthority> authorities;
    Set<String> authorities;
}
