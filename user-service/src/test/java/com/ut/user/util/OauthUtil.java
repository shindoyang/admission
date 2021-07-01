package com.ut.user.util;

import org.junit.Assert;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Collections;


/**
 * @auth: 陈佳攀
 * @Description:
 * @Date: Created in 15:06 2018-4-10
 */
public class OauthUtil {

    static RestTemplate restTemplate(){
        return new RestTemplate();
    }

    public static OAuth2AccessToken getToken(String tokenUrl, String user, String pswd) throws Exception{
        //Http Basic 验证
        String clientAndSecret = "sso-gateway:sso-gateway-secret";
        clientAndSecret = "Basic "+Base64.getEncoder().encodeToString(clientAndSecret.getBytes());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization",clientAndSecret);
        //授权请求信息
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.put("username", Collections.singletonList(user));
        map.put("password", Collections.singletonList(pswd));
        map.put("grant_type", Collections.singletonList("password"));
        map.put("scope", Collections.singletonList("read"));
        //HttpEntity
        HttpEntity httpEntity = new HttpEntity(map,httpHeaders);
        //获取 Token
        ResponseEntity<OAuth2AccessToken> responseEntity = restTemplate().exchange(tokenUrl, HttpMethod.POST,httpEntity,OAuth2AccessToken.class);
        Assert.assertEquals(true, responseEntity.getStatusCode().is2xxSuccessful());
        return responseEntity.getBody();
    }
}
