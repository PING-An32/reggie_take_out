package com.pingan.takeout.manage.center.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁用于防止重复提交订单
 */
@Component
public class RedisLock {
    @Autowired
    private StringRedisTemplate redisTemplate;
    private static final String ID_PREFIX = UUID.randomUUID().toString()+"-";
    private static final DefaultRedisScript<Long> UNLOCK_SCRIPT;//用于读取lua脚本
    private String lockKey;
    static {
        UNLOCK_SCRIPT = new DefaultRedisScript<>();
        UNLOCK_SCRIPT.setLocation(new ClassPathResource("unlock.lua"));//去类路径下寻找lua脚本
        UNLOCK_SCRIPT.setResultType(Long.class);
    }

    public boolean tryLock(String lockKey,long seconds){
        this.lockKey = lockKey;
        String threadId = ID_PREFIX + Thread.currentThread().getId();//生成一个与某线程相关的在Redis中的唯一Value，不同的JVM可能线程值相同，所以还要加UUID
        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(lockKey,threadId,seconds, TimeUnit.SECONDS);

        return Boolean.TRUE.equals(success);
    }
    public void unlock(){
        //调用lua脚本（变成一行代码了，原子性）
        redisTemplate.execute(
                UNLOCK_SCRIPT,
                Collections.singletonList(lockKey),
                ID_PREFIX + Thread.currentThread().getId()
        );//不关心返回值，如果成功则锁被删除，如果失败则锁已被其他人删除
    }
}
