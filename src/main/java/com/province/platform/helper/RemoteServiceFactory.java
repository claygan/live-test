package com.province.platform.helper;

import org.summercool.hsf.netty.service.HsfConnector;
import org.summercool.hsf.proxy.ServiceProxyFactory;

import com.zzh.course.api.CourseRemoteService;
import com.zzh.live.remote.client.live.LiveClient;
import com.zzh.live.remote.client.manager.AppClient;
import com.zzh.live.remote.client.manager.AppInnerClient;
import com.zzh.live.remote.client.manager.ResourceClient;
import com.zzh.live.remote.client.manager.RoomClient;

public class RemoteServiceFactory {
	

	/**
	 * @Title: getResourceClient
	 * @Description: 获取资源远程服务对象
	 * @author jupiter@zhongzhihui.com
	 * @return
	 */
	public static CourseRemoteService getCourseRemoteService (){
		HsfConnector courseConnector = ServiceFactory.getBean("courseConnector", HsfConnector.class);
		CourseRemoteService courseRemoteService = ServiceProxyFactory.getRoundFactoryInstance(courseConnector).wrapSyncProxy(CourseRemoteService.class);
		return courseRemoteService;
	}
	
	public static LiveClient getLiveClient(){
		LiveClient liveClient = ServiceFactory.getBean("hessianLiveClient", LiveClient.class);
		return liveClient;
	}
	
	public static AppInnerClient getAppInnerClient(){
		AppInnerClient appInnerClient = ServiceFactory.getBean("hessianAppInnerClient", AppInnerClient.class);
		return appInnerClient;
	}
	
	public static AppClient getAppClient(){
		AppClient appClient = ServiceFactory.getBean("hessianAppClient", AppClient.class);
		return appClient;
	}
	
	public static RoomClient getRoomClient(){
		RoomClient roomClient = ServiceFactory.getBean("hessianRoomClient", RoomClient.class);
		return roomClient;
	}
	
	public static ResourceClient getResourceClient(){
		ResourceClient resourceClient = ServiceFactory.getBean("hessianResourceClient", ResourceClient.class);
		return resourceClient;
	}
	
	
	
	


}
