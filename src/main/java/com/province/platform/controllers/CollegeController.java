package com.province.platform.controllers;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.province.platform.commons.ApiResult;
import com.province.platform.commons.GlobalDefine;
import com.province.platform.commons.LiveApiResult;
import com.province.platform.commons.LiveUtil;
import com.province.platform.commons.Pager;
import com.province.platform.constants.IndexOperationEnum;
import com.province.platform.cookies.CookieUtil;
import com.province.platform.helper.LiveHelper;
import com.province.platform.helper.ResourceHelper;
import com.province.platform.helper.ServiceFactory;
import com.province.platform.helper.UploadHelper;
import com.zzh.base.api.AddServiceRemoteService;
import com.zzh.base.api.SiteRemoteService;
import com.zzh.base.api.entity.site.SiteModuleItem;
import com.zzh.base.api.entity.site.SiteSectionItem;
import com.zzh.base.api.entity.site.SiteSectionQueryRequest;
import com.zzh.base.api.entity.site.SiteTemplateCompanyRequest;
import com.zzh.common.constants.AddServiceConstants;
import com.zzh.common.constants.SiteTemplateConstants;
import com.zzh.common.utils.EncryptUtils;
import com.zzh.common.utils.LangUtil;
import com.zzh.common.utils.StackTraceUtil;
import com.zzh.common.utils.UAUtils;
import com.zzh.course.api.CourseCounterRemoteService;
import com.zzh.course.api.CourseRemoteService;
import com.zzh.course.api.CourseRemoteService.CourseBuilderEnum;
import com.zzh.course.api.UserCourseRemoteService;
import com.zzh.course.api.entity.CompanyCourseIterm;
import com.zzh.course.api.entity.CourseInfo;
import com.zzh.course.api.entity.CourseItem;
import com.zzh.course.api.entity.CourseReplayInfo;
import com.zzh.course.api.entity.StudyProgressItem;
import com.zzh.course.api.entity.UserCourseInfo;
import com.zzh.course.api.entity.UserStudyItem;
import com.zzh.course.api.entity.category.ClientCategoryInfo;
import com.zzh.course.api.enums.CourseCounterTypeEnum;
import com.zzh.course.api.enums.CourseOpenedEnum;
import com.zzh.course.api.enums.CourseSourceEnum;
import com.zzh.course.api.enums.CourseStatusEnum;
import com.zzh.course.api.enums.CourseTypeEnum;
import com.zzh.course.api.enums.LiveRoleEnum;
import com.zzh.course.api.enums.LiveStatusEnum;
import com.zzh.course.api.enums.UserStudyStatusEnum;
import com.zzh.course.api.query.CourseQuery;
import com.zzh.course.api.query.category.ClientCategoryQuery;
import com.zzh.file.api.ResourceRemoteService;
import com.zzh.live.remote.client.ClientSign;
import com.zzh.live.remote.constants.UserAgentType;
import com.zzh.live.remote.model.AccessModelWrapper;
import com.zzh.live.remote.model.AccessModelWrapperImpl;
import com.zzh.user.api.UserRemoteService;
import com.zzh.user.api.constants.UserConstants;
import com.zzh.user.api.entity.UserEntityInfo;

@Controller
@RequestMapping("/course")
public class CollegeController {
	
	private static final Logger logger = LoggerFactory.getLogger(CollegeController.class);
	
	private static final int HOT_PAGE_SIZE = 5;
	
	private static final long ALLOW_GOTO_LIVE_TIME_OFFSET = 30;
	
	private static final int ONLINE_STUDY_PAGE_SIZE = 8;
	
	public static final String  VER = "1.0";
	
	final static String LIVESHOWVIEW = "/views/college/liveshow";
	
	@Resource
	private CourseRemoteService courseRemoteService;
	
	@Resource
	private SiteRemoteService siteRemoteService;
	
	@Resource
	private UserRemoteService userRemoteService;
	
	@Resource
	private UserCourseRemoteService userCourseRemoteService;
	
	@Resource
	private CourseCounterRemoteService courseCounterRemoteService;
	
	@Resource
	private ResourceRemoteService resourceRemoteService;
	
	@Resource
	private AddServiceRemoteService addServiceRemoteService;
	
