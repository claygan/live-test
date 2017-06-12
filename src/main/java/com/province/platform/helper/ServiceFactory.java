package com.province.platform.helper;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @Title: ServiceFactory.java
 * @Package com.ws.platform.core.module.wscms.service
 * @Description: TODO(用一句话描述该文件做什么)
 * @author luog
 * @date 2012-11-18 下午11:04:21
 * @version V1.0
 */
public class ServiceFactory implements ApplicationContextAware {

	private static ApplicationContext applicationContext;

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ServiceFactory.applicationContext = applicationContext;
	}

	public static ApplicationContext getApplicationContext() {
		return ServiceFactory.applicationContext;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getBean(String bean, Class<T> clazz) {
		return (T) applicationContext.getBean(bean);
	}
}
