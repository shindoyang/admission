package com.ut.security.dao;

import com.ut.security.model.AuthorityEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @auth: 陈佳攀
 * @Description:
 * @Date: Created in 10:34 2017-11-20
 */
public interface AuthorityDao extends JpaRepository<AuthorityEntity, String> {
    AuthorityEntity findByAppKeyAndAuthorityKey(String appKey, String authKey);

    AuthorityEntity findByAuthorityKey(String authKey);

    int deleteByAppKeyAndAuthorityKey(String appKey, String authKey);

    Page<AuthorityEntity> findByAppKey(String appKey, Pageable pageble);

    List<AuthorityEntity> findAllByAppKey(String appKey);

    List<AuthorityEntity> findByAppKeyAndNameLike(String appKey, String keyword);
}
