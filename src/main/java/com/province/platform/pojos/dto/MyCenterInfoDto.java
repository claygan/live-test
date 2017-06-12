package com.province.platform.pojos.dto;

/**
* @ClassName: MyCenterInfoDto 
* @Description: 个人中心DTO 
* @author Norcy 
* @date 2016年7月20日 上午10:11:45 
*
 */
public class MyCenterInfoDto {

	private long userId;
	
	/**
	 * @Description: 头像路径
	 */
	private String avatarUrl;
	
	/**
	 * @Description: 头像ID
	 */
	private String avatar;
	
	/**
	 * @Description: 昵称
	 */
	private String nickname;
	
	/**
	 * @Description: 真实姓名
	 */
	private String realName;
	
	/**
	 * @Description: 身份证ID
	 */
	private String identityNum;
	
	/**
	 * @Description: 手机号码
	 */
	private String mobile;
	
	/**
	 * @Description: 电子邮箱
	 */
	private String eMail;
	
	/**
	 * @Description: 性别
	 */
	private String sex;
	
	/**
	 * @Description: 省
	 */
	private String provinceId;
	
	/**
	 * @Description: 市
	 */
	private String cityId;
	
	/**
	 * @Description: 区
	 */
	private String countyId;
	/**
	 * 省
	 */
	private String provinceName;
	/**
	 * 市
	 */
	private String cityName;
	
	/**
	 * 区县
	 */
	private String countyName;
	
	/**
	 * @Description: 详细地址
	 */
	private String address;
	
	/**
	 * @Description: 个性签名
	 */
	private String signature;
	/** 
	 * @Description:个人简介
	 */  
	private String description;
	
	/** 
	 * @Description:职业
	 */  
	private String post;

	private String qq;

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getIdentityNum() {
		return identityNum;
	}

	public void setIdentityNum(String identityNum) {
		this.identityNum = identityNum;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String geteMail() {
		return eMail;
	}

	public void seteMail(String eMail) {
		this.eMail = eMail;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(String provinceId) {
		this.provinceId = provinceId;
	}

	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}

	public String getCountyId() {
		return countyId;
	}

	public void setCountyId(String countyId) {
		this.countyId = countyId;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getCountyName() {
		return countyName;
	}

	public void setCountyName(String countyName) {
		this.countyName = countyName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPost() {
		return post;
	}

	public void setPost(String post) {
		this.post = post;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}
	
}
