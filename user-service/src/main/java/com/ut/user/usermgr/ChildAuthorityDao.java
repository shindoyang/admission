package com.ut.user.usermgr;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author chenglin
 * @creat 2019/10/30
 */

public interface ChildAuthorityDao extends JpaRepository<ChildAuthorityEntity, String> {
	ChildAuthorityEntity findByUsername(String username);
}
