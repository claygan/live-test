/**
 * Project Name: province-platform1-0
 * File Name: ServiceController.java
 * Package Name: com.province.platform.controllers
 * Date: 2017年4月1日上午11:21:25 
 * Copyright (c) 2017, www.zhongzhihui.com All Rights Reserved. 
 */

package com.province.platform.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.province.platform.commons.ApiResult;
import com.province.platform.commons.GlobalDefine;
import com.province.platform.commons.Pager;
import com.province.platform.cookies.CookieUtil;
import com.province.platform.helper.PagerHelper;
import com.province.platform.helper.VendorHelper;
import com.province.platform.utils.DefenseUtil;
import com.zzh.common.constants.vendor.ProductCheckStatusEnum;
import com.zzh.common.constants.vendor.ProductStatusEnum;
import com.zzh.common.constants.vendor.VendorOrderStatusEnum;
import com.zzh.common.entity.Pageable;
import com.zzh.common.utils.DateUtil;
import com.zzh.common.utils.LangUtil;
import com.zzh.user.api.UserRemoteService;
import com.zzh.user.api.entity.UserEntityInfo;
import com.zzh.vendor.api.ProductRemoteService;
import com.zzh.vendor.api.VendorRemoteService;
import com.zzh.vendor.api.entity.VCategoryItem;
import com.zzh.vendor.api.entity.VCategoryQueryRequest;
import com.zzh.vendor.api.entity.VProductItem;
import com.zzh.vendor.api.entity.VProductQueryRequest;
import com.zzh.vendor.api.entity.VendorItem;
import com.zzh.vendor.api.entity.VendorOrderItem;
import com.zzh.vendor.api.entity.VendorOrderQueryRequest;

/** 
 * ClassName: ServiceController
 * Description: TODO(用一句话描述这个类)
 * 
 * @author zhuningkang@zhongzhihui.com
 * @date: 2017年4月1日 上午11:21:25
 */
@Controller
@RequestMapping("/vendor")
public class VendorController {

	private static Logger logger = LoggerFactory.getLogger(VendorController.class);
	
	private static final int INDEX_PRODUCT_PAGESIZE = 24;
	
	@Resource
	private VendorRemoteService vendorRemoteService;
	
	@Resource
	private ProductRemoteService productRemoteService;
	
	@Resource
	private UserRemoteService userRemoteService;

	@RequestMapping("/index")
	public String showService(HttpServletRequest request,HttpServletResponse response,Model model){
		VCategoryQueryRequest vCategoryQueryRequest = new VCategoryQueryRequest();
		vCategoryQueryRequest.setPageSize(Integer.MAX_VALUE);
		Pageable<VCategoryItem> pageVCategory = vendorRemoteService.pageVCategory(vCategoryQueryRequest);
		List<VCategoryItem> vCategoryItems = pageVCategory.getRows();
		List<VCategoryItem> vCategoryParent = new ArrayList<VCategoryItem>();//一级分类
		List<VCategoryItem> vCategoryChild= new ArrayList<VCategoryItem>();//子分类
		if(!CollectionUtils.isEmpty(vCategoryItems)){
			for (VCategoryItem vCategoryItem : vCategoryItems) {
				if (vCategoryItem.getParentId() == 0) {
					vCategoryParent.add(vCategoryItem);
				} else {
					vCategoryChild.add(vCategoryItem);
				}
			}
		}
		model.addAttribute("vCategoryParent", vCategoryParent);
		model.addAttribute("vCategoryChild", vCategoryChild);
		return "/views/vendor/index";
	}
	
