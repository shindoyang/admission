package com.ut.user.vo;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

@Data
public class GrantAuthorityVO {
//    Set<GrantedAuthority> authorities;
    Set<String> authorities;
}
