package com.emulate.cache.annotation;

import java.lang.annotation.*;


/**
 * @author hgr
 * @description: 本地缓存注解不建议使用在接口上
 * @date 2021/10/1823:46
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LocalCacheEvent {
    String keyPrefix() default "localCache";
}
