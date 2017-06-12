package com.province.platform.commons;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.province.platform.helper.RemoteServiceFactory;
import com.province.platform.helper.ServiceFactory;
import com.zzh.base.api.CompanyRemoteService;
import com.zzh.base.api.entity.CompanyItem;
import com.zzh.common.constants.resource.ResourcePlatformConstants;
import com.zzh.common.entity.resource.ResourceItem;
import com.zzh.common.utils.MD5Util;
import com.zzh.course.api.entity.CourseInfo;
import com.zzh.file.api.ResourceRemoteService;
import com.zzh.live.remote.client.ClientResponse;
import com.zzh.live.remote.client.ClientSign;
import com.zzh.live.remote.client.live.LiveClient;
import com.zzh.live.remote.client.manager.AppClient;
import com.zzh.live.remote.client.manager.AppInnerClient;
import com.zzh.live.remote.client.manager.RoomClient;
import com.zzh.live.remote.model.AccessModel;
import com.zzh.live.remote.model.AccessModelWrapper;
import com.zzh.live.remote.model.AppModel;
import com.zzh.live.remote.model.ResourceModel;
import com.zzh.live.remote.model.RoomModel;
import com.zzh.user.api.UserRemoteService;
import com.zzh.user.api.entity.UserEntityInfo;

public class LiveUtil {
	


	private static Logger logger = LoggerFactory.getLogger(LiveUtil.class);
	
	/**
	 * @Title: createApp
	 * @Description: 创建App
	 * @author jupiter@zhongzhihui.com
	 * @param appModel
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> createApp(AppModel appModel) {
		Map<String, String> result = null;
		try {
			AppInnerClient appInnerClient = RemoteServiceFactory.getAppInnerClient();
			ClientResponse response = appInnerClient.createApp(appModel);
			if (response.isSuccess()) {
				result = (Map<String, String>) response.getResult();
			} else {
				logger.error("call remote service failure. createApp code=[" + response.getErrorCode() + "], message=[" + response.getErrorMessage() + "].");
			}
		} catch (Exception e) {
			//
			e.printStackTrace();
			logger.error("call remote service error. createApp " + e.getMessage());
		}
		return result;
	}

	public static void updateApp(AppModel appModel) {
		try {
			AppInnerClient appInnerClient = RemoteServiceFactory.getAppInnerClient();
			ClientResponse response = appInnerClient.updateApp(appModel);
			if (!response.isSuccess()) {
				logger.error("call remote service failure. updateApp code=[" + response.getErrorCode() + "], message=[" + response.getErrorMessage() + "].");
			}
		} catch (Exception e) {
			//
			e.printStackTrace();
			logger.error("call remote service error. updateApp " + e.getMessage());
		}
	}

	/**
	 * @Title: findAppByAppkey
	 * @Description: 根据Appkey和secret查询app信息
	 * @author jupiter@zhongzhihui.com
	 * @param appKey
	 * @param appSecret
	 * @return
	 */
	public static AppModel findAppByAppkey(String appKey, String appSecret) {
		try {
			AppClient appClient = RemoteServiceFactory.getAppClient();
			ClientSign sign = getClientSign(appKey, appSecret);
			ClientResponse response = appClient.findAppByAppKey(sign);
			if (response.isSuccess()) {
				AppModel appModel = (AppModel) response.getResult();
				return appModel;
			} else {
				logger.error("call remote service findAppByAppkey failure. code=[" + response.getErrorCode() + "], message=[" + response.getErrorMessage()
						+ "].");
			}
		} catch (Exception e) {
			//
			e.printStackTrace();
			logger.error("call remote service findAppByAppkey error. " + e.getMessage());
		}
		return null;
	}

