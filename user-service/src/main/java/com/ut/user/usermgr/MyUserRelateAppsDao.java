package com.ut.user.usermgr;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface MyUserRelateAppsDao extends JpaRepository<MyUserRelateApps, Long>{
    MyUserRelateApps findByUsernameAndAppKey(String username, String appKey);
    List<MyUserRelateApps> findByUsername(String username);
    Page<MyUserRelateApps> findByAppKey(String appKey, Pageable pageable);
}
