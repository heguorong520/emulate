package com.emulate.cache.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

/**
* @description: 缓存start配置
* @author hgr
* @date 2021/10/19 15:25
*/
@Configuration
@EnableRedisRepositories(basePackages = {"com.emulate.cache"})
@ComponentScan(value = "com.emulate.cache")
public class CacheConfiguration {

}
