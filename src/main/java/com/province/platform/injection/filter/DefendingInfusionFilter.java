package com.province.platform.injection.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.province.platform.injection.SqlHttpServletRequestWrapper;
import com.province.platform.injection.XssHttpServletRequestWrapper;




/**
 * 防xss,sql注入的过滤器
 * web.xml配置如下：
 * <code>
 * <!-- 防注入 -->
 *  <filter>
 *    <filter-name>defendingInfusionFilter</filter-name>
 *    <filter-class>com.province.platform.http.injection.filter.DefendingInfusionFilter
 * 		</filter-class>
 * 	<!-- 防sql注入 -->
 *    <init-param>
 *      <param-name>sqlFilter</param-name>
 *      <param-value>true</param-value>
 *    </init-param>
 *    <!-- 防xss注入 -->
 *    <init-param>
 *      <param-name>xssFilter</param-name>
 *      <param-value>true</param-value>
 *    </init-param>
 *  </filter>
 *  <filter-mapping>
 *    <filter-name>defendingInfusionFilter</filter-name>
 *    <url-pattern>/*</url-pattern>
 *  </filter-mapping>
 * </code>
 * @author saleson
 *
 */
public class DefendingInfusionFilter implements Filter {

	private String[][] sqlKeyValues = { { "；", ";" } };
	protected FilterConfig filterConfig = null;
	protected boolean sqlFilter = false;
	protected boolean xssFilter = false;

	@Override
	public void init(FilterConfig config) throws ServletException {
		this.filterConfig = config;
		// key:value|key:value|key:value
		this.sqlFilter = StringUtils.equalsIgnoreCase(filterConfig.getInitParameter("sqlFilter"), "true");
		this.xssFilter = StringUtils.equalsIgnoreCase(filterConfig.getInitParameter("xssFilter"), "true");

		if (sqlFilter) {
			initSqlInfusion();
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest requestWrapper = (HttpServletRequest) request;
		if (sqlFilter) {
			requestWrapper = new SqlHttpServletRequestWrapper(requestWrapper, sqlKeyValues);
		}
		if(xssFilter){
			requestWrapper = new XssHttpServletRequestWrapper(requestWrapper);
		}
		chain.doFilter(requestWrapper, response);
	}

	@Override
	public void destroy() {

	}

	/**
	 * 初始化sql注入的参数
	 * 
	 * @param keywords
	 */
	private void initSqlInfusion() {
		String keywords = filterConfig.getInitParameter("sqlKeywords");
		if (StringUtils.isNotEmpty(keywords)) {
			String[] arys = keywords.split("\\|");
			sqlKeyValues = new String[arys.length][2];
			for (int i = 0, size = arys.length; i < size; i++) {
				String[] words = arys[i].split(":");
				if (words.length == 2) {
					sqlKeyValues[i] = words;
				}
			}
		}
	}
}
