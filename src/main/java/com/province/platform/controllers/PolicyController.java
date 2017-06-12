package com.province.platform.controllers;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
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
import com.zzh.base.api.NewsRemoteService;
import com.zzh.base.api.entity.NewsCategoryItem;
import com.zzh.base.api.entity.NewsCategoryQueryRequest;
import com.zzh.base.api.entity.NewsDetailItem;
import com.zzh.base.api.entity.NewsItem;
import com.zzh.base.api.entity.NewsQueryRequest;
import com.zzh.base.api.entity.Pageable;
import com.zzh.common.utils.LangUtil;

@Controller
@RequestMapping("/policy")
public class PolicyController {
	
	private static final Logger logger = LoggerFactory.getLogger(PolicyController.class);
	
	/**
	 * @Description:电商政策分页大小
	 */
	private static final int POLICY_PAGE_SIZE = 6;
	
	public static final String  VER = "1.0";
	
	@Resource
	private NewsRemoteService newsRemoteService;
	
	/**
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * 异步加载电商政策列表
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
			//获取电商政策的分类
			NewsCategoryQueryRequest newsCategoryQueryRequest = new NewsCategoryQueryRequest();
			newsCategoryQueryRequest.setCompanyId(companyId);
			newsCategoryQueryRequest.setPageSize(Integer.MAX_VALUE);
			newsCategoryQueryRequest.setType(NewsCategoryEnum.INDUSTRIAL_POLICY.getCode());			
			newsCategoryQueryRequest.setOrderBy("update_time desc");
			//分页查询电商政策列表
			NewsQueryRequest newsQueryRequest = new NewsQueryRequest();
			long [] categoryIds = new long[1];
			newsQueryRequest.setCompanyId(companyId);
			if(StringUtils.isNotBlank(categoryId) && LangUtil.parseLong(categoryId) != 0){
				categoryIds[0] = LangUtil.parseLong(categoryId);
				newsQueryRequest.setCategoryIds(categoryIds);
			}
			if(StringUtils.isNotBlank(title)){
				newsQueryRequest.setTitle(title);			
			}
			newsQueryRequest.setType(NewsCategoryEnum.INDUSTRIAL_POLICY.getCode());
			newsQueryRequest.setPageSize(POLICY_PAGE_SIZE);
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
			String keyWord = request.getParameter("keyWord");
			if(StringUtils.isNotBlank(keyWord)){
				model.addAttribute("keyWord",keyWord);
			}
			Long companyId = CookieUtil.getCompanyId(request);
			//获取电商政策的分类
			NewsCategoryQueryRequest newsCategoryQueryRequest = new NewsCategoryQueryRequest();
			newsCategoryQueryRequest.setCompanyId(companyId);
			newsCategoryQueryRequest.setPageSize(Integer.MAX_VALUE);
			newsCategoryQueryRequest.setType(NewsCategoryEnum.INDUSTRIAL_POLICY.getCode());			
			newsCategoryQueryRequest.setOrderBy("update_time desc");
			List<NewsCategoryItem> newsCategoryItems = newsRemoteService.queryCategorys(newsCategoryQueryRequest);
			model.addAttribute("newsCategoryItems",newsCategoryItems);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/views/policy/policy-index";
	}
	
	/**
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * 电商政策详情页
	 */
	@RequestMapping("/toPolicyDetail")
	public String toPolicyDetail(HttpServletRequest request,HttpServletResponse response,Model model){
		try {
			String policyId = request.getParameter("policyId");
			if(StringUtils.isNotBlank(policyId)){
				NewsDetailItem newsDetailItem = newsRemoteService.findNewsDetailById(LangUtil.parseLong(policyId));	
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
		return "/views/policy/policy-detail";
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
	
}
