package com.emulate.cache.annotation;

import com.emulate.cache.enums.ParamDataTypeEnum;

import java.lang.annotation.*;

/**
 * @author hgr
 * @description: cache method prams key 只在方法参数上使用 用于拼装key
 * @date 2021/10/190:03
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CMPKey {

    /**
     * @description: 参数数据类型
     */
    ParamDataTypeEnum paramDataType() default ParamDataTypeEnum.BDT;

    /**
     *@description: 字段 不填写默认取对象多有字段拼装 fieldValue1:fieldValue2:fieldValue3
     */
    String[] fields() default {};
}
