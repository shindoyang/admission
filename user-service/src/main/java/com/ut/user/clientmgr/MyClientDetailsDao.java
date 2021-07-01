package com.ut.user.clientmgr;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @auth: 陈佳攀
 * @Description:
 * @Date: Created in 16:33 2017-11-17
 */
public interface MyClientDetailsDao extends JpaRepository<MyClientDetailsEntity, String> {
}
