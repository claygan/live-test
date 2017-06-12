package com.province.platform.widgets;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.province.platform.cookies.CookieUtil;
import com.province.platform.helper.ServiceFactory;
import com.zzh.base.api.AddServiceRemoteService;
import com.zzh.base.api.ParkRemoteService;
import com.zzh.base.api.entity.ParkItem;
import com.zzh.common.constants.AddServiceConstants;
import com.zzh.common.utils.LangUtil;


/** 
 * @ClassName: ModOrgnizeWidget
 * @Description: 园区辅导
 * 
 * @author ganshimin@zhongzhihui.com
 * @date: 2017年4月7日 下午3:14:03
 */  
@Widget("widgets/mod-park.html")
public class ModParkWidget implements FreeMakerWidget {
	Logger logger = LoggerFactory.getLogger(ModParkWidget.class);
	
	/** 
	 * @Description:活动模块显示个数
	 */  
	//private static final int ACTIVITY_MOD_PAGESIZE = 3;

	@Override
	public void referenceData(HttpServletRequest request, Map<String, Object> model) {
		long companyId = CookieUtil.getCompanyId(request);
		AddServiceRemoteService addServiceRemoteService = ServiceFactory.getBean("addServiceRemoteService", AddServiceRemoteService.class);
		ParkRemoteService parkRemoteService = ServiceFactory.getBean("parkRemoteService", ParkRemoteService.class);
		String selectAddServiceMetaValue = addServiceRemoteService.selectAddServiceMetaValue(companyId, AddServiceConstants.ADD_SERVICE_META_PARK);
		List<ParkItem> parkItems = new ArrayList<ParkItem>();
		if(StringUtils.isNotBlank(selectAddServiceMetaValue)){
			String[] split = selectAddServiceMetaValue.split(",");
			for(int i=0;i<split.length;i++){
				ParkItem parkItem = parkRemoteService.findParkById(LangUtil.parseLong(split[i]));
				if(parkItem != null){
					parkItems.add(parkItem);
				}
			}
		}
		model.put("parkItems", parkItems);
	}

}
