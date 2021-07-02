package com.ut.security.service;

import com.ut.security.usermgr.MyUserEntity;
import com.ut.security.usermgr.MyUserService;
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
    private MyUserService myUserService;
    @Autowired
    AES_ECB_128_Service aes_ecb_128_service;

    public User assemblingUserDetail(MyUserEntity loginUser) {
        //获取用户的所有权限
        Set<GrantedAuthority> grantedAuthorities = null;
        String password = null;
        try {
            List<String> myAuthorities = myUserService.getGrantedAuthorities(loginUser.getUsername());
            grantedAuthorities = new HashSet<>();
            if (null != myAuthorities && myAuthorities.size() > 0) {
                for (int i = 0; i < myAuthorities.size(); i++) {
                    GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(myAuthorities.get(i));
                    grantedAuthorities.add(grantedAuthority);
                }
            }
            password = loginUser.getPassword() == null ? "" : loginUser.getPassword();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new User(loginUser.getUsername(), password, grantedAuthorities);
    }
}
