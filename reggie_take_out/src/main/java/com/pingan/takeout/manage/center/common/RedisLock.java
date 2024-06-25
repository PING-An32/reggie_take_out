package com.pingan.takeout.manage.center.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisLock {
    @Autowired
    private StringRedisTemplate redisTemplate;

    public boolean tryLock(String lockKey,String clientId,long seconds){
        return redisTemplate.opsForValue().setIfAbsent(lockKey,clientId,seconds, TimeUnit.SECONDS);
    }
}
