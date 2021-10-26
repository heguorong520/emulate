package com.emulate.cache.aspect;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.emulate.cache.annotation.CMPKey;
import com.emulate.cache.annotation.LocalCacheEvent;
import com.emulate.cache.annotation.LocalCachePut;
import com.emulate.cache.caffeine.factory.CacheFactory;
import com.emulate.cache.caffeine.service.LocalCache;
import com.emulate.cache.enums.LocalCacheSubEventEnum;
import com.emulate.cache.enums.ParamDataTypeEnum;
import com.emulate.cache.redis.entity.CacheKeyEntity;
import com.emulate.cache.redis.entity.LocalCacheUpdateEntity;
import com.emulate.cache.redis.repository.CacheKeyRepository;
import com.emulate.cache.redis.service.RedisService;
import com.emulate.cache.redssion.RedissonLock;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

/**
 * @author hgr
 * @description: 注解切面基类
 * @date 2021/10/190:31
 */
@Slf4j
public abstract class BaseAspect {
    @Autowired
    private CacheKeyRepository keyRepository;
    @Autowired
    private RedisService redisService;
    @Value("${spring.application.name}")
    protected String applicationName;
    @Autowired
    protected RedissonLock redissonLock;

    protected static final ConcurrentMap<String, Lock> LOCK_MAP = new ConcurrentHashMap<>();

    /**
     * @description: 解析注解拼装Key
     * @author hgr
     * @date 2021/10/22 0:42
     */
    protected String getPramsAssembleCacheKey(ProceedingJoinPoint point) throws IllegalAccessException {
        Method method = getMethod(point);
        StringBuilder keyBuilder = new StringBuilder();
        Annotation[][] annotations = method.getParameterAnnotations();
        for (int i = 0; i < annotations.length; i++) {
            for (int j = 0; j < annotations[i].length; j++) {
                Object param = point.getArgs()[i];
                Annotation annotation = annotations[i][j];
                if (!(annotation instanceof CMPKey)) {
                    continue;
                }
                // 处理带有这个CMPKey的参数进行组装Key
                CMPKey cmpKey = (CMPKey)annotation;
                // java自带的数据类型MAP处理
                if (cmpKey.paramDataType().equals(ParamDataTypeEnum.BDT) && (param instanceof Map)) {
                    if (cmpKey.fields().length == 0) {
                        continue;
                    }
                    Map map = (Map)param;
                    for (String field : cmpKey.fields()) {
                        Object value = map.get(field);
                        if (ObjectUtil.isEmpty(value)) {
                            continue;
                        }
                        keyBuilder.append(value);
                    }
                    continue;
                }
                if (cmpKey.paramDataType().equals(ParamDataTypeEnum.BDT)) {
                    keyBuilder.append(param).append(":");
                    continue;
                }

                if (cmpKey.fields().length == 0) {
                    continue;
                }
                // 自定义类型处理
                Field[] fields = param.getClass().getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true);
                    Object value = field.get(param);
                    keyBuilder.append(value).append(":");
                }

            }
        }
        if (keyBuilder.length() > 0) {
            keyBuilder.deleteCharAt(keyBuilder.length() - 1);
        }
        return keyBuilder.toString();
    }

    /**
     * @description: 获取方法信息
     * @author hgr
     * @date 2021/10/22 0:41
     */
    protected Method getMethod(ProceedingJoinPoint point) {
        MethodSignature signature = (MethodSignature)point.getSignature();
        return signature.getMethod();
    }



    /**
     * @description: 缓存事件发布
     * @author hgr
     * @date 2021/10/22 0:40
     */
    public void publishLocalCacheEventMsg(String cacheKey, Integer time, LocalCacheSubEventEnum eventEnum,
        Object content, List<String> keys) {
        LocalCacheUpdateEntity updateEntity = new LocalCacheUpdateEntity();
        updateEntity.setKey(cacheKey);
        updateEntity.setTime(time);
        updateEntity.setEvenEnum(eventEnum);
        updateEntity.setContent(content);
        updateEntity.setKeys(keys);
        redisService.publishLocalCacheUpdateMsg(applicationName, updateEntity);
    }

    /**
     * key拼装服务名
     * 
     * @param point
     * @param keyPrefix
     * @return
     * @throws IllegalAccessException
     */
    protected String getKey(ProceedingJoinPoint point, String keyPrefix) throws IllegalAccessException {
        StringBuilder keyBuilder = new StringBuilder(applicationName);
        keyBuilder.append(":").append(keyPrefix).append(":").append(getPramsAssembleCacheKey(point));
        return keyBuilder.toString();
    }



    /**
     * 保存key用于清理缓存
     * 
     * @param id
     * @param keyPrefix
     * @param time
     */
    @Transactional
    protected void saveCacheKeyEntity(String id, String keyPrefix, Integer time) {
        CacheKeyEntity entity = new CacheKeyEntity();
        entity.setId(id);
        entity.setCreateTime(DateUtil.formatDateTime(new Date()));
        entity.setExpire(time.longValue());
        entity.setApplicationNameKeyPrefix(applicationName + ":" + keyPrefix);
        keyRepository.save(entity);
    }

    /**
     * @description: 通过前缀获取所有的Key
     * @author hgr
     * @date 2021/10/19 15:12
     */
    protected List<String> getCacheKeyList(String keyPrefix) {
        List<CacheKeyEntity> keyEntityList =
            keyRepository.findByApplicationNameKeyPrefix(applicationName + ":" + keyPrefix);
        return keyEntityList.stream().map(CacheKeyEntity::getId).collect(Collectors.toList());
    }

}
