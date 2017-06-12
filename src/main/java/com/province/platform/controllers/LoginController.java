/**
 * Project Name: province-platform1-0
 * File Name: LoginController.java
 * Package Name: com.province.platform.controllers
 * Date: 2017年3月30日上午10:33:46 
 * Copyright (c) 2017, www.zhongzhihui.com All Rights Reserved. 
 */

package com.province.platform.controllers;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.province.platform.commons.ApiResult;
import com.province.platform.commons.GlobalDefine;
import com.province.platform.cookies.CookieUtil;
import com.province.platform.pojos.dto.UserDto;
import com.zzh.base.api.SmsRemoteService;
import com.zzh.base.api.exception.TooManyFrequencyException;
import com.zzh.common.utils.LangUtil;
import com.zzh.common.utils.ValidateUtils;
import com.zzh.user.api.UserRemoteService;
import com.zzh.user.api.entity.UserEntityInfo;
import com.zzh.user.api.exception.NonUserException;
import com.zzh.user.api.exception.UserRemoteServiceException;
import com.zzh.user.api.exception.WrongPasswordException;

/** 
 * ClassName: LoginController
 * Description: TODO(用一句话描述这个类)
 * 
 * @author zhuningkang@zhongzhihui.com
 * @date: 2017年3月30日 上午10:33:46
 */
@Controller
@RequestMapping("/login")
public class LoginController {
	
	@Resource
	private UserRemoteService userRemoteService;

	@Resource
	private SmsRemoteService smsRemoteService;
	

	/** 
	 * @Title: toLoginPage
	 * @Description: 跳转到登录页面
	 * @author zhuningkang@zhongzhihui.com
	 * @param request
	 * @param response
	 * @param model
	 * @return  
	 */  
	@RequestMapping("/toLogin")
	public String toLoginPage(HttpServletRequest request, HttpServletResponse response, Model model) {
		//Todo...
		
		return "views/login/login";
	}
	
	/** 
	 * @Title: toLogin
	 * @Description: 登入
	 * @author zhuningkang@zhongzhihui.com
	 * @param request
	 * @param response
	 * @param username
	 * @param password
	 * @param returnUrl
	 * @return  
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/signIn")
	@ResponseBody
	public ApiResult toLogin(HttpServletRequest request, HttpServletResponse response, String username, String password, String returnUrl) {
		ApiResult result = new ApiResult();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long companyId = CookieUtil.getCompanyId(request);
			try {
				if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
					UserEntityInfo userEntityInfo = userRemoteService.login(username, password, companyId);
					if(userEntityInfo == null){
						throw new Exception("userEntityInfo查询为空");
					}
					CookieUtil.writeCookieForUser(request, String.valueOf(userEntityInfo.getUserId()));
					resultMap.put("returnUrl", returnUrl);
					resultMap.put("userEntityInfo", userEntityInfo);
					result.setData(resultMap);
					result.setMsg("登录成功");
				}else{
					result.setError(GlobalDefine.resultCode.LOGIN_ERROR);
					result.setMsg("用户名或密码不能为空");
				}
			} catch (NonUserException e) {
				result.setError(GlobalDefine.resultCode.LOGIN_ERROR);
				result.setMsg(e.getMessage());
				e.printStackTrace();
			} catch (WrongPasswordException e) {
				result.setError(GlobalDefine.resultCode.LOGIN_ERROR);
				result.setMsg(e.getMessage());
				e.printStackTrace();
			} catch (UserRemoteServiceException e) {
				result.setError(GlobalDefine.resultCode.LOGIN_ERROR);
				result.setMsg("系统异常");
				e.printStackTrace();
			} catch (Exception e) {
				result.setError(GlobalDefine.resultCode.LOGIN_ERROR);
				result.setMsg("系统异常");
				e.printStackTrace();
			}
			
		return result;
	}
	
	/** 
	 * @Title: toLoginOut
	 * @Description: 登出
	 * @author zhuningkang@zhongzhihui.com
	 * @param request
	 * @param response
	 * @return  
	 */  
	@RequestMapping("/toLogout")
	public String toLoginOut(HttpServletRequest request, HttpServletResponse response,String redirectUrl) {
		CookieUtil.clearCookie(request, CookieUtil.COOKIE_USER_ID);
		if(StringUtils.isNotBlank(redirectUrl)){
			return "redirect:"+redirectUrl;
		}else{
			return "redirect:/index";
		}
	}
	
