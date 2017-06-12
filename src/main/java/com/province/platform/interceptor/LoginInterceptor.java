package com.province.platform.interceptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
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
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import com.alibaba.fastjson.JSON;
import com.province.platform.annotations.LoginIntercept;
import com.province.platform.commons.ApiResult;
import com.province.platform.commons.CloudFastJsonHttpMessageConverter;
import com.province.platform.commons.InterceptAccess;
import com.province.platform.cookies.CookieUtil;
import com.province.platform.utils.SessionUtil;
import com.zzh.user.api.entity.UserEntityInfo;


/** 
 * @ClassName: LoginInterceptor
 * @Description: 登录拦截
 * 
 * @author ganshimin@zhongzhihui.com
 * @date: 2017年3月29日 下午1:54:54
 */  
public class LoginInterceptor extends HandlerInterceptorAdapter implements AnnotationInterceptorAware {

	private static Logger LOG = LoggerFactory.getLogger(LoginInterceptor.class);
	private LoginIntercept loginIntercept;
	private static final String loginMsg = "您还未登录，请先登录！";
	private CloudFastJsonHttpMessageConverter jsonMessageConverter;
	private String loginURL = "/login/index";

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (handler instanceof HandlerMethod) {
			if (loginIntercept.intercept() == InterceptAccess.LOGIN) {
				return checkLogin(request, response, handler);
			}
		}
		return super.preHandle(request, response, handler);
	}

	/**
	 * 检验是否登录，如果没有登录，响应未登录的json串，并返回false. <br>
	 * <b>如果是session为空，cookie不为空，依赖于 {@link OrganInterceptor} 填充session数据</b><br>
	 * 
	 * @param request
	 * @return
	 */
	private boolean checkLogin(HttpServletRequest request, HttpServletResponse response, Object handler) {
		Object sessionUser = SessionUtil.getUserInfo(request);
		Long userId = CookieUtil.getUserId(request);
		if(userId == null){
			if(sessionUser != null){
				CookieUtil.writeCookieForUser(request, String.valueOf(((UserEntityInfo)sessionUser).getUserId()));
			}
			return noLogined(request, response, handler);
		}
		return true;
	}

	public boolean noLogined(HttpServletRequest request, HttpServletResponse response, Object handler) {
		HandlerMethod method = (HandlerMethod) handler;
		if (method.getMethod().getAnnotation(ResponseBody.class) != null) {
			outputJson(request, response);
		} else {
			redirectToLogin(request, response);
		}
		return false;
	}

	@Override
	public void setAnnotation(Annotation annotation) {
		loginIntercept = (LoginIntercept) annotation;
	}

	/**
	 * 从mvc的ApplicationContext中获取到CloudFastJsonHttpMessageConverter
	 * 
	 * @param request
	 * @return
	 */
	private CloudFastJsonHttpMessageConverter getJsonMessageConverter(HttpServletRequest request) {
		if (jsonMessageConverter == null) {
			synchronized (this) {
				if (jsonMessageConverter == null) {
					ApplicationContext application = (ApplicationContext) request.getAttribute(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE);
					Map<String, AbstractHandlerMethodAdapter> map = application.getBeansOfType(AbstractHandlerMethodAdapter.class);
					RequestMappingHandlerAdapter handlerAdapter = (RequestMappingHandlerAdapter) map.values().iterator().next();
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
	private CloudFastJsonHttpMessageConverter findCloudFastJsonHttpMessageConverter(List<HttpMessageConverter<?>> messageConverterList) {
		for (int i = 0, size = messageConverterList.size(); i < size; i++) {
			HttpMessageConverter<?> messageConverter = messageConverterList.get(i);
			if (messageConverter instanceof CloudFastJsonHttpMessageConverter) {
				return (CloudFastJsonHttpMessageConverter) messageConverter;
			}
		}
		return null;
	}

	/**
	 * 输出json
	 * 
	 * @param response
	 */
	private void outputJson(HttpServletRequest request, HttpServletResponse response) {
		ApiResult<Object> apiResult = new ApiResult<Object>();
		apiResult.setError(-100);
		apiResult.setMsg(loginMsg);

		CloudFastJsonHttpMessageConverter jsonMessageConverter = getJsonMessageConverter(request);
		String json = jsonMessageConverter != null ? jsonMessageConverter.toJson(apiResult) : JSON.toJSONString(apiResult);
		String encoding = "UTF-8";
		response.setContentType("text/plain;charset=" + encoding);
		response.setCharacterEncoding(encoding);
		PrintWriter out = null;
		try {
			out = response.getWriter();
			out.write(json);
			out.flush();
		} catch (IOException e) {
			LOG.error("登录拦截出现异常(outputJson)：", e);
		}
	}

	/**
	 * 跳转到登录页面
	 * 
	 * @param request
	 * @param response
	 */
	private void redirectToLogin(HttpServletRequest request, HttpServletResponse response) {
		String path = request.getContextPath() + "/";
		String requestURI = request.getRequestURI();
		requestURI = requestURI.substring(requestURI.indexOf(path) + path.length());
		String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path;
		try {
			response.setCharacterEncoding("utf-8");
			response.sendRedirect(basePath + loginURL);
		} catch (IOException e) {
			LOG.error("登录拦截出现异常(redirectToLogin)：", e);
		}
	}

	public String getLoginURL() {
		return loginURL;
	}

	public void setLoginURL(String loginURL) {
		this.loginURL = loginURL;
	}
}
