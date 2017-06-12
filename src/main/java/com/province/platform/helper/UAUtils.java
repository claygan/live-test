package com.province.platform.helper;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

public class UAUtils {

	private static String[] robots;

	private static final String UA_KEY = "user-agent";
	private static final String PARAM_KEY = "User-Agent";
	private static final String PARAM_KEY_TB = "TB-UA";
	@SuppressWarnings("unused")
	private static final String[] PARAMS = { "ADRQB", "IQB", "MCAQB" };

	public static boolean isH5(HttpServletRequest request) {
		String platform = getPlatform(request);
		if ("meego".equals(platform) || "ios".equals(platform) || "android".equals(platform)
				|| "symbian^3".equals(platform) || "webos".equals(platform) || "uPhone".equals(platform)
				|| "ipad".equals(platform) || "yunOS".equals(platform)) {
			return true;
		} else {
			return false;
		}
	}

	public static String getPlatform(HttpServletRequest request) {
		String ua;
		//
		String id = request.getHeader(PARAM_KEY_TB) == null ? request.getHeader(PARAM_KEY)
				: request.getHeader(PARAM_KEY_TB);
		if ("iphone".equalsIgnoreCase(id)) {
			return PlatformEnum.ANDROID.getPlatform();
		} else if ("adclient".equalsIgnoreCase(id)) {
			return PlatformEnum.ANDROID.getPlatform();
		} else if ("anclient".equalsIgnoreCase(id)) {
			return PlatformEnum.ANDROID.getPlatform();
		} else if ("kjc".equalsIgnoreCase(id)) {
			return PlatformEnum.ANDROID.getPlatform();
		}
		//
		// String value = request.getHeader("Q-UA");
		// for (String item : PARAMS) {
		// if (item.equalsIgnoreCase(value)) {
		// return PlatformEnum.SYMBIAN.getPlatform();
		// }
		// }
		//
		ua = request.getHeader("User-Agent");
		if (StringUtils.isNotBlank(ua) && ua.toLowerCase().indexOf("aliyun") > 0) {
			return PlatformEnum.ALIYUN.getPlatform();
		} else if (StringUtils.isNotBlank(ua) && ua.indexOf("Bada") > 0) {
			return PlatformEnum.BADA.getPlatform();
		} else if (StringUtils.isNotBlank(ua) && ua.indexOf("UCWEB7") > 0) {
			return PlatformEnum.UCWEB7.getPlatform();
		} else if (StringUtils.isNotBlank(ua) && ua.indexOf("BlackBerry") >= 0) {
			return PlatformEnum.BLACKBERRY.getPlatform();
		} else if (StringUtils.isNotBlank(ua) && (ua.indexOf("WoPhone") >= 0 || ua.indexOf("UPhone") >= 0)) {
			return PlatformEnum.UPHONE.getPlatform();
		} else if (StringUtils.isNotBlank(ua) && ua.indexOf("MeeGo; Nokia") >= 0) {
			return PlatformEnum.MEEGO.getPlatform();
		} else if (StringUtils.isNotBlank(ua) && (ua.indexOf("Symbian") > 0 || ua.indexOf("Nokia") >= 0)) {
			return PlatformEnum.SYMBIAN.getPlatform();
		} else if (StringUtils.isNotBlank(ua) && ua.indexOf("iPad") >= 0) {
			return PlatformEnum.IPAD.getPlatform();
		} else if (StringUtils.isNotBlank(ua)// ua.toLowerCase().indexOf("android") >= 0)) 改为存在android才属于h5
				&& (ua.indexOf("iPhone") >= 0 || ua.indexOf("iPod touch") >= 0
						|| (ua.indexOf("AppleWebKit") > 0 && ua.toLowerCase().indexOf("android") >= 0))
				&& ua.toLowerCase().indexOf("ucweb") < 0) {
			return PlatformEnum.IOS.getPlatform();
		} else if (StringUtils.isNotBlank(ua) && (ua.indexOf("Android") >= 0
				|| ua.indexOf("Linux; U;") >= 0 && ua.toLowerCase().indexOf("ucweb") < 0)) {
			return PlatformEnum.ANDROID.getPlatform();
		} else if (StringUtils.isNotBlank(ua) && ua.indexOf("UC ") >= 0 && ua.indexOf("Mobile Safari") > 0) {
			return PlatformEnum.ANDROID.getPlatform();
		} else if (StringUtils.isNotBlank(ua) && ua.toLowerCase().indexOf("webOS") > 0) {
			return PlatformEnum.WEBOS.getPlatform();
		} else if (StringUtils.isNotBlank(ua) && ua.indexOf("Windows CE;") > 0) {
			return PlatformEnum.WINCE.getPlatform();
		} else if (StringUtils.isNotBlank(ua) && ua.indexOf("Windows Phone OS") > 0) {
			return PlatformEnum.WINPHONE7.getPlatform();
		}
		//
		return PlatformEnum.UNKNOWN.getPlatform();
	}

	public static boolean isRobot(HttpServletRequest request) {
		if (request == null) {
			return false;
		}
		//
		String currentUA = request.getHeader(UA_KEY);
		if (currentUA == null || "".equals(currentUA)) {
			return false;
		}
		for (String item : robots) {
			if (currentUA.indexOf(item) >= 0) {
				return true;
			}
		}
		return false;
	}

	public enum PlatformEnum {

		IPAD("ipad", 2), IOS("ios", 2), ANDROID("android", 2),

		SYMBIAN3("symbian^3", 2), SYMBIAN("symbian", 1), SYMBIAN_S60V2("s60v2", 1), SYMBIAN_S60V3("s60v3",
				1), SYMBIAN_S40("s40", 1),

		WEBOS("webos", 2), UPHONE("uPhone", 2), ALIYUN("yunOS", 2), MEEGO("meego", 2),

		UCWEB7("ucweb7", 1),

		WINCE("wince", 1), WINMOBILE("winmobile", 1), WINPHONE7("winphone7", 1),

		MTK("mtk", 1), LINUX("linux", 1),

		BLACKBERRY("BlackBerry", 1), NEWPLUS("newplus", 1), BADA("bada", 1),

		UNKNOWN("0", 1);

		private String platform;
		private int platformLevel = 1; // level越大，默认对应平台的显示屏越大

		PlatformEnum(String model, int level) {
			this.platform = model;
			this.platformLevel = level;
		}

		public String getPlatform() {
			return this.platform;
		}

		public int getPlatformLevel() {
			return this.platformLevel;
		}
	}
}
