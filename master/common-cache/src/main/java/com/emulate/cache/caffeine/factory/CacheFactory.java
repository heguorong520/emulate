package com.emulate.cache.caffeine.factory;

import com.emulate.cache.caffeine.service.CaffeineCache;
import com.emulate.cache.caffeine.service.DefaultCaffeineCache;
import com.emulate.cache.caffeine.service.LocalCache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @description: 工厂类
 * @author hgr
 * @date 2021/10/18 23:32
 */
public class CacheFactory {

    private static final String HASHMAP_KEY_PREFIX = "CAFFEINE:";
    private static final ConcurrentMap<String, LocalCache> MAP_CACHE = new ConcurrentHashMap<>(3);

    public static LocalCache build(int duration) {
        LocalCache cache = new CaffeineCache(duration);
        MAP_CACHE.put(HASHMAP_KEY_PREFIX + duration, cache);;
        return cache;
    }

    /**
     * @description: 获取对应的缓存
     * @author hgr
     * @param duration 缓存时间
     * @date 2021/10/18 23:32
     */
    public static LocalCache getCache(int duration) {
        LocalCache localCache =  MAP_CACHE.get(HASHMAP_KEY_PREFIX+duration);
        if (localCache == null) {
            return build(duration);
        }
        return localCache;
    }

    /**
     * @description: 删除对应的缓存
     * @author hgr
     * @param cacheKey 缓存的Key
     * @date 2021/10/18 23:32
     */
    public static void cleanAllCacheByKey(String cacheKey) {
        MAP_CACHE.forEach((index,localCache)->{
            localCache.delete(cacheKey);
        });
    }
}
