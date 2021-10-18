package com.emulate.cache.caffeine.service;

import com.emulate.cache.caffeine.builder.CaffeineBuilder;
import com.github.benmanes.caffeine.cache.Cache;

/**
 * @description: 封装本地缓存的方法
 * @author hgr
 * @date 2021/10/18 23:42
 */
public class CaffeineCache implements LocalCache {
    private Cache<String, Object> cache;

    public CaffeineCache(int duration) {
        this.cache = CaffeineBuilder.cache(duration);
    }

    @Override
    public Object get(String key) {
        return get(key, null);
    }

    @Override
    public Object get(String key, Object defaultValue) {
        Object o = cache.getIfPresent(key);
        if (o == null) {
            return defaultValue;
        }
        return o;
    }

    @Override
    public void delete(String key) {
        cache.invalidate(key);
    }

    @Override
    public void set(String key, Object value) {
        cache.put(key, value);
    }

    @Override
    public void removeAll() {
        cache.invalidateAll();
    }
}
