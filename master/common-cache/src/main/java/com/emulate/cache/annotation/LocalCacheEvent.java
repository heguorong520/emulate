package com.emulate.cache.annotation;

import java.lang.annotation.*;

import com.emulate.cache.enums.ExpireTimeEnum;

/**
 * @author hgr
 * @description: 本地缓存注解
 * @date 2021/10/1823:46
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LocalCacheEvent {

    ExpireTimeEnum expire() default ExpireTimeEnum.second5;

    String keyPrefix() default "localCache";

}
