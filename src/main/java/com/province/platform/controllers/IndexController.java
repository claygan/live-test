package com.province.platform.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.province.platform.commons.ApiResult;
import com.province.platform.commons.GlobalDefine;
import com.province.platform.commons.Pager;
import com.province.platform.constants.NewsCategoryEnum;
import com.province.platform.cookies.CookieUtil;
import com.province.platform.helper.ResourceHelper;
import com.province.platform.pojos.dto.IndexNewsDto;
import com.zzh.base.api.NewsRemoteService;
import com.zzh.base.api.SiteRemoteService;
import com.zzh.base.api.ThemedActivityRemoteService;
import com.zzh.base.api.constants.CheckStatusEnum;
import com.zzh.base.api.entity.NewsCategoryItem;
import com.zzh.base.api.entity.NewsCategoryQueryRequest;
import com.zzh.base.api.entity.NewsItem;
import com.zzh.base.api.entity.NewsQueryRequest;
import com.zzh.base.api.entity.site.SiteModuleItem;
import com.zzh.base.api.entity.site.SiteSectionItem;
import com.zzh.base.api.entity.site.SiteSectionQueryRequest;
import com.zzh.base.api.entity.site.SiteTemplateCompanyRequest;
import com.zzh.common.constants.SiteTemplateConstants;
import com.zzh.course.api.CourseRemoteService;

@Controller
@RequestMapping(value={"","/"})
public class IndexController {
	private static Logger logger = LoggerFactory.getLogger(IndexController.class);
	@Resource
	private SiteRemoteService siteRemoteService;
	@Resource
	private NewsRemoteService newsRemoteService;
	@Resource
	private ThemedActivityRemoteService themedActivityRemoteService;
	@Resource
	private CourseRemoteService courseRemoteService;
	/** 
	 * @Description:首页“最新资讯”展示个数
	 */  
	private static final int INDEX_NEW_NEWS_SHOW_SIZE = 8;
	/** 
	 * @Description:首页“电商活动”下最新资讯展示个数
	 */  
	private static final int INDEX_POLICY_NEWS_SHOW_SIZE = 4;
	/** 
	 * @Description:首页“资讯动态”展示资讯个数 
	 */  
	private static final int INDEX_CATEGORY_NEWS_SHOW_SIZE = 12;
	
	/** 
	 * @Description:热门连接显示个数
	 */  
	private static final int HOT_URL_MOD_PAGESIZE = 8;
	 
	/** 
	 * @Description:电商活动（互联网+）资讯推送分类
	 */  
	private static final int PLATFORM_CATEGORY_ACTIVITY_ID = 1006;
	
	
	@RequestMapping({ "", "/index" })
	public String toIndex(HttpServletRequest request,HttpServletResponse response, Model model) {
		Long companyId = CookieUtil.getCompanyId(request);
		//轮播图
		SiteTemplateCompanyRequest siteTemplateCompany = siteRemoteService.selectTemplateCompanyByCompanyId(companyId);
		if (siteTemplateCompany == null) {
			logger.error("companyId:"+companyId+", siteTemplateCompany查询为空");
		}
		long instanceId = siteTemplateCompany.getId();
		SiteModuleItem siteModuleItem = siteRemoteService.selectModuleByInstanceIdAndCode(instanceId, SiteTemplateConstants.MODULE_CODE_CAROUSEL_FIGURE);
		if (siteModuleItem == null) {
			logger.error("轮播图查询为空");
		}
		SiteSectionQueryRequest siteSectionQuery = new SiteSectionQueryRequest();
		siteSectionQuery.setDisabled(true);
		siteSectionQuery.setModuleId(siteModuleItem.getId());
		siteSectionQuery.setOrderBy("sort asc");
		List<SiteSectionItem> siteSectionItems = siteRemoteService.querySiteSection(siteSectionQuery);
		for (SiteSectionItem item : siteSectionItems) {
			item.setImgUrl(ResourceHelper.getDataUrl(item.getImage()));
		}
		model.addAttribute("indexCarousels", siteSectionItems);
		//热门链接
		SiteModuleItem hotModuleItem = siteRemoteService.selectModuleByInstanceIdAndCode(instanceId, SiteTemplateConstants.MODULE_HOT_LINK);
		if (hotModuleItem == null) {
			logger.error("热门链接查询为空");
		}
		SiteSectionQueryRequest hotSectionQuery = new SiteSectionQueryRequest();
		hotSectionQuery.setDisabled(true);
		hotSectionQuery.setModuleId(hotModuleItem.getId());
		hotSectionQuery.setPageSize(HOT_URL_MOD_PAGESIZE);
		hotSectionQuery.setOrderBy("sort asc");
		List<SiteSectionItem> hotSectionItems = siteRemoteService.querySiteSection(hotSectionQuery);
		for (SiteSectionItem item : hotSectionItems) {
			item.setImgUrl(ResourceHelper.getDataUrl(item.getImage()));
		}
		model.addAttribute("hotLinkItems", hotSectionItems);
		//最新资讯
		NewsQueryRequest newsQuery = new NewsQueryRequest();
		newsQuery.setType(NewsCategoryEnum.GENERAL_NEWS.getCode());//默认资讯动态
		newsQuery.setCompanyId(companyId);
		newsQuery.setDisabled(0);
		newsQuery.setCheckStatus(CheckStatusEnum.CHECK_PASS.getCode());// 已审批通过的
		newsQuery.setOrderBy("update_time desc");
		List<NewsItem> newsItems = newsRemoteService.queryNews(newsQuery, 0, INDEX_NEW_NEWS_SHOW_SIZE);
		model.addAttribute("newNewsItems", newsItems);
		//电商活动最新资讯
		NewsQueryRequest newsQuery2 = new NewsQueryRequest();
		newsQuery2.setType(NewsCategoryEnum.GENERAL_NEWS.getCode());//默认资讯动态
		newsQuery2.setCompanyId(companyId);
		newsQuery2.setDisabled(0);
		newsQuery2.setCheckStatus(CheckStatusEnum.CHECK_PASS.getCode());// 已审批通过的
		newsQuery2.setOrderBy("update_time desc");
		//获取资讯动态的分类
		NewsCategoryQueryRequest newsCategoryQueryRequest = new NewsCategoryQueryRequest();
		newsCategoryQueryRequest.setCompanyId(companyId);
		newsCategoryQueryRequest.setPageSize(Integer.MAX_VALUE);
		newsCategoryQueryRequest.setType(NewsCategoryEnum.GENERAL_NEWS.getCode());			
		newsCategoryQueryRequest.setOrderBy("update_time desc");
		newsCategoryQueryRequest.setPlatformCategoryId(PLATFORM_CATEGORY_ACTIVITY_ID);//互联网+下对应的分类-（电商活动）
		List<NewsCategoryItem> newsCategoryItems = newsRemoteService.queryCategorys(newsCategoryQueryRequest);
		if(CollectionUtils.isNotEmpty(newsCategoryItems)){
			newsQuery2.setCategoryIds(new long[]{newsCategoryItems.get(0).getId()});
		}
		List<NewsItem> newsItems2 = newsRemoteService.queryNews(newsQuery2, 0, INDEX_POLICY_NEWS_SHOW_SIZE);
		model.addAttribute("policyNewsItems", newsItems2);
		
		return "/views/index";
	}

	
	
