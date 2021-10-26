package com.emulate.cache.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum RedisConnedPreFixEnum {

	REDIS_CONNECTION_PREFIX("redis://", "Redis地址配置前缀");

	private final String constant_value;
	private final String constant_desc;
}
