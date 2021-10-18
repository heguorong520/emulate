package com.emulate.cache.aspect;

import com.emulate.cache.annotation.LocalCachePut;
import com.emulate.cache.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;


/**
 * @author hgr
 * @description: 本地缓存切面处理
 * @date 2021/10/190:16
 */
@Aspect
@Slf4j
@Component
public class LocalCacheAspect extends BaseAspect {

    @Pointcut("@annotation(com.emulate.cache.annotation.LocalCachePut)")
    public void pointCut() {}

    @Around(value = "pointCut()")
    public Object LocalCache(ProceedingJoinPoint point) {

        return null;
    }


    @Override
    protected RedisService redisService() {
        return null;
    }
}
