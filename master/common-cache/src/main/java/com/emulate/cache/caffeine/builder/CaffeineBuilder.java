package com.emulate.cache.caffeine.builder;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.TimeUnit;

/**
 * @description: 缓存构建对象
 * @author hgr
 * @date 2021/10/18 23:31
 */
public class CaffeineBuilder {

    public static Cache<String, Object> cache(int duration) {
        return cache(128, 1000, duration);
    }

    public static Cache<String, Object> cache() {
        return cache(128, 1000, 60);
    }

    /**
     * @description: 构建方法
     * @author hgr
     * @date 2021/10/18 23:31
     */
    public static Cache<String, Object> cache(int minSize, int maxSize, int expireSeconds) {
        return Caffeine.newBuilder().initialCapacity(minSize)// 初始大小
            .maximumSize(maxSize)// 最大数量
            .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)// 过期时间
            .build();
    }

}
