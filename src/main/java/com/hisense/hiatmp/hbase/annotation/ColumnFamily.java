package com.hisense.hiatmp.hbase.annotation;

import java.lang.annotation.*;

/**
 * @author zhangyong
 * @date 2020/12/22  9:53
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ColumnFamily {
    String value() default "";
}
