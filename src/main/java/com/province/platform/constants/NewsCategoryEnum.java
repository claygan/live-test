/**
 * Project Name: client_admin_core
 * File Name: NewsCategoryEnum.java
 * Package Name: com.zzh.core.module.biz.constant.news
 * Date: 2016-1-18下午3:45:19 
 * Copyright (c) 2016, www.zhongzhihui.com All Rights Reserved. 
 */

package com.province.platform.constants;
/** 
 * ClassName: NewsCategoryEnum
 * Description: TODO(用一句话描述这个类)
 * 
 * @author wangzhen@zhongzhihui.com
 * @date: 2016-1-18 下午3:45:19
 */
public enum NewsCategoryEnum {

	GENERAL_NEWS(2,"资讯动态"),INDUSTRIAL_POLICY(9, "电商政策");

	private int code;

	private String title;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	private NewsCategoryEnum(int code, String title) {
		this.code = code;
		this.title = title;
	}

	public static String getNameByCode(int code) {
		NewsCategoryEnum[] newsCategoryEnums = NewsCategoryEnum.values();
		for (NewsCategoryEnum categoryEnum : newsCategoryEnums) {
			if (categoryEnum.getCode() == code) {
				return categoryEnum.title;
			}
		}
		return "";
	}

}
