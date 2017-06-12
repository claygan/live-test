package com.province.platform.commons;

import java.io.Serializable;
import java.util.List;

public class Pager implements Serializable {
	private static final long serialVersionUID = -7846618338657036050L;
	public static final int DEFAULT_PAGE_SIZE = 10;
	private int pageSize;
	private int page;
	private int records;
	private int total;
	private List<?> resultList;

	public Pager() {
		this.pageSize = 10;

		this.page = 1;
	}

	public int getPageSize() {
		return this.pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getRecords() {
		return this.records;
	}

	public void setRecords(int records) {
		this.records = records;
	}

	public int getTotal() {
		return this.total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<?> getResultList() {
		return this.resultList;
	}

	public void setResultList(List<?> resultList) {
		this.resultList = resultList;
	}

	public int getPage() {
		return this.page;
	}

	public void setPage(int page) {
		this.page = page;
	}
}