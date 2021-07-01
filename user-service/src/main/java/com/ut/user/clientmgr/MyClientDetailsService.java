package com.ut.user.clientmgr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

/**
 * @auth: 陈佳攀
 * @Description:
 * @Date: Created in 15:59 2017-11-17
 */
@Service
public class MyClientDetailsService {

    @Autowired
    MyClientDetailsDao myClientDetailsDao;

    @PreAuthorize("hasAuthority('platform_admin')")
    public void addClient(MyClientDetailsEntity client) throws Exception {
        if (null == myClientDetailsDao.save(client)) {
            throw new Exception("client with the same id is already exist");
        }
    }

    @PreAuthorize("hasAuthority('platform_admin')")
    public MyClientDetailsEntity getClientDetailsById(String clientId){
        return myClientDetailsDao.getOne(clientId);
    }

    public void delClient(String clientId){
        myClientDetailsDao.deleteById(clientId);
    }

}
