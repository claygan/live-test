package com.province.platform.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.province.platform.commons.InterceptAccess;


/** 
 * @ClassName: LoginIntercept
 * @Description: 登录拦截注解
 * 
 * @author ganshimin@zhongzhihui.com
 * @date: 2017年3月29日 下午1:48:44
 */  
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginIntercept {

    /**
     * 拦截类型
     * @return
     */
    InterceptAccess intercept() default  InterceptAccess.LOGIN;
}