	public static void createRoom(long companyId, CourseInfo courseInfo, Long[] resourceIds) {
		try {
			CompanyRemoteService companyRemoteService = ServiceFactory.getBean("companyRemoteService", CompanyRemoteService.class);
			CompanyItem companyInfo = companyRemoteService.findById(companyId);
			String appKey = null;
			String appSecret = null;
			if(companyInfo != null){
				appKey = companyInfo.getAppKey();
				appSecret = companyInfo.getAppSecret();
			}else{
				Properties config = ServiceFactory.getBean("config", Properties.class);
				appKey = config.getProperty("live.appkey");
				appSecret = config.getProperty("live.appsecret");
			}
			createRoom(appKey, appSecret, courseInfo, resourceIds);

		} catch (Exception e) {
			//
			e.printStackTrace();
			logger.error("call remote service createRoom error. " + e.getMessage());
		}
	}
	
	public static void createRoom(String appKey, String appSecret, CourseInfo courseInfo, Long[] resourceIds){
		try{
			ClientSign sign = getClientSign(appKey, appSecret);
			//
			RoomModel roomModel = convertToRoomModel(courseInfo);
			//
			List<ResourceModel> resourceModels = new ArrayList<ResourceModel>();
			if (resourceIds != null && resourceIds.length > 0) {
				ResourceRemoteService resourceRemoteService = ServiceFactory.getBean("resourceRemoteService", ResourceRemoteService.class);
				for (Long resourceId : resourceIds) {
					ResourceItem resourceInfo = resourceRemoteService.findByResourceId(resourceId, ResourcePlatformConstants.PUBLIC_PLATFORM);
					ResourceModel resourceModel = convertToResourceModel(resourceInfo);
					resourceModels.add(resourceModel);
				}
			}
			RoomClient roomClient = RemoteServiceFactory.getRoomClient();
			roomClient.createRoomWithResource(sign, roomModel, resourceModels);
		}catch(Exception e){
			//
			e.printStackTrace();
			logger.error("call remote service createRoom error. " + e.getMessage());
		}
		
		
	}

	public static void updateRoom(long companyId, CourseInfo courseInfo, Long[] resourceIds) {
		try {
			CompanyRemoteService companyRemoteService = ServiceFactory.getBean("companyRemoteService", CompanyRemoteService.class);
			CompanyItem companyInfo = companyRemoteService.findById(companyId);
			String appKey = null;
			String appSecret = null;
			if(companyInfo != null){
				appKey = companyInfo.getAppKey();
				appSecret = companyInfo.getAppSecret();
			}else{
				Properties config = ServiceFactory.getBean("config", Properties.class);
				appKey = config.getProperty("live.appkey");
				appSecret = config.getProperty("live.appsecret");
			}
			updateRoom(appKey, appSecret, courseInfo, resourceIds);

		} catch (Exception e) {
			//
			e.printStackTrace();
			logger.error("call remote service updateRoom error. " + e.getMessage());
		}
	}
	
	public static void updateRoom(String appKey, String appSecret, CourseInfo courseInfo, Long[] resourceIds){
		try {
			ClientSign sign = getClientSign(appKey, appSecret);
			//
			RoomModel roomModel = convertToRoomModel(courseInfo);
			RoomClient roomClient = RemoteServiceFactory.getRoomClient();
			ClientResponse response = roomClient.findRoomByBizId(sign, String.valueOf(courseInfo.getId()));
			if (response.isSuccess()) {
				RoomModel oldRoomModel = (RoomModel) response.getResult();
				roomModel.setId(oldRoomModel.getId());
				roomModel.setAttributes(oldRoomModel.getAttributes());
				roomModel.setAppId(oldRoomModel.getAppId());
				//
				List<ResourceModel> resourceModels = new ArrayList<ResourceModel>();
				if (resourceIds != null && resourceIds.length > 0) {
					ResourceRemoteService resourceRemoteService = ServiceFactory.getBean("resourceRemoteService", ResourceRemoteService.class);
					for (Long resourceId : resourceIds) {
						ResourceItem resourceInfo = resourceRemoteService.findByResourceId(resourceId, ResourcePlatformConstants.PUBLIC_PLATFORM);
						ResourceModel resourceModel = convertToResourceModel(resourceInfo);
						resourceModels.add(resourceModel);
					}
				}
				roomClient.updateRoomWithResource(sign, roomModel, resourceModels);
			} else {
				logger.error("call remote service updateRoom error. errorCode=[" + response.getErrorCode() + "], errorMessage=[" + response.getErrorMessage()
						+ "].");
			}

		} catch (Exception e) {
			//
			e.printStackTrace();
			logger.error("call remote service updateRoom error. " + e.getMessage());
		}
	}

