package com.province.platform.controllers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
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
import com.zzh.base.api.AddServiceRemoteService;
import com.zzh.base.api.NewsRemoteService;
import com.zzh.base.api.entity.NewsCategoryItem;
import com.zzh.base.api.entity.NewsCategoryQueryRequest;
import com.zzh.base.api.entity.NewsDetailItem;
import com.zzh.base.api.entity.NewsItem;
import com.zzh.base.api.entity.NewsQueryRequest;
import com.zzh.base.api.entity.Pageable;
import com.zzh.common.constants.AddServiceConstants;
import com.zzh.common.utils.LangUtil;

@Controller
@RequestMapping("/profession")
public class ProfessionController {
	
	private static final Logger logger = LoggerFactory.getLogger(ProfessionController.class);
	
	/**
	 * @Description:资讯动态分页大小
	 */
	private static final int PROFESSION_PAGE_SIZE = 6;
	
	public static final String  VER = "1.0";
	
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	@Resource
	private NewsRemoteService newsRemoteService;
	
	@Resource
	private AddServiceRemoteService addServiceRemoteService;
	
	/**
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * 异步加载资讯动态列表
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ResponseBody
	@RequestMapping("/queryNews")
	public ApiResult queryNews(HttpServletRequest request, HttpServletResponse response,Model model,Pager pager){
		ApiResult result = new ApiResult();
		String title = request.getParameter("keyWord");
		String categoryId = request.getParameter("categoryId");
		String page = request.getParameter("page");
		Long companyId = CookieUtil.getCompanyId(request);
		try {
			//获取资讯动态的分类
			NewsCategoryQueryRequest newsCategoryQueryRequest = new NewsCategoryQueryRequest();
			newsCategoryQueryRequest.setCompanyId(companyId);
			newsCategoryQueryRequest.setPageSize(Integer.MAX_VALUE);
			newsCategoryQueryRequest.setType(NewsCategoryEnum.GENERAL_NEWS.getCode());			
			newsCategoryQueryRequest.setOrderBy("update_time desc");
			List<NewsCategoryItem> newsCategoryItems = newsRemoteService.queryCategorys(newsCategoryQueryRequest);
			//分页查询资讯动态列表
			NewsQueryRequest newsQueryRequest = new NewsQueryRequest();
			newsQueryRequest.setCompanyId(companyId);
			if(StringUtils.isBlank(categoryId) || "-1".equals(categoryId)){
				long [] categoryIds = new long[newsCategoryItems.size()];
				for (int i = 0; i < categoryIds.length; i++) {
					categoryIds[i] = newsCategoryItems.get(i).getId();
				}
				newsQueryRequest.setCategoryIds(categoryIds);
			}else{
				long [] categoryIds = new long[1];
				categoryIds[0] = LangUtil.parseLong(categoryId);
				newsQueryRequest.setCategoryIds(categoryIds);
			}
			if(StringUtils.isNotBlank(title)){
				newsQueryRequest.setTitle(title);			
			}
			newsQueryRequest.setType(NewsCategoryEnum.GENERAL_NEWS.getCode());
			newsQueryRequest.setPageSize(PROFESSION_PAGE_SIZE);
			if(StringUtils.isNotBlank(page)){
				newsQueryRequest.setPage(LangUtil.parseInt(page));
			}else{
				newsQueryRequest.setPage(1);
			}
			newsQueryRequest.setOrderBy("create_time desc");
			Pageable<NewsItem> newsItems = newsRemoteService.queryNewsPage(newsQueryRequest);
			pager = pageableToPager(newsItems);
			if(pager != null){
				result.setError(0);
				result.setVer(VER);
				result.setData(pager);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.setMsg("系统出错");
			result.setError(GlobalDefine.resultCode.INTERNAL_ERROR);
		}
		return result;
	}
	
	@RequestMapping("/index")
	public String toQueryNews(HttpServletRequest request,HttpServletResponse response,Model model){
		try {
			Long companyId = CookieUtil.getCompanyId(request);
			String keyWord = request.getParameter("keyWord");
			if(StringUtils.isNotBlank(keyWord)){
				model.addAttribute("keyWord", keyWord);
			}
			//获取资讯动态的分类
			NewsCategoryQueryRequest newsCategoryQueryRequest = new NewsCategoryQueryRequest();
			newsCategoryQueryRequest.setCompanyId(companyId);
			newsCategoryQueryRequest.setPageSize(Integer.MAX_VALUE);
			newsCategoryQueryRequest.setType(NewsCategoryEnum.GENERAL_NEWS.getCode());
			newsCategoryQueryRequest.setOrderBy("update_time desc");
			List<NewsCategoryItem> newsCategoryItems = newsRemoteService.queryCategorys(newsCategoryQueryRequest);
			//分类截取修改文案处理
			model.addAttribute("newsCategoryItems",newsCategoryItems);
			//获取行业动态资讯图展示
			String selectAddServiceMetaValue = addServiceRemoteService.selectAddServiceMetaValue(companyId, AddServiceConstants.ADD_SERVICE_META_SAVE_DYNAMICS);
			if(StringUtils.isNotBlank(selectAddServiceMetaValue)){
				Map<Integer,Map<String,Object>> object = objectMapper.readValue(selectAddServiceMetaValue, new TypeReference<Map<Integer,Map<String,Object>>>() {});
				//第一条资讯
				Map<String,Object> firstValueMap = object.get(1);
				if(firstValueMap!=null){
					model.addAttribute("firstValueItem", getNewsItem(firstValueMap));
				}
				//第二条资讯
				Map<String,Object> secondValueMap = object.get(2);
				if(secondValueMap!=null){
					model.addAttribute("secondValueItem", getNewsItem(secondValueMap));
				}
				//第三条资讯
				Map<String,Object> thirdValueMap = object.get(3);
				if(thirdValueMap!=null){
					model.addAttribute("thirdValueItem", getNewsItem(thirdValueMap));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/views/profession/info-dyna";
	}
	
	@RequestMapping("/toProfessionDetail")
	public String toPolicyDetail(HttpServletRequest request,HttpServletResponse response,Model model){
		try {
			String professionId = request.getParameter("professionId");
			if(StringUtils.isNotBlank(professionId)){
				NewsDetailItem newsDetailItem = newsRemoteService.findNewsDetailById(LangUtil.parseLong(professionId));	
				if(newsDetailItem != null){
					//处理页面展示发布时间
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String createTimeStr = sdf.format(newsDetailItem.getCreateTime());
					newsDetailItem.setCreateTimeStr(createTimeStr);
					model.addAttribute("newsDetailItem",newsDetailItem);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/views/profession/info-detail";
	}
	
	public static Pager pageableToPager(Pageable<NewsItem> newsItems){
		Pager pager = new Pager();
		pager.setResultList(newsItems.getRows());
		pager.setPage(newsItems.getPage());
		pager.setPageSize(newsItems.getPageSize());
		pager.setTotal(newsItems.getTotal());		
		pager.setRecords(newsItems.getRecords());
		return pager;
	}
	
	//行业动态首页展示资讯
	public NewsDetailItem getNewsItem(Map<String,Object> valueMap){
		String newsId = (String) valueMap.get("id");
		String categoryName = (String) valueMap.get("name");
		NewsDetailItem  newsDetailItem = newsRemoteService.findNewsDetailById(Long.parseLong(newsId));
		if(newsDetailItem != null){
			newsDetailItem.setTitle(StringUtils.abbreviate(newsDetailItem.getTitle(), 20));
			newsDetailItem.setCategoryName(categoryName);
		}
		return newsDetailItem;
	}

}
