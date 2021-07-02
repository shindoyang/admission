package com.ut.security.model.vo;

import lombok.Data;

import java.util.Set;

@Data
public class GrantAuthorityVO {
    //    Set<GrantedAuthority> authorities;
    Set<String> authorities;
}
