package com.pingan.takeout.manage.center.common;

import com.pingan.takeout.manage.center.annotation.RepeatSubmit;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.UUID;

@Component
@Aspect
@Slf4j
public class NoRepeatSubmitAspect {
    @Pointcut("@annotation(com.pingan.takeout.manage.center.annotation.RepeatSubmit)")
    public void repeatSubmit() {}
    @Autowired
    private RedisLock redisLock;

    @Around("repeatSubmit()")
    public void around(ProceedingJoinPoint jointPoint) {
        log.info("校验重复订单...");
        Long currentId = BaseContext.getCurrentId();
        String key = String.valueOf(currentId);
        Method method = ((MethodSignature)jointPoint.getSignature()).getMethod();

        RepeatSubmit annotation = method.getAnnotation(RepeatSubmit.class);

        String clientId = getClientId();
        boolean isSuccess = redisLock.tryLock(key,clientId,annotation.expireTime());

        if(isSuccess){
            try{
                jointPoint.proceed();
                log.info("订单不重复，可以提交");
            }catch(Throwable e){
                throw new RuntimeException(e);
            }
        }else{
            log.info("订单重复提交");
        }
    }
    public String getClientId(){
        return UUID.randomUUID().toString();
    }
}
