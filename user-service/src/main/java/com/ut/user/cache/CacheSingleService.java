package com.ut.user.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Component
@Slf4j
public class CacheSingleService implements ICacheService {
    @Autowired
    private JedisPool jedisPool;
    public String setex(String key, String value){
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.setex(key, 120, value);
        }catch (Exception e){
            log.error(e.getMessage());
            throw e;
        } finally {
            returnResource(jedis);
        }
    }

    public String setex(String key, int seconds, String value){
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.setex(key, seconds, value);
        } catch (Exception e){
            log.error(e.getMessage());
            throw e;
        }finally {
            returnResource(jedis);
        }
    }
    public String get(String key){
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.get(key);
        } catch (Exception e){
            log.error(e.getMessage());
            throw e;
        }finally {
            returnResource(jedis);
        }
    }
    public Jedis getResource(){
        return jedisPool.getResource();
    }
    public void returnResource(Jedis jedis){
        if(null != jedis)
        jedis.close();
    }
}