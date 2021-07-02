package com.ut.security.api.client.username.login;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * app 用户名密码登录 验证信息封装类
 */
public class LoginAuthenticationToken extends AbstractAuthenticationToken {

    private static final long serialVersionUID = 500L;
    private final Object principal;
    private Object credentials;
    private String encryption;

    public LoginAuthenticationToken(Object principal, Object credentials) {
        super((Collection)null);
        this.principal = principal;
        this.credentials = credentials;
        this.setAuthenticated(false);
    }

    public LoginAuthenticationToken(Object principal, Object credentials,String encryption) {
        super((Collection)null);
        this.principal = principal;
        this.credentials = credentials;
        this.encryption = encryption;
        this.setAuthenticated(false);
    }

    public LoginAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        super.setAuthenticated(true);
    }

    public Object getCredentials() {
        return this.credentials;
    }

    public Object getPrincipal() {
        return this.principal;
    }

    public String getEncryption() {
        return this.encryption;
    }

    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if(isAuthenticated) {
            throw new IllegalArgumentException("Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        } else {
            super.setAuthenticated(false);
        }
    }

    public void eraseCredentials() {
        super.eraseCredentials();
        this.credentials = null;
    }
}

