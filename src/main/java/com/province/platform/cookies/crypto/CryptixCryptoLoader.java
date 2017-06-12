/**
 * Project Name: zzh-party-admin
 * File Name: CryptixCryptoLoader.java
 * Package Name: com.zzh.admin.cookie.crypto
 * Date: 2016-8-18下午4:47:39 
 * Copyright (c) 2016, www.zhongzhihui.com All Rights Reserved. 
 */

package com.province.platform.cookies.crypto;
/** 
 * ClassName: CryptixCryptoLoader
 * Description: TODO(用一句话描述这个类)
 * 
 * @author wangzhen@zhongzhihui.com
 * @date: 2016-8-18 下午4:47:39
 */
public class CryptixCryptoLoader {
	
	static {
		java.security.Security.addProvider(new cryptix.jce.provider.CryptixCrypto());
	}
}
