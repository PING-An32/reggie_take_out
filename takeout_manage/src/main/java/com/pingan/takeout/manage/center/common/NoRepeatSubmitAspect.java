package com.pingan.takeout.manage.center.common;

import com.pingan.takeout.manage.center.annotation.RepeatSubmit;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
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
        //当对MethodSignature对象调用getMethod()方法,会返回一个描述签名的方法对象
        //Method对象是java.lang.reflect包下的对象，包含了一个方法的 方法名，返回类型，参数类型和其他元数据
        //可以通过Method.getName     Method.getReturnType    Method.getAnnotation调用
        //jointPoint.getSignature()返回的是在当前连接点伤被执行的方法的签名（和Method是一个东西）
        //同样可以通过signature.getName     signature.getReturnType
        //但是Signature对象无法获得方法的注解，所以要转成Method对象
        Signature signature = jointPoint.getSignature();
        Method method = ((MethodSignature)signature).getMethod();

        RepeatSubmit annotation = method.getAnnotation(RepeatSubmit.class);//得到连接点方法的注解

        //此处将当前用户id作为key
        String key = String.valueOf(currentId);
        //通过注解的值得到过期时间
        boolean isSuccess = redisLock.tryLock(key,annotation.expireTime());

        if(isSuccess){
            try{
                jointPoint.proceed();
                log.info("订单不重复，可以提交");
            }catch(Throwable e){
                throw new RuntimeException(e);
            }
        }else{
            log.info("订单重复提交");
            throw new CustomException("订单重复提交");
        }
    }
}
