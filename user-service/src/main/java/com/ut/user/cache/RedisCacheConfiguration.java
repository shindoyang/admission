package com.ut.user.cache;

import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@EnableCaching
public class RedisCacheConfiguration extends CachingConfigurerSupport {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.timeout}")
    private String timeout;

    @Value("${spring.redis.jedis.pool.max-idle}")
    private int maxIdle;

    @Value("${spring.redis.jedis.pool.max-wait}")
    private String maxWaitMillis;

    @Value("${spring.redis.password}")
    private String password;

    @Bean
    public JedisPool redisPoolFactory() {

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();

        jedisPoolConfig.setMaxIdle(maxIdle);

        jedisPoolConfig.setMaxWaitMillis(Long.valueOf(maxWaitMillis.substring(0,maxWaitMillis.length()-2)));

        JedisPool jedisPool = null;
        if(Strings.isNullOrEmpty(password)){
            jedisPool = new JedisPool(jedisPoolConfig, host, port, Integer.valueOf(timeout.substring(0,timeout.length()-2)));
        }else{
            jedisPool = new JedisPool(jedisPoolConfig, host, port, Integer.valueOf(timeout.substring(0,timeout.length()-2)), password);
        }

        return jedisPool;
    }
}