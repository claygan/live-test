package com.province.platform.interceptor;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.text.MessageFormat;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;


/** 
 * @ClassName: AnnotationHandlerInterceptor
 * @Description: 通过注解实现调用指定的拦截器
 * 
 * @author ganshimin@zhongzhihui.com
 * @date: 2017年3月29日 下午4:31:13
 */  
public class AnnotationHandlerInterceptor extends HandlerInterceptorAdapter {

	private static final Logger LOG = LoggerFactory.getLogger(AnnotationHandlerInterceptor.class);

	/**
	 * 需要过滤的注解类型
	 */
	@SuppressWarnings("rawtypes")
	private Class annotationClass;

	/**
	 * 过滤器
	 */
	private AsyncHandlerInterceptor[] handlerInterceptors;

	/**
	 * 检测类或方法的注解
	 */
	private AnnotationTarget annotationTarget;

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void init() throws Exception {
		if (this.annotationClass == null) {
			throw new Exception("属性annotationClass为空");
		} else if (handlerInterceptors == null) {
			throw new Exception("属性handlerInterceptors为空");
		}
		if (annotationTarget == null) {
			Target target = (Target) annotationClass.getAnnotation(Target.class);
			if (target == null) {
				throw new Exception(MessageFormat.format("注解{0}没有定义{1}注解", annotationClass, target));
			}
			ElementType[] eTypes = target.value();
			if (ArrayUtils.contains(eTypes, ElementType.METHOD) && ArrayUtils.contains(eTypes, ElementType.TYPE)) {
				annotationTarget = AnnotationTarget.TYPE_AND_METHOD;

			} else if (ArrayUtils.contains(eTypes, ElementType.METHOD)) {
				annotationTarget = AnnotationTarget.METHOD;

			} else if (ArrayUtils.contains(eTypes, ElementType.TYPE)) {
				annotationTarget = AnnotationTarget.TYPE;

			} else {
				throw new Exception(MessageFormat.format("注解{0}中{1}注解没有定义{2}或{3}", annotationClass, target, ElementType.TYPE, AnnotationTarget.METHOD));
			}
		}
		printDebugInitLog();
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (handler instanceof HandlerMethod) {
			// 拦截到的拦截器的Anootation
			Annotation runtimeAnnotation = getInterceptorAnnotation(handler);
			if (runtimeAnnotation != null) {
				return invokeHandlerInterceptorsPreHandle(request, response, handler, runtimeAnnotation);
			}
		}
		return true;
	}

	/**
	 * 调用过滤器
	 * 
	 * @param request
	 * @param response
	 * @param handler
	 * @return
	 * @throws Exception
	 */
	private boolean invokeHandlerInterceptorsPreHandle(HttpServletRequest request, HttpServletResponse response, Object handler, Annotation runtimeAnnotation) throws Exception {

		for (AsyncHandlerInterceptor handlerInterceptor : handlerInterceptors) {

			if (handlerInterceptor instanceof AnnotationInterceptorAware) {
				((AnnotationInterceptorAware) handlerInterceptor).setAnnotation(runtimeAnnotation);
			}

			// if(handler instanceof AnnotationInterceptor){
			// ((AnnotationInterceptor) handler).setAnnotation(annotations);
			// }

			if (!handlerInterceptor.preHandle(request, response, handler)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @param handlerInterceptors
	 *            the handlerInterceptors to set
	 */
	public void setHandlerInterceptors(AsyncHandlerInterceptor[] handlerInterceptors) {
		this.handlerInterceptors = handlerInterceptors;
	}

	/**
	 * @param annotationClass
	 *            the annotationClass to set
	 */
	public void setAnnotationClass(String annotationClass) {
		try {
			this.annotationClass = Class.forName(annotationClass);
		} catch (ClassNotFoundException e) {
			LOG.error(MessageFormat.format("找不到类 ==> {0}", annotationClass), e);
		}
	}

	/**
	 * @param annotationTarget
	 *            the annotationTarget to set
	 */
	public void setAnnotationTarget(AnnotationTarget annotationTarget) {
		this.annotationTarget = annotationTarget;
	}

	/**
	 * 查找Annotation
	 * 
	 * @param handler
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Annotation getInterceptorAnnotation(Object handler) {
		HandlerMethod method = (HandlerMethod) handler;
		Annotation annotation = null;
		switch (annotationTarget) {
		case METHOD:
			annotation = method.getMethod().getAnnotation(annotationClass);
			break;
		case TYPE:
			annotation = method.getBeanType().getAnnotation(annotationClass);
			break;
		case TYPE_AND_METHOD:
			annotation = method.getMethod().getAnnotation(annotationClass);
			if (annotation == null) {
				annotation = method.getBeanType().getAnnotation(annotationClass);
			}
			break;
		}
		return annotation;
	}

	/**
	 * 打印Debug日志
	 */
	private void printDebugInitLog() {
		switch (annotationTarget) {
		case TYPE_AND_METHOD:
			LOG.debug("拦截标有{}注解的Controller类和方法, 调用过滤器{}", annotationClass, handlerInterceptors);
			break;
		case TYPE:
			LOG.debug("拦截标有{}注解的Controller类, 调用过滤器{}", annotationClass, handlerInterceptors);
			break;
		case METHOD:
			LOG.debug("拦截标有{}注解的Controller类方法, 调用过滤器{}", annotationClass, handlerInterceptors);
		}
	}

	enum AnnotationTarget {

		TYPE_AND_METHOD, TYPE, METHOD
	}

}
