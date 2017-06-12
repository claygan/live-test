
package com.province.platform.controllers;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.province.platform.commons.ApiResult;
import com.province.platform.commons.Pager;
import com.province.platform.cookies.CookieUtil;
import com.province.platform.helper.PagerHelper;
import com.zzh.base.api.ParkRemoteService;
import com.zzh.base.api.entity.AreaItem;
import com.zzh.base.api.entity.ListParkQueryRequest;
import com.zzh.base.api.entity.Pageable;
import com.zzh.base.api.entity.ParkItem;

/** 
 * @ClassName: ParkController
 * @Description: 园区辅导
 * 
 * @author ganshimin@zhongzhihui.com
 * @date: 2017年4月6日 下午7:23:29
 */  
@Controller
@RequestMapping("/park")
public class ParkController {
	@Resource
	private ParkRemoteService parkRemoteService;
	
	/** 
	 * @Description:园区辅导首页显示个数
	 */  
	private static final int INDEX_PARK_PAGESIZE = 16;
	
	/** 
	 * @Title: toParkIndex
	 * @Description: 进入园区辅导首页，查询地区列表
	 * @author ganshimin@zhongzhihui.com
	 * @param request
	 * @param response
	 * @param model
	 * @return  
	 */  
	@RequestMapping("/index")
	public String toParkIndex(HttpServletRequest request,HttpServletResponse response, Model model){
		long companyId = CookieUtil.getCompanyId(request);
		//查询地区
		List<AreaItem> areaItems = parkRemoteService.queryArea(companyId);
		model.addAttribute("areaItems", areaItems);
		return "views/park/index";
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ResponseBody
	@RequestMapping("/queryPark")
	public ApiResult queryParkList(HttpServletRequest request,HttpServletResponse response,Pager pager,Long areaId,String keyWord){
		ApiResult result = new ApiResult();
		long companyId = CookieUtil.getCompanyId(request);
		ListParkQueryRequest queryRequest = new ListParkQueryRequest();
		if(areaId != null && areaId > 0){
			queryRequest.setAreaId(areaId);
		}
		if(StringUtils.isNotEmpty(keyWord)){
			queryRequest.setName(keyWord);
		}
		queryRequest.setCompanyId(companyId);
		pager.setPageSize(INDEX_PARK_PAGESIZE);
		queryRequest.setPage(pager.getPage());
		queryRequest.setPageSize(INDEX_PARK_PAGESIZE);
		Pageable<ParkItem> pageable = parkRemoteService.queryPark(queryRequest);
		result.setData(PagerHelper.pageableToPager(pageable));
		return result;
	}
	
	@RequestMapping("/detail")
	public String toDetail(HttpServletRequest request,HttpServletResponse response, Model model,@RequestParam(value="id", required=true) Long id){
		ParkItem parkItem = parkRemoteService.findParkById(id);
		model.addAttribute("parkItem", parkItem);
		
		return "views/park/detail";
	}
}
