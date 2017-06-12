
package com.province.platform.cookies;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

import com.province.platform.commons.GlobalDefine;

/**
 * ClassName: CookieUtil Description: TODO(用一句话描述这个类)
 * 
 * @author jupiter@zhongzhihui.com
 * @date: 2015-8-23 下午11:14:22
 */
public class CookieUtil {
	
	/** 
	 * @Description:用户id
	 */  
	public static final String COOKIE_USER_ID = "userId";
	
	/** 
	 * @Description:平台id 
	 */  
	public static final String COOKIE_COMPANY_ID = "companyId";
	
	/** 
	 * @Description:直播令牌
	 */  
	public static final String COOKIE_LIVE_TOKEN = "liveToken";
	
	static final String[] AUTHORITIES = new String[0];


	public static void writeCookie(HttpServletRequest request, String key, String value, int expire) {

		Assert.notNull(request, "write cookie error. http request cannot be null!");

		CookieModule jar = (CookieModule) request.getAttribute(CookieModule.COOKIE);
		if (jar == null) {
			throw new NullPointerException();
		}
		jar.remove(key);
		jar.set(key, value, expire);
	}

	public static void clearCookie(HttpServletRequest request, String key) {
		CookieModule jar = (CookieModule) request.getAttribute(CookieModule.COOKIE);
		if (jar != null) {
			jar.remove(key);
		}
	}
	
	/** 
	 * @Title: clearCookie
	 * @Description: 清空cookie
	 * @author ganshimin@zhongzhihui.com
	 * @param request  
	 */  
	public static void clearCookie(HttpServletRequest request) {
		clearCookie(request,COOKIE_USER_ID);
		clearCookie(request,COOKIE_COMPANY_ID);
		clearCookie(request,COOKIE_LIVE_TOKEN);
	}
	
	/** 
	 * @Title: writeCookieForUser
	 * @Description: 写入用户id到cookie
	 * @author ganshimin@zhongzhihui.com
	 * @param request
	 * @param userId
	 * @param expire  
	 */  
	public static void writeCookieForUser(HttpServletRequest request, String userId, int expire) {
		writeCookie(request, COOKIE_USER_ID, userId, expire);
	}
	
	public static void writeCookieForUser(HttpServletRequest request, String userId) {
		writeCookie(request, COOKIE_USER_ID, userId, GlobalDefine.commonConstants.DEFUALT_EXPIRE);
	}
	
	/** 
	 * @Title: writeCookieForCompany
	 * @Description: 写入平台id到cookie
	 * @author ganshimin@zhongzhihui.com
	 * @param request
	 * @param companyId
	 * @param expire  
	 */  
	public static void writeCookieForCompany(HttpServletRequest request, String companyId, int expire){
		writeCookie(request, COOKIE_COMPANY_ID, companyId, expire);
	}
	
	public static void writeCookieForCompany(HttpServletRequest request, String companyId){
		writeCookie(request, COOKIE_COMPANY_ID, companyId, GlobalDefine.commonConstants.COMPANY_EXPIRE);
	}
	
	/** 
	 * @Title: getLiveToken
	 * @Description: 获取liveToken
	 * @author ganshimin@zhongzhihui.com
	 * @param request
	 * @return  
	 */  
	public static String getLiveToken(HttpServletRequest request){
		if (request == null) {
			throw new IllegalArgumentException();
		}
		CookieModule jar = (CookieModule) request.getAttribute(CookieModule.COOKIE);
		if (jar == null) {
			return null;
		}
		String liveToken = jar.get(COOKIE_LIVE_TOKEN);
		return liveToken;
	}
	
	/** 
	 * @Title: getUserId
	 * @Description: 获取userId
	 * @author ganshimin@zhongzhihui.com
	 * @param request
	 * @return  
	 */  
	public static Long getUserId(HttpServletRequest request) {
		if (request == null) {
			throw new IllegalArgumentException();
		}
		CookieModule jar = (CookieModule) request.getAttribute(CookieModule.COOKIE);

		if (jar == null) {
			return null;
		}
		String idStr = jar.get(COOKIE_USER_ID);
		if (StringUtils.isNumeric(idStr)) {
			return Long.valueOf(idStr);
		}
		return null;
	}
	
	/** 
	 * @Title: isLogin
	 * @Description: 判断是否登录
	 * @author ganshimin@zhongzhihui.com
	 * @param request
	 * @return  
	 */  
	public static boolean isLogin(HttpServletRequest request) {
		Long userId = getUserId(request);
		if (userId != null) {
			return true;
		}
		return false;
	}
	
	/** 
	 * @Title: getCompanyId
	 * @Description: 获取当前平台id
	 * @author ganshimin@zhongzhihui.com
	 * @param request
	 * @return  
	 */  
	public static Long getCompanyId(HttpServletRequest request) {
		if (request == null) {
			throw new IllegalArgumentException();
		}
		CookieModule jar = (CookieModule) request.getAttribute(CookieModule.COOKIE);

		if (jar == null) {
			return null;
		}
		String idStr = jar.get(COOKIE_COMPANY_ID);
		if (StringUtils.isNumeric(idStr)) {
			return Long.valueOf(idStr);
		}else{
			//set companyIds
		}
		return null;
	}
	
}
