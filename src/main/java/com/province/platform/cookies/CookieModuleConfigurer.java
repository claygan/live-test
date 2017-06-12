/**
 * Project Name: zzh-live-remote
 * File Name: CookieModuleConfigurer.java
 * Package Name: com.zzh.live.remote.config.cookie
 * Date: 2015-6-12下午3:35:59 
 * Copyright (c) 2015, www.zhongzhihui.com All Rights Reserved. 
 */

package com.province.platform.cookies;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

import com.province.platform.cookies.crypto.CryptixCryptoLoader;
import com.province.platform.cookies.crypto.DefaultCryptoBeanDefinition;

/**
 * ClassName: CookieModuleConfigurer Description: TODO(用一句话描述这个类)
 * 
 * @author jupiter@zhongzhihui.com
 * @date: 2015-6-12 下午3:35:59
 */
public class CookieModuleConfigurer implements InitializingBean {

	private static final String DEFAULT_CRYPTO_KEY = "^#16qweqv88cde729!@#$3450abfg^%";

	@SuppressWarnings("unused")
	private CryptixCryptoLoader cryptixCryptoLoader;

	// CryptoKey
	private String cryptoKey;

	// CookieConfigurerList
	private List<CookieConfigurer> cookieConfigurerList;

	private Map<String, CookieConfigurer> clientName2CfgMap;

	private Map<String, CookieConfigurer> name2CfgMap;

	private DefaultCryptoBeanDefinition blowfishCrypto;

	public void setCryptoKey(String cryptoKey) {
		this.cryptoKey = cryptoKey;
	}

	public List<CookieConfigurer> getCookieConfigurerList() {
		return cookieConfigurerList;
	}

	public void setCookieConfigurerList(List<CookieConfigurer> cookieConfigurerList) {
		this.cookieConfigurerList = cookieConfigurerList;
	}

	public Map<String, CookieConfigurer> getClientName2CfgMap() {
		return clientName2CfgMap;
	}

	public Map<String, CookieConfigurer> getName2CfgMap() {
		return name2CfgMap;
	}

	public void afterPropertiesSet() throws Exception {
		cryptixCryptoLoader = new CryptixCryptoLoader();
		// 设置blowfishCrypto开始
		if (!StringUtils.hasText(cryptoKey)) {
			cryptoKey = DEFAULT_CRYPTO_KEY;
		}
		blowfishCrypto = new DefaultCryptoBeanDefinition();
		blowfishCrypto.setAlgorithm("Blowfish");
		blowfishCrypto.setTransformation("Blowfish/CBC/NoPadding");
		blowfishCrypto.setKey(cryptoKey);
		blowfishCrypto.setProvider("CryptixCrypto");
		// 设置blowfishCrypto结束
		if (this.cookieConfigurerList != null) {
			for (CookieConfigurer cookieConfigurer : this.cookieConfigurerList) {
				if (cookieConfigurer.isEncrypted()) {
					cookieConfigurer.setCrypto(blowfishCrypto);
				}
			}
		} else {
			return;
		}
		// 设置name2CfgMap和clientName2CfgMap开始
		name2CfgMap = new HashMap<String, CookieConfigurer>(this.cookieConfigurerList.size());
		clientName2CfgMap = new HashMap<String, CookieConfigurer>(this.cookieConfigurerList.size());
		for (CookieConfigurer cfg : this.cookieConfigurerList) {
			if (cfg.getName() == null) {
				throw new NullPointerException("CookieConfigurer's name can't be null.");
			}
			name2CfgMap.put(cfg.getName(), cfg);
			if (cfg.getClientName() == null) {
				throw new NullPointerException("CookieConfigurer's client name can't be null.");
			}

			clientName2CfgMap.put(cfg.getClientName(), cfg);
		}
		name2CfgMap = Collections.unmodifiableMap(name2CfgMap);
		clientName2CfgMap = Collections.unmodifiableMap(clientName2CfgMap);
		// 设置name2CfgMap和clientName2CfgMap结束
	}

}
