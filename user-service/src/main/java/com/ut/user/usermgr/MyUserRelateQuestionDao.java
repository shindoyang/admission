package com.ut.user.usermgr;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MyUserRelateQuestionDao extends JpaRepository<MyUserRelateQuestion,Long> {
	List<MyUserRelateQuestion> findAllByUserName(String userName);
	MyUserRelateQuestion findByUserNameAndQuestionId(String username, String questionId);

}
