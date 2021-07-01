package com.ut.user.authritymgr;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Size;

/**
 * @auth: 陈佳攀
 * @Description: 角色
 * @Date: Created in 15:36 2018-1-29
 */
@Entity
@Data
public class AuthorityEntity {

    @Id
    @Column(updatable = false, nullable = false)
    @Size(max = 150)
    @ApiModelProperty(value="角色key，规则：应用key_角色英文名，如：iot_deleteOrder", name="key")
    private String authorityKey;
    @ApiModelProperty(value="角色中文名", name="name")
    private String name;
    @ApiModelProperty(value="描述", name="description")
    private String description;
    @ApiModelProperty(value="父节点(暂时不用)", name="parent")
    private String parent;
    @ApiModelProperty(value="应用前缀(前端回填)", name="appKey")
    private String appKey;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuthorityEntity authority = (AuthorityEntity) o;

        if (!authorityKey.equals(authority.authorityKey)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return authorityKey.hashCode();
    }

}
