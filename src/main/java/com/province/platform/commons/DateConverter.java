/**
 * Project Name: zzh-cloud-web
 * File Name: DateConverter.java
 * Package Name: com.zzh.cloud.web
 * Date: 2015-8-26下午3:07:18 
 * Copyright (c) 2015, www.zhongzhihui.com All Rights Reserved. 
 */

package com.province.platform.commons;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.core.convert.converter.Converter;

/**
 * ClassName: DateConverter Description: TODO(用一句话描述这个类)
 * 
 * @author jupiter@zhongzhihui.com
 * @date: 2015-8-26 下午3:07:18
 */
public class DateConverter implements Converter<String, Date> {
	private static final String[] formats = { "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", "HH:mm:ss","yyyy-MM-dd HH:mm"};

	@Override
	public Date convert(String source) {
		if (StringUtils.isBlank(source)) {
			return null;
		}
		Date result = null;
		try {
			result = DateUtils.parseDate(source, formats);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}
	
	
	public static void main(String[] args) throws ParseException {
		String source  = "2015-12-12 11:11";
		System.out.println(DateUtils.parseDate(source, formats));
	}

}
