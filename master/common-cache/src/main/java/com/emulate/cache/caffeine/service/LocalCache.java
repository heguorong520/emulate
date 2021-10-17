package com.emulate.cache.caffeine.service;

public interface LocalCache {

    Object get(String key);

    Object get(String key, Object defaultValue);

    void delete(String key);

    void set(String key, Object value);

    void set(String key, Object value, long expire);

    void removeAll();
}
