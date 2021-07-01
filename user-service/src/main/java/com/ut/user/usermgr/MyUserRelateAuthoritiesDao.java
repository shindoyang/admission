package com.ut.user.usermgr;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface MyUserRelateAuthoritiesDao extends JpaRepository<MyUserRelateAuthorities, Long>{
    int deleteByAppKeyAndAuthorityKey(String appKey, String authKey);
    int deleteByAppKeyAndUsername(String appKey, String username);
    int deleteByUsernameAndAuthorityKeyAndAppKey(String username, String authKey, String appKey);
    List<MyUserRelateAuthorities> findByUsernameAndAppKey(String username, String appKey);
}
