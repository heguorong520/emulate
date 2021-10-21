package com.emulate.cache.redis.listener;

import cn.hutool.core.util.ObjectUtil;
import com.emulate.cache.annotation.LocalCachePut;
import com.emulate.cache.caffeine.factory.CacheFactory;
import com.emulate.cache.enums.LocalCacheSubEventEnum;
import com.emulate.cache.redis.entity.CacheKeyEntity;
import com.emulate.cache.redis.entity.LocalCacheUpdateEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author hgr
 * @description: 本地缓存更新消息-订阅消息监听
 * @date 2021/10/21 22:35
 */
@Slf4j
public class LocalCacheRedisListener implements MessageListener {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        LocalCacheUpdateEntity localCacheUpdateEntity =
            (LocalCacheUpdateEntity)redisTemplate.getValueSerializer().deserialize(message.getBody());
        if (localCacheUpdateEntity.getEvenEnum().equals(LocalCacheSubEventEnum.NEW)) {
            CacheFactory.getCache(localCacheUpdateEntity.getTime()).set(localCacheUpdateEntity.getKey(),
                localCacheUpdateEntity.getContent());
            log.info("收到新增本地缓存事件通知,新增key:{}", localCacheUpdateEntity.getKey());
        }

        if (localCacheUpdateEntity.getEvenEnum().equals(LocalCacheSubEventEnum.CLEAR)
            && ObjectUtil.isNotEmpty(localCacheUpdateEntity.getKeys())) {
            for (String key : localCacheUpdateEntity.getKeys()) {
                CacheFactory.cleanAllCacheByKey(key);
                log.info("收到清除本地缓存事件通知,清除key:{}", key);
            }
        }
    }

}
