/**
 * Project Name: province-platform1-0
 * File Name: ActivityController.java
 * Package Name: com.province.platform.controllers
 * Date: 2017年4月5日下午5:12:49 
 * Copyright (c) 2017, www.zhongzhihui.com All Rights Reserved. 
 */

package com.province.platform.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.province.platform.commons.ApiResult;
import com.province.platform.commons.GlobalDefine;
import com.province.platform.commons.Pager;
import com.province.platform.constants.NewsCategoryEnum;
import com.province.platform.cookies.CookieUtil;
import com.province.platform.helper.PagerHelper;
import com.province.platform.utils.DefenseUtil;
import com.zzh.base.api.AddServiceRemoteService;
import com.zzh.base.api.NewsRemoteService;
import com.zzh.base.api.RegionRemoteService;
import com.zzh.base.api.ThemedActivityRemoteService;
import com.zzh.base.api.constants.ActivityBaomingConstants;
import com.zzh.base.api.constants.CheckStatusEnum;
import com.zzh.base.api.entity.ListThemedActivityQueryRequest;
import com.zzh.base.api.entity.NewsCategoryItem;
import com.zzh.base.api.entity.NewsCategoryQueryRequest;
import com.zzh.base.api.entity.NewsItem;
import com.zzh.base.api.entity.NewsQueryRequest;
import com.zzh.base.api.entity.Pageable;
import com.zzh.base.api.entity.RegionItem;
import com.zzh.base.api.entity.activity.ActivityBaomingItem;
import com.zzh.base.api.entity.activity.ActivityBaomingMetaItem;
import com.zzh.base.api.entity.activity.ActivityBaomingOptfieldItem;
import com.zzh.base.api.entity.activity.ThemedActivityItem;
import com.zzh.common.constants.AddServiceConstants;
import com.zzh.common.utils.DateUtil;
import com.zzh.common.utils.LangUtil;
import com.zzh.user.api.UserRemoteService;
import com.zzh.user.api.entity.UserEntityInfo;

/** 
 * ClassName: ActivityController
 * Description:
 * 
 * @author zhuningkang@zhongzhihui.com
 * @date: 2017年4月5日 下午5:12:49
 */
@Controller
@RequestMapping("/activity")
public class ActivityController {
	
	private static Logger logger = LoggerFactory.getLogger(ActivityController.class);
	
	private static final int INDEX_ACTIVITY_PAGESIZE = 6;
	
	public static final String  VER = "1.0";
	/** 
	 * @Description:首页“电商活动”下最新资讯展示个数
	 */  
	private static final int INDEX_POLICY_NEWS_SHOW_SIZE = 3;
	
	/** 
	 * @Description:电商活动（互联网+）资讯推送分类
	 */  
	private static final int PLATFORM_CATEGORY_ACTIVITY_ID = 1006;
	
	@Resource
	private ThemedActivityRemoteService themedActivityRemoteService;
	
	@Resource
	private AddServiceRemoteService addServiceRemoteService;
	
	@Resource
	private RegionRemoteService regionRemoteService;
	
	@Resource
	private UserRemoteService userRemoteService;
	
	@Resource
	private NewsRemoteService newsRemoteService;
	
	@RequestMapping("/index")
	public String showActivity(HttpServletRequest request,HttpServletResponse response,Model model){
		Long companyId = CookieUtil.getCompanyId(request);
		//判断该平台属于哪个省份
		String provinceId = addServiceRemoteService.selectAddServiceMetaValue(companyId, AddServiceConstants.ADD_SERVICE_META_PROVINCE_ID);
		if(StringUtils.isNotBlank(provinceId)){
			List<RegionItem> regionItems = regionRemoteService.findByParentId(LangUtil.parseLong(provinceId));
			if(!CollectionUtils.isEmpty(regionItems)){
				model.addAttribute("regionItems", regionItems);
			}
		}
		
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
		if(!CollectionUtils.isEmpty(newsCategoryItems)){
			newsQuery2.setCategoryIds(new long[]{newsCategoryItems.get(0).getId()});
		}
		List<NewsItem> newsItems2 = newsRemoteService.queryNews(newsQuery2, 0, INDEX_POLICY_NEWS_SHOW_SIZE);
		model.addAttribute("policyNewsItems", newsItems2);
		
		return "/views/activity/index";
	}
	
