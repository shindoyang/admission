package com.ut.security.authritymgr;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @auth: 陈佳攀
 * @Description:
 * @Date: Created in 15:40 2018-1-29
 */
public interface AuthorityGroupDao extends JpaRepository<AuthorityGroupEntity, String> {
    int deleteByAppKeyAndAuthorityGroupKey(String appKey, String authorityGroupKey);

    List<AuthorityGroupEntity> findByAppKeyAndAuthorityGroupParent(String appKey, String parentName);

    List<AuthorityGroupEntity> findAllByAppKey(String appKey);

    AuthorityGroupEntity findByAuthorityGroupKey(String authorityGroupKey);

    AuthorityGroupEntity findByAppKeyAndAuthorityGroupKey(String appKey, String authorityGroupKey);
}
