package com.emulate.cache.annotation;

import com.emulate.cache.enums.ExpireTimeEnum;

import java.lang.annotation.*;

/**
 * @author hgr
 * @description: 本地缓存注解不建议使用在接口上
 * @date 2021/10/1823:46
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LocalCachePut {

    ExpireTimeEnum expire() default ExpireTimeEnum.second5;

    String keyPrefix() default "localCache";

    boolean dbLock() default false;
}
