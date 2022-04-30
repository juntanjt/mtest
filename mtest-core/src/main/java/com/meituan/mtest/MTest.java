package com.meituan.mtest;

import java.lang.annotation.*;

/**
 *
 * @author Jun Tan
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MTest {

    String name() default "";

    Class<?> testClass();

    String method();

    String beanName() default "";

    int overload() default -1;

}
