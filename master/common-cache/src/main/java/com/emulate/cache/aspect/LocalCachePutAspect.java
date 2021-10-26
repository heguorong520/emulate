package com.emulate.cache.aspect;

import java.util.Date;

import com.emulate.cache.caffeine.factory.CacheFactory;
import com.emulate.cache.caffeine.service.LocalCache;
import com.emulate.cache.enums.LocalCacheSubEventEnum;
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
            log.debug("【LocalCachePut】开始执行注解参数dbLock:{},expire:{},keyPrefix:{}", localCachePut.dbLock(),
                localCachePut.expire(), localCachePut.keyPrefix());
            LocalCache localCache = CacheFactory.getCache(localCachePut.expire().time());
            String cacheKey = getKey(point, localCachePut.keyPrefix());
            log.debug("【LocalCachePut】Key:{}", cacheKey);
            Object result = localCache.get(cacheKey);
            if (result != null) {
                return result;
            }
            // 判断访问方法是否加锁加本地锁.根据Key进行加锁
            if (localCachePut.dbLock()) {
                return actionMethodLock(point, localCachePut, localCache, cacheKey);
            }
            return actionMethod(point, localCachePut,localCache, cacheKey);
        } catch (Exception e) {
            log.info("【LocalCachePut】执行异常：{}", e.getMessage());
        } finally {
            log.debug("【LocalCachePut】执行完成耗时{}ms", System.currentTimeMillis() - start);
        }
        return null;
    }

    /**
     * @description: 循环获取本地缓存数据
     * @author hgr
     * @date 2021/10/19 15:00
     */
    private Object getLocalCache(LocalCache localCache, String key, int count) throws InterruptedException {
        Thread.sleep(20);
        count++;
        Object result = localCache.get(key);
        if (result == null) {
            if (count > 5) {
                return null;
            }
            getLocalCache(localCache, key, count);
        }
        return result;
    }

    private Object actionMethodLock(ProceedingJoinPoint point, LocalCachePut localCachePut, LocalCache localCache,
        String cacheKey) throws Throwable {
        boolean lock = redissonLock.lock(cacheKey, 10);
        Object result;
        if (!lock) {
            result = getLocalCache(localCache, cacheKey, 0);
            return result;
        }
        try {
            result = actionMethod(point, localCachePut, localCache, cacheKey);
        } catch (Exception e) {
            throw e;
        } finally {
            redissonLock.release(cacheKey);
        }
        return result;
    }

    private Object actionMethod(ProceedingJoinPoint point, LocalCachePut localCachePut, LocalCache localCache,
        String cacheKey)

        throws Throwable {
        Object result = point.proceed();
        localCache.set(cacheKey, result == null ? "null" : result);
        publishLocalCacheEventMsg(cacheKey, localCachePut.expire().time(), LocalCacheSubEventEnum.NEW,
            result == null ? "" : result, null);
        saveCacheKeyEntity(cacheKey, localCachePut.keyPrefix(), localCachePut.expire().time());
        return result;
    }

}
