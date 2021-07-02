package com.ut.security.model.vo;

import java.util.Set;

public class AuthorityVo {
    private Set<String> authorityKeys;

    public Set<String> getAuthorityKeys() {
        return authorityKeys;
    }

    public void setAuthorityKeys(Set<String> authorityKeys) {
        this.authorityKeys = authorityKeys;
    }
}
