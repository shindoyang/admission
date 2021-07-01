package com.ut.user.clientmgr;

import lombok.Data;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Size;
import java.util.Set;

/**
 * @auth: 陈佳攀
 * @Description:
 * @Date: Created in 16:30 2017-11-17
 */

@Entity
@Data
public class MyClientDetailsEntity {

    @Id
    @Size(max = 20)
    String clientId;
    @ElementCollection
    Set<String> resourceIds;
    @ElementCollection
    Set<String> scopes;
    @ElementCollection
    Set<String> grantTypes;
    /*@ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    private Set<AuthorityEntity> authorities;*/
}
