package com.province.platform.controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.province.platform.commons.LiveApiResult;
import com.province.platform.helper.LiveHelper;

@Controller
@RequestMapping("/live")
public class LiveController {
	
	
	@RequestMapping("/toLive")
	public String toLivePage(HttpServletRequest request, HttpServletResponse response, Model model){
		int liveId = 238309349;
		model.addAttribute("liveId", liveId);
		
		model.addAttribute("appKey", LiveHelper.getAppKey());
		model.addAttribute("now", System.currentTimeMillis());
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("webinar_id", ""+liveId);
		paramMap.put("fields", "introduction");
		
		model.addAttribute("sign", LiveHelper.getSign(paramMap));
		
		//观众信息
		model.addAttribute("audienceEmail", "142857@domain.com");
		model.addAttribute("audienceName", "祁同伟");
		
		return "views/live/newliveshow";
	}
	@SuppressWarnings("unchecked")
	@RequestMapping("getUserInfo")
	public String getUserInfo(HttpServletRequest request, HttpServletResponse response, Model model,Long userId){
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("user_id", ""+userId);
		paramMap.put("fields", "name");
		String url = "http://e.vhall.com/api/vhallapi/v2/user/get-user-info";
		try {
			LiveApiResult<JSONObject> result = LiveHelper.sendPostRequest(url, paramMap);
			if("200".equals(String.valueOf(result.getCode()))){
				model.addAttribute("userName", result.getData().getString("name"));
				model.addAttribute("userId", "18808420");
			}
			model.addAttribute("errormsg", result.getMsg());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "views/live/userInfo";
	}
	@SuppressWarnings("unchecked")
	@RequestMapping("getUserPower")
	public String getUserPower(HttpServletRequest request, HttpServletResponse response, Model model,Long userId){
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("user_id", ""+userId);
		String url = "http://e.vhall.com/api/vhallapi/v2/user/get-user-power";
		try {
			LiveApiResult<JSONObject> result = LiveHelper.sendPostRequest(url, paramMap);
			if("200".equals(String.valueOf(result.getCode()))){
				model.addAttribute("is_child", result.getData().getString("is_child"));
				model.addAttribute("assign", result.getData().getString("assign"));
				model.addAttribute("userId", userId);
			}
			model.addAttribute("errormsg", result.getMsg());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "views/live/userInfo"; 
	}
	@SuppressWarnings("unchecked")
	@RequestMapping("listUser")
	public String listUser(HttpServletRequest request, HttpServletResponse response, Model model){
		Map<String, String> paramMap = new HashMap<String, String>();
		String url = "http://e.vhall.com/api/vhallapi/v2/user/get-child-list";
		try {
			LiveApiResult<JSONArray> result = LiveHelper.sendPostRequest(url, paramMap);
			if("200".equals(String.valueOf(result.getCode()))){
				model.addAttribute("userList", result.getData());
			}
			model.addAttribute("errormsg", result.getMsg());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "views/live/listUser";
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("createUser")
	public String createUser(HttpServletRequest request, HttpServletResponse response, Model model){
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("third_user_id", "167635");
		paramMap.put("pass", "111111");
		paramMap.put("phone", "15925656279");
		paramMap.put("name", "詹姆斯");
		paramMap.put("email", "james@zzh.com");
		String url = "http://e.vhall.com/api/vhallapi/v2/user/register";
		try {
			LiveApiResult<JSONObject> result = LiveHelper.sendPostRequest(url, paramMap);
			if("200".equals(String.valueOf(result.getCode()))){
				model.addAttribute("userId", result.getData().getString("user_id"));
			}
			model.addAttribute("errormsg", result.getMsg());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "views/live/createUser";
	}
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping("changeUserPower")
	public LiveApiResult<String> changeUserPower(HttpServletRequest request, HttpServletResponse response, Model model, Long userId){
		if(userId != null && userId > 0){
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("user_id", ""+userId);
			paramMap.put("is_child", "1");
			paramMap.put("assign", "20");
			String url = "http://e.vhall.com/api/vhallapi/v2/user/change-user-power";
			try {
				LiveApiResult<String> result = LiveHelper.sendPostRequest(url, paramMap);
				return result;
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	@RequestMapping("getUserId")
	public String getUserId(HttpServletRequest request, HttpServletResponse response, Model model){
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("third_user_id", "145715");
		String url = "http://e.vhall.com/api/vhallapi/v2/user/get-user-id";
		try {
			LiveApiResult<JSONObject> result = LiveHelper.sendPostRequest(url, paramMap);
			model.addAttribute("errormsg", result.getMsg()+result.getData().getString("id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "views/live/anchor";
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("createLive")
	public String createLive(HttpServletRequest request, HttpServletResponse response, Model model,Long userId,String layout){
		Map<String, String> paramMap = new HashMap<String, String>();
		String url = "http://e.vhall.com/api/vhallapi/v2/webinar/create";
		if(userId != null && userId > 0){
			paramMap.put("user_id", ""+userId);
		}
		paramMap.put("subject", "测试直播课2");
		Calendar nowTime = Calendar.getInstance();
		nowTime.add(Calendar.MINUTE, 5);
		paramMap.put("start_time", ""+(nowTime.getTimeInMillis()/1000));
		paramMap.put("introduction", "微信端直播测试的简介2");
		paramMap.put("layout", layout);
		paramMap.put("auto_record", "1");
		paramMap.put("host", "高育良");
		LiveApiResult<String> liveResult = LiveHelper.sendPostRequest(url, paramMap);
		if("200".equals(String.valueOf(liveResult.getCode()))){
			model.addAttribute("webinarId", liveResult.getData());
		}
		model.addAttribute("errormsg", liveResult.getMsg());
		
		//图片上传参数
		return "views/live/create";
	}
	
	@RequestMapping("begin")
	public String beginLive(HttpServletRequest request, HttpServletResponse response, Model model, Long webinarId){
		model.addAttribute("webinar_id", webinarId);
		return "views/live/begin";
	}
	
	@ResponseBody
	@RequestMapping("authParam")
	public Map<String, Object> getAuthParam(HttpServletRequest request, HttpServletResponse response, Long webinarId){
		Map<String, Object> resutlMap = new HashMap<String, Object>();
		Map<String, String> paramMap = new HashMap<String, String>();
		if(webinarId != null && webinarId > 0){
			paramMap.put("webinar_id", String.valueOf(webinarId));
			paramMap.put("is_sec_auth", "1");
			resutlMap.putAll(paramMap);
			resutlMap.putAll(LiveHelper.getAuthParam(paramMap));
		}
		return resutlMap;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("getRecord")
	public String getRecord(HttpServletRequest request, HttpServletResponse response, Model model, Long webinarId){
		try {
			if(webinarId != null && webinarId > 0){
				//获取活动开始结束信息
				String recordUrl = "http://e.vhall.com/api/vhallapi/v2/record/list";
				Map<String, String> recordParamMap = new HashMap<String, String>();
				recordParamMap.put("webinar_id", String.valueOf(webinarId));
				recordParamMap.put("limit", "1");
				LiveApiResult<JSONObject> recordInfo = LiveHelper.sendPostRequest(recordUrl, recordParamMap);
				//获得第一个回放
				JSONObject resultObject = (JSONObject)(recordInfo.getData().getObject("lists", JSONArray.class).get(0));
				
				model.addAttribute("webinarId",webinarId);
				model.addAttribute("recordId",resultObject.getString("id"));
				System.out.println(resultObject.getString("id"));
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return "views/live/record";
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("createRecord")
	public String createRecord(HttpServletRequest request, HttpServletResponse response, Model model, Long webinarId){
		try {
			if(webinarId != null && webinarId > 0){
				//获取活动开始结束信息
				String getInfoUrl = "http://e.vhall.com/api/vhallapi/v2/webinar/last-option-time";
				Map<String, String> infoParamMap = new HashMap<String, String>();
				infoParamMap.put("webinar_id", String.valueOf(webinarId));
				LiveApiResult<JSONObject> subjectInfo = LiveHelper.sendPostRequest(getInfoUrl, infoParamMap);
				if("200".equals(String.valueOf(subjectInfo.getCode()))){
					String startTime = subjectInfo.getData().getString("start_time");
					String endTime = subjectInfo.getData().getString("end_time");
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					//生成回放
					String createRecordUrl = "http://e.vhall.com/api/vhallapi/v2/record/create";
					Map<String, String> recordParamMap = new HashMap<String, String>();
					recordParamMap.put("webinar_id", String.valueOf(webinarId));
					recordParamMap.put("subject", "中智汇直播课");
					recordParamMap.put("type", "0");
					recordParamMap.put("start_time", sdf.parse(startTime).getTime()+"");
					recordParamMap.put("end_time", sdf.parse(endTime).getTime()+"");
					LiveApiResult<String> recordInfo = LiveHelper.sendPostRequest(createRecordUrl, recordParamMap);
					if("200".equals(String.valueOf(recordInfo.getCode()))){
						model.addAttribute("recordId", recordInfo.getData());
						model.addAttribute("errormsg", "createRecord:"+recordInfo.getMsg());
					}else{
						model.addAttribute("errormsg", "createRecord:"+recordInfo.getMsg());
					}
					
				}else{
					model.addAttribute("errormsg", "getLiveInfo:"+subjectInfo.getMsg());
				}
				
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return "views/live/record";
	}
	
	@ResponseBody
	@RequestMapping("validateAuth")
	public String validateAuth(HttpServletRequest request, HttpServletResponse response){
		
		return "";
	}
	
}
