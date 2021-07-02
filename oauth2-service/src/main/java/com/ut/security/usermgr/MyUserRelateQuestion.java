package com.ut.security.usermgr;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Data
public class MyUserRelateQuestion {
	@Id
	@Column(updatable = false, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	@Size(max = 100)
	@Column(name = "user_uid")
	String userName;
	@Size(max = 20)
	String questionId;
	@Size(max = 100)
	String answer;
}
