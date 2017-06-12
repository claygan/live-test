/**   
* @Title: FreeMakerWidget.java
* @Package com.zzh.platform.web.module.col.widget
* @Description: TODO(用一句话描述该文件做什么)
* @author Jupiter
* @date 2014-5-2 上午12:44:01
* @version V1.0   
*/
package com.province.platform.widgets;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName: FreeMakerWidget
 * @Package com.zzh.platform.web.module.col.widget
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author Jupiter
 * @date 2014-5-2 上午12:44:01
 * 
 */
public interface FreeMakerWidget {
	
	public void referenceData(HttpServletRequest request, Map<String, Object> model);

}
