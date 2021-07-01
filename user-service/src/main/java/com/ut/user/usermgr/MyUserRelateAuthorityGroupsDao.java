package com.ut.user.usermgr;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @auth: 陈佳攀
 * @Description:
 * @Date: Created in 17:09 2017-11-17
 */

public interface MyUserRelateAuthorityGroupsDao extends JpaRepository<MyUserRelateAuthorityGroups, Long>{
    int deleteByAppKeyAndAuthorityGroupKey(String appKey, String authorityGroupKey);
    int deleteByAppKeyAndUsername(String appKey, String username);
    int deleteByUsernameAndAuthorityGroupKeyAndAppKey(String username, String authGroupKey, String appKey);
    List<MyUserRelateAuthorityGroups> findByUsernameAndAppKey(String username, String appKey);
    MyUserRelateAuthorityGroups findByAppKeyAndUsernameAndAuthorityGroupKey(String appKey, String username, String authGroupKey);

    Page<MyUserRelateAuthorityGroups> findByAppKeyAndAuthorityGroupKey(String appKey, String authGroupKey, Pageable pageable);
}
