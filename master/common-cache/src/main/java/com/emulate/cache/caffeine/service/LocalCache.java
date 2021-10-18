package com.emulate.cache.caffeine.service;


/**
* @description: 本地缓存接口定义
* @author hgr
* @date 2021/10/18 23:44
*/
public interface LocalCache {

    Object get(String key);

    Object get(String key, Object defaultValue);

    void delete(String key);

    void set(String key, Object value);

    void removeAll();
}