	/** 
	 * @Title: sendSmsToMobile
	 * @Description: 手机发送验证码
	 * @author zhuningkang@zhongzhihui.com
	 * @param request
	 * @param response
	 * @param mobile
	 * @param type
	 * @return  
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("/sendSmsToMobile")
	@ResponseBody
	public ApiResult sendSmsToMobile(HttpServletRequest request, HttpServletResponse response,@RequestParam(required=true, value="mobile") String mobile, String type) {
		ApiResult result = new ApiResult();
		try {
			if(StringUtils.isNotBlank(mobile) && StringUtils.isNotBlank(type)){
				if ("1".equals(type)) {// 发送验证码用于注册
					UserEntityInfo userEntityInfo = userRemoteService.selectUserByMobile(mobile);
					if(userEntityInfo != null){
						result.setError(GlobalDefine.resultCode.LOGIN_ERROR);
						result.setMsg("手机号码已注册");
						return result;
					}
				}else if("2".equals(type)){ // 发送验证码用于找回密码
					UserEntityInfo userEntityInfo = userRemoteService.findByMobile(mobile);
					if(userEntityInfo == null){
						result.setMsg("手机号码未注册");
						return result;
					}else if(userEntityInfo.getEnabled() == 1){
						result.setMsg("帐号已禁用,无法登陆或找回密码");
						return result;
					}
				}
				String code = this.smsRemoteService.getCode(mobile);
				System.out.println("=============code=============>"+code);
				result.setError(0);
				result.setMsg("发送成功");
			}else{
				result.setError(GlobalDefine.resultCode.LOGIN_ERROR);
				result.setMsg("参数有误");
			}
		} catch (NonUserException e) {
			result.setError(GlobalDefine.resultCode.LOGIN_ERROR);
			result.setMsg(e.getMessage());
			e.printStackTrace();
		}catch (UserRemoteServiceException e) {
			result.setError(GlobalDefine.resultCode.LOGIN_ERROR);
			result.setMsg("系统异常");
			e.printStackTrace();
		} catch (TooManyFrequencyException e) {
			result.setError(GlobalDefine.resultCode.LOGIN_ERROR);
			result.setMsg("操作过于频繁");
			e.printStackTrace();
		} catch (Exception e){
			result.setError(GlobalDefine.resultCode.LOGIN_ERROR);
			result.setMsg("系统错误");
			e.printStackTrace();
		}
		return result;
	}
	
	/** 
	 * @Title: toSignUp
	 * @Description: 跳转注册页面
	 * @author zhuningkang@zhongzhihui.com
	 * @param request
	 * @param response
	 * @param model
	 * @return  
	 */
	@RequestMapping("/toRegister")
	public String toSignUp(HttpServletRequest request, HttpServletResponse response, Model model) {
		return "views/login/register";
	}
	
