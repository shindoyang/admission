package com.ut.security.usermgr;

import org.springframework.data.jpa.repository.JpaRepository;


public interface MyUserExtendDao extends JpaRepository<MyUserExtendEntity, String> {
    MyUserExtendEntity findByUsername(String username);
}
