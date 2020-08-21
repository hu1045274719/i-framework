package com.hu.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ClassName RequestMapping
 * @Description 请求映射
 * @Author hunan
 * @Date 14:17 2020/8/12
 * @Version 1.0
**/
@Target({ElementType.TYPE, ElementType.METHOD})  // 该注解能标注在类和方法上
@Retention(RetentionPolicy.RUNTIME) // 该注解运行期有效
public @interface RequestMapping {
    String value() default "";
}
