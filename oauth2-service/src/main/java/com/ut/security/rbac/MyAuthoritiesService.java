package com.ut.security.rbac;

import com.ut.security.feign.FeignUserService;
import com.ut.security.support.AES_ECB_128_Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class MyAuthoritiesService {

    @Autowired
    private FeignUserService feignUserService;
    @Autowired
    AES_ECB_128_Service aes_ecb_128_service;

    public User assemblingUserDetail(MyUserEntity loginUser){
        //获取用户的所有权限
        List<String> myAuthorities = feignUserService.getGrantedAuthorities(loginUser.getUsername(), aes_ecb_128_service.getSecurityToken());
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        if(null != myAuthorities && myAuthorities.size() > 0){
            for(int i = 0; i < myAuthorities.size(); i++){
                GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(myAuthorities.get(i));
                grantedAuthorities.add(grantedAuthority);
            }
        }
        String password = loginUser.getPassword() == null ? "" : loginUser.getPassword();
        return new User(loginUser.getUsername(), password, grantedAuthorities);
    }
}