	/** 
	 * @Title: queryIndexNews
	 * @Description: 查询首页资讯热点
	 * @author ganshimin@zhongzhihui.com
	 * @param request
	 * @param response
	 * @return  
	 */  
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ResponseBody
	@RequestMapping("/queryIndexNews")
	public ApiResult queryIndexNews(HttpServletRequest request, HttpServletResponse response, Model model){
		ApiResult result = new ApiResult();
		Pager pager = new Pager();
		try {
			Long companyId = CookieUtil.getCompanyId(request);
			List<NewsCategoryItem>  newsCategoryItems = newsRemoteService.selectNewsCategoryForIndexShow(companyId, NewsCategoryEnum.GENERAL_NEWS.getCode());
			List<IndexNewsDto> indexNewsDtos = new ArrayList<IndexNewsDto>();
			for (int i = 0; i < newsCategoryItems.size(); i++) {
				NewsQueryRequest newsQuery = new NewsQueryRequest();
				newsQuery.setCategoryIds(new long[]{newsCategoryItems.get(i).getId()});
				newsQuery.setCompanyId(companyId);
				newsQuery.setDisabled(0);//未失效的
				newsQuery.setType(NewsCategoryEnum.GENERAL_NEWS.getCode());
				newsQuery.setCheckStatus(CheckStatusEnum.CHECK_PASS.getCode());// 已审批通过的
				newsQuery.setOrderBy("create_time desc");// 发布时间倒序
				List<NewsItem> newsItems = newsRemoteService.queryNews(newsQuery, 0, INDEX_CATEGORY_NEWS_SHOW_SIZE);
				//存入dto
				IndexNewsDto indexNewsDto = new IndexNewsDto();
				indexNewsDto.setCategoryName(newsCategoryItems.get(i).getName());
				indexNewsDto.setSlogan(newsCategoryItems.get(i).getSlogan());
				//左侧数据，取前6条
				List<NewsItem> leftNewsItems = newsItems.subList(0, newsItems.size() > (INDEX_CATEGORY_NEWS_SHOW_SIZE/2)?INDEX_CATEGORY_NEWS_SHOW_SIZE/2:newsItems.size());
				indexNewsDto.setLeftNewsItems(leftNewsItems);
				//左侧数据，取后6条
				List<NewsItem> rightNewsItems = null;
				if(newsItems.size() > (INDEX_CATEGORY_NEWS_SHOW_SIZE/2)){
					rightNewsItems = newsItems.subList(INDEX_CATEGORY_NEWS_SHOW_SIZE/2, newsItems.size() > INDEX_CATEGORY_NEWS_SHOW_SIZE ? INDEX_CATEGORY_NEWS_SHOW_SIZE:newsItems.size());
				}
				indexNewsDto.setRightNewsItems(rightNewsItems);
				indexNewsDtos.add(indexNewsDto);
			}
			pager.setResultList(indexNewsDtos);
			result.setData(pager);
		} catch (Exception e) {
			e.printStackTrace();
			result.setMsg("系统出错");
			result.setError(GlobalDefine.resultCode.INTERNAL_ERROR);
		}
		return result;
	}
	
	
}
