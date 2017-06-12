package com.province.platform.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.province.platform.cookies.CookieUtil;
import com.province.platform.helper.ServiceFactory;
import com.zzh.base.api.AddServiceRemoteService;
import com.zzh.base.api.ThemedActivityRemoteService;
import com.zzh.base.api.entity.ListThemedActivityQueryRequest;
import com.zzh.base.api.entity.Pageable;
import com.zzh.base.api.entity.activity.ThemedActivityItem;
import com.zzh.common.constants.AddServiceConstants;
import com.zzh.common.utils.LangUtil;
import com.zzh.course.api.CourseRemoteService;
import com.zzh.course.api.CourseRemoteService.CourseBuilderEnum;
import com.zzh.course.api.entity.CourseInfo;
import com.zzh.course.api.entity.CourseItem;

/** 
 * @ClassName: ModActivityWidget
 * @Description: 活动模块
 * 
 * @author ganshimin@zhongzhihui.com
 * @date: 2017年4月7日 上午11:23:05
 */  
@Widget("widgets/mod-study.html")
public class ModStudyWidget implements FreeMakerWidget {
	Logger logger = LoggerFactory.getLogger(ModStudyWidget.class);
	
	/** 
	 * @Description:线上学习显示个数
	 */  
	//private static final int STUDY_MOD_PAGESIZE = 6;
	
	@Override
	public void referenceData(HttpServletRequest request, Map<String, Object> model) {
		long companyId = CookieUtil.getCompanyId(request);
		AddServiceRemoteService addServiceRemoteService = ServiceFactory.getBean("addServiceRemoteService", AddServiceRemoteService.class);
		CourseRemoteService courseRemoteService = ServiceFactory.getBean("courseRemoteService", CourseRemoteService.class);
		String selectAddServiceMetaValue = addServiceRemoteService.selectAddServiceMetaValue(companyId, AddServiceConstants.ADD_SERVICE_META_ONLINE_STUDY);
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
		model.put("courseItems", courseItems);
		
	}

}
