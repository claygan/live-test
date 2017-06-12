/**
 * Project Name: platform
 * File Name: PersonalDto.java
 * Package Name: com.zzh.youyong.platform.dto.user
 * Date: 2016年12月8日下午3:54:31 
 * Copyright (c) 2016, www.zhongzhihui.com All Rights Reserved. 
 */

package com.province.platform.pojos.dto;

import java.util.List;

/** 
 * ClassName: PersonalDto
 * Description: TODO(用一句话描述这个类)
 * 
 * @author linxiaojiu@zhongzhihui.com
 * @date: 2016年12月8日 下午3:54:31
 */
public class CenterMenuDto {
	
	private String link;

	private String name;
	
	private boolean current;
	
	private List<CenterMenuDto> sonMenus;

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isCurrent() {
		return current;
	}

	public void setCurrent(boolean current) {
		this.current = current;
	}

	public List<CenterMenuDto> getSonMenus() {
		return sonMenus;
	}

	public void setSonMenus(List<CenterMenuDto> sonMenus) {
		this.sonMenus = sonMenus;
	}

	
}
