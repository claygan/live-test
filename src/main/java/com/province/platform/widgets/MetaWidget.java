package com.province.platform.widgets;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.province.platform.cookies.CookieUtil;

/** 
 * @ClassName: MetaWidget
 * @Description: 公共基本
 * 
 * @author ganshimin@zhongzhihui.com
 * @date: 2017年4月8日 下午4:29:36
 */  
@Widget("widgets/meta.html")
public class MetaWidget implements FreeMakerWidget {
	
	@Override
	public void referenceData(HttpServletRequest request, Map<String, Object> model) {
		//判断用户是否登录
		boolean isLogin = CookieUtil.isLogin(request);
		model.put("isLogin", isLogin);
	}
	/** 
	 * @Title: convertStarString
	 * @Description: 字符串加星号处理
	 * @author ganshimin@zhongzhihui.com
	 * @param content
	 * @param begin
	 * @param end
	 * @return  
	 */  
	public static String convertStarString(String content,int begin,int end){
		content = content.substring(0,begin)+"***"+content.substring(content.length()-end, content.length());
		return content;
	}

}
