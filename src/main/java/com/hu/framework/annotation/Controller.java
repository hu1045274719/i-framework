package com.hu.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ClassName Controller
 * @Description 注解在类上，表示该类可以被DispatcherServlet调用
 * @Author hunan
 * @Date 14:13 2020/8/12
 * @Version 1.0
**/
@Target(ElementType.TYPE)  // 该注解只能标注在类上
@Retention(RetentionPolicy.RUNTIME) // 该注解运行期有效
public @interface Controller {

}
