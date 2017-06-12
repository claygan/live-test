package com.province.platform.pojos.dto;

import java.util.List;

import com.zzh.base.api.entity.NewsItem;

public class IndexNewsDto {
	/** 
	 * @Description:新闻分类名称 
	 */  
	private String categoryName;
	/** 
	 * @Description:分类宣传语（省级平台）
	 */  
	private String slogan;
	/** 
	 * @Description:左侧资讯列表
	 */  
	private List<NewsItem> leftNewsItems;
	/** 
	 * @Description:右侧资讯列表 
	 */  
	private List<NewsItem> rightNewsItems;
	
	
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public String getSlogan() {
		return slogan;
	}
	public void setSlogan(String slogan) {
		this.slogan = slogan;
	}
	public List<NewsItem> getLeftNewsItems() {
		return leftNewsItems;
	}
	public void setLeftNewsItems(List<NewsItem> leftNewsItems) {
		this.leftNewsItems = leftNewsItems;
	}
	public List<NewsItem> getRightNewsItems() {
		return rightNewsItems;
	}
	public void setRightNewsItems(List<NewsItem> rightNewsItems) {
		this.rightNewsItems = rightNewsItems;
	}
	
	
	
}
