package com.emulate.cache.redis.entity;

import com.emulate.cache.enums.LocalCacheSubEventEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author hgr
 * @description: 本地缓存更新消息实体
 * @date 2021/10/21 23:52
 */
@Data
public class LocalCacheUpdateEntity implements Serializable {

    /**
     * 缓存内容
     */
    private Object content;
    /**
     * 1=清除缓存2=新增本地缓存
     */
    private LocalCacheSubEventEnum evenEnum;
    /**
     * 缓存时间
     */
    private Integer time;
    /**
     * 缓存key
     */
    private String key;

    /**
     * 缓存key
     */
    private List<String> keys;
}