	@RequestMapping("/index")
	public String toQueryCourseIndex(HttpServletRequest request,HttpServletResponse response,Model model){
		Long companyId = CookieUtil.getCompanyId(request);
		try {
			//加载课程首页顶部热门课程
			String selectAddServiceMetaValue = addServiceRemoteService.selectAddServiceMetaValue(companyId, AddServiceConstants.ADD_SERVICE_TOP_ONLINE_STUDY);
			List<CourseItem> courseItems = new ArrayList<CourseItem>();
			if(StringUtils.isNotBlank(selectAddServiceMetaValue)){
				String[] split = selectAddServiceMetaValue.split(",");
				for(int i=0;i<split.length;i++){
					Map<CourseBuilderEnum, Object> builderMap = new HashMap<CourseRemoteService.CourseBuilderEnum, Object>();
					builderMap.put(CourseRemoteService.CourseBuilderEnum.COVERIMAGE, null);
					builderMap.put(CourseRemoteService.CourseBuilderEnum.COUNTER, null);
					builderMap.put(CourseRemoteService.CourseBuilderEnum.DETAIL, null);
					builderMap.put(CourseRemoteService.CourseBuilderEnum.TEACHER, null);
					builderMap.put(CourseRemoteService.CourseBuilderEnum.RELOAD_PROGRESS, null);
					CourseItem courseItem = courseRemoteService.getCourseItem(LangUtil.parseLong(split[i]), companyId, null, builderMap);
					if(courseItem != null){
						courseItems.add(courseItem);					
					}
				}
			}
			//当热门课程数量不满5门时，补充最新课程
			if(courseItems.size() < 5 ){
				CourseQuery courseQuery = new CourseQuery();
				courseQuery.setCompanyId(companyId);
				courseQuery.setStatus(CourseStatusEnum.ONLINE.getCode());
				courseQuery.setOrderBy("INDEX");
				courseQuery.setPage(1);
				courseQuery.setPageSize(HOT_PAGE_SIZE);
				courseQuery.setOpened(CourseOpenedEnum.OPEN.getCode());
				Map<CourseBuilderEnum, Object> builderMap = new HashMap<CourseRemoteService.CourseBuilderEnum, Object>();
				builderMap.put(CourseRemoteService.CourseBuilderEnum.COVERIMAGE, null);
				courseQuery.setPageSize(5);
				List<CourseItem> courseItems2 = courseRemoteService.queryCourse(courseQuery, companyId, null, builderMap);
				if(courseItems2 != null){
					//防止重复课程
					for(int i=0;i<courseItems2.size();i++){
						for(int j=0;j<courseItems.size();j++){
							if(courseItems2.get(i).getId() == courseItems.get(j).getId()){
								courseItems2.remove(i);
							}
						}
					}
					for(CourseItem courseItem:courseItems2){
						courseItems.add(courseItem);
					}					
				}
			}	
			//直播课直播时间页面展示
			for(CourseItem courseItem:courseItems){
				String time = formatTime(courseItem.getStartTime(),courseItem.getEndTime());
				courseItem.setTime(time);
			}
			if(courseItems != null && courseItems.size()>=1){
				model.addAttribute("bigCourseItem",courseItems.get(0));				
			}
			//返回4门小的热门课程
			List<CourseItem> smallCourseItems = new ArrayList<CourseItem>();
			for(int i=1;i<5;i++){
				if(courseItems != null && courseItems.size()>=5){
					smallCourseItems.add(courseItems.get(i));				
				}
			}
			model.addAttribute("smallCourseItems",smallCourseItems);				
			//加载中间图
			List<SiteSectionItem> siteSectionItems = new ArrayList<SiteSectionItem>();
			SiteTemplateCompanyRequest selectTemplateCompanyByCompanyId = siteRemoteService.selectTemplateCompanyByCompanyId(companyId);
			long instanceId = selectTemplateCompanyByCompanyId.getId();
			SiteModuleItem siteModuleItem = siteRemoteService.selectModuleByInstanceIdAndCode(instanceId,SiteTemplateConstants.COLLEGE_CENTER_PIC);
			if(siteModuleItem != null){
				SiteSectionQueryRequest siteSectionQueryRequest = new SiteSectionQueryRequest();
				siteSectionQueryRequest.setDisabled(true);
				siteSectionQueryRequest.setModuleId(siteModuleItem.getId());
				siteSectionQueryRequest.setType(SiteTemplateConstants.COLLEGE_CENTER_PIC_TYPE);
				siteSectionItems = siteRemoteService.querySiteSection(siteSectionQueryRequest);
				for(SiteSectionItem siteSectionItem:siteSectionItems){
					siteSectionItem.setImgUrl(ResourceHelper.getDataUrl(siteSectionItem.getImage()));
				}
			}
			if(siteSectionItems != null && siteSectionItems.size()>=1){
				model.addAttribute("siteSectionItem",siteSectionItems.get(0));				
			}
			//首次加载课程分类
			ClientCategoryQuery clientCategoryQuery = new ClientCategoryQuery();
			clientCategoryQuery.setCompanyId(companyId);
			List<ClientCategoryInfo> clientCategoryInfos = courseRemoteService.queryCategoryInfos(clientCategoryQuery);
			model.addAttribute("clientCategoryInfos",clientCategoryInfos);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/views/college/course-index";
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ResponseBody
	@RequestMapping("/queryCourse")
	public ApiResult queryCourse(HttpServletRequest request, HttpServletResponse response,Model model,Pager pager){
		ApiResult result = new ApiResult();
		try {
			String categoryId = request.getParameter("categoryId");
			Long companyId = CookieUtil.getCompanyId(request);
			//查询课程分类
			ClientCategoryQuery clientCategoryQuery = new ClientCategoryQuery();
			clientCategoryQuery.setCompanyId(companyId);
			List<ClientCategoryInfo> clientCategoryInfos = courseRemoteService.queryCategoryInfos(clientCategoryQuery);
			CourseQuery courseQuery = new CourseQuery();
			courseQuery.setCompanyId(companyId);
			courseQuery.setStatus(CourseStatusEnum.ONLINE.getCode());
			courseQuery.setOpened(CourseOpenedEnum.OPEN.getCode());
			courseQuery.setOrderBy("create_time desc");
			courseQuery.setPage(1);
			courseQuery.setPageSize(ONLINE_STUDY_PAGE_SIZE);
			long [] categoryIds = new long[1];
			if(StringUtils.isNotBlank(categoryId)){
				categoryIds[0] = LangUtil.parseLong(categoryId);
				courseQuery.setClientCategorys(categoryIds);
			}else{
				categoryIds[0] = clientCategoryInfos.get(0).getId();
				courseQuery.setClientCategorys(categoryIds);
			}
			Map<CourseBuilderEnum, Object> builderMap = new HashMap<CourseRemoteService.CourseBuilderEnum, Object>();
			builderMap.put(CourseRemoteService.CourseBuilderEnum.COVERIMAGE, null);
			builderMap.put(CourseRemoteService.CourseBuilderEnum.COUNTER, null);
			List<CourseItem> courseItems = courseRemoteService.queryCourse(courseQuery, companyId, null, builderMap);
			long courseCount = courseRemoteService.getCourseCount(courseQuery);
			pager = pageableToPager(courseQuery,courseItems,courseCount);
			if(CollectionUtils.isNotEmpty(pager.getResultList())){
				result.setData(pager);
			}else{
				pager.setResultList(new ArrayList<CourseItem>());
				result.setData(pager);
			}
		} catch (Exception e) {
			result.setError(GlobalDefine.resultCode.INTERNAL_ERROR);
			result.setMsg("系统出错");
			e.printStackTrace();
		}
		return result;
	}
	
	@RequestMapping("/toCourseDetail")
	public String toCourseDetail(HttpServletRequest request, HttpServletResponse response,Model model){
		String type = request.getParameter("type");
		String id = request.getParameter("courseId");
		CourseItem courseItem = null;
		String view = "";
		//
		String gotoLiveError = request.getParameter("gotoLiveError");
		String liveshow = request.getParameter("liveshow");
		model.addAttribute("gotoLiveError", gotoLiveError);
		boolean hasPermission = true;// 登录用户是否有权限
		boolean hasLogin = false;
		try {
			// 需校验是否登录
			Long userId = CookieUtil.getUserId(request);

			if (userId != null) {
				hasLogin = true;
			}
			Long companyId = CookieUtil.getCompanyId(request);

			if (StringUtils.isBlank(id)) {
				throw new Exception("course id is null.");
			}

			long courseId = LangUtil.parseLong(id);
			Map<CourseBuilderEnum, Object> builderMap = new HashMap<CourseRemoteService.CourseBuilderEnum, Object>();
			builderMap.put(CourseRemoteService.CourseBuilderEnum.COVERIMAGE, null);
			builderMap.put(CourseRemoteService.CourseBuilderEnum.COUNTER, null);
			builderMap.put(CourseRemoteService.CourseBuilderEnum.DETAIL, null);
			builderMap.put(CourseRemoteService.CourseBuilderEnum.TEACHER, null);
			builderMap.put(CourseRemoteService.CourseBuilderEnum.RELOAD_PROGRESS, null);
			if("goToReply".equals(type)){
				builderMap.put(CourseRemoteService.CourseBuilderEnum.COURSE_REPLAY, null);
			}else{
				builderMap.put(CourseRemoteService.CourseBuilderEnum.RESOURCE, null);				
			}
			builderMap.put(CourseRemoteService.CourseBuilderEnum.EXAM, null);
			courseItem = courseRemoteService.getCourseItem(courseId, null, null, builderMap);
			//截取课程简介
			if(courseItem.getDescription().length() > 61){
				courseItem.setDescription(courseItem.getDescription().substring(60)+"...");				
			}
			// 越权校验 平台公开课不校验
			if (CourseSourceEnum.PLATFORM.getCode() != courseItem.getSource() || CourseOpenedEnum.OPEN.getCode() != courseItem.getOpened()) {
				CompanyCourseIterm companyCourseIterm = courseRemoteService.selectCompanyCourse(companyId, courseId);
				if (companyCourseIterm == null) {
					throw new Exception("不能访问其他企业课程！");
				}
			}
			// 课程上下架校验
			if (CourseStatusEnum.OFFLINE.getCode() == courseItem.getStatus()) {
				throw new Exception("课程已下架！");
			}
			//hasPermission = PermissionHelper.hasCoursePermission(request, courseId);

			if (CourseTypeEnum.LIVE.getCode() == courseItem.getType()) {
				if("goToReply".equals(type)){
					model.addAttribute("videoUrl", courseItem.getResourceItem().getMobileVideoUrl());					
				}else{
					model.addAttribute("distance", courseItem.getStartTimeDistance());					
				}
			} else {
				// 点播和文档课程构建资源上次播放进度
				if (hasLogin) {
					// 构建考试链接
					model.addAttribute("courseExamUrl", courseItem.getExamUrl());
				}
				if (CourseTypeEnum.VIDEO.getCode() == courseItem.getType()) {
					model.addAttribute("videoUrl", courseItem.getResourceItem().getMobileVideoUrl());
				}
			}
			if (hasLogin) {
				UserCourseInfo userCourseInfo = userCourseRemoteService.selectUserCourse(userId, courseItem.getId(), companyId);
				if (userCourseInfo != null) {
					courseItem.setCurrentUserApplyFlag(true);
				}
			}

			if (courseItem.getCategorys() != null && courseItem.getCategorys().size() > 0) {
				model.addAttribute("categoryName", courseItem.getCategorys().get(0).getName());
			}
			// 直播判断是否有回放
			if (CourseTypeEnum.LIVE.getCode() == courseItem.getType() && courseItem.getHasLiveEnded()) {
				boolean doReplay = false;
				CourseReplayInfo courseReplayInfo = null;
				courseReplayInfo = courseRemoteService.selectCourseReplay(courseId);
				if (courseReplayInfo != null) {
					// 判断回放时间
					Date now = new Date();
					if (courseReplayInfo.getStartTime() != null) {
						if (now.after(courseReplayInfo.getStartTime())) {
							if (courseReplayInfo.getEndTime() != null) {
								if (now.before(courseReplayInfo.getEndTime())) {
									doReplay = true;
								}
							}else{
								doReplay = true;
							}
						}
					} else {
						if (courseReplayInfo.getEndTime() != null) {
							if (now.before(courseReplayInfo.getEndTime())) {
								doReplay = true;
							}
						} else {
							doReplay = true;
						}
					}
				}
				courseItem.setCourseReplayInfo(courseReplayInfo);
				courseItem.setDoReplay(doReplay);
			}
			model.addAttribute("currentUser", userId);
			model.addAttribute("courseItem", courseItem);
			model.addAttribute("indexOperation", getIndexOperation(courseItem));
			model.addAttribute("companyId", companyId);
			if (hasLogin) {
				String userIdStr = EncryptUtils.encodeBase64String(String.valueOf(userId));
				model.addAttribute("userId", userIdStr);
			}
			// 记录该次访问(配合点击率排行)
			CourseRemoteService courseRemoteService = ServiceFactory.getBean("courseRemoteService", CourseRemoteService.class);
			courseRemoteService.incrVisitCount(companyId, courseId);

		} catch (Exception e) {
			logger.error("show course info error. courseId = " + id);
			logger.error(StackTraceUtil.getStackTrace(e));
			model.addAttribute("exception", "课程不存在，或已下架！");
		}
		model.addAttribute("hasLogin", hasLogin);
		model.addAttribute("hasPermission", hasPermission);
		view = getReturnView(courseItem.getType(),type);
		if ("true".equals(liveshow)) {
			return LIVESHOWVIEW;
		}
		return view;
	}
	
	@RequestMapping("/gotoLive")
	public String gotoLive(HttpServletRequest request, HttpServletResponse response){
		
		//
		long companyId = CookieUtil.getCompanyId(request);
		String id = request.getParameter("id");
		Long userId = CookieUtil.getUserId(request);
		if (userId == null) {
			return "redirect:/course/toErrorView?errorMsg=notlogin";
		}
		UserEntityInfo userInfo = userRemoteService.findByUserId(userId, companyId);			
		Long courseId = LangUtil.parseLong(id);
		try {
			//
			CourseInfo courseInfo = courseRemoteService.selectCourseById(courseId);
			//
			if (LiveStatusEnum.ENDED.getCode() == courseInfo.getLiveStatus()) {
				return "redirect:/course/toErrorView?errorMsg=ended";
			}

			if (CourseStatusEnum.OFFLINE.getCode() == courseInfo.getStatus()) {
				return "redirect:/course/toErrorView?errorMsg=offline";
			}

			Date now = new Date();
			if (courseInfo.getStartTime().getTime() - now.getTime() > ALLOW_GOTO_LIVE_TIME_OFFSET * 60L * 1000L) {
				return "redirect:/course/toErrorView?isNotBegin";
			}
			//
			
			UserCourseInfo userCourseInfo = userCourseRemoteService.selectUserCourse(userId, courseId, companyId);
			if (userCourseInfo == null) {
				userCourseInfo = new UserCourseInfo();
				userCourseInfo.setCourseId(courseId);
				userCourseInfo.setUserId(userId);
				userCourseInfo.setCompanyId(companyId);
				userCourseInfo.setStudyStatus(UserStudyStatusEnum.NOT_STUDY.getCode());
				userCourseRemoteService.insertUserCourse(userCourseInfo);
			}
			int liveRoleCode = LiveRoleEnum.STUDENT.getCode();

			if (userId == courseInfo.getTeacher()) {
				liveRoleCode = LiveRoleEnum.LECTR.getCode();
			} else {
				if (userInfo.getRole() == UserConstants.USER_ROLE_ADMIN) {
					liveRoleCode = LiveRoleEnum.ADMIN.getCode();
				} else {
					List<Long> assistants = courseRemoteService.selectCourseAssistant(courseId);
					if (assistants != null && assistants.size() > 0) {
						for (Long assistant : assistants) {
							if (userId == assistant) {
								liveRoleCode = LiveRoleEnum.ASSISTANT.getCode();
								break;
							}
						}
					}

				}
			}
			courseCounterRemoteService.plusCourseCounter(courseId, CourseCounterTypeEnum.STUDY.getCode());
			//
			String ipAddress = request.getRemoteHost();
			int userAgent = UAUtils.isH5(request) ? UserAgentType.MOBILE.getCode() : UserAgentType.PC.getCode();
			//
			String domain = request.getServerName();
			//
			ClientSign clientSign = LiveUtil.getClientSign(courseInfo.getCompanyId());
			//
			AccessModelWrapper wapper = new AccessModelWrapperImpl();
			wapper.setBizRoomId(String.valueOf(courseId));
			wapper.setBizUserId(String.valueOf(userId));
			wapper.setClientSign(clientSign);
			wapper.setDomain(domain);
			wapper.setFromCompanyId(companyId);
			wapper.setIpAddress(ipAddress);
			wapper.setRole(liveRoleCode);
			wapper.setUserAgent(userAgent);
			wapper.setUsername(userInfo.getRealName());
			//
			String accessToken = LiveUtil.getAccessToken(wapper);

			Properties config = ServiceFactory.getBean("config", Properties.class);
			String liveShowUrl = config.getProperty("live.show.url");
			liveShowUrl += accessToken;
			return "redirect:" + liveShowUrl;
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:/course/toErrorView?errorMsg=systemError";
		}
	}
	
	@RequestMapping("/toErrorView")
	public String toLiveErrorView(HttpServletRequest request,Model model){
		String errorMsg = request.getParameter("errorMsg");
		model.addAttribute("errorMsg", errorMsg);
		return "views/college/error-view";
	}
	
	// 用户报名
	@SuppressWarnings("rawtypes")
	@ResponseBody
	@RequestMapping("/toApply")
	public ApiResult toApply(HttpServletRequest request, UserCourseInfo userCourseInfo,Long courseId) {
		ApiResult result = new ApiResult();
		long companyId = CookieUtil.getCompanyId(request);
		long userId = CookieUtil.getUserId(request);
		try {
			UserEntityInfo userEntityInfo = userRemoteService.findByUserId(userId, companyId);
			if (userEntityInfo != null) {
				userCourseInfo.setUserId(userId);
				userCourseInfo.setCompanyId(companyId);
				if (courseId != null) {//判断该课程是否属于专题课程
					userCourseInfo.setCourseId(courseId);
					userCourseInfo.setStudyStatus(UserStudyStatusEnum.NOT_STUDY.getCode());
					userCourseRemoteService.insertUserCourse(userCourseInfo);
				}
				result.setError(0);// 报名成功
				result.setVer(VER);
				result.setMsg("报名成功！");
			} else {
				result.setError(1);// 用户没有登录
				result.setMsg("请登录后再报名！");
			}
			// 查询是否报名成功
		} catch (Exception e) {
			e.printStackTrace();
			result.setError(1);// 系统错误
			result.setMsg("系统错误！");
		}
		return result;
	}
	
	@RequestMapping("/updateVideoStudyProgress")
	public String updateVideoStudyProgress(HttpServletRequest request, HttpServletResponse response){

		//
		Long userId = CookieUtil.getUserId(request);
		String userIdStr = request.getParameter("userId");
		String playTimeStr = request.getParameter("playTime");
		String courseIdStr = request.getParameter("courseId");
		String totalTimeStr = request.getParameter("duration");

		if (StringUtils.isEmpty(playTimeStr)) {
			return "";
		}
		if (StringUtils.isEmpty(totalTimeStr)) {
			return "";
		}

		try {
			if (userId == null) {
				userId = LangUtil.parseLong(EncryptUtils.decodeBase64String(userIdStr));
			}
			long courseId = LangUtil.parseLong(courseIdStr);
			double playTime = Double.parseDouble(playTimeStr);
			double totalTime = Double.parseDouble(totalTimeStr);
			long companyId = CookieUtil.getCompanyId(request);
			int progress = 0;
			if (totalTime != 0) {
				progress = (int) ((playTime / totalTime) * 100);
				progress = progress == 99 ? 100 : progress;
			}
			UserCourseInfo userCourseInfo = userCourseRemoteService.selectUserCourse(userId, courseId, companyId);
			// 学习记录对象
			StudyProgressItem studyProgressItem = new StudyProgressItem();
			studyProgressItem.setUserId(userId);
			studyProgressItem.setCourseId(courseId);
			studyProgressItem.setStudyNum((int) playTime);
			studyProgressItem.setProgress(progress);
			studyProgressItem.setCompanyId(companyId);
			//
			if (userCourseInfo == null) {
				// 插入用户课程关系
				userCourseInfo = new UserCourseInfo();
				userCourseInfo.setCourseId(courseId);
				userCourseInfo.setStudyStatus(UserStudyStatusEnum.HAS_STUDY.getCode());
				userCourseInfo.setUserId(userId);
				userCourseInfo.setCompanyId(companyId);
				//
				userCourseRemoteService.insertUserCourseAndProgress(userCourseInfo, studyProgressItem);
				// 更新已学习次数
				userCourseRemoteService.plusCourseCounter(courseId, CourseCounterTypeEnum.STUDY.getCode());
			} else {
				StudyProgressItem studyItem = userCourseRemoteService.selectStudyProgress(userId, courseId, companyId);
				UserStudyItem userStudyItem = new UserStudyItem();
				BeanUtils.copyProperties(studyProgressItem, userStudyItem);
				if( studyItem == null){
					userCourseRemoteService.insertStudyProgress(userStudyItem);
				}else{
					if(studyItem.getStudyNum() < (new Double(playTime).longValue())){
						userCourseRemoteService.updateStudyProgress(userStudyItem);
					}
				}
			}
		} catch (Exception e) {
			logger.error("update video study progress error! userId=" + userIdStr + ", playTime=" + playTimeStr + ", courseId=" + courseIdStr + ", totalTime=" + totalTimeStr);
			logger.error(StackTraceUtil.getStackTrace(e));
		}

		return "";
	
	}
	
	@RequestMapping("/toLive")
	public String toLivePage(HttpServletRequest request, HttpServletResponse response, Model model){
		int liveId = 238309349;
		model.addAttribute("liveId", liveId);
		
		model.addAttribute("appKey", LiveHelper.getAppKey());
		model.addAttribute("now", System.currentTimeMillis());
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("webinar_id", ""+liveId);
		paramMap.put("fields", "introduction");
		
		model.addAttribute("sign", LiveHelper.getSign(paramMap));
		
		//观众信息
		model.addAttribute("audienceEmail", "142857@domain.com");
		model.addAttribute("audienceName", "祁同伟");
		
		return "views/college/newliveshow";
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("user")
	public String createUser(HttpServletRequest request, HttpServletResponse response, Model model){
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("third_user_id", "166083");
		paramMap.put("pass", "111111");
		paramMap.put("phone", "18667112887");
		paramMap.put("name", "巧克力");
		paramMap.put("email", "chocolate@zzh.com");
		String url = "http://e.vhall.com/api/vhallapi/v2/user/register";
		try {
			LiveApiResult<String> result = LiveHelper.sendPostRequest(url, paramMap);
			model.addAttribute("errormsg", result.getMsg());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "views/college/anchor";
	}
	@SuppressWarnings("unchecked")
	@RequestMapping("getUserId")
	public String getUserId(HttpServletRequest request, HttpServletResponse response, Model model){
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("third_user_id", "145715");
		String url = "http://e.vhall.com/api/vhallapi/v2/user/get-user-id";
		try {
			LiveApiResult<JSONObject> result = LiveHelper.sendPostRequest(url, paramMap);
			model.addAttribute("errormsg", result.getMsg()+result.getData().getString("id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "views/college/anchor";
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("createLive")
	public String createLive(HttpServletRequest request, HttpServletResponse response, Model model){
		Map<String, String> paramMap = new HashMap<String, String>();
		String url = "http://e.vhall.com/api/vhallapi/v2/webinar/create";
		paramMap.put("subject", "微信端测试直播课");
		Calendar nowTime = Calendar.getInstance();
		nowTime.add(Calendar.MINUTE, 5);
		paramMap.put("start_time", ""+(nowTime.getTimeInMillis()/1000));
		paramMap.put("introduction", "微信端直播测试的简介");
		paramMap.put("layout", "3");
		paramMap.put("auto_record", "1");
		paramMap.put("host", "高育良");
		LiveApiResult<String> liveResult = LiveHelper.sendPostRequest(url, paramMap);
		if("200".equals(String.valueOf(liveResult.getCode()))){
			model.addAttribute("webinarId", liveResult.getData());
		}
		model.addAttribute("errormsg", liveResult.getMsg());
		
		//图片上传参数
		return "views/college/create";
	}
	
	@RequestMapping("begin")
	public String beginLive(HttpServletRequest request, HttpServletResponse response, Model model, Long webinarId){
		model.addAttribute("webinar_id", webinarId);
		return "views/college/begin";
	}
	
	@ResponseBody
	@RequestMapping("authParam")
	public Map<String, Object> getAuthParam(HttpServletRequest request, HttpServletResponse response, Long webinarId){
		Map<String, Object> resutlMap = new HashMap<String, Object>();
		Map<String, String> paramMap = new HashMap<String, String>();
		if(webinarId != null && webinarId > 0){
			paramMap.put("webinar_id", String.valueOf(webinarId));
			paramMap.put("is_sec_auth", "1");
			resutlMap.putAll(paramMap);
			resutlMap.putAll(LiveHelper.getAuthParam(paramMap));
		}
		return resutlMap;
	}
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping("setImage")
	public LiveApiResult<JSONObject> setImage(HttpServletRequest request, HttpServletResponse response, Long webinarId, String imageUrl){
		LiveApiResult<JSONObject> subjectInfo = new LiveApiResult<JSONObject>();
		try {
			if(webinarId != null && webinarId > 0){
				//获取活动开始结束信息
				String setImageUrl = "http://e.vhall.com/api/vhallapi/v2/webinar/activeimage";
				Map<String, String> infoParamMap = new HashMap<String, String>();
				infoParamMap.put("webinar_id", String.valueOf(webinarId));
				imageUrl = "D:\\images\\20.jpg";
				subjectInfo = LiveHelper.sendImagePostRequest(setImageUrl, infoParamMap, imageUrl);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return subjectInfo;
		
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("getRecord")
	public String getRecord(HttpServletRequest request, HttpServletResponse response, Model model, Long webinarId){
		try {
			if(webinarId != null && webinarId > 0){
				//获取活动开始结束信息
				String recordUrl = "http://e.vhall.com/api/vhallapi/v2/record/list";
				Map<String, String> recordParamMap = new HashMap<String, String>();
				recordParamMap.put("webinar_id", String.valueOf(webinarId));
				recordParamMap.put("limit", "1");
				LiveApiResult<JSONObject> recordInfo = LiveHelper.sendPostRequest(recordUrl, recordParamMap);
				//获得第一个回放
				JSONObject resultObject = (JSONObject)(recordInfo.getData().getObject("lists", JSONArray.class).get(0));
				
				model.addAttribute("webinarId",webinarId);
				model.addAttribute("recordId",resultObject.getString("id"));
				System.out.println(resultObject.getString("id"));
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return "views/college/record";
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("createRecord")
	public String createRecord(HttpServletRequest request, HttpServletResponse response, Model model, Long webinarId){
		try {
			if(webinarId != null && webinarId > 0){
				//获取活动开始结束信息
				String getInfoUrl = "http://e.vhall.com/api/vhallapi/v2/webinar/last-option-time";
				Map<String, String> infoParamMap = new HashMap<String, String>();
				infoParamMap.put("webinar_id", String.valueOf(webinarId));
				LiveApiResult<JSONObject> subjectInfo = LiveHelper.sendPostRequest(getInfoUrl, infoParamMap);
				if("200".equals(String.valueOf(subjectInfo.getCode()))){
					String startTime = subjectInfo.getData().getString("start_time");
					String endTime = subjectInfo.getData().getString("end_time");
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					//生成回放
					String createRecordUrl = "http://e.vhall.com/api/vhallapi/v2/record/create";
					Map<String, String> recordParamMap = new HashMap<String, String>();
					recordParamMap.put("webinar_id", String.valueOf(webinarId));
					recordParamMap.put("subject", "中智汇直播课");
					recordParamMap.put("type", "0");
					recordParamMap.put("start_time", sdf.parse(startTime).getTime()+"");
					recordParamMap.put("end_time", sdf.parse(endTime).getTime()+"");
					LiveApiResult<String> recordInfo = LiveHelper.sendPostRequest(createRecordUrl, recordParamMap);
					if("200".equals(String.valueOf(recordInfo.getCode()))){
						model.addAttribute("recordId", recordInfo.getData());
						model.addAttribute("errormsg", "createRecord:"+recordInfo.getMsg());
					}else{
						model.addAttribute("errormsg", "createRecord:"+recordInfo.getMsg());
					}
					
				}else{
					model.addAttribute("errormsg", "getLiveInfo:"+subjectInfo.getMsg());
				}
				
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return "views/college/record";
	}
	
	@ResponseBody
	@RequestMapping("validateAuth")
	public String validateAuth(HttpServletRequest request, HttpServletResponse response){
		
		return "";
	}
	
	private String formatTime(Date startTime,Date endTime){
		SimpleDateFormat startSdf = new SimpleDateFormat("MM月dd日 HH:mm");
		SimpleDateFormat endSdf = new SimpleDateFormat("HH:mm");
		String startTimeStr = startSdf.format(startTime);
		String endTimeStr = endSdf.format(endTime);
		String time = startTimeStr+"-"+endTimeStr;
		return time;
	}
	
	public static Pager pageableToPager(CourseQuery courseQuery,List<CourseItem> courseItems,long courseCount){
		Pager pager = new Pager();
		pager.setResultList(courseItems);
		pager.setPage(courseQuery.getPage());
		pager.setPageSize(courseQuery.getPageSize());
		pager.setRecords((int)courseCount);
		int totalPage = 0;
		if (courseCount % courseQuery.getPageSize() == 0) {
			totalPage = (int) courseCount / courseQuery.getPageSize();
		} else {
			totalPage = (int) courseCount / courseQuery.getPageSize() + 1;
		}
		pager.setTotal(totalPage);		
		return pager;
	}
	
	public IndexOperationEnum getIndexOperation(CourseItem courseItem) {
		if (CourseTypeEnum.LIVE.getCode() == courseItem.getType()) {
			// 直播
			if (courseItem.getHasLiveEnded()) {
				// 已结束
				return IndexOperationEnum.ENDED;
			}
			if (CourseSourceEnum.CLIENT.getCode() == courseItem.getSource()) {
				if (courseItem.getStartTime() != null && courseItem.getEndTime() != null) {
					Date now = new Date();
					long offset = 30L * 60 * 1000;
					if ((courseItem.getStartTime().getTime() - now.getTime()) > offset) {
						if (courseItem.getCurrentUserApplyFlag()) {
							return IndexOperationEnum.HAS_JOINED;
						} else {
							return IndexOperationEnum.JOIN_MY_SCHEDULE;
						}
					}
					return IndexOperationEnum.GOTO_LIVE;
				}
			} else {
				if (courseItem.getCurrentUserApplyFlag()) {
					Date now = new Date();
					long offset = 30L * 60 * 1000;
					if ((courseItem.getStartTime().getTime() - now.getTime()) > offset) {
						return IndexOperationEnum.HAS_JOINED;
					}
					return IndexOperationEnum.GOTO_LIVE;
				} else {
					return IndexOperationEnum.JOIN_MY_SCHEDULE;
				}
			}
		} else if (CourseTypeEnum.VIDEO.getCode() == courseItem.getType() || CourseTypeEnum.DOC.getCode() == courseItem.getType()) {
			if (CourseStatusEnum.ONLINE.getCode() == courseItem.getStatus()) {
				return IndexOperationEnum.GOTO_VIDEO;
			}
		} else if (CourseTypeEnum.OFF_LINE.getCode() == courseItem.getType()) {
			// 线下课
			if (courseItem.getHasLiveEnded()) {
				return IndexOperationEnum.ENDED;
			}
			if (courseItem.getCurrentUserApplyFlag()) {
				return IndexOperationEnum.HAS_JOINED;
			} else {
				return IndexOperationEnum.JOIN_MY_SCHEDULE;
			}
		}
		return null;
	}
	
	/**
	 * @Description: 获取返回页面 getReturnView
	 * @param courseType
	 * @return
	 */
	private String getReturnView(int courseType,String type) {
		if (CourseTypeEnum.LIVE.getCode() == courseType) {
			if("goToReply".equals(type)){
				return "/views/college/video";
			}else{
				return "/views/college/live";				
			}
		} else if (CourseTypeEnum.VIDEO.getCode() == courseType) {
			return "/views/college/video";
		}
		return "";
	}

}
