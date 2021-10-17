package com.emulate.cache.caffeine.factory;

import com.emulate.cache.caffeine.service.CaffeineCache;
import com.emulate.cache.caffeine.service.DefaultCaffeineCache;
import com.emulate.cache.caffeine.service.LocalCache;

import java.util.HashMap;
import java.util.Map;

public class CacheFactory {

    private static final LocalCache DEFAULT_CACHE = new DefaultCaffeineCache();

    private static final Map<String,LocalCache> MAP_CACHE = new HashMap<>();

    public static LocalCache build(int duration) {
        return new CaffeineCache(duration);
    }

    public static LocalCache getNonNullCache(String key) {
        LocalCache localCache = getCache(key);
        if (localCache == null) {
            return DEFAULT_CACHE;
        }
        return localCache;
    }

    public static LocalCache getCache(String key) {
        return MAP_CACHE.get(key);
    }
}
