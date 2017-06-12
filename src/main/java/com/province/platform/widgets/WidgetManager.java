package com.province.platform.widgets;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * @ClassName: Widget
 * @Package com.zzh.platform.web.module.col.widget
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author Jupiter
 * @date 2014-4-30 上午9:51:43
 * 
 */
public class WidgetManager {

	protected Properties config;

	protected FreeMarkerConfigurer freeMarkerConfigurer;

	protected FreeMarkerViewResolver freeMakerViewResolver;

	protected Map<String, FreeMakerWidget> widgetMap = new HashMap<String, FreeMakerWidget>();

	private String widgetClassPath;

	public void setConfig(Properties config) {
		this.config = config;
	}

	public void setFreeMakerViewResolver(FreeMarkerViewResolver freeMakerViewResolver) {
		this.freeMakerViewResolver = freeMakerViewResolver;
	}

	public void setFreeMarkerConfigurer(FreeMarkerConfigurer freeMarkerConfigurer) {
		this.freeMarkerConfigurer = freeMarkerConfigurer;
	}
	
	public void setWidgetClassPath(String widgetClassPath) {
		this.widgetClassPath = widgetClassPath;
	}

	public WidgetManager() {
		
	}

	/**
	 * @Title: initFreeMakerWidgets
	 * @Description:
	 * @return void
	 */
	@SuppressWarnings("rawtypes")
	public void initFreeMakerWidgets() {
		try {
			ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
			MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory();
			Resource[] resources = resourcePatternResolver.getResources(widgetClassPath);
			if (resources != null) {
				for (Resource resource : resources) {
					MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
					AnnotationMetadata annotationMetaData = metadataReader.getAnnotationMetadata();
					boolean hasAnnotation = annotationMetaData.hasAnnotation(Widget.class.getName());
					//
					if (hasAnnotation) {
						Class widgetClass = Class.forName(annotationMetaData.getClassName());
						Object object = widgetClass.newInstance();
						if (object instanceof FreeMakerWidget) {
							FreeMakerWidget freeMakerWidget = (FreeMakerWidget) object;
							Map<String, Object> attributes = annotationMetaData.getAnnotationAttributes(Widget.class.getName());
							String path = (String) attributes.get("value");
							//
							if (StringUtils.isBlank(path)) {
								throw new Exception("the widget has no set path : " + annotationMetaData.getClassName());
							}
							//
							if (widgetMap.containsKey(path)) {
								throw new Exception("the widget path has exists. " + path);
							}

							widgetMap.put(path, freeMakerWidget);
						}
					}
				}
			}

		} catch (Exception e) {
			//
			e.printStackTrace();
		}
	}

	/**
	 * @Title: get
	 * @Description: 页面调用widget方法
	 * @return String
	 * @param widgetPath
	 * @param params
	 * @return
	 */
	public String get(String widgetPath) {
		return get(widgetPath, null);
	}

	/**
	 * @Title: get
	 * @Description: 页面调用widget方法, 待参数的
	 * @return String
	 * @param widgetPath
	 * @param params
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public String get(String widgetPath, Map<String, Object> params) {
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			HttpServletRequest request = requestAttributes.getRequest();
			//
			if (params != null) {
				model.putAll(params);
			}
			//
			Enumeration requestAttrNames = request.getAttributeNames();
			if (requestAttrNames != null) {
				while (requestAttrNames.hasMoreElements()) {
					String attrName = (String) requestAttrNames.nextElement();
					model.put(attrName, request.getAttribute(attrName));
				}
			}
			//
			HttpSession session = request.getSession();
			Enumeration sessionAttrNames = session.getAttributeNames();
			while (sessionAttrNames.hasMoreElements()) {
				String attrName = (String) sessionAttrNames.nextElement();
				model.put(attrName, session.getAttribute(attrName));
			}
			//
			FreeMakerWidget freeMakerWidget = null;
			if (!widgetMap.containsKey(widgetPath)) {
				freeMakerWidget = new FreeMakerWidget() {
					@Override
					public void referenceData(HttpServletRequest request, Map<String, Object> model) {
					}
				};
			} else {
				freeMakerWidget = widgetMap.get(widgetPath);
				freeMakerWidget.referenceData(request, model);
			}
			//
			model.put("request", request);
			return process(widgetPath, model);
		} catch (Exception e) {
			//
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * @Title: process
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @return String
	 * @param ftl
	 * @param map
	 * @return
	 * @throws IOException
	 * @throws TemplateException
	 */
	private String process(String ftl, Map<String, Object> model) throws TemplateException, IOException {
		Writer out = null;
		try {
			out = new StringWriter();
			Configuration configuration = freeMarkerConfigurer.getConfiguration();
			//
			Map<String, Object> attributes = freeMakerViewResolver.getAttributesMap();
			if (attributes != null) {
				Set<Entry<String, Object>> entrys = attributes.entrySet();
				for (Entry<String, Object> entry : entrys) {
					if (entry.getValue() instanceof String) {
						model.put(entry.getKey(), entry.getValue());
					}
				}
			}
			Template template = configuration.getTemplate(ftl);
			template.process(model, out);
			return out.toString();
		} catch (TemplateException e) {
			//
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			//
			e.printStackTrace();
			throw e;
		} finally {
			if (out != null) {
				out.flush();
				out.close();
			}
		}
	}

}
