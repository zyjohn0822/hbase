package com.hisense.hiatmp.hbase.annotation;

import java.lang.annotation.*;

/**
 * 表的字段名
 * @author zhangyong
 * @date 2020/12/22  9:51
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Column {
    String value() default "";
    String family() default "";
}
