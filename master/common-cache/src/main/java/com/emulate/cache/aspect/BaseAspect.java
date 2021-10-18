package com.emulate.cache.aspect;

import com.emulate.cache.annotation.LocalCacheEvent;
import com.emulate.cache.annotation.LocalCachePut;
import com.emulate.cache.redis.service.RedisService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Method;

/**
 * @author hgr
 * @description: 注解切面基类
 * @date 2021/10/190:31
 */
@Repository
@RedisHash
public abstract class BaseAspect {

    protected String getPramsAssembleCacheKey(ProceedingJoinPoint point) {
        Method method = getMethod(point);
        return null;
    }

    protected Method getMethod(ProceedingJoinPoint point) {
        MethodSignature signature = (MethodSignature)point.getSignature();
        return signature.getMethod();
    }

    /**
     * @description:选择
     * @param point
     * @author hgr
     * @date 2021/10/19 0:42
     */
    protected Object chooseActionBusiness(ProceedingJoinPoint point) throws Throwable {
        Method method = getMethod(point);
        LocalCachePut localCachePut = method.getAnnotation(LocalCachePut.class);
        LocalCacheEvent localCacheEvent = method.getAnnotation(LocalCacheEvent.class);
        if (localCachePut != null) {
            return null;
        }

        if (localCacheEvent != null) {
            Object result = point.proceed();
            return point.proceed();
        }

        return null;
    }

    protected RedisService redisService(){
        return null;
    };
}
