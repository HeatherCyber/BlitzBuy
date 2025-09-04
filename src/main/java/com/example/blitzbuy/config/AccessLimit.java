package com.example.blitzbuy.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * AccessLimit annotation:custom annotation for rate limiting
 * @author Heather
 * @version 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AccessLimit {
    int seconds(); // time interval
    int maxCount(); // maximum number of requests
    boolean needLogin() default true; // whether need login

}
