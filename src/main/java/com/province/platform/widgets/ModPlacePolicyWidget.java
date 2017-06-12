package com.province.platform.widgets;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.province.platform.constants.NewsCategoryEnum;
import com.province.platform.cookies.CookieUtil;
import com.province.platform.helper.ServiceFactory;
import com.zzh.base.api.NewsRemoteService;
import com.zzh.base.api.constants.CheckStatusEnum;
import com.zzh.base.api.entity.NewsCategoryItem;
import com.zzh.base.api.entity.NewsCategoryQueryRequest;
import com.zzh.base.api.entity.NewsItem;
import com.zzh.base.api.entity.NewsQueryRequest;

@Widget("widgets/mod-place-policy.html")
public class ModPlacePolicyWidget implements FreeMakerWidget{
	
	Logger logger = LoggerFactory.getLogger(ModPlacePolicyWidget.class);
	
	private static final int PLACE_POLICY_MOD_PAGESIZE = 5;

	@Override
	public void referenceData(HttpServletRequest request,Map<String, Object> model) {

		long companyId = CookieUtil.getCompanyId(request);
		NewsRemoteService newsRemoteService = ServiceFactory.getBean("newsRemoteService", NewsRemoteService.class);
		//国家政策
		NewsQueryRequest newsQuery = new NewsQueryRequest();
		newsQuery.setType(NewsCategoryEnum.INDUSTRIAL_POLICY.getCode());//默认资讯动态
		newsQuery.setCompanyId(companyId);
		newsQuery.setDisabled(0);
		newsQuery.setCheckStatus(CheckStatusEnum.CHECK_PASS.getCode());// 已审批通过的
		newsQuery.setOrderBy("update_time desc");
		//获取国家政策的分类
		NewsCategoryQueryRequest newsCategoryQueryRequest = new NewsCategoryQueryRequest();
		newsCategoryQueryRequest.setCompanyId(companyId);
		newsCategoryQueryRequest.setPageSize(Integer.MAX_VALUE);
		newsCategoryQueryRequest.setType(NewsCategoryEnum.INDUSTRIAL_POLICY.getCode());			
		newsCategoryQueryRequest.setOrderBy("update_time desc");
		List<NewsCategoryItem> newsCategoryItems = newsRemoteService.queryCategorys(newsCategoryQueryRequest);
		long[] categoryIds = new long[1];
		for(NewsCategoryItem newsCategoryItem:newsCategoryItems){
			if("地方政策".equals(newsCategoryItem.getName())){
				categoryIds[0] = newsCategoryItem.getId();
			}
		}
		newsQuery.setCategoryIds(categoryIds);
		List<NewsItem> newsItems = newsRemoteService.queryNews(newsQuery, 0, PLACE_POLICY_MOD_PAGESIZE);
		model.put("policyNewsItems", newsItems);
		
	}

}
