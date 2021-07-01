package com.ut.user.controller;

import com.ut.user.clientmgr.MyClientDetailsEntity;
import com.ut.user.clientmgr.MyClientDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @auth: 陈佳攀
 * @Description:
 * @Date: Created in 10:19 2017-11-20
 */
@ApiIgnore
@RestController
@RequestMapping("/client")
public class ClientController {

    @Autowired
    MyClientDetailsService myClientDetailsService;

    @PutMapping
    public void addClient(MyClientDetailsEntity clientEntity) throws Exception {
        myClientDetailsService.addClient(clientEntity);
    }

    @GetMapping
    public MyClientDetailsEntity getClient(String clientId){
        return myClientDetailsService.getClientDetailsById(clientId);
    }

    @DeleteMapping
    public void delClient(String clientId){
        myClientDetailsService.delClient(clientId);
    }

}