	/** 
	 * @Title: queryActivity
	 * @Description: 首页活动列表
	 * @author zhuningkang@zhongzhihui.com
	 * @param request
	 * @param response
	 * @return  
	 */  
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ResponseBody
	@RequestMapping("/queryActivity")
	public ApiResult queryActivity(HttpServletRequest request, HttpServletResponse response,Pager pager){
		ApiResult result = new ApiResult();

		String keyWord = request.getParameter("keyWord");
		String cityId = request.getParameter("cityId");
		Long companyId = CookieUtil.getCompanyId(request);
		try {
			ListThemedActivityQueryRequest listThemedActivityQueryRequest = new ListThemedActivityQueryRequest();
			if(StringUtils.isNotBlank(keyWord)){
				listThemedActivityQueryRequest.setName(DefenseUtil.dealSqlStr(keyWord));
				logger.debug("=======分类搜索活动条件：keyWord======="+keyWord);
			}
			if(StringUtils.isNotBlank(cityId)){
				if(LangUtil.parseInt(cityId) != -1){
					listThemedActivityQueryRequest.setCityId(LangUtil.parseInt(cityId));
					logger.debug("=======分类搜索活动条件：cityId======="+cityId);
				}
			}
			pager.setPageSize(INDEX_ACTIVITY_PAGESIZE);//默认一页的条数
			listThemedActivityQueryRequest.setPage(pager.getPage());
			listThemedActivityQueryRequest.setPageSize(INDEX_ACTIVITY_PAGESIZE);
			listThemedActivityQueryRequest.setCompanyId(companyId);
			listThemedActivityQueryRequest.setIsOnline(true);
			listThemedActivityQueryRequest.setOrderBy("create_time desc");
			Pageable<ThemedActivityItem> queryPage = themedActivityRemoteService.queryPage(listThemedActivityQueryRequest);
			result.setData(PagerHelper.pageableToPager(queryPage));
		} catch (Exception e) {
			e.printStackTrace();
			result.setMsg("系统错误");
			result.setError(GlobalDefine.resultCode.INTERNAL_ERROR);
		}
		return result;
	}
	
