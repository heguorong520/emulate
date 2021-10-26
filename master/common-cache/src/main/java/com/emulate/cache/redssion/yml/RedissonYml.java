package com.emulate.cache.redssion.yml;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * Redisson配置映射类
 *
 * @author pangu
 * @date 2020-10-22
 */
@Data
@ConfigurationProperties(prefix = "redisson.lock.server")
public class RedissonYml {

	/**
	 * redis主机地址，ip：port，有多个用半角逗号分隔
	 */
	private String address;
	/**
	 * redis连接密码
	 */
	private String password;
	/**
	 * 选取数据库
	 */
	private int database;

}
