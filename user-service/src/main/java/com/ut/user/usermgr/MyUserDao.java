package com.ut.user.usermgr;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @auth: 陈佳攀
 * @Description:
 * @Date: Created in 17:09 2017-11-17
 */

public interface MyUserDao extends JpaRepository<MyUserEntity, String> {
	Page<MyUserEntity> findAllByParentUser(String parentUser, Pageable pageable);

	MyUserEntity findByUsername(String username);

	MyUserEntity findByUsernameAndParentUser(String username, String parentUser);

	MyUserEntity findByMobileAndAccountSystemKey(String mobile,String accountSystemKey);

	MyUserEntity findByLoginNameAndAccountSystemKey(String loginName,String accountSystemKey);
}
