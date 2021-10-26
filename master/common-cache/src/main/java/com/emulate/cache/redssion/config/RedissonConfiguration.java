package com.emulate.cache.redssion.config;

import com.emulate.cache.redssion.RedissonLock;
import com.emulate.cache.redssion.RedissonManager;
import com.emulate.cache.redssion.yml.RedissonYml;
import org.redisson.Redisson;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import lombok.extern.slf4j.Slf4j;


/**
 * Redisson自动化配置
 *
 * @author pangu
 * @date 2020-10-20
 */
@Slf4j
@Configuration
@ConditionalOnClass(Redisson.class)
@EnableConfigurationProperties(RedissonYml.class)
public class RedissonConfiguration {

	@Bean
	@ConditionalOnMissingBean
	@Order(value = 2)
	public RedissonLock redissonLock(RedissonManager redissonManager) {
		RedissonLock redissonLock = new RedissonLock();
		redissonLock.setRedissonManager(redissonManager);
		log.info("[RedissonLock]组装完毕");
		return redissonLock;
	}

	@Bean
	@ConditionalOnMissingBean
	@Order(value = 1)
	public RedissonManager redissonManager(RedissonYml redissonProperties) {
		return new RedissonManager(redissonProperties);
	}
}