	/** 
	 * @Title: showActivityDetail
	 * @Description: 活动详情页
	 * @author zhuningkang@zhongzhihui.com
	 * @param request
	 * @param response
	 * @param model
	 * @return  
	 */  
	@RequestMapping("/activityDetail")
	public String showActivityDetail(HttpServletRequest request,HttpServletResponse response,Model model){
		String idValue = request.getParameter("id");
		Long companyId = CookieUtil.getCompanyId(request);
		Long userId = CookieUtil.getUserId(request);
		try {
			if(StringUtils.isNotBlank(idValue)){
				ThemedActivityItem themedActivityItem = themedActivityRemoteService.queryOne(LangUtil.parseLong(idValue), userId, companyId);
				if(themedActivityItem != null){
					if(themedActivityItem.getStartTime()!= null){
						String str = DateUtil.getDateString(themedActivityItem.getStartTime(), "yyyy-MM-dd HH:mm");
						themedActivityItem.setStartTimeStr(str);
					}
					if(themedActivityItem.getEndTime() != null){
						String str = DateUtil.getDateString(themedActivityItem.getEndTime(), "yyyy-MM-dd HH:mm");
						themedActivityItem.setEndTimeStr(str);
					}
					model.addAttribute("themedActivityItem", themedActivityItem);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/views/activity/detail";
	}
	
	@RequestMapping("/apply")
	public String showApply(HttpServletRequest request,HttpServletResponse response,Model model){
		String id = request.getParameter("id");
		model.addAttribute("id",id);
		if(StringUtils.isNotBlank(id)){
			
			List<ActivityBaomingOptfieldItem> constfields = new ArrayList<ActivityBaomingOptfieldItem>();
			constfields.add(themedActivityRemoteService.getBaomingFieldByCode(ActivityBaomingConstants.BAOMING_FIELD_NAME));
			constfields.add(themedActivityRemoteService.getBaomingFieldByCode(ActivityBaomingConstants.BAOMING_FIELD_MOBILE));
			constfields.add(themedActivityRemoteService.getBaomingFieldByCode(ActivityBaomingConstants.BAOMING_FIELD_COMPANY));
			constfields.add(themedActivityRemoteService.getBaomingFieldByCode(ActivityBaomingConstants.BAOMING_FIELD_JOB));
			
			List<ActivityBaomingOptfieldItem> optfields = themedActivityRemoteService.getSelectedBaomingOptfieldList(LangUtil.parseLong(id));
			
			if(!CollectionUtils.isEmpty(optfields)){
				for(ActivityBaomingOptfieldItem activityBaomingOptfieldItem:optfields){
					String valueSet = activityBaomingOptfieldItem.getValueSet();
					if(StringUtils.isNotBlank(valueSet)){
						String[] split = valueSet.split("\\|");
						List<String> userList = new ArrayList<String>(split.length);
						for(String uid: split){
							userList.add(uid);
						}
						activityBaomingOptfieldItem.setValueString(userList);
					}
					
					constfields.add(activityBaomingOptfieldItem);
				}
			}
			model.addAttribute("constfields",constfields);
		}
		return "/views/apply";
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/toShowArea")
	@ResponseBody
	public ApiResult toShowArea(HttpServletRequest request,HttpServletResponse response){
		ApiResult result = new ApiResult();
		String pidStr = request.getParameter("pid");
		try {
			Long pid;
			if(StringUtils.isNotBlank(pidStr)){
				pid = LangUtil.parseLong(pidStr);
			}else{
				pid=1l;
			}
			List<RegionItem> regionItems = regionRemoteService.findByParentId(pid);
			Pager pager = new Pager();
			pager.setResultList(regionItems);
			result.setData(pager);
			result.setVer(VER);
			result.setMsg("加载地区成功");
			result.setError(0);
		} catch (Exception e) {
			e.printStackTrace();
			result.setError(1);
			result.setMsg("加载地区失败");
		}
		return result;
	}
	

	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/toApplyActivity")
	@ResponseBody
	public ApiResult toApplyActivity(HttpServletRequest request, HttpServletResponse response){
		ApiResult result = new ApiResult();
		String realname = request.getParameter("realname");
		String mobile = request.getParameter("mobile");
		String school = request.getParameter("school");
		String job = request.getParameter("job");
		String region = request.getParameter("region");
		String interest = request.getParameter("interest");
		String work_age = request.getParameter("work_age");
		String thank_word = request.getParameter("thank_word");
		String birthday = request.getParameter("birthday");
		String gender = request.getParameter("gender");
		Long companyId = CookieUtil.getCompanyId(request);
		Long activityId = LangUtil.parseLong(request.getParameter("activityId"));
		Long userId = CookieUtil.getUserId(request);
		try {
			if(userId != null){
				UserEntityInfo findByUserId = userRemoteService.findByUserId(userId, companyId);
				if(findByUserId != null){
					ActivityBaomingItem activityBaomingItem = new ActivityBaomingItem();
					//插入用户报名主表信息
					if(activityId != null){
						activityBaomingItem.setActivityId(activityId);					
					}
					activityBaomingItem.setUserId(userId);
					activityBaomingItem.setCompanyId(companyId);
					activityBaomingItem.setMobile(findByUserId.getMobile());
					activityBaomingItem.setRealname(findByUserId.getRealName());
					activityBaomingItem.setEmail(findByUserId.geteMail());
					activityBaomingItem.setHasSignup(false);
					//初始化meta信息
					
					Map<Long, ActivityBaomingMetaItem> activityBaomingMetaItems = new HashMap<Long, ActivityBaomingMetaItem>();
					//名字
					ActivityBaomingMetaItem activityBaomingMetaItem1 = new ActivityBaomingMetaItem();
					ActivityBaomingOptfieldItem nameOptfieldItem = themedActivityRemoteService.getBaomingFieldByCode(ActivityBaomingConstants.BAOMING_FIELD_NAME);
					if(nameOptfieldItem == null){
						throw new Exception("名称字段获取失败");
					}
					activityBaomingMetaItem1.setMetaName(nameOptfieldItem.getName());
					activityBaomingMetaItem1.setMetaValue(realname);
					activityBaomingMetaItems.put(nameOptfieldItem.getOptfieldId(), activityBaomingMetaItem1);
					
					//手机
					ActivityBaomingMetaItem activityBaomingMetaItem2 = new ActivityBaomingMetaItem();
					ActivityBaomingOptfieldItem mobileOptfieldItem = themedActivityRemoteService.getBaomingFieldByCode(ActivityBaomingConstants.BAOMING_FIELD_MOBILE);
					if(mobileOptfieldItem == null){
						throw new Exception("手机字段获取失败");
					}
					activityBaomingMetaItem2.setMetaName(mobileOptfieldItem.getName());
					activityBaomingMetaItem2.setMetaValue(mobile);
					activityBaomingMetaItems.put(mobileOptfieldItem.getOptfieldId(), activityBaomingMetaItem2);
					
					//学校
					ActivityBaomingMetaItem activityBaomingMetaItem3 = new ActivityBaomingMetaItem();
					ActivityBaomingOptfieldItem SchooOptfieldItem = themedActivityRemoteService.getBaomingFieldByCode(ActivityBaomingConstants.BAOMING_FIELD_COMPANY);
					if(SchooOptfieldItem == null){
						throw new Exception("企业字段获取失败");
					}
					activityBaomingMetaItem3.setMetaName(SchooOptfieldItem.getName());
					activityBaomingMetaItem3.setMetaValue(school);
					activityBaomingMetaItems.put(SchooOptfieldItem.getOptfieldId(), activityBaomingMetaItem3);
					
					//岗位
					ActivityBaomingMetaItem activityBaomingMetaItem4 = new ActivityBaomingMetaItem();
					ActivityBaomingOptfieldItem JobOptfieldItem = themedActivityRemoteService.getBaomingFieldByCode(ActivityBaomingConstants.BAOMING_FIELD_JOB);
					if(JobOptfieldItem == null){
						throw new Exception("岗位字段获取失败");
					}
					activityBaomingMetaItem4.setMetaName(JobOptfieldItem.getName());
					activityBaomingMetaItem4.setMetaValue(job);
					activityBaomingMetaItems.put(JobOptfieldItem.getOptfieldId(), activityBaomingMetaItem4);
					//
					List<ActivityBaomingOptfieldItem> optfields = themedActivityRemoteService.getSelectedBaomingOptfieldList(activityId);
					for(ActivityBaomingOptfieldItem activityBaomingOptfieldItem:optfields){
						//地区
						if(activityBaomingOptfieldItem.getCode().equals("region") ){
							ActivityBaomingMetaItem activityBaomingMetaItem = new ActivityBaomingMetaItem();
							activityBaomingMetaItem.setMetaName(activityBaomingOptfieldItem.getName());
							activityBaomingMetaItem.setMetaValue(region);
							activityBaomingMetaItems.put(activityBaomingOptfieldItem.getOptfieldId(), activityBaomingMetaItem);
						}
						//获奖感言
						if(activityBaomingOptfieldItem.getCode().equals("thank_word") ){
							ActivityBaomingMetaItem activityBaomingMetaItem = new ActivityBaomingMetaItem();
							activityBaomingMetaItem.setMetaName(activityBaomingOptfieldItem.getName());
							activityBaomingMetaItem.setMetaValue(thank_word);
							activityBaomingMetaItems.put(activityBaomingOptfieldItem.getOptfieldId(), activityBaomingMetaItem);
						}
						//性别
						if(activityBaomingOptfieldItem.getCode().equals("gender") ){
							ActivityBaomingMetaItem activityBaomingMetaItem = new ActivityBaomingMetaItem();
							activityBaomingMetaItem.setMetaName(activityBaomingOptfieldItem.getName());
							activityBaomingMetaItem.setMetaValue(gender);
							activityBaomingMetaItems.put(activityBaomingOptfieldItem.getOptfieldId(), activityBaomingMetaItem);
						}
						//工作年限
						if(activityBaomingOptfieldItem.getCode().equals("work_age") ){
							ActivityBaomingMetaItem activityBaomingMetaItem = new ActivityBaomingMetaItem();
							activityBaomingMetaItem.setMetaName(activityBaomingOptfieldItem.getName());
							activityBaomingMetaItem.setMetaValue(work_age);
							activityBaomingMetaItems.put(activityBaomingOptfieldItem.getOptfieldId(), activityBaomingMetaItem);
						}
						//兴趣爱好
						if(activityBaomingOptfieldItem.getCode().equals("interest") ){
							ActivityBaomingMetaItem activityBaomingMetaItem = new ActivityBaomingMetaItem();
							activityBaomingMetaItem.setMetaName(activityBaomingOptfieldItem.getName());
							activityBaomingMetaItem.setMetaValue(interest);
							activityBaomingMetaItems.put(activityBaomingOptfieldItem.getOptfieldId(), activityBaomingMetaItem);
						}
						//生日
						if(activityBaomingOptfieldItem.getCode().equals("birthday") ){
							ActivityBaomingMetaItem activityBaomingMetaItem = new ActivityBaomingMetaItem();
							activityBaomingMetaItem.setMetaName(activityBaomingOptfieldItem.getName());
							activityBaomingMetaItem.setMetaValue(birthday);
							activityBaomingMetaItems.put(activityBaomingOptfieldItem.getOptfieldId(), activityBaomingMetaItem);
						}
					}
					//设置报名信息字段
					activityBaomingItem.setActivityBaomingMetaItems(activityBaomingMetaItems);
					themedActivityRemoteService.baoming(activityBaomingItem);
					result.setVer(VER);
					result.setError(0);
					result.setMsg("报名成功！");
				}
			}
		} catch (Exception e) {
			result.setError(1);
			result.setData("报名失败！");
			e.printStackTrace();
		}
		return result;
	}
}
