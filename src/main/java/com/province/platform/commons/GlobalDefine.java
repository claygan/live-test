package com.province.platform.commons;

public class GlobalDefine {
	
	/** 
	 * @ClassName: commonConstants
	 * @Description: 公共常量
	 * 
	 * @author ganshimin@zhongzhihui.com
	 * @date: 2017年3月29日 下午2:34:48
	 */  
	public static class commonConstants{
		public static final int DEFUALT_EXPIRE = 7*24*3600;
		public static final int COMPANY_EXPIRE = 1*24*3600;
	}
	
	/** 
	 * @ClassName: resultCode
	 * @Description:返回状态码规范
	 * 
	 * @author ganshimin@zhongzhihui.com
	 * @date: 2017年3月29日 下午2:33:45
	 */  
	public static class resultCode {
		/** 
		 * @Description:返回成功
		 */  
		public static final int SUCCESS = 0;
		/** 
		 * @Description:后端接口未知错误
		 */  
		public static final int INTERNAL_ERROR = 1;
		/** 
		 * @Description:页面跳转响应
		 */  
		public static final int REDIRECT_ERROR = 2;
		/** 
		 * @Description:登录异常 
		 */  
		public static final int LOGIN_ERROR = -100;
		/** 
		 * @Todo:业务状态码扩展从100开始
		 */
		
	}
	
}
