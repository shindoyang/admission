package com.ut.security.appmgr;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@EntityListeners(AuditingEntityListener.class)
@Entity
@Data
public class AppEntity {

    @Id
    @Column(updatable = false, nullable = false)
    @Size(max = 50)
    private String appKey;//"iot" （对应原型中的应用前缀）
    private String name;//"设备云" （对应原型中的应用名称）
    private String description;
    private String developer;

    @ApiModelProperty(value = "应用状态:true-启用，false-禁用", name = "status", example = "true(1)：启用，false(0)：禁用")
    @NotNull
    private boolean status = true;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate = new Date();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppEntity applicat = (AppEntity) o;

        if (!appKey.equals(applicat.appKey)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return appKey.hashCode();
    }

}
