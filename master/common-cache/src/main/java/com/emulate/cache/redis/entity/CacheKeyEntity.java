package com.emulate.cache.redis.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

/**
 * @author hgr
 * @description: 存储缓存key集合
 * @date 2021/10/19 1:08
 */
@RedisHash
@Data
public class CacheKeyEntity {
    @Id
    private String id;

    private String applicationNameKeyPrefix;

    private String createTime;

    @TimeToLive
    private Long expire;
}
