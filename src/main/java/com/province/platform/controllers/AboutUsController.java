package com.province.platform.controllers;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.province.platform.cookies.CookieUtil;
import com.zzh.base.api.AddServiceRemoteService;
import com.zzh.common.constants.AddServiceConstants;

@Controller
public class AboutUsController {
	@Resource
	private AddServiceRemoteService addServiceRemoteService;
	/** 
	 * @Title: toAboutUs
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @author ganshimin@zhongzhihui.com
	 * @return  
	 */  
	@RequestMapping("/aboutUs")
	public String toAboutUs(HttpServletRequest request, HttpServletResponse response, Model model){
		long companyId = CookieUtil.getCompanyId(request);
		//地址
		String address = addServiceRemoteService.selectAddServiceMetaValue(companyId, AddServiceConstants.ADD_SERVICE_META_ADDRESS);
		model.addAttribute("address", address);
		//电话
		String tel = addServiceRemoteService.selectAddServiceMetaValue(companyId, AddServiceConstants.ADD_SERVICE_META_TEL);
		model.addAttribute("tel", tel);
		//邮箱
		String email = addServiceRemoteService.selectAddServiceMetaValue(companyId, AddServiceConstants.ADD_SERVICE_META_EMAIL);
		model.addAttribute("email", email);
		//坐标图片链接
		String locationImgUrl = addServiceRemoteService.selectAddServiceMetaValue(companyId, AddServiceConstants.ADD_SERVICE_META_LOCATION_IMGURL);
		model.addAttribute("locationImgUrl", locationImgUrl);
		
		return "views/about-us";
	}
}
