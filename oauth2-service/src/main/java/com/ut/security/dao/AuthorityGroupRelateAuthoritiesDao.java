package com.ut.security.dao;

import com.ut.security.model.AuthorityGroupRelateAuthorities;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

/**
 * @auth: 陈佳攀
 * @Description:
 * @Date: Created in 10:34 2017-11-20
 */
public interface AuthorityGroupRelateAuthoritiesDao extends JpaRepository<AuthorityGroupRelateAuthorities, Long> {
    int deleteByAppKeyAndAuthorityKey(String appKey, String authKey);

    int deleteByAppKeyAndAuthorityGroupKey(String appKey, String authorityGroupKey);

    int deleteByAppKeyAndAuthorityGroupKeyAndAuthorityKey(String appKey, String authGroupKey, String authKey);

    List<AuthorityGroupRelateAuthorities> findByAppKeyAndAuthorityGroupKey(String appKey, String authGroupKey);

    Set<AuthorityGroupRelateAuthorities> findByAppKeyAndAuthorityGroupKeyIn(String appKey, String[] authGroupKey);

    AuthorityGroupRelateAuthorities findByAppKeyAndAuthorityGroupKeyAndAuthorityKey(String appKey, String authGroup, String auth);
}
