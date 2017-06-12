package com.province.platform.commons;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;



/**
 * api接口返回的响应数据
 * @author saleson
 *
 * @param <T>
 */
@JSONType(asm=false)
public class ApiResult<T> implements Serializable {
	private static final long serialVersionUID = 1L;

	private int error;
	private String msg;
	private String ver;
	private T data;
	
	
	
	@SuppressWarnings("unchecked")
	public ApiResult(){
		error = 0;
		ver = "1.0";
		msg = "";
		setData((T) "");
	}
	
	@SuppressWarnings("unchecked")
	public ApiResult(int error, String msg){
		this.error = error;
		this.msg = msg;
		setData((T) "");
	}

	
	public ApiResult(T data){
		this.data = data;
	}
	
	
	/**
	 * 状态码
	 * 状态码为字符串类型 (String). eg. "0" (注意有双引号)
	 * 0 ~ 100 为系统保留状态
	 * "-100" - session timeout (登陆验证)
	 * "0" - api success (default value)
	 * "1" - internal error (后端接口未知错误)
	 * "2" - redirect url (页面跳转响应)
	 * 业务状态码建议使用 100以后的数值 (>100). eg: 0x101F, 0x1013
	 * @return
	 */
	@JSONField(format="#.0")
	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

	
	
	/**
	 * api status message
	 * api状态信息
	 * @return
	 */
	public String getMsg() {
		return msg;
	}

	
	/**
	 * @see {@link #getMsg()}
	 * @param msg
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}

	
	/**
	 * version identify, defaults to '1.0'
	 * 接口版本
	 * @return
	 */
	public String getVer() {
		return ver;
	}

	
	/**
	 * @see {@link #getVer()}
	 * @param ver
	 */
	public void setVer(String ver) {
		this.ver = ver;
	}

	
	/**
	 * Generic type for api data response, can be null, empty "", 0, {}, [] etc,.
	 * 响应数据
	 * @return
	 */
	public T getData() {
		return data;
	}

	
	/**
	 * @see {@link #getData()}
	 * @param data
	 */
	public void setData(T data) {
		this.data = data;
	}

}