	public static void updateRoomResources(long companyId, long courseId, Long[] resourceIds) {
		try {
			CompanyRemoteService companyRemoteService = ServiceFactory.getBean("companyRemoteService", CompanyRemoteService.class);
			CompanyItem companyInfo = companyRemoteService.findById(companyId);
			String appKey = null;
			String appSecret = null;
			if(companyInfo != null){
				appKey = companyInfo.getAppKey();
				appSecret = companyInfo.getAppSecret();
			}else{
				Properties config = ServiceFactory.getBean("config", Properties.class);
				appKey = config.getProperty("live.appkey");
				appSecret = config.getProperty("live.appsecret");
			}
			updateRoomResources(appKey, appSecret, courseId, resourceIds);
		} catch (Exception e) {
			//
			e.printStackTrace();
			logger.error("call remote service updateRoom error. " + e.getMessage());
		}
	}
	
	public static void updateRoomResources(String appKey, String appSecret,  long courseId, Long[] resourceIds){
		try {
			ClientSign sign = getClientSign(appKey, appSecret);
			//
			RoomClient roomClient = RemoteServiceFactory.getRoomClient();
			List<ResourceModel> resourceModels = new ArrayList<ResourceModel>();
			if (resourceIds != null && resourceIds.length > 0) {
				ResourceRemoteService resourceRemoteService = ServiceFactory.getBean("resourceRemoteService", ResourceRemoteService.class);
				for (Long resourceId : resourceIds) {
					ResourceItem resourceInfo = resourceRemoteService.findByResourceId(resourceId, ResourcePlatformConstants.PUBLIC_PLATFORM);
					ResourceModel resourceModel = convertToResourceModel(resourceInfo);
					resourceModels.add(resourceModel);
				}
				roomClient.updateRoomByBizWithResource(sign, String.valueOf(courseId), resourceModels);
			}
		} catch (Exception e) {
			//
			e.printStackTrace();
			logger.error("call remote service updateRoom error. " + e.getMessage());
		}
	}
	
	public static String getAccessToken(AccessModelWrapper wrapper){
		//
		AccessModel accessModel = wrapper.getAccessModel();
		Properties config = ServiceFactory.getBean("config", Properties.class);
		String bizSysCode = config.getProperty("biz.sys.code");
		accessModel.setBizSysCode(bizSysCode);
		//
		LiveClient liveClient = RemoteServiceFactory.getLiveClient();
		ClientResponse clientResponse = liveClient.getAccessToken(wrapper.getClientSign(), accessModel);
		//
		String accessToken = "";
		if (clientResponse.isSuccess()) {
			accessToken = (String) clientResponse.getResult();
		}
		return accessToken;
	}

	public static String getAccessToken(long companyId, long courseId, String userId, String username, int role, String ipAddress, int userAgent) {
		//
		CompanyRemoteService companyRemoteService = ServiceFactory.getBean("companyRemoteService", CompanyRemoteService.class);
		CompanyItem companyInfo = companyRemoteService.findById(companyId);
		String appKey = null;
		String appSecret = null;
		if(companyInfo != null){
			appKey = companyInfo.getAppKey();
			appSecret = companyInfo.getAppSecret();
		}else{
			Properties config = ServiceFactory.getBean("config", Properties.class);
			appKey = config.getProperty("live.appkey");
			appSecret = config.getProperty("live.appsecret");
		}
		return getAccessToken(appKey, appSecret, courseId, userId, username, role, ipAddress, userAgent);
	}
	
