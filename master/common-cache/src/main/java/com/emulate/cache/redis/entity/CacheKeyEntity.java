package com.emulate.cache.redis.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

/**
 * @author hgr
 * @description: 存储缓存key集合
 * @date 2021/10/19 1:08
 */
@RedisHash(value = "cache:key:hash",timeToLive = 60*60)
@Data
public class CacheKeyEntity {
    @Id
    private String id;
    @Indexed
    private String applicationNameKeyPrefix;

    private String createTime;

    @TimeToLive
    private Long expire;
}
