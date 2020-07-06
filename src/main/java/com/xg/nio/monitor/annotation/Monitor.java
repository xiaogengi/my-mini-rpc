package com.xg.nio.monitor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @program: nio-rpc
 * @description:
 * @author: gzk
 * @create: 2020-07-06 11:42
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Monitor {
    String name() default "";
}
