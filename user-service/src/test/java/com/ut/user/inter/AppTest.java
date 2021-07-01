//package com.ut.user.inter;
//
//import com.ut.user.UserApp;
//import feign.Feign;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes=UserApp.class,
//		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
//		properties = {"spring.cloud.discovery.enabled=false", "spring.cloud.config.enabled=false", "spring.profiles.active=test"})
//public class AppTest {
//	@Value("http://localhost:${local.server.port}")
//	String host;
//
////	@Test
////	public void testCreateApp(){
////		httpPost(host+"/createApp", paramMap, body);
////	}
//
//}
