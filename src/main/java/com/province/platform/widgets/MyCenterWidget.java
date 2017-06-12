/**
 * Project Name: platform
 * File Name: HeaderWidget.java
 * Package Name: com.zzh.youyong.platform.widgets
 * Date: 2016年12月7日上午9:46:34
 * Copyright (c) 2016, www.zhongzhihui.com All Rights Reserved.
 */

package com.province.platform.widgets;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import com.province.platform.cookies.CookieUtil;
import com.province.platform.helper.ServiceFactory;
import com.province.platform.pojos.dto.CenterMenuDto;
import com.zzh.user.api.UserRemoteService;
import com.zzh.user.api.entity.UserEntityInfo;

/**
 * @ClassName: HeaderWidget
 * @Description: 个人中心
 * @author zhuningkang@zhongzhihui.com
 * @date: 2016年12月7日 上午9:46:34
 */
@Widget("widgets/myCenter.html")
public class MyCenterWidget implements FreeMakerWidget {
	
	public static List<CenterMenuDto> menus;
	static {
		menus = new ArrayList<CenterMenuDto>();
		// 我的账户
		CenterMenuDto myInfo = new CenterMenuDto();
		myInfo.setLink("/mycenter/myInfo");
		myInfo.setName("我的信息");
		menus.add(myInfo);

		// 我的消息
//		CenterMenuDto myMsg = new CenterMenuDto();
//		myMsg.setLink("/userCenter/messgae/my");
//		myMsg.setName("我的消息");
//		menus.add(myMsg);
	}

	@Override
	public void referenceData(HttpServletRequest request, Map<String, Object> model) {
		String url = request.getRequestURI();
		String content = request.getContextPath();//如果有部署名
		url = url.split("\\?")[0];
		for (CenterMenuDto menu : menus) {
			if (menu.getSonMenus() == null) {
				String link = content+menu.getLink();
				if (link.equals(url)) {
					menu.setCurrent(true);
				} else {
					menu.setCurrent(false);
				}
			} else {
				// 二级
				List<CenterMenuDto> twoMenus = menu.getSonMenus();
				for (CenterMenuDto personalTwoMenu : twoMenus) {
					String link = content+personalTwoMenu.getLink();
					if (link.equals(url)) {
						personalTwoMenu.setCurrent(true);
					} else {
						personalTwoMenu.setCurrent(false);
					}
				}
			}
		}
		Properties properties = ServiceFactory.getBean("config", Properties.class);
		UserRemoteService userRemoteService = ServiceFactory.getBean("userRemoteService", UserRemoteService.class);
		long userId = CookieUtil.getUserId(request);
		UserEntityInfo userBaseInfo =  userRemoteService.findByUserId(userId, null);
		if(userBaseInfo.getAvatarUrl() == null){
			userBaseInfo.setAvatarUrl(properties.getProperty("default.avatar"));
		}else{
			userBaseInfo.setAvatarUrl(userBaseInfo.getAvatarUrl());
		}
		model.put("menus", menus);
		model.put("myCenterDto", userBaseInfo);

	}

}
