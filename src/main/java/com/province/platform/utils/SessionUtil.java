package com.province.platform.utils;

import javax.servlet.http.HttpServletRequest;

import com.zzh.user.api.entity.UserEntityInfo;

public class SessionUtil {
	
	/** 
	 * @Description:用户信息 
	 */  
	public static final String USER_INFO_SESSION = "USER_INFO_SESSION";
	
	/** 
	 * @Title: getUserInfo
	 * @Description: 获取用户信息
	 * @author ganshimin@zhongzhihui.com
	 * @param request
	 * @return  
	 */  
	public static UserEntityInfo getUserInfo(HttpServletRequest request){
		return (UserEntityInfo)request.getSession().getAttribute(USER_INFO_SESSION);
	}
	/** 
	 * @Title: setUserInfo
	 * @Description: 存入用户信息
	 * @author ganshimin@zhongzhihui.com
	 * @param request
	 * @param userInfo  
	 */  
	public static void setUserInfo(HttpServletRequest request,UserEntityInfo userInfo){
		request.getSession().setAttribute(USER_INFO_SESSION, userInfo);
	}
	
	/** 
	 * @Title: removeUserInfo
	 * @Description: 清除用户信息
	 * @author ganshimin@zhongzhihui.com
	 * @param request  
	 */  
	public static void removeUserInfo(HttpServletRequest request){
		request.getSession().removeAttribute(USER_INFO_SESSION);
	}
}
