package com.emulate.cache.redis.repository;

import com.emulate.cache.redis.entity.CacheKeyEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author hgr
 * @description: TODO
 * @date 2021/10/191:15
 */
@Repository
public interface CacheKeyRepository extends CrudRepository<CacheKeyEntity,String> {
    List<CacheKeyEntity> findByApplicationNameKeyPrefix(String keyPrefix);

}
