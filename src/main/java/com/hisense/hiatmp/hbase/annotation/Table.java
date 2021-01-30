package com.hisense.hiatmp.hbase.annotation;

import java.lang.annotation.*;

/**
 * 声明HBase的表名
 * @author zhangyong
 * @date 2020/12/22  9:47
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Table {
    String value() default "";
}
