package com.province.platform.helper;

import java.io.File;
import java.util.Date;
import java.util.Properties;

import com.zzh.common.constants.resource.ResourceConstants;
import com.zzh.common.constants.resource.ResourcePlatformConstants;
import com.zzh.common.entity.resource.ResourceItem;
import com.zzh.common.enums.resource.ResourceConvertStatusEnum;
import com.zzh.common.enums.resource.ResourceTypeEnum;
import com.zzh.common.utils.DateUtil;
import com.zzh.common.utils.FileUtil;
import com.zzh.common.utils.MD5Util;
import com.zzh.common.utils.UUIDUtil;
import com.zzh.file.api.ResourceRemoteService;
import com.zzh.user.api.entity.UserEntityInfo;

/**
 * @ClassName: ResourceItemBuilder
 * @Package com.zzh.platform.web.module.client.helper
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author Jupiter
 * @date 2013-10-31 上午11:32:16
 * 
 */
public class ResourceHelper {

	public static String getResourceUrl(ResourceItem resourceItem) {
		Properties config = ServiceFactory.getBean("config", Properties.class);
		if (resourceItem != null && resourceItem.getUrl() != null) {
			String url = resourceItem.getUrl().replace("\\", "/");
			// +url
			String resourceUrl = config.getProperty("data.url");
			return resourceUrl + url;
		}
		return null;
	}
	
	public static String getDataUrl(long resourceId){
		ResourceRemoteService resourceService = ServiceFactory.getBean("resourceRemoteService", ResourceRemoteService.class);
		ResourceItem resourceItem = resourceService.findByResourceId(resourceId,ResourcePlatformConstants.PUBLIC_PLATFORM);
		return getDataUrl(resourceItem);
	}
	
	public static String getDataUrl(ResourceItem resourceItem) {
		Properties config = ServiceFactory.getBean("config", Properties.class);
		if (resourceItem != null && resourceItem.getUrl() != null) {
			String url = resourceItem.getUrl().replace("\\", "/");
			// +url
			String resourceUrl = config.getProperty("data.url");
			if(!url.contains(resourceUrl)){
				return resourceUrl + url;
			}else{
				return url;
			}
		}
		return null;
	}


	public static ResourceItem getresourceItem(String fileName, long size, UserEntityInfo userInfo, String type) {
		String id = UUIDUtil.getUUID();
		String extName = FileUtil.getFileSuffix(fileName);
		// 保存在磁盘上
		/**
		 * 资源存放路径 parentDir : F:/resources uploadDir : /resourceType.getCode()/yyyy/mm/yyyy-MM-dd/id.extName 访问相对URL
		 * relativeUrl： /resourceType.getCode()/yyyy/mm/yyyy-MM-dd/id.extName
		 */
		String resourceType = type;
		if (ResourceConstants.MEDIA_TYPE_DOCUMENT.equalsIgnoreCase(ResourceConstants.getResourceMediaType(extName)) || ResourceConstants.MEDIA_TYPE_VIDEO.equalsIgnoreCase(ResourceConstants.getResourceMediaType(extName))) {
			resourceType = ResourceTypeEnum.OTHER.getCode();
		}
		// 保存原文件至数据库
		ResourceItem resource = new ResourceItem();
		resource.setFileName(fileName);
		resource.setFileType(extName);
		resource.setType(resourceType);
		resource.setMediaType(ResourceConstants.getResourceMediaType(extName));
		resource.setSize(size);
		resource.setUrl(getFileUrl(resourceType, userInfo.getCompanyId()) + id + "." + extName);
		resource.setStatus(ResourceConvertStatusEnum.NEEDNOT.getCode());
		resource.setCreateUser(userInfo.getUserId());
		resource.setUpdateUser(userInfo.getUserId());
		resource.setCompanyId(userInfo.getCompanyId());
		return resource;
	}

	public static ResourceItem getresourceItemEx(String fileName, UserEntityInfo userInfo, String type) {
		//
		String extName = FileUtil.getFileSuffix(fileName);
		String md5Name = getMd5FileName(fileName);
		if (ResourceConstants.MEDIA_TYPE_DOCUMENT.equalsIgnoreCase(ResourceConstants.getResourceMediaType(extName)) || ResourceConstants.MEDIA_TYPE_VIDEO.equalsIgnoreCase(ResourceConstants.getResourceMediaType(extName))) {
			type = ResourceTypeEnum.OTHER.getCode();
		}
		// 保存原文件至数据库
		ResourceItem resource = new ResourceItem();
		resource.setFileName(fileName);
		resource.setFileType(extName);
		resource.setType(type);
		resource.setMediaType(ResourceConstants.getResourceMediaType(extName));
		resource.setUrl(getFileUrl(type, userInfo.getCompanyId()) + md5Name + "." + extName);
		resource.setStatus(ResourceConvertStatusEnum.NEEDNOT.getCode());
		resource.setCreateUser(userInfo.getUserId());
		resource.setUpdateUser(userInfo.getUserId());
		resource.setCompanyId(userInfo.getCompanyId());
		//
		return resource;
	}

	public static String getMd5FileName(String fileName) {
		return MD5Util.getMD5Format(fileName);
	}

	public static String getFileUrl(String type, long companyId) {
		// upload/vod/xxxx-xx-xx/fileName
		StringBuilder uploadDir = new StringBuilder();
		uploadDir.append(File.separator);
		uploadDir.append(type);
		uploadDir.append(File.separator);
		uploadDir.append(String.valueOf(companyId));
		uploadDir.append(File.separator);
		uploadDir.append(DateUtil.getYear(new Date()));
		uploadDir.append(File.separator);
		uploadDir.append(DateUtil.getMonth(new Date()) + 1);
		uploadDir.append(File.separator);
		uploadDir.append(DateUtil.getDateString(new Date(), "yyyy-MM-dd"));
		uploadDir.append(File.separator);
		//
		return uploadDir.toString();
	}
}
