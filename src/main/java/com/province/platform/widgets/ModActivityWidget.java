package com.province.platform.widgets;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.province.platform.cookies.CookieUtil;
import com.province.platform.helper.ServiceFactory;
import com.zzh.base.api.ThemedActivityRemoteService;
import com.zzh.base.api.entity.ListThemedActivityQueryRequest;
import com.zzh.base.api.entity.Pageable;
import com.zzh.base.api.entity.activity.ThemedActivityItem;

/** 
 * @ClassName: ModActivityWidget
 * @Description: 活动模块
 * 
 * @author ganshimin@zhongzhihui.com
 * @date: 2017年4月7日 上午11:23:05
 */  
@Widget("widgets/mod-activity.html")
public class ModActivityWidget implements FreeMakerWidget {
	Logger logger = LoggerFactory.getLogger(ModActivityWidget.class);
	
	/** 
	 * @Description:活动模块显示个数
	 */  
	private static final int ACTIVITY_MOD_PAGESIZE = 3;

	@Override
	public void referenceData(HttpServletRequest request, Map<String, Object> model) {
		long companyId = CookieUtil.getCompanyId(request);
		ThemedActivityRemoteService activityService = ServiceFactory.getBean("themedActivityRemoteService", ThemedActivityRemoteService.class);
		ListThemedActivityQueryRequest activityQuery = new ListThemedActivityQueryRequest();
		activityQuery.setCompanyId(companyId);
		activityQuery.setIsOnline(true);
		activityQuery.setOrderBy("start_time desc");
		activityQuery.setPageSize(ACTIVITY_MOD_PAGESIZE);
		activityQuery.setPage(1);
		Pageable<ThemedActivityItem> pageable = activityService.queryPage(activityQuery);
		
		model.put("modActivityItems", pageable.getRows());
		
		
	}

}
