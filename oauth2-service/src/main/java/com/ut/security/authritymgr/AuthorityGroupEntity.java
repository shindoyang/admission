package com.ut.security.authritymgr;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;

/**
 * @auth: 陈佳攀
 * @Description: 角色组
 * @Date: Created in 15:36 2018-1-29
 */
@Entity
@Data
public class AuthorityGroupEntity {
    @Id
    @Column(updatable = false, nullable = false)
    @Size(max = 150)
    @ApiModelProperty(value = "角色组key，规则：应用key_角色组英文名，如：iot_manager", name = "authorityGroupKey")
    String authorityGroupKey;
    @ApiModelProperty(value = "角色组中文名", name = "authorityGroupName")
    String authorityGroupName;
    @ApiModelProperty(value = "角色组父节点", name = "authorityGroupParent")
    String authorityGroupParent;
    @ApiModelProperty(value = "描述", name = "description")
    String description;
    @ApiModelProperty(value = "应用id", name = "appKey")
    private String appKey;

    //@ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    @Transient
    Set<AuthorityEntity> authorityEntities;
    @Transient
    List<AuthorityGroupEntity> children;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuthorityGroupEntity authGroup = (AuthorityGroupEntity) o;

        if (!appKey.equals(authGroup.appKey) && !authorityGroupKey.equals(authGroup.authorityGroupKey)) return false;

        return true;
    }
}
