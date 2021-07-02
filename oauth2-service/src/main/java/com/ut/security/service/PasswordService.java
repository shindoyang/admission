package com.ut.security.service;

import com.ut.security.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;

@Service
public class PasswordService {

    @Autowired
    PasswordEncoder passwordEncoder;

    /**
     * 指定方式加密用户密码
     */
    public String encryPassword(String password, String encryption)throws AuthenticationServiceException{
        if(null == encryption)
            password = passwordEncoder.encode(password);
        if("1".equals(encryption)){
            try {
                password = MD5Utils.getMD5(password);
            } catch (NoSuchAlgorithmException e) {
                throw new AuthenticationServiceException(e.toString());
            }
        }
        if(null != encryption && !"1".equals(encryption))
            throw new AuthenticationServiceException("暂不支持此加密类型!");
        return password;
    }

}
