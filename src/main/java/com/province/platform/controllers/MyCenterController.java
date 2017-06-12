package com.province.platform.controllers;

import java.util.Date;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.province.platform.commons.ApiResult;
import com.province.platform.commons.GlobalDefine;
import com.province.platform.cookies.CookieUtil;
import com.province.platform.helper.UploadHelper;
import com.province.platform.utils.SessionUtil;
import com.zzh.common.utils.LangUtil;
import com.zzh.common.utils.MD5Util;
import com.zzh.msgpush.api.MsgpushRemoteService;
import com.zzh.msgpush.api.entity.ListUserMsgpushQueryRequest;
import com.zzh.msgpush.api.entity.UserMsgpushItem;
import com.zzh.msgpush.api.entity.base.Pageable;
import com.zzh.user.api.UserRemoteService;
import com.zzh.user.api.entity.UserEntityInfo;
import com.zzh.user.api.exception.ExistsEmailException;
import com.zzh.user.api.exception.ExistsIdentityNumException;
import com.zzh.user.api.exception.ExistsMobileException;
import com.zzh.user.api.exception.UserRemoteServiceException;

@Controller
@RequestMapping("/mycenter")
public class MyCenterController {

	@Resource(name="userRemoteService")
	private UserRemoteService userRemoteService;
	
	@Resource(name="msgpushRemoteService")
	private MsgpushRemoteService msgpushRemoteService;
	
	@Resource(name="config")
	private Properties config;
	
