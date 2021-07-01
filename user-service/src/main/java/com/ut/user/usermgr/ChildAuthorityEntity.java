package com.ut.user.usermgr;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Size;

/**
 * @author chenglin
 * @creat 2019/10/30
 */
@Entity
@Data
public class ChildAuthorityEntity {

	@Id
	@Column(name="user_uid",updatable = false, nullable = false)
	@Size(max = 100)
	private String username;

	@Size(max = 100)
	@Column(name="parent_user_uid")
	private String parentUser;

	@ApiModelProperty(value = "是否具有父账户权限", name = "authUp")
	private boolean authUp;
}
