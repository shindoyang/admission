package com.ut.user.support;


import com.ut.user.vo.AppVo;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@EnableFeignClients(value="user-service")
public interface FeignUser {
	@RequestMapping(value="/app",method = RequestMethod.POST)
	boolean createApp(@RequestBody AppVo appVo);

}
