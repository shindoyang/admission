package com.ut.security.client.sms.login;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * app 短信验证码登录 验证信息封装类
 */
public class SmsLoginAuthenticationToken extends AbstractAuthenticationToken {

    private static final long serialVersionUID = 500L;
    private final Object principal;

    public SmsLoginAuthenticationToken(Object principal) {
        super((Collection)null);
        this.principal = principal;
        this.setAuthenticated(false);
    }

    public SmsLoginAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        super.setAuthenticated(true);
    }

    public Object getCredentials() {
        return null;
    }

    public Object getPrincipal() {
        return this.principal;
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
    }
}

