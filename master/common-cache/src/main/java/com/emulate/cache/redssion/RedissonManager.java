package com.emulate.cache.redssion;

import com.emulate.cache.enums.RedisConnedPreFixEnum;
import com.emulate.cache.redssion.yml.RedissonYml;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;


import lombok.extern.slf4j.Slf4j;


@Slf4j
public class RedissonManager {

	private Config config = new Config();

	private RedissonClient redisson = null;

	public RedissonManager() {
	}

	public RedissonManager(RedissonYml redissonProperties) {
		try {
			config = RedissonConfigFactory.getInstance().createConfig(redissonProperties);
			redisson =  Redisson.create(config);
		} catch (Exception e) {
			log.error("Redisson init error", e);
			throw new IllegalArgumentException("please input correct configurations");
		}
	}

	public RedissonClient getRedisson() {
		return redisson;
	}

	/**
	 * Redisson连接方式配置工厂
	 * 双重检查锁
	 */
	static class RedissonConfigFactory {

		private RedissonConfigFactory() {
		}

		private static volatile RedissonConfigFactory factory = null;

		public static RedissonConfigFactory getInstance() {
			if (factory == null) {
				synchronized (Object.class) {
					if (factory == null) {
						factory = new RedissonConfigFactory();
					}
				}
			}
			return factory;
		}

		private Config config = new Config();

		/**
		 * 根据连接类型获取对应连接方式的配置,基于策略模式
		 *
		 * @param redissonProperties redisson配置
		 * @return Config
		 */
		Config createConfig(RedissonYml redissonProperties) {
			Config config = new Config();
			try {
				String address = redissonProperties.getAddress();
				String password = redissonProperties.getPassword();
				int database = redissonProperties.getDatabase();
				String redisAddr = RedisConnedPreFixEnum.REDIS_CONNECTION_PREFIX.getConstant_value() + address;
				config.useSingleServer().setAddress(redisAddr);
				config.useSingleServer().setDatabase(database);
				if (StringUtils.isNotBlank(password)) {
					config.useSingleServer().setPassword(password);
				}
				log.info("初始化[standalone]方式Config,redisAddress:" + address);
			} catch (Exception e) {
				log.error("standalone Redisson init error", e);
				e.printStackTrace();
			}
			return config;
		}
	}
}
