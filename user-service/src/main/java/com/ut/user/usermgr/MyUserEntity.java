package com.ut.user.usermgr;


import com.ut.user.authritymgr.AuthorityEntity;
import com.ut.user.authritymgr.AuthorityGroupEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Email;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @auth: 陈佳攀
 * @Description:
 * @Date: Created in 17:09 2017-11-17
 */
@Entity
@Data
public class MyUserEntity {

	@Id
	@Column(name = "user_uid",updatable = false, nullable = false)
	@Size(max = 100)
	private String username;
	@Size(max = 100)
	@Column(name = "username")
	private String loginName;
	@Size(max = 100)
	private String nickname;
	private String parentUser;
	@Size(max = 256)
	private String password;
	@Size(max = 50)
	private String mobile;
	@Email
	@Size(max = 50)
	private String email;
	private boolean activated = true;
	@Size(max = 100)
	private String activationKey;
	@ApiModelProperty(value = "绑定微信的标识", name = "wechat")
	private Boolean wechat = false;
	@ApiModelProperty(value = "绑定微博的标识", name = "weibo")
	private Boolean weibo = false;
	@ApiModelProperty(value = "绑定QQ的标识", name = "qq")
	private Boolean qq = false;
	@ApiModelProperty(value = "绑定小程序的标识", name = "miniProgram")
	private Boolean miniProgram = false;
	@ApiModelProperty(value = "用户体系标识", name = "accountSystemKey")
	private String accountSystemKey;
	@ApiModelProperty(value = "创建日期")
	private Date createdDate = new Date();

	@Transient
	private List<MyUserRelateQuestion> myUserRelateQuestionList;
	@Transient
	private Set<AuthorityEntity> authorities;
	@Transient
	private Set<AuthorityGroupEntity> authorityGroups;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MyUserEntity user = (MyUserEntity) o;

		return username.equals(user.username);
	}

	@Override
	public int hashCode() {
		return username.hashCode();
	}

}