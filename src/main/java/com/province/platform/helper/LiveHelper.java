package com.province.platform.helper;

import java.io.File;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.log4j.chainsaw.Main;

import com.alibaba.fastjson.JSONObject;
import com.province.platform.commons.LiveApiResult;
import com.zzh.common.utils.Base64Util;
import com.zzh.common.utils.MD5Util;

/** 
 * @ClassName: LiveHelper
 * @Description: 使用微吼第三方直播插件
 * 
 * @author ganshimin@zhongzhihui.com
 * @date: 2017年6月2日 下午2:37:24
 */  
public class LiveHelper {
	//18969817003
//	private static final String APPKEY = "6557bee7729a8b8610a69b3650ee6b5c";
//	private static final String SECRETKEY = "667eb21560775d5f2316d7bd429cb0eb";
	
	//13291851376
	private static final String APPKEY = "41af9858175442db52a5360931dc9da8";
	private static final String SECRETKEY = "29094223e42b76cbe89b8f7b2244cb94";
	/** 
	 * @Description:公共参数个数
	 */  
	private static final int COMMON_PARAM_NUM = 4;
	
	/** 
	 * @Title: getKValue
	 * @Description: 创建K值
	 * @author ganshimin@zhongzhihui.com
	 * @param userId 用户id
	 * @param webinarId 直播id
	 * @return  
	 */  
	public static String getKValue(Long userId, Long webinarId){
		return Base64Util.encode(""+userId+""+webinarId+""+System.currentTimeMillis());
	}
	
	/** 
	 * @Title: validateKValue
	 * @Description: 验证K值（时效一个小时）
	 * @author ganshimin@zhongzhihui.com
	 * @param kValue
	 * @return  
	 */  
	public static String validateKValue(String kValue, Long userId, Long webinarId){
		String result = "pass";
		String[] strValue = (Base64Util.decode(kValue)).split(""+webinarId);
		if(strValue.length > 1){
			//匹配用户
			if(!strValue[0].equals(String.valueOf(userId))){
				result = "fail";
			}
			//匹配时间戳
			long dtime = System.currentTimeMillis()-Long.valueOf(strValue[1]);
			//一个小时内有效
			if(dtime > (60*60*1000)){
				result = "fail";
			}
			
		}else{
			result = "fail";
		}
		return result;
	}
	
	public static void main(String[] args) {
		String k = getKValue(12345L, 10001L);
		System.out.println("k:"+k);
		String _k = validateKValue(k, 12345L, 10001L);
		System.out.println("_k:"+_k);
	}
	
	public static String getAppKey(){
		return APPKEY;
	}
	
	public static Map<String, String> getAuthParam(Map<String,String> paramMap){
		Map<String, String> resultMap = new HashMap<String, String>();
		
		resultMap.put("appKey", APPKEY);
		resultMap.put("signed_at", ""+(System.currentTimeMillis()/1000));
		resultMap.put("sign", LiveHelper.getSign(paramMap));
		
		return resultMap;
	}
	
	/** 
	 * @Title: getSign
	 * @Description: 根据外部参数获取sign值
	 * @author ganshimin@zhongzhihui.com
	 * @param paramMap
	 * @return  
	 */  
	public static String getSign(Map<String,String> paramMap){
		Map<String,String> modelMap = new TreeMap<String, String>();
		//放入公共参数
		modelMap.put("app_key", APPKEY);
		modelMap.put("auth_type", "2");
		modelMap.put("signed_at", ""+(System.currentTimeMillis()/1000));
		//放入外部参数
		modelMap.putAll(paramMap);
		//按key排序
		List<Entry<String, String>> resultList = sortMapByKey(modelMap);
		
		StringBuilder signValue = new StringBuilder(SECRETKEY);
		for (Entry<String, String> entry : resultList) {
			signValue.append(entry.getKey()+""+entry.getValue());
		}
		signValue.append(SECRETKEY);
		System.out.println("===>"+signValue.toString());
		return MD5Util.getMD5Format(signValue.toString());
	}
	
