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
import com.zzh.base.api.entity.NewsItem;
import com.zzh.base.api.entity.NewsQueryRequest;

@Widget("widgets/mod-hot-news.html")
public class ModHotNewsWidget implements FreeMakerWidget{
	
	Logger logger = LoggerFactory.getLogger(ModHotNewsWidget.class);
	
	private static final int HOT_NEWS_MOD_PAGESIZE = 8;

	@Override
	public void referenceData(HttpServletRequest request,Map<String, Object> model) {
		long companyId = CookieUtil.getCompanyId(request);
		NewsRemoteService newsService = ServiceFactory.getBean("newsRemoteService", NewsRemoteService.class);
		NewsQueryRequest newsQuery = new NewsQueryRequest();
		newsQuery.setType(NewsCategoryEnum.GENERAL_NEWS.getCode());//默认资讯动态
		newsQuery.setCompanyId(companyId);
		newsQuery.setDisabled(0);
		newsQuery.setCheckStatus(CheckStatusEnum.CHECK_PASS.getCode());// 已审批通过的
		newsQuery.setOrderBy("update_time desc");
		List<NewsItem> newsItems = newsService.queryNews(newsQuery, 0, HOT_NEWS_MOD_PAGESIZE);
		model.put("newNewsItems", newsItems);
		
	}

}
