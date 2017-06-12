package com.province.platform.widgets;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.province.platform.cookies.CookieUtil;
import com.province.platform.helper.ResourceHelper;
import com.province.platform.helper.ServiceFactory;
import com.zzh.base.api.SiteRemoteService;
import com.zzh.base.api.entity.site.SiteContentItem;
import com.zzh.base.api.entity.site.SiteContentQueryRequest;
import com.zzh.base.api.entity.site.SiteModuleItem;
import com.zzh.base.api.entity.site.SiteSectionItem;
import com.zzh.base.api.entity.site.SiteSectionQueryRequest;
import com.zzh.base.api.entity.site.SiteTemplateCompanyRequest;
import com.zzh.common.constants.SiteContentKeyValue;
import com.zzh.common.constants.SiteTemplateConstants;
import com.zzh.user.api.UserRemoteService;
import com.zzh.user.api.entity.UserEntityInfo;

@Widget("widgets/header.html")
public class HeaderWidget implements FreeMakerWidget{
	
	Logger logger = LoggerFactory.getLogger(HeaderWidget.class);

	@Override
	public void referenceData(HttpServletRequest request, Map<String, Object> model) {
		long companyId = CookieUtil.getCompanyId(request);
		try {
			//获取site信息
			SiteRemoteService siteService = ServiceFactory.getBean("siteRemoteService", SiteRemoteService.class);
			SiteTemplateCompanyRequest  siteTemplateCompany = siteService.selectTemplateCompanyByCompanyId(companyId);
			if (siteTemplateCompany == null) {
				logger.error("companyId:"+companyId+"，查询模板为空");
				throw new Exception("SiteTemplateCompanyRequest查询为空");
			}
			//---------------查询logo--------------------
			SiteModuleItem logoModuleItem = siteService.selectModuleByInstanceIdAndCode(siteTemplateCompany.getId(), SiteTemplateConstants.MODULE_CODE_LOGO);
			if (logoModuleItem == null) {
				logger.error("logo模块查询为空");
				throw new Exception("logo模块查询为空");
			}
			SiteSectionQueryRequest  logoSectionQuery = new SiteSectionQueryRequest ();
			logoSectionQuery.setDisabled(true);
			logoSectionQuery.setModuleId(logoModuleItem.getId());
			logoSectionQuery.setOrderBy("sort asc");
			List<SiteSectionItem> logoSectionItems = siteService.querySiteSection(logoSectionQuery);
			if (logoSectionItems.size() > 0) {
				long imgId = logoSectionItems.get(0).getImage();
				String imgUrl = ResourceHelper.getDataUrl(imgId);
				model.put("logoImg", imgUrl);
			}
			
			//---------------查询导航栏--------------------
			SiteModuleItem navModuleItem = siteService.selectModuleByInstanceIdAndCode(siteTemplateCompany.getId(), SiteTemplateConstants.MODULE_CODE_NAV);
			if (navModuleItem == null) {
				logger.error("导航栏模块查询为空");
				throw new Exception("导航栏模块查询为空");
			}
			SiteSectionQueryRequest  navSectionQuery = new SiteSectionQueryRequest();
			navSectionQuery.setDisabled(true);
			navSectionQuery.setModuleId(navModuleItem.getId());
			navSectionQuery.setOrderBy("sort asc");
			List<SiteSectionItem> navSectionItems = siteService.querySiteSection(navSectionQuery);
			model.put("navItems", navSectionItems);
			
			//---------------查询微信二维码--------------------
			SiteContentQueryRequest siteContentQueryRequest = new SiteContentQueryRequest();
			siteContentQueryRequest.setCode(SiteTemplateConstants.MODULE_CODE_FOOTER);//footer 
			siteContentQueryRequest.setName(SiteContentKeyValue.QRCODE.getTitle());//二维码
			siteContentQueryRequest.setCompanyId(companyId);
			siteContentQueryRequest.setPageSize(Integer.MAX_VALUE);
			List<SiteContentItem> siteContentItems = siteService.querySiteContent(siteContentQueryRequest);
			if(CollectionUtils.isNotEmpty(siteContentItems)){
				model.put("qrcodeUrl", siteContentItems.get(0).getMetaValue());
			}else{
				logger.error("微信二维码查询为空");
			}
			
			//---------------查询是否用户登录--------------
			//判断用户是否登录
			boolean isLogin = CookieUtil.isLogin(request);
			model.put("isLogin", isLogin);
			//查询用户登录信息
			if(isLogin){
				Long userId = CookieUtil.getUserId(request);
				UserRemoteService userService = ServiceFactory.getBean("userRemoteService", UserRemoteService.class);
				UserEntityInfo userEntityInfo = userService.findByUserId(userId, companyId);
				if(StringUtils.isBlank(userEntityInfo.getAvatarUrl())){
					Properties config = ServiceFactory.getBean("config", Properties.class);
					userEntityInfo.setAvatarUrl(config.getProperty("default.avatar"));
				}
				model.put("userInfo", userEntityInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
