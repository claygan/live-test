package com.province.platform.controllers;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zzh.base.api.RegionRemoteService;
import com.zzh.base.api.entity.RegionItem;

@Controller
@RequestMapping("/region")
public class RegionController {

	@Resource(name="regionRemoteService")
	private RegionRemoteService regionRemoteService;
	
	
	/**
	* @Title: queryRegionByParentId 
	* @Description: 根据父ID查询地区，如果父ID为0，则查询所有省份 
	* @param @param request
	* @param @param response
	* @param @param parentId
	* @param @return    设定文件 
	* @return List<RegionInfo>    返回类型 
	* @throws
	 */
	@RequestMapping("/queryRegion")
	public List<RegionItem> queryRegionByParentId(HttpServletRequest request,HttpServletResponse response,String parentId){
		long _parentId = 1;
		if(StringUtils.isNotBlank(parentId)){
			_parentId = Long.valueOf(parentId);
		}
		List<RegionItem> regionList = this.regionRemoteService.findByParentId(_parentId);
		return regionList;
	}
	
}
