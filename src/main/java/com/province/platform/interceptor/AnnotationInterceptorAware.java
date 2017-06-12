package com.province.platform.interceptor;

import java.lang.annotation.Annotation;

/**
 * 通过{@code AnnotationHandlerInterceptor}对http请求按照Handler标注的Annotation调用指定的拦截器
 * 拦截器实现这个接口，可以将拦截到的Handler的Controller类和方法上标注的运行时Annotation注入 <code>
 * <mvc:interceptors>
 * 		<!-- 验证是否登录 -->
 * 		<mvc:interceptor>
 * 			<mvc:mapping path="/**" />
 * 			<bean class=
"com.zzh.college.interceptors.AnnotationHandlerInterceptor">
 * 				<property name="annotationTarget">
 * 					<value>TYPE_AND_METHOD</value>
 * 				</property>
 * 				<property name="annotationClass"
 * 					value="com.zzh.college.request.LoginIntercept" />
 * 				<property name="handlerInterceptors">
 * 					<array>
 * 						<bean class=
"com.zzh.college.interceptors.LoginInterceptor"></bean>
 * 					</array>
 * 				</property>
 * 			</bean>
 * 		</mvc:interceptor>
 * </code> <code>
 * public class LoginInterceptor extends HandlerInterceptorAdapter implements AnnotationInterceptorAware {
 * 
 * 	private LoginIntercept loginIntercept;
 * public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
 * 			throws Exception {
 * 		if (loginIntercept.intercept() == InterceptAccess.LOGIN) {
 * 			return checkLogin(request, response, handler);
 * 		}
 * 		return super.preHandle(request, response, handler);
 * 	}
 * 	
 * 		&#64;Override
 * 	public void setAnnotation(Annotation annotation) {
 * 		loginIntercept = (LoginIntercept) annotation;
 * 	}
 * 
 * }
 * </code>
 * 
 *           TODO <b>之后会加入泛型扩展</b>
 * 
 */
public interface AnnotationInterceptorAware {

	/**
	 * Annotation的注入方法
	 * 
	 * @param annotation
	 */
	void setAnnotation(Annotation annotation);
}
