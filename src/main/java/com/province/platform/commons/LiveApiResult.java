package com.province.platform.commons;

import java.io.Serializable;


public class LiveApiResult<T>  implements Serializable {
	/** 
	 * @Description:TODO(用一句话描述这个变量表示什么). 
	 */  
	private static final long serialVersionUID = -1449344463741335983L;
	
	private int code;
	private String msg;
	private T data;
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	
	
}
