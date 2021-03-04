package com.yhh.studysys.common.MenuConfig.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Menu {

    String icon();

    String index();

    String title();

    boolean status() default true;

    int order() default Integer.MAX_VALUE;

    String parent() default "";
}
