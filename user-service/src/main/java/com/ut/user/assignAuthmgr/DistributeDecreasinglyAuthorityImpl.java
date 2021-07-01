package com.ut.user.assignAuthmgr;

import com.ut.user.usermgr.MyUserRelateAuthorities;
import com.ut.user.usermgr.MyUserRelateAuthoritiesService;
import com.ut.user.utils.SpringUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class DistributeDecreasinglyAuthorityImpl extends AbstractDistributeAuthority {

    @Override
    public void deleteAuth(String appKey, String username){
    }

    /**
     * 给指定账户分配--删功能
     */
    @Override
    public void distribute(String username, String appKey,  Collection<String> authorityVo){
        MyUserRelateAuthoritiesService myUserRelateAuthoritiesService = SpringUtils.getBean(MyUserRelateAuthoritiesService.class);
        //功能授权
        Set<String> authIn = (Set)authorityVo;
        Set<String> result =  new HashSet<>();
        result.addAll(authIn);
        result.forEach(authDel -> myUserRelateAuthoritiesService.deleteAuthRelate(username, authDel, appKey));
    }
}