	public static String getAccessToken(String appKey, String appSecret,long courseId, String userId, String username, int role, String ipAddress, int userAgent){
		ClientSign clientSign = getClientSign(appKey, appSecret);
		//
		AccessModel accessModel = new AccessModel();
		accessModel.setAppKey(appKey);
		accessModel.setIpAddress(ipAddress);
		accessModel.setRole(role);
		accessModel.setBizUserId(userId);
		accessModel.setUsername(username);
		accessModel.setBizRoomId(String.valueOf(courseId));
		accessModel.setUserAgent(userAgent);
		Properties config = ServiceFactory.getBean("config", Properties.class);
		String bizSysCode = config.getProperty("biz.sys.code");
		accessModel.setBizSysCode(bizSysCode);
		//
		LiveClient liveClient = RemoteServiceFactory.getLiveClient();
		ClientResponse clientResponse = liveClient.getAccessToken(clientSign, accessModel);
		//
		String accessToken = "";
		if (clientResponse.isSuccess()) {
			accessToken = (String) clientResponse.getResult();
		}
		return accessToken;
	}

	/**
	 * @Title: getClientSign
	 * @Description: 获取调用sign值对象
	 * @author jupiter@zhongzhihui.com
	 * @param appKey
	 * @param appSecret
	 * @return
	 */
	public static ClientSign getClientSign(String appKey, String appSecret) {
		ClientSign sign = new ClientSign();
		String timestamp = String.valueOf(new Date().getTime());
		sign.setAppKey(appKey);
		sign.setSign(getSign(appKey, appSecret, timestamp));
		sign.setTimestamp(timestamp);
		return sign;
	}
	
	public static ClientSign getClientSign(long companyId){
		CompanyRemoteService companyRemoteService = ServiceFactory.getBean("companyRemoteService", CompanyRemoteService.class);
		CompanyItem companyInfo = companyRemoteService.findById(companyId);
		String appKey = null;
		String appSecret = null;
		if(companyInfo != null){
			appKey = companyInfo.getAppKey();
			appSecret = companyInfo.getAppSecret();
		}else{
			Properties config = ServiceFactory.getBean("config", Properties.class);
			appKey = config.getProperty("live.appkey");
			appSecret = config.getProperty("live.appsecret");
		}
		return getClientSign(appKey, appSecret);
	}

	private static String getSign(String appKey, String appSecret, String timestamp) {
		return MD5Util.getMD5Format(MD5Util.getMD5Format(appKey + timestamp) + appSecret);
	}

	private static int convertToLiveStatus(int origStatus) {
		return origStatus - 1;
	}

	private static ResourceModel convertToResourceModel(ResourceItem resourceInfo) {
		if (resourceInfo == null) {
			return null;
		}
		ResourceModel resourceModel = new ResourceModel();
		resourceModel.setExtName(resourceInfo.getFileType());
		resourceModel.setMediaType(resourceInfo.getMediaType());
		resourceModel.setName(resourceInfo.getFileName());
		resourceModel.setPageCount(resourceInfo.getPageCount());
		resourceModel.setUrl(resourceInfo.getUrl());
		return resourceModel;
	}

	private static RoomModel convertToRoomModel(CourseInfo courseInfo) {
		RoomModel roomModel = new RoomModel();
		roomModel.setBizId(String.valueOf(courseInfo.getId()));
		roomModel.setCreateTime(courseInfo.getCreateTime());
		roomModel.setCreateUser(String.valueOf(courseInfo.getCreateUser()));
		roomModel.setDescription(courseInfo.getDescription());
		roomModel.setEndTime(courseInfo.getEndTime());
		roomModel.setLiveStatus(convertToLiveStatus(courseInfo.getLiveStatus()));
		roomModel.setName(courseInfo.getName());
		roomModel.setNotice("");
		roomModel.setPubisherId(String.valueOf(courseInfo.getTeacher()));
		roomModel.setStartTime(courseInfo.getStartTime());
		roomModel.setUpdateTime(courseInfo.getUpdateTime());
		roomModel.setUpdateUser(String.valueOf(courseInfo.getUpdateUser()));
		//
		UserRemoteService userRemoteService = ServiceFactory.getBean("userRemoteService", UserRemoteService.class);
		UserEntityInfo teacherInfo = userRemoteService.findByUserId(courseInfo.getTeacher(), null);
		roomModel.setPublisher(teacherInfo.getRealName());
		return roomModel;
	}



}
