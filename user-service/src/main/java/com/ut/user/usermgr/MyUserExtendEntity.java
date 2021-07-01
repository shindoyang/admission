package com.ut.user.usermgr;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * 用户扩展信息表
 */
@Entity
@Data
public class MyUserExtendEntity {

    @Id
    @Column(name="user_uid",updatable = false, nullable = false)
    private String username;

    @Size(max = 1)
    @ApiModelProperty(value="性别(填数字) 0：男， 1：女", name="sex")
    private String sex;

    @Temporal(TemporalType.DATE)
    @ApiModelProperty(value="生日 swagger参数参考格式：Mon Oct 29 17:16:04 CST 2018 ", name="birthday")
    private Date birthday;

    @Size(max = 255)
    @ApiModelProperty(value="头像", name="logo")
    private String logo;

    @ApiModelProperty(value="省", name="province")
    @Size(max = 20)
    private String province;

    @ApiModelProperty(value="市", name="city")
    @Size(max = 20)
    private String city;

    @ApiModelProperty(value="区", name="area")
    @Size(max = 20)
    private String area;

    @ApiModelProperty(value="地址", name="address")
    private String address;

    @Size(max = 255)
    @ApiModelProperty(value="简介", name="intro")
    private String intro;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MyUserExtendEntity user = (MyUserExtendEntity) o;

        return username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }

}