	/** 
	 * @Title: registerUser
	 * @Description: 注册账号
	 * @author zhuningkang@zhongzhihui.com
	 * @param request
	 * @param response
	 * @param mobile
	 * @param email
	 * @param password
	 * @param vierfyCode
	 * @param returnUrl
	 * @return  
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/signUp")
	@ResponseBody
	public ApiResult registerUser(HttpServletRequest request, HttpServletResponse response,UserDto userDto, String returnUrl) {
		ApiResult result = new ApiResult();
		Map<String, String> resultMap = new HashMap<String, String>();
		UserEntityInfo userEntityInfo = new UserEntityInfo();
		Long companyId = CookieUtil.getCompanyId(request);
		try {
			//用户名是否存在验证
			if(StringUtils.isNotBlank(userDto.getUsername())){
				UserEntityInfo userInfoByNickname = userRemoteService.selectUserByNickname(userDto.getUsername());
				if(userInfoByNickname != null){
					result.setMsg("用户名已存在");
					result.setError(GlobalDefine.resultCode.LOGIN_ERROR);
					return result;
				}
			}else{
				result.setMsg("请输入用户名");
				result.setError(GlobalDefine.resultCode.LOGIN_ERROR);
				return result;
			}
			//验证邮箱，手机以及验证码是否正确
			if ( ValidateUtils.isEmail(userDto.getEmail()) && ValidateUtils.isMobileNO(userDto.getMobile()) && smsRemoteService.validateCode(userDto.getMobile(), userDto.getCode())) {
				UserEntityInfo userInfoByMobile = userRemoteService.selectUserByMobile(userDto.getMobile());
				if(userInfoByMobile != null){
					result.setMsg("手机号码已注册");
					result.setError(GlobalDefine.resultCode.LOGIN_ERROR);
					return result;
				}
				UserEntityInfo userInfoByEmail = userRemoteService.selectUserByEmail(userDto.getEmail());
				if(userInfoByEmail != null){
					result.setMsg("邮箱已注册");
					result.setError(GlobalDefine.resultCode.LOGIN_ERROR);
					return result;
				}
				userEntityInfo.setMobile(userDto.getMobile());
				userEntityInfo.setCompanyId(companyId);
				userEntityInfo.seteMail(userDto.getEmail());
				userEntityInfo.setNickname(userDto.getUsername());
				userEntityInfo.setPassword(userDto.getPassword());
				long userId = userRemoteService.insertUserInfoAndMetaInfo(userEntityInfo);
				//清理用户cookie
				CookieUtil.clearCookie(request,CookieUtil.COOKIE_USER_ID);
				CookieUtil.writeCookieForUser(request,LangUtil.parseString(userId));
				//返回页
				if (StringUtils.isEmpty(returnUrl)) {
					returnUrl = "/index";
				}
				resultMap.put("returnUrl", returnUrl);
				result.setData(resultMap);
				result.setMsg("注册成功");
			}else{
				if (!this.smsRemoteService.validateCode(userDto.getMobile(), userDto.getCode())) {
					result.setMsg("验证码不正确");
				} else {
					result.setMsg("手机/邮箱格式不正确");
				}
				result.setError(GlobalDefine.resultCode.LOGIN_ERROR);
			}
		} catch (Exception e) {
			result.setError(GlobalDefine.resultCode.LOGIN_ERROR);
			result.setMsg("系统错误，创建账号失败");
			e.printStackTrace();
		}
		return result;
	}
	
	/** 
	 * @Title: toResetPwd
	 * @Description: 跳转到找回密码页面
	 * @author ganshimin@zhongzhihui.com
	 * @return  
	 */  
	@RequestMapping("/toResetPwd")
	public String toResetPwd() {
		return "views/login/reset-pwd";
	}
	/** 
	 * @Title: editPasswordByMobile
	 * @Description: 修改密码
	 * @author zhuningkang@zhongzhihui.com
	 * @param request
	 * @param response
	 * @param mobile
	 * @param vierfyCode
	 * @param newPassword
	 * @param returnUrl
	 * @return  
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/resetPassword")
	@ResponseBody
	public ApiResult editPasswordByMobile(HttpServletRequest request, HttpServletResponse response,@RequestParam(required=true,value="mobile") String mobile, String code, String newPassword, String returnUrl) {
		ApiResult result = new ApiResult();
		Map<String, String> resultMap = new HashMap<String, String>();
		//返回页
		if (StringUtils.isEmpty(returnUrl)) {
			returnUrl = "/index";
		} 
		try {
			if (ValidateUtils.isMobileNO(mobile)) {
				if (smsRemoteService.validateCode(mobile,code)) {
					this.userRemoteService.resetPassword(mobile, newPassword);
					resultMap.put("returnUrl", returnUrl);
					result.setData(resultMap);
					result.setMsg("修改成功");
				}else{
					result.setError(GlobalDefine.resultCode.LOGIN_ERROR);
					result.setMsg("动态验证码不正确");
				}
			}else{
				result.setError(GlobalDefine.resultCode.LOGIN_ERROR);
				result.setMsg("手机号码格式不正确");
			}
		} catch (Exception e) {
			result.setError(GlobalDefine.resultCode.LOGIN_ERROR);
			result.setMsg("系统异常");
			e.printStackTrace();
		}
		return result;
	}

	/** 
	 * @Title: toProtocolPage
	 * @Description: 进入注册协议页面
	 * @author zhuningkang@zhongzhihui.com
	 * @param request
	 * @param response
	 * @return  
	 */  
	@RequestMapping("/protocol")
	public String toProtocolPage(HttpServletRequest request, HttpServletResponse response) {
		return "/views/login/protocol";
	}
}
