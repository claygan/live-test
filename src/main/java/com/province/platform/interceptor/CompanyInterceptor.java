package com.province.platform.interceptor;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.UrlPathHelper;

import com.province.platform.cookies.CookieModule;
import com.province.platform.cookies.CookieModuleConfigurer;
import com.province.platform.cookies.CookieUtil;
import com.province.platform.cookies.RootContextCookieModule;
import com.province.platform.helper.ServiceFactory;
import com.zzh.base.api.CompanyRemoteService;
import com.zzh.base.api.SiteRemoteService;
import com.zzh.base.api.entity.site.SiteModuleItem;
import com.zzh.base.api.entity.site.SiteSectionItem;
import com.zzh.base.api.entity.site.SiteSectionQueryRequest;
import com.zzh.base.api.entity.site.SiteTemplateCompanyRequest;
import com.zzh.common.constants.SiteTemplateConstants;

/** 
 * @ClassName: CompanyInterceptor
 * @Description: 平台全局操作拦截器
 * 
 * @author ganshimin@zhongzhihui.com
 * @date: 2017年3月29日 下午4:22:19
 */  
public class CompanyInterceptor extends HandlerInterceptorAdapter{
	Logger logger = LoggerFactory.getLogger(CompanyInterceptor.class);
	@Resource
	private CookieModuleConfigurer cookieModuleConfigurer;
	
	private AntPathMatcher antPathMatcher = new AntPathMatcher();
	private UrlPathHelper urlPathHelper = new UrlPathHelper();
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		//初始化cookie
		CookieModule cookieModule = null;
		if (this.cookieModuleConfigurer != null) {
			cookieModule = new RootContextCookieModule(this.cookieModuleConfigurer.getClientName2CfgMap(), this.cookieModuleConfigurer.getName2CfgMap(), request, response);
		}
		request.setAttribute(CookieModule.COOKIE, cookieModule);
		
		//处理平台id
		Long companyId = CookieUtil.getCompanyId(request);
		if(companyId == null){
			String domain = request.getServerName();
			CompanyRemoteService companyService = ServiceFactory.getBean("companyRemoteService", CompanyRemoteService.class);
			long companyIdValue = companyService.findCompanyIdByDomain(domain);
			if(companyIdValue >0 ){
				CookieUtil.writeCookieForCompany(request, String.valueOf(companyIdValue));
			}else{
				logger.error("获取平台id失败");
				throw new Exception("CompanyInterceptor：获取平台id失败");
			}
		}
		//获取基本路径
		String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
		basePath = basePath + request.getContextPath();
		request.setAttribute("basePath", basePath);
		
		//获取当前访问导航
		try {
			SiteRemoteService siteService = ServiceFactory.getBean("siteRemoteService", SiteRemoteService.class);
			if (siteService == null) {
				throw new Exception("SiteRemoteService为空");
			}
			SiteTemplateCompanyRequest  siteTemplateCompany = siteService.selectTemplateCompanyByCompanyId(companyId);
			if (siteTemplateCompany == null) {
				logger.error("companyId:"+companyId+"，查询模板为空");
				throw new Exception("SiteTemplateCompanyRequest查询为空");
			}
			SiteModuleItem siteModuleItem = siteService.selectModuleByInstanceIdAndCode(siteTemplateCompany.getId(), SiteTemplateConstants.MODULE_CODE_NAV);
			if (siteModuleItem == null) {
				logger.error("导航栏模块查询模板为空");
				throw new Exception("导航栏模块查询为空");
			}
			SiteSectionQueryRequest siteSectionQuery = new SiteSectionQueryRequest();
			siteSectionQuery.setDisabled(true);
			siteSectionQuery.setModuleId(siteModuleItem.getId());
			siteSectionQuery.setOrderBy("sort asc");
			List<SiteSectionItem> siteSectionItems = siteService.querySiteSection(siteSectionQuery);
			String mod = "";
			for (int i = 0 ; i < siteSectionItems.size(); i++) {
				SiteSectionItem siteSectionItem = siteSectionItems.get(i);
				String link = siteSectionItem.getLink(); 
				link = link.substring(0, link.lastIndexOf("/")) +"/**/*";
				if(matchHelp(link,request)){
					mod = siteSectionItem.getTitle();
				}
				if (i == siteSectionItems.size() -1 && StringUtils.isEmpty(mod)) {
					mod = "首页";
				}
			}
			request.setAttribute("modPath", mod);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return super.preHandle(request, response, handler);
	}
	
	public boolean matchHelp(String pattern, HttpServletRequest request) {
		return antPathMatcher.match(pattern, urlPathHelper.getLookupPathForRequest(request));
	}
}
