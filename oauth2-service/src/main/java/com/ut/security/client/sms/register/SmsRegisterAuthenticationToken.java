package com.ut.security.client.sms.register;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * app 短信验证码注册 验证信息封装类
 */
public class SmsRegisterAuthenticationToken extends AbstractAuthenticationToken {

    private static final long serialVersionUID = 500L;
    private final Object principal;

    public SmsRegisterAuthenticationToken(Object principal) {
        super((Collection)null);
        this.principal = principal;
        this.setAuthenticated(false);
    }

    public SmsRegisterAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
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