	/**
     * 使用 Map按key进行排序
     * @param map
     * @return
     */
    private static List<Map.Entry<String,String>> sortMapByKey(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        //将map.entrySet()转换成list
        List<Map.Entry<String,String>> list = new ArrayList<Map.Entry<String,String>>(map.entrySet());
        //然后通过比较器来实现排序
        Collections.sort(list,new Comparator<Map.Entry<String,String>>() {
            public int compare(Entry<String, String> o1, Entry<String, String> o2) {
                return o1.getKey().compareToIgnoreCase(o2.getKey());
            }
        });
        return list;
    }
    
    
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static LiveApiResult sendPostRequest(String url, Map<String, String> paramMap){
		HttpClient httpClient = new HttpClient();
		try {
			PostMethod postMethod = new PostMethod(url);
			postMethod.setRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=UTF-8"); 
			NameValuePair[] param = new NameValuePair[paramMap.size()+COMMON_PARAM_NUM];
			//公共四个参数（必传）
			param[0] = new NameValuePair("auth_type","2");//第三方登录
			param[1] = new NameValuePair("app_key",APPKEY);
			param[2] = new NameValuePair("signed_at",""+(System.currentTimeMillis()/1000));//当前时间戳
			param[3] = new NameValuePair("sign",getSign(paramMap));//sign值
			//外部参数代入请求参数中
			List<Map.Entry<String,String>> list = new ArrayList<Map.Entry<String,String>>(paramMap.entrySet());
			for (int i = 0,len = list.size(); i < len; i++) {
				param[i+COMMON_PARAM_NUM] = new NameValuePair(list.get(i).getKey(),list.get(i).getValue());
			}
			postMethod.setRequestBody(param);
			postMethod.releaseConnection();
			
			httpClient.executeMethod(postMethod);
			String resultResponse = new String(postMethod.getResponseBodyAsString().getBytes("UTF-8"));
			System.out.println("--------------resultResponse---------------"+url);
			System.out.println(resultResponse);
			System.out.println("--------------resultResponse---------------");
			JSONObject jsonObject = JSONObject.parseObject(resultResponse);
			LiveApiResult result = new LiveApiResult();
			result.setCode(jsonObject.getIntValue("code"));
			result.setMsg(jsonObject.getString("msg"));
			result.setData(jsonObject.get("data"));
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static LiveApiResult sendImagePostRequest(String url, Map<String, String> paramMap, String imageUrl){
		try {
			PostMethod postMethod = new PostMethod(url);
			//参数打包
			Part[] parts = new Part[paramMap.size()+COMMON_PARAM_NUM+1];//最后加上文件参数
			//公共四个参数（必传）
			parts[0] = new StringPart("auth_type","2");//第三方登录
			parts[1] = new StringPart("app_key",APPKEY);
			parts[2] = new StringPart("signed_at",""+(System.currentTimeMillis()/1000));//当前时间戳
			parts[3] = new StringPart("sign",getSign(paramMap));//sign值
			//传入文件
			File file = new File(imageUrl);
			FilePart fp = new FilePart("image", file);//上传图片文件
			fp.setContentType(getMimeType(imageUrl));
			parts[4] = fp;
			//外部参数代入请求参数中
			List<Map.Entry<String,String>> list = new ArrayList<Map.Entry<String,String>>(paramMap.entrySet());
			for (int i = 0,len = list.size(); i < len; i++) {
				parts[i+COMMON_PARAM_NUM+1] = new StringPart(list.get(i).getKey(),list.get(i).getValue());
			}
			

			//对于MIME类型的请求，httpclient建议全用MulitPartRequestEntity进行包装
			MultipartRequestEntity mre=new MultipartRequestEntity(parts,postMethod.getParams());

			postMethod.setRequestEntity(mre);
			postMethod.setRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");
			HttpClient client = new HttpClient();
			client.getHttpConnectionManager().getParams().setConnectionTimeout(50000);// 设置连接时间
			int status = client.executeMethod(postMethod);
			if(status == HttpStatus.SC_OK) {
				String resultResponse = new String(postMethod.getResponseBodyAsString().getBytes("UTF-8"));
				System.out.println("--------------fileResultResponse---------------"+url);
				System.out.println(resultResponse);
				System.out.println("--------------fileResultResponse---------------");
				JSONObject jsonObject = JSONObject.parseObject(resultResponse);
				LiveApiResult result = new LiveApiResult();
				result.setCode(jsonObject.getIntValue("code"));
				result.setMsg(jsonObject.getString("msg"));
				result.setData(jsonObject.get("data"));
				return result;
			}else{
				return null;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String getMimeType(String fileUrl)throws IOException{
		FileNameMap fileNameMap = URLConnection.getFileNameMap();
		String type = fileNameMap.getContentTypeFor(fileUrl);  
		return type; 
	}  
	
}
