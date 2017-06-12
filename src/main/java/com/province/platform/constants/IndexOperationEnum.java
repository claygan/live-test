package com.province.platform.constants;

public enum IndexOperationEnum {
	


	ENDED("ended", "已结束"),

	HAS_JOINED("hasJoined", "已报名"),

	JOIN_MY_SCHEDULE("joinMySchedule", "我要报名"),

	GOTO_LIVE("gotoLive", "进入学习"),
	
	GOTO_VIDEO("gotoVideo", "进入学习")

	;

	private String code;

	private String title;

	private IndexOperationEnum(String code, String title) {
		this.code = code;
		this.title = title;
	}

	public String getCode() {
		return code;
	}

	public String getTitle() {
		return title;
	}

	public static String getTitleByCode(String code) {
		IndexOperationEnum[] enums = IndexOperationEnum.values();
		for (IndexOperationEnum type : enums) {
			if (type.code.equals(code)) {
				return type.title;
			}
		}
		return "";
	}



}
