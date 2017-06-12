package com.province.platform.widgets;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.province.platform.cookies.CookieUtil;
import com.province.platform.helper.ServiceFactory;
import com.zzh.base.api.SiteRemoteService;
import com.zzh.base.api.entity.site.SiteContentItem;
import com.zzh.base.api.entity.site.SiteModuleItem;
import com.zzh.base.api.entity.site.SiteTemplateCompanyRequest;
import com.zzh.common.constants.SiteTemplateConstants;
import com.zzh.common.constants.SiteContentKeyValue;

@Widget("widgets/footer.html")
public class FooterWidget implements FreeMakerWidget {
	Logger logger = LoggerFactory.getLogger(FooterWidget.class);

	@Override
	public void referenceData(HttpServletRequest request, Map<String, Object> model) {
		// foot信息
		long companyId = CookieUtil.getCompanyId(request);
		SiteRemoteService siteService = ServiceFactory.getBean("siteRemoteService", SiteRemoteService.class);
		SiteTemplateCompanyRequest siteTemplateCompany = siteService.selectTemplateCompanyByCompanyId(companyId);
		if (siteTemplateCompany == null) {
			logger.error("companyId:" + companyId + ", siteTemplateCompany查询为空");
		}
		SiteModuleItem siteModuleItem = siteService.selectModuleByInstanceIdAndCode(siteTemplateCompany.getId(), SiteTemplateConstants.MODULE_CODE_FOOTER);
		if (siteModuleItem == null) {
			logger.error("网站底部footer模块查询为空");
		}
		List<SiteContentItem> siteContentItems = siteService.selectSiteContenByModuleId(siteModuleItem.getId());
		for (SiteContentItem siteContentItem : siteContentItems) {
			if (siteContentItem.getMetaKey().equals(SiteContentKeyValue.getTitleByCode(SiteTemplateConstants.STATISTICS_CODE))) {
				model.put("statisticsCode", siteContentItem.getMetaValue());
			}
			if (siteContentItem.getMetaKey().equals(SiteContentKeyValue.getTitleByCode(SiteTemplateConstants.FOOTER_SPONSOR))) {
				model.put("footerSponsor", siteContentItem.getMetaValue());
			}
			if (siteContentItem.getMetaKey().equals(SiteContentKeyValue.getTitleByCode(SiteTemplateConstants.FOOTER_CONTRACTORS))) {
				model.put("footerContractors", siteContentItem.getMetaValue());
			}
			if (siteContentItem.getMetaKey().equals(SiteContentKeyValue.getTitleByCode(SiteTemplateConstants.FOOTER_COPYRIGHT))) {
				model.put("footerCopyright", siteContentItem.getMetaValue());
			}
			if (siteContentItem.getMetaKey().equals(SiteContentKeyValue.getTitleByCode(SiteTemplateConstants.TECHNICAL_SUPPORT))) {
				model.put("technicalSupport", siteContentItem.getMetaValue());
			}
			if (siteContentItem.getMetaKey().equals(SiteContentKeyValue.getTitleByCode(SiteTemplateConstants.FILING_NUMBER))) {
				model.put("filingNumber", siteContentItem.getMetaValue());
			}
		}
	}

}
