package com.province.platform.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zzh.common.utils.MD5Util;

 
/** 
 * @ClassName: UploadHelper
 * @Description: 文件上传
 * 
 * @author ganshimin@zhongzhihui.com
 * @date: 2017年3月29日 上午11:21:01
 */  
public class UploadHelper {
	
	private static UploadHelper uploadHelper;
	
	private String platform = "client";
	
	public void setPlatform(String platform){
		this.platform = platform;
	}
	
	public String getPlatform(){
		return platform;
	}
	
	public void init(){
		uploadHelper = this;
		uploadHelper.platform = this.platform;
	}
	
	
	/** 
	 * @Title: getImageUploadParams
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @author jupiter@zhongzhihui.com
	 * @param userId
	 * @param companyId
	 * @return  
	 */  
	public static Map<String, Object> getImageUploadParams(long userId){
		Map<String, Object> params = new HashMap<String, Object>();
		String resourceType = "other";
		params.put("userId", userId);
		params.put("companyId", 0);
		params.put("type", resourceType);
		params.put("platform", uploadHelper.platform);
		params.put("appkey", UploadHelper.getAppkey());
		params.put("sign", UploadHelper.getSign(userId, 0, resourceType));
		return params;
	}
	
	/** 
	 * @Title: getEditorImageUploadParam
	 * @Description: 获取editor图片上传的参数
	 * @author jupiter@zhongzhihui.com
	 * @param userId
	 * @param companyId
	 * @return  
	 */  
	public static String getEditorImageUploadParam(long userId){
		return getUploadParam(userId, 0L, "other",null);
	}

	/**
	 * @Title: checkSign
	 * @Description: 校验参数中的sign值
	 * @return boolean 返回类型
	 * @param sParaTemp
	 *            获取sign的参数
	 * @param key
	 *            获取sign的appkey
	 * @return
	 */
	public static boolean checkSign(Map<String, String> sParaTemp, String appKey) {
		// 除去数组中的空值和签名参数
		Map<String, String> sPara = paraFilter(sParaTemp);
		// 生成签名结果
		String mysign = "";
		try {
			mysign = createLinkString(sPara);
			mysign = MD5Util.getMD5Format(mysign + appKey);
		} catch (Exception e) {
		}

		// 签名结果与签名方式加入请求提交参数组中
		String sign = sParaTemp.get("sign");
		//
		return mysign.equals(sign);
	}

	/**
	 * 除去数组中的空值和签名参数
	 * 
	 * @param sArray
	 *            签名参数组
	 * @return 去掉空值与签名参数后的新签名参数组
	 */
	public static Map<String, String> paraFilter(Map<String, String> sArray) {

		Map<String, String> result = new HashMap<String, String>();

		if (sArray == null || sArray.size() <= 0) {
			return result;
		}

		for (String key : sArray.keySet()) {
			String value = sArray.get(key);
			if (value == null || value.equals("") || key.equalsIgnoreCase("sign")) {
				continue;
			}
			result.put(key, value);
		}

		return result;
	}

	/**
	 * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
	 * 
	 * @param params
	 *            需要排序并参与字符拼接的参数组
	 * @return 拼接后字符串
	 */
	public static String createLinkString(Map<String, String> params) {

		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);

		String prestr = "";

		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = params.get(key);

			if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
				prestr = prestr + key + "=" + value;
			} else {
				prestr = prestr + key + "=" + value + "&";
			}
		}

		return prestr;
	}

	public static String getAppkey() {
		return "03f29d0d7c97dbb031925bd66a229b0a";
	}

	public static String getSign(long userId, long companyId, String type) {
		//
		Map<String, String> params = new HashMap<String, String>();
		params.put("appkey", "03f29d0d7c97dbb031925bd66a229b0a");
		params.put("userId", String.valueOf(userId));
		params.put("companyId", String.valueOf(companyId));
		params.put("type", type);
		params.put("platform", uploadHelper.platform);
		String param = createLinkString(params);
		String sign = MD5Util.getMD5Format(param + "a0bcb43d39142e7864b86cc3fcf3ab66");
		//
		return sign;
	}

	public static String getUploadParam(Long userId, Long companyId, String type, Long limitSpace) {
		//
		Map<String, String> params = new HashMap<String, String>();
		params.put("appkey", "03f29d0d7c97dbb031925bd66a229b0a");
		params.put("userId", String.valueOf(userId));
		params.put("companyId", String.valueOf(companyId));
		params.put("type", type);
		params.put("platform", uploadHelper.platform);
		if (limitSpace != null) {
			params.put("limitSpace", String.valueOf(limitSpace));
		}
		String param = createLinkString(params);
		String sign = MD5Util.getMD5Format(param + "a0bcb43d39142e7864b86cc3fcf3ab66");
		param = param + "&sign=" + sign;
		//
		return param;
	}

	public static String getUploadParamExt(Long userId, Long companyId, String type, Long limitSpace) {
		//
		Map<String, String> params = new HashMap<String, String>();
		params.put("appkey", "03f29d0d7c97dbb031925bd66a229b0a");
		params.put("userId", String.valueOf(userId));
		params.put("companyId", String.valueOf(companyId));
		params.put("type", type);
		params.put("platform", uploadHelper.platform);
		params.put("ext", "doc");
		if (limitSpace != null) {
			params.put("limitSpace", String.valueOf(limitSpace));
		}
		String param = createLinkString(params);
		String sign = MD5Util.getMD5Format(param + "a0bcb43d39142e7864b86cc3fcf3ab66");
		param = param + "&sign=" + sign;
		//
		return param;
	}
	
	public static void main(String[] args){
		
		String param = getUploadParam(50671L, 50011L, "courseware", Long.MAX_VALUE);
		System.out.println(param);
	}
}
