package com.emulate.cache.redssion.yml;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;


@Data
@ConfigurationProperties(prefix = "redisson.lock.server")
public class RedissonYml {

	/**
	 * redis主机地址，ip：port目前只使用单机
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
