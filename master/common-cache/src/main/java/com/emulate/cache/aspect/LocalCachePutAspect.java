package com.emulate.cache.aspect;

import java.util.Date;

import com.emulate.cache.redis.service.RedisService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.emulate.cache.annotation.LocalCachePut;

import lombok.extern.slf4j.Slf4j;

/**
 * @author hgr
 * @description: 本地缓存切面处理
 * @date 2021/10/190:16
 */
@Aspect
@Slf4j
@Component
public class LocalCachePutAspect extends BaseAspect {


    @Pointcut(value = "@annotation(com.emulate.cache.annotation.LocalCachePut)")
    private void pointCut() {}

    @Around(value = "pointCut() && @annotation(localCachePut)")
    public Object around(ProceedingJoinPoint point, LocalCachePut localCachePut) throws Throwable {
        long start = new Date().getTime();
        try {

            log.debug("【LocalCachePut】开始执行注解参数dbLock:{},expire:{},keyPrefix:{}",localCachePut.dbLock(),localCachePut.expire(),localCachePut.keyPrefix());
            Object result = this.chooseActionBusiness(point, localCachePut);
            return result;
        } catch (Exception e) {
           log.info("【LocalCachePut】执行异常：{}",e);
        } finally {
            log.debug("【LocalCachePut】执行完成耗时{}ms",System.currentTimeMillis()-start);
        }
        return null;
    }
}
