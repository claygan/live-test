/**
 * Project Name: province-platform1-0
 * File Name: VendorHelper.java
 * Package Name: com.province.platform.helper
 * Date: 2017年4月5日下午6:48:04 
 * Copyright (c) 2017, www.zhongzhihui.com All Rights Reserved. 
 */

package com.province.platform.helper;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.zzh.vendor.api.ProductRemoteService;
import com.zzh.vendor.api.VendorRemoteService;
import com.zzh.vendor.api.entity.VCategoryItem;
import com.zzh.vendor.api.entity.VProductItem;
import com.zzh.vendor.api.entity.VendorItem;


/** 
 * ClassName: VendorHelper
 * Description: TODO(用一句话描述这个类)
 * 
 * @author zhuningkang@zhongzhihui.com
 * @date: 2017年4月5日 下午6:48:04
 */
public class VendorHelper {

	public static VProductItem getVProductItem(VProductItem vProductItem) {
		try {
			VendorRemoteService vendorRemoteService = ServiceFactory.getBean("vendorRemoteService",VendorRemoteService.class);
			ProductRemoteService productRemoteService = ServiceFactory.getBean("productRemoteService", ProductRemoteService.class);
			VendorItem vendorItem = vendorRemoteService.findById(vProductItem.getVendorId());
			if (vendorItem != null) {
				vProductItem.setVendorName(vendorItem.getName());
			}
			if(vProductItem.getPrice() == -1){
				vProductItem.setPriceStr("面谈");
			}else{
				vProductItem.setPriceStr("￥"+vProductItem.getPrice());
			}
			vProductItem.setImgUrl(ResourceHelper.getDataUrl(vProductItem.getImg()));
			//
			List<Long> categoryIds = productRemoteService.selectProductCategoryByProductId(vProductItem.getId());
			vProductItem.setCategoryIds(categoryIds);
			if (categoryIds != null && categoryIds.size() > 0) {
				StringBuffer categoryName = new StringBuffer();
				for (Long categoryId : categoryIds) {
					VCategoryItem vCategoryItem = vendorRemoteService.selectVCategoryById(categoryId);
					if (vCategoryItem != null) {
						if (StringUtils.isNotBlank(categoryName.toString())) {
							categoryName.append(",");
						} 
						categoryName.append(vCategoryItem.getName());
					}
				}
				vProductItem.setCategoryName(categoryName.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vProductItem;
	}
}
