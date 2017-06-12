package com.province.platform.helper;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import com.province.platform.commons.CloudFastJsonHttpMessageConverter;


/**
 * 统一的异常处理
 * @author saleson
 *
 */
public class PlatformExceptionResolver extends SimpleMappingExceptionResolver{

	private static Logger LOG = LoggerFactory.getLogger(PlatformExceptionResolver.class);
	
	
	private CloudFastJsonHttpMessageConverter jsonMessageConverter;
	
	protected ModelAndView doResolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex){
		
		LOG.error(handler + ex.getMessage(), ex);
		if(handler instanceof HandlerMethod){
			HandlerMethod method = (HandlerMethod) handler;
			if (method.getMethod().getAnnotation(ResponseBody.class) != null) {
				outputJson(request, response);
				return null;
			} 
		}
		return super.doResolveException(request, response, handler, ex);
	}
	
	
	
	
	/**
	 * 输出json
	 * 
	 * @param response
	 */
	private void outputJson(HttpServletRequest request, HttpServletResponse response) {

//		CloudFastJsonHttpMessageConverter jsonMessageConverter = getJsonMessageConverter(request);
//		String json = jsonMessageConverter != null ? jsonMessageConverter.toJson(apiResult)
//				: JSON.toJSONString(apiResult);
//		String encoding = "UTF-8";
//		response.setContentType("text/plain;charset=" + encoding);
//		response.setCharacterEncoding(encoding);
//		PrintWriter out = null;
//		try {
//			out = response.getWriter();
//			out.write(json);
//			out.flush();
//		} catch (IOException e) {
//			LOG.error("登录拦截出现异常(outputJson)：", e);
//		}
	}

	/**
	 * 跳转到登录页面
	 * 
	 * @param request
	 * @param response
	 */
	/*private ModelAndView redirectToError(HttpServletRequest request, HttpServletResponse response) {
		//String path = request.getContextPath() + "/";
		//String requestURI = request.getRequestURI();
		//requestURI = requestURI.substring(requestURI.indexOf(path) + path.length());
		//String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path;
		return new ModelAndView("/view/404");
	}*/




	/**
	 * 从mvc的ApplicationContext中获取到CloudFastJsonHttpMessageConverter
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unused")
	private CloudFastJsonHttpMessageConverter getJsonMessageConverter(HttpServletRequest request) {
		if (jsonMessageConverter == null) {
			synchronized (this) {
				if (jsonMessageConverter == null) {
					ApplicationContext application = (ApplicationContext) request
							.getAttribute(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE);
					Map<String, AbstractHandlerMethodAdapter> map = application
							.getBeansOfType(AbstractHandlerMethodAdapter.class);
					RequestMappingHandlerAdapter handlerAdapter = (RequestMappingHandlerAdapter) map.values().iterator()
							.next();
					List<HttpMessageConverter<?>> messageConverterList = handlerAdapter.getMessageConverters();
					jsonMessageConverter = findCloudFastJsonHttpMessageConverter(messageConverterList);
				}
			}
		}
		return jsonMessageConverter;
	}
	
	
	/**
	 * find the CloudFastJsonHttpMessageConverter object by messageConverterList
	 * 
	 * @param list
	 * @return
	 */
	private CloudFastJsonHttpMessageConverter findCloudFastJsonHttpMessageConverter(
			List<HttpMessageConverter<?>> messageConverterList) {
		for (int i = 0, size = messageConverterList.size(); i < size; i++) {
			HttpMessageConverter<?> messageConverter = messageConverterList.get(i);
			if (messageConverter instanceof CloudFastJsonHttpMessageConverter) {
				return (CloudFastJsonHttpMessageConverter) messageConverter;
			}
		}
		return null;
	}




	public void setJsonMessageConverter(CloudFastJsonHttpMessageConverter jsonMessageConverter) {
		this.jsonMessageConverter = jsonMessageConverter;
	}
}
