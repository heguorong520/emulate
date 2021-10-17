package com.emulate.cache.caffeine.service;


import com.emulate.cache.caffeine.builder.CaffeineBuilder;
import com.github.benmanes.caffeine.cache.Cache;

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
    public void set(String key, Object value, long expire) {
        set(key, value);
    }

    @Override
    public void removeAll() {
        cache.invalidateAll();
    }
}