	/** 
	 * @Title: queryVProduct
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @author zhuningkang@zhongzhihui.com
	 * @param request
	 * @param response
	 * @return  
	 */  
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ResponseBody
	@RequestMapping("/queryVProduct")
	public ApiResult queryVProduct(HttpServletRequest request, HttpServletResponse response,Pager pager){
		ApiResult result = new ApiResult();
		String keyWord = request.getParameter("keyWord");
		String categoryId = request.getParameter("categoryId");
		try {
			VProductQueryRequest vProductQueryRequest = new VProductQueryRequest();
			Long companyId = CookieUtil.getCompanyId(request);
			vProductQueryRequest.setCompanyId(companyId);
			if(StringUtils.isNotBlank(keyWord)){
				vProductQueryRequest.setName(DefenseUtil.dealSqlStr(keyWord));
				logger.debug("=======分类搜索服务商条件：keyWord======="+keyWord);
			}
			if(StringUtils.isNotBlank(categoryId) && LangUtil.parseLong(categoryId) != -1){
				vProductQueryRequest.setCategoryId(LangUtil.parseLong(categoryId));
				logger.debug("=======分类搜索服务商条件：categoryId======="+categoryId);
			}
			vProductQueryRequest.setPage(pager.getPage());
			pager.setPageSize(INDEX_PRODUCT_PAGESIZE);
			vProductQueryRequest.setPageSize(INDEX_PRODUCT_PAGESIZE);
			vProductQueryRequest.setStatus(ProductStatusEnum.ONLINE.getCode());//产品默认上架
			vProductQueryRequest.setCheckStatus(ProductCheckStatusEnum.PASS.getCode());//产品默认审核通过
			
			Pageable<VProductItem> pageVProduct = productRemoteService.pageVProduct(vProductQueryRequest);
			List<VProductItem> vProductItems = pageVProduct.getRows();
			if(!CollectionUtils.isEmpty(vProductItems)){
				for(VProductItem vProductItem:vProductItems){
					VendorHelper.getVProductItem(vProductItem);
				}
				pageVProduct.setRows(vProductItems);
			}
			result.setData(PagerHelper.pageableToPager(pageVProduct));
		} catch (Exception e) {
			e.printStackTrace();
			result.setMsg("系统错误");
			result.setError(GlobalDefine.resultCode.INTERNAL_ERROR);
		}
		return result;
	}
	
