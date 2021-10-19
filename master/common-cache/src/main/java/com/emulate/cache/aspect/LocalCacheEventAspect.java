package com.emulate.cache.aspect;

import com.emulate.cache.annotation.LocalCacheEvent;
import com.emulate.cache.annotation.LocalCachePut;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author hgr
 * @description: 本地缓存切面处理
 * @date 2021/10/19 0:16
 */
@Aspect
@Slf4j
@Component
public class LocalCacheEventAspect extends BaseAspect {

    @Pointcut(value = "@annotation(com.emulate.cache.annotation.LocalCacheEvent)")
    private void pointCut() {}

    @Around(value = "pointCut() && @annotation(localCacheEvent)")
    public Object around(ProceedingJoinPoint point, LocalCacheEvent localCacheEvent) throws Throwable {
        long start = new Date().getTime();
        try {
            log.debug("【LocalCacheEvent】开始执行注解参数{}",localCacheEvent);
            Object result = this.chooseActionBusiness(point,localCacheEvent);
            return result;
        } catch (Exception e) {
            log.info("【LocalCacheEvent】执行异常：{}",e);
            throw  e;
        } finally {
            log.debug("【LocalCacheEvent】执行完成耗时{}ms",System.currentTimeMillis()-start);
        }
    }
}
