package com.province.platform.commons;

public enum VProductStatusEnum {
	
	OFFLINE(1, "未上架"), ONLINE(2, "已上架"),UNCHECK(0,"未审核"),PASS(1,"通过"),NOPASS(2,"未通过");
	

	private int code;

	private String name;

	private VProductStatusEnum(int code, String name) {
		this.code = code;
		this.name = name;
	}

	public int getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public static String getNameByCode(int code) {
		VProductStatusEnum[] enumSet = VProductStatusEnum.values();
		for (VProductStatusEnum type : enumSet) {
			if (type.code == code) {
				return type.name;
			}
		}
		return "";
	}
}
