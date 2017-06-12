/**
 * Project Name: zzh-party-admin
 * File Name: CryptoBeanDefinition.java
 * Package Name: com.zzh.admin.cookie.crypto
 * Date: 2016-8-18下午4:48:07 
 * Copyright (c) 2016, www.zhongzhihui.com All Rights Reserved. 
 */

package com.province.platform.cookies.crypto;
/** 
 * ClassName: CryptoBeanDefinition
 * Description: TODO(用一句话描述这个类)
 * 
 * @author wangzhen@zhongzhihui.com
 * @date: 2016-8-18 下午4:48:07
 */
public interface CryptoBeanDefinition {
	/**
	 * 对string串进行加密
	 * 
	 * @param s 不能为null
	 * @return 加密后的string
	 */
	public String encrypt(String s);

	/**
	 * 对string串使用指定的编码进行加密
	 * 
	 * @param s 不能为null
	 * @param encoding 编码
	 * @return 加密后的string
	 */
	public String encrypt(String s, String encoding);

	/**
	 * 对string串进行解密
	 * 
	 * @param s 不能为null
	 * @return 解密后的string
	 */
	public String dectypt(String s);

	/**
	 * 对string串使用指定的编码进行解密
	 * 
	 * @param s 不能为null
	 * @param encoding 编码
	 * @return 解密后的string
	 */
	public String dectypt(String s, String encoding);

	public byte[] encrypt(byte[] bytes);

	public byte[] dectypt(byte[] bytes);
}