	/**
	* @Title: toMyCenterPage
	* @Description: 跳转到个人中心
	* @param @param request
	* @param @param response
	* @param @return    设定文件
	* @return String    返回类型
	* @throws
	 */
	@RequestMapping("/myInfo")
	public String toMyCenterPage(HttpServletRequest request,HttpServletResponse response,Model model){
		try {
			Long userId = CookieUtil.getUserId(request);
			//如果未获取userId则进入首页
			if(userId == null || userId == 0){
				return "redirect:/index";
			}
			//先从session中取
			UserEntityInfo entityInfo = SessionUtil.getUserInfo(request);
			if(entityInfo == null){
				entityInfo = userRemoteService.findByUserId(userId, null);
			}
			//设置默认头像
			if(entityInfo.getAvatarUrl() == null){
				entityInfo.setAvatarUrl(config.getProperty("default.avatar"));
			}
			model.addAttribute("user", entityInfo);
			
			Map<String, Object> imageUploadParams = UploadHelper.getImageUploadParams(userId);
			model.addAllAttributes(imageUploadParams);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/views/mycenter/myinfo";
	}
	
	
	/**
	* @Title: saveMyInfo
	* @Description: 保存个人中心
	* @param @param request
	* @param @param response
	* @param @param myCenterInfoDto
	* @param @return    设定文件
	* @return Map<String,String>    返回类型
	* @throws
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("/editInfo")
	@ResponseBody
	public ApiResult saveMyInfo(HttpServletRequest request,HttpServletResponse response, UserEntityInfo entityInfo){
		ApiResult apiResult = new ApiResult();
			
			try {
				long userId = CookieUtil.getUserId(request);
				//先从session中取
				UserEntityInfo oldUserInfo = SessionUtil.getUserInfo(request);
				if(oldUserInfo == null){
					oldUserInfo = userRemoteService.findByUserId(userId, null);
				}
				//修改内容
				if(entityInfo.getProvinceId() != null && entityInfo.getProvinceId() > 0){
					oldUserInfo.setProvinceId(LangUtil.parseLong(entityInfo.getProvinceId()));//省
				}
				if(entityInfo.getCityId() != null && entityInfo.getCityId() > 0){
					oldUserInfo.setCityId(LangUtil.parseLong(entityInfo.getCityId()));//市
				}
				if(entityInfo.getCountyId() != null && entityInfo.getCountyId() > 0){
					oldUserInfo.setCountyId(LangUtil.parseLong(entityInfo.getCountyId()));//县
				}
				if(StringUtils.isNotBlank(entityInfo.getAddress())){
					oldUserInfo.setAddress(entityInfo.getAddress());
				}
				if(StringUtils.isNotBlank(entityInfo.getQq())){
					oldUserInfo.setQq(entityInfo.getQq());
				}
				oldUserInfo.setAvatarUrl(entityInfo.getAvatarUrl());
				oldUserInfo.setAvatar(LangUtil.parseLong(entityInfo.getAvatar()));
				oldUserInfo.setRealName(entityInfo.getRealName());
				oldUserInfo.setNickname(entityInfo.getNickname());
				oldUserInfo.setJobPost(entityInfo.getJobPost());
				oldUserInfo.setCompany(entityInfo.getCompany());
//			oldUserInfo.setMobile(entityInfo.getMobile());//暂时不能修改
//			oldUserInfo.setEmail(entityInfo.getEmail());//暂时不能修改
				oldUserInfo.setSex(entityInfo.getSex());
				oldUserInfo.setUpdateTime(new Date());
				oldUserInfo.setUpdateUser(oldUserInfo.getUserId());
				//执行
				userRemoteService.updateUser(oldUserInfo);
				
				//查询更新后的结果
				UserEntityInfo newUserEntityInfo = userRemoteService.findByUserId(userId, null);
				SessionUtil.setUserInfo(request, newUserEntityInfo);
				
				apiResult.setMsg("保存成功");
			} catch (UserRemoteServiceException e) {
				apiResult.setError(GlobalDefine.resultCode.INTERNAL_ERROR);
				apiResult.setMsg("系统错误");
				e.printStackTrace();
			} catch (ExistsEmailException e) {
				apiResult.setError(GlobalDefine.resultCode.INTERNAL_ERROR);
				apiResult.setMsg(e.getMessage());
				e.printStackTrace();
			} catch (ExistsMobileException e) {
				apiResult.setError(GlobalDefine.resultCode.INTERNAL_ERROR);
				apiResult.setMsg(e.getMessage());
				e.printStackTrace();
			} catch (ExistsIdentityNumException e) {
				apiResult.setError(GlobalDefine.resultCode.INTERNAL_ERROR);
				apiResult.setMsg(e.getMessage());
				e.printStackTrace();
			}
		return apiResult;
	}
	

	/** 
	 * @Title: editPassword
	 * @Description: 修改密码
	 * @author ganshimin@zhongzhihui.com
	 * @param request
	 * @param response
	 * @param oldPassword
	 * @param newPassword
	 * @param rePassword
	 * @return  
	 */  
	@SuppressWarnings("rawtypes")
	@RequestMapping("/editPassword")
	@ResponseBody
	public ApiResult editPassword(HttpServletRequest request,HttpServletResponse response,String oldPassword,String newPassword,String rePassword){
		ApiResult apiResult = new ApiResult();
		try {
			if(StringUtils.length(newPassword)<6){
				apiResult.setError(1);
				apiResult.setVer("1.0");
				apiResult.setMsg("新密码不能小于6位");
			}
			long userId = CookieUtil.getUserId(request);
			UserEntityInfo entityInfo = userRemoteService.findByUserId(userId, null);
			if(entityInfo == null){
				apiResult.setError(1);
				apiResult.setMsg("系统错误");
			}
			String password_before = MD5Util.getMD5FormatForDiscuz(oldPassword,entityInfo.getSalt());
			if(entityInfo.getPassword().equals(password_before)){
				if(newPassword.equals(rePassword)){
					if(StringUtils.isNotBlank(entityInfo.getMobile())){
						userRemoteService.resetPassword(entityInfo.getMobile(), newPassword);
					}
					apiResult.setError(0);
					apiResult.setMsg("修改成功");
				}else{
					apiResult.setError(1);
					apiResult.setMsg("两次密码不一致");
				}
			}else{
				apiResult.setError(1);
				apiResult.setMsg("原密码不正确");
			}
		} catch (Exception e) {
			apiResult.setError(1);
			apiResult.setMsg("系统异常");
			e.printStackTrace();
		}
		return apiResult;
	}
	
	@RequestMapping("/myMessage")
	public String toMyMessage(HttpServletRequest request,HttpServletResponse response){
		return "/views/mycenter/my-message";
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/queryMyMessage")
	@ResponseBody
	public ApiResult queryMyMessage(HttpServletRequest request,HttpServletResponse response){
		ApiResult result = new ApiResult();
		long userId = CookieUtil.getUserId(request);
		Long companyId = CookieUtil.getCompanyId(request);
		try {
			ListUserMsgpushQueryRequest queryRequest = new ListUserMsgpushQueryRequest();
			queryRequest.setCompanyId(companyId);
			queryRequest.setUserId(userId);
			queryRequest.setPage(1);
			queryRequest.setPageSize(10);
			Pageable<UserMsgpushItem> queryMsgpushList = msgpushRemoteService.queryMsgpushList(queryRequest);
			result.setVer("1.0");
			result.setError(0);
			result.setData(queryMsgpushList);
		} catch (Exception e) {
			e.printStackTrace();
			result.setError(1);
		}
		return result;
	}
	
}
