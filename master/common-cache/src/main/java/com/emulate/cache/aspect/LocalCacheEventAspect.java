package com.emulate.cache.aspect;

import cn.hutool.core.util.ObjectUtil;
import com.emulate.cache.annotation.LocalCacheEvent;
import com.emulate.cache.enums.LocalCacheSubEventEnum;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

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

            Object result = point.proceed();
            List<String> keyList = getCacheKeyList(localCacheEvent.keyPrefix());
            if (ObjectUtil.isNotEmpty(keyList))
                publishLocalCacheEventMsg(null, null, LocalCacheSubEventEnum.CLEAR, null, keyList);

            return result;
        } catch (Exception e) {
            log.info("【LocalCacheEvent】执行异常：{}", e.getMessage());
            throw e;
        } finally {
            log.debug("【LocalCacheEvent】执行完成耗时{}ms", System.currentTimeMillis() - start);
        }
    }
}
