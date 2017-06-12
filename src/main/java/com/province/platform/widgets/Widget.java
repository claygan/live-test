/**   
* @Title: Widget.java
* @Package com.zzh.platform.web.module.col.config.annotation
* @Description: TODO(用一句话描述该文件做什么)
* @author Jupiter
* @date 2014-5-2 上午12:39:18
* @version V1.0   
*/
package com.province.platform.widgets;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ClassName: Widget
 * @Package com.zzh.platform.web.module.col.config.annotation
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author Jupiter
 * @date 2014-5-2 上午12:39:18
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Widget {
	
	String value() default "";
}