	@RequestMapping("/productDetail")
	public String showProductDetail(HttpServletRequest request,HttpServletResponse response,Model model){
		String idValue = request.getParameter("id");
		Long companyId = CookieUtil.getCompanyId(request);
		try {
			if(StringUtils.isNotBlank(idValue)){
				VProductItem vProductItem = productRemoteService.selectVProductById(LangUtil.parseLong(idValue));
				VendorHelper.getVProductItem(vProductItem);
				if(vProductItem != null){
					long vendorId = vProductItem.getVendorId();
					VendorItem vendorItem = vendorRemoteService.findById(vendorId);
					model.addAttribute("vProductItem", vProductItem);
					model.addAttribute("vendorItem", vendorItem);
				}
				VendorOrderQueryRequest vendorOrderQueryRequest = new VendorOrderQueryRequest();
				vendorOrderQueryRequest.setCompanyId(companyId);
				vendorOrderQueryRequest.setPageSize(Integer.MAX_VALUE);
				vendorOrderQueryRequest.setVproductId(LangUtil.parseLong(idValue));
				Pageable<VendorOrderItem> vendorOrderPage = vendorRemoteService.pageVendorOrder(vendorOrderQueryRequest);
				List<VendorOrderItem> vendorOrderItems = vendorOrderPage.getRows();
				if(!CollectionUtils.isEmpty(vendorOrderItems)){
					for(VendorOrderItem vendorOrderItem:vendorOrderItems){
						vendorOrderItem.setStatusName(VendorOrderStatusEnum.getNameByCode(vendorOrderItem.getStatus()));
						vendorOrderItem.setCreateTimeStr(DateUtil.getDateString(vendorOrderItem.getCreateTime(), "yyyy-MM-dd"));
					}
				}
				model.addAttribute("vendorOrderItems", vendorOrderItems);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/views/vendor/productDetail";
	}
	
	@RequestMapping("/vendorDetail")
	public String showVendorDetail(HttpServletRequest request,HttpServletResponse response,Model model){
		String idValue = request.getParameter("id");
		try {
			if(StringUtils.isNotBlank(idValue)){
				VendorItem vendorItem = vendorRemoteService.findById(LangUtil.parseLong(idValue));
				if(vendorItem != null){
					model.addAttribute("vendorItem", vendorItem);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/views/vendor/vendorDetail";
	}
	
	@RequestMapping("/vendorProduct")
	public String showVendorProduct(HttpServletRequest request,HttpServletResponse response,Model model){
		String idValue = request.getParameter("id");
		try {
			if(StringUtils.isNotBlank(idValue)){
				VendorItem vendorItem = vendorRemoteService.findById(LangUtil.parseLong(idValue));
				if(vendorItem != null){
					model.addAttribute("vendorItem", vendorItem);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/views/vendor/vendorProductList";
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ResponseBody
	@RequestMapping("/queryVendorProduct")
	public ApiResult queryVendorProduct(HttpServletRequest request, HttpServletResponse response,Pager pager){
		ApiResult result = new ApiResult();
		String idValue = request.getParameter("vendorId");
		try {
			VProductQueryRequest vProductQueryRequest = new VProductQueryRequest();
			if(StringUtils.isNotBlank(idValue)){
				vProductQueryRequest.setVendorId(LangUtil.parseLong(idValue));
			}
			pager.setPageSize(INDEX_PRODUCT_PAGESIZE);
			vProductQueryRequest.setPage(pager.getPage());
			vProductQueryRequest.setPageSize(INDEX_PRODUCT_PAGESIZE);
			vProductQueryRequest.setStatus(ProductStatusEnum.ONLINE.getCode());
			Pageable<VProductItem> pageVProduct = productRemoteService.pageVProduct(vProductQueryRequest);
			List<VProductItem> vProductItems = pageVProduct.getRows();
			if(!CollectionUtils.isEmpty(vProductItems)){
				for(VProductItem vProductItem:vProductItems){
					VendorHelper.getVProductItem(vProductItem);
				}
				pageVProduct.setRows(vProductItems);
			}
			result.setData(PagerHelper.pageableToPager(pageVProduct));
		} catch (Exception e) {
			e.printStackTrace();
			result.setMsg("系统错误");
			result.setError(GlobalDefine.resultCode.INTERNAL_ERROR);
		}
		return result;
	}
	
	@RequestMapping("/showOrder")
	public String showOrder(HttpServletRequest request,HttpServletResponse response,Model model){
		long userId = CookieUtil.getUserId(request);
		String productId = request.getParameter("productId");
		if(StringUtils.isNotBlank(productId)){
			VProductItem vProductItem = productRemoteService.selectVProductById(LangUtil.parseLong(productId));
			if(vProductItem != null){
				model.addAttribute("vProductItem", vProductItem);
			}
		}
		UserEntityInfo userEntityInfo = userRemoteService.findByUserId(userId,null);
		if(userEntityInfo != null){
			model.addAttribute("userEntityInfo", userEntityInfo);
		}
		
		return "/views/vendor/order";
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping("/toOrder")
	@ResponseBody
	public ApiResult toOrder(HttpServletRequest request, HttpServletResponse response, String username ,String mobile, String remark, String vProductId, String returnUrl) {
		ApiResult result = new ApiResult();
		long userId = CookieUtil.getUserId(request);
		Long companyId = CookieUtil.getCompanyId(request);
		try {
			if(userId == 0L){
				logger.error("用户未登入");
				result.setMsg("未登入");
				result.setError(GlobalDefine.resultCode.INTERNAL_ERROR);
				return result;
			}
			VendorOrderItem vendorOrderItem = new VendorOrderItem();
			vendorOrderItem.setUserId(userId);
			vendorOrderItem.setNumber(" ");
			vendorOrderItem.setVproductId(LangUtil.parseLong(vProductId));
			vendorOrderItem.setUserName(username);
			vendorOrderItem.setUserMobile(mobile);
			vendorOrderItem.setStatus(VendorOrderStatusEnum.SUBMITTED.getCode());
			vendorOrderItem.setRemark(remark);
			vendorOrderItem.setCompanyId(companyId);
			VProductItem vProductItem = productRemoteService.selectVProductById(LangUtil.parseLong(vProductId));
			if(vProductItem != null){
				vendorOrderItem.setVendorId(vProductItem.getVendorId());
			}
			vendorOrderItem.setCreateUser(userId);
			vendorOrderItem.setUpdateUser(userId);
			vendorRemoteService.insertVendorOrder(vendorOrderItem);
			result.setMsg("约谈成功");
		} catch (Exception e) {
			e.printStackTrace();
			result.setMsg("系统错误");
			result.setError(GlobalDefine.resultCode.INTERNAL_ERROR);
		}
		return result;
	}
}
