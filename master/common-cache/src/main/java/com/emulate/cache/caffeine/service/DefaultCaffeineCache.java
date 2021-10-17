package com.emulate.cache.caffeine.service;

public class DefaultCaffeineCache extends CaffeineCache {
    public DefaultCaffeineCache() {
        this(60);
    }

    public DefaultCaffeineCache(int duration) {
        super(duration);
    }
}
