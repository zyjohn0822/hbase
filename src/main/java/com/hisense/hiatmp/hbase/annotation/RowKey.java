package com.hisense.hiatmp.hbase.annotation;

import com.hisense.hiatmp.hbase.api.RowKeyStrategy;

import java.lang.annotation.*;

/**
 * 指定row key 的策略方式、字段名、组合项目等
 * @author zhangyong
 * @date 2020/12/23  15:22
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RowKey {
    //指定row key的字段名
    String value() default "";
    //row的生成策略
    Class<? extends RowKeyStrategy> strategy();
}
