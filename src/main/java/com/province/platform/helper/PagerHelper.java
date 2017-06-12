package com.province.platform.helper;

import com.province.platform.commons.Pager;
import com.zzh.base.api.entity.Pageable;

public class PagerHelper {
	
	public static Pager pageableToPager(Pageable<?> pageable){
		Pager pager = new Pager();
		pager.setResultList(pageable.getRows());
		pager.setPage(pageable.getPage());
		pager.setPageSize(pageable.getPageSize());
		pager.setTotal(pageable.getTotal());		
		pager.setRecords(pageable.getRecords());
		return pager;
	}
	
	public static Pager pageableToPager(com.zzh.common.entity.Pageable<?> pageable){
		Pager pager = new Pager();
		pager.setResultList(pageable.getRows());
		pager.setPage(pageable.getPage());
		pager.setPageSize(pageable.getPageSize());
		pager.setTotal(pageable.getTotal());		
		pager.setRecords(pageable.getRecords());
		return pager;
	}
}
