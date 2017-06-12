/**
 * Project Name: zzh-cloud-admin
 * File Name: CloudFastJsonHttpMessageConverter.java
 * Package Name: com.zzh.cloud.web.admin.config
 * Date: 2015-8-19下午1:47:54 
 * Copyright (c) 2015, www.zhongzhihui.com All Rights Reserved. 
 */

package com.province.platform.commons;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;

/**
 * ClassName: CloudFastJsonHttpMessageConverter Description: 重写覆盖fastjson的converter，使用自定义时间格式
 * 
 * @author jupiter@zhongzhihui.com
 * @date: 2015-8-19 下午1:47:54
 */
public class CloudFastJsonHttpMessageConverter extends FastJsonHttpMessageConverter {

//	private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static final String JSONP_CALLBACK = "callback";
	private static final Pattern FUNCTION_NAME_REG = Pattern.compile("^[a-zA-Z_]+");//js function name regexp

	@Override
	protected void writeInternal(Object obj, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
		//
		OutputStream out = outputMessage.getBody();
		//String text = JSON.toJSONStringWithDateFormat(obj, DATE_TIME_FORMAT, getFeatures());
		String text = toJson(obj);
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();   
		
		String callback = request.getParameter(JSONP_CALLBACK);
		if(StringUtils.isNotEmpty(callback)){
			Matcher mat = FUNCTION_NAME_REG.matcher(callback);
			if(mat.find()){
				text = callback + "(" + text + ")";
			}
		}
		byte[] bytes = text.getBytes(getCharset());
		out.write(bytes);
	}
	
	/**
	 * 将对象转换Json
	 * @param obj
	 * @return
	 */
	public String toJson(Object obj){
		//return toJson(obj, DATE_TIME_FORMAT, getFeatures());
		return toJson(obj, null, getFeatures());
	}
	
	/**
	 * 将对像转换成Json
	 * @param object
	 * @param dateFormat
	 * @param features
	 * @return
	 */
	private String toJson(Object object, String dateFormat,
            SerializerFeature... features){
		SerializeWriter out = new SerializeWriter();

        try {
            JSONSerializer serializer = new JSONSerializer(out);
            for (SerializerFeature feature : features) {
                serializer.config(feature, true);
            }

            serializer.config(SerializerFeature.WriteDateUseDateFormat, true);
            NumberSerializer numberSerializer = new NumberSerializer();
            serializer.getMapping().put(int.class, numberSerializer);
            serializer.getMapping().put(Integer.class, numberSerializer);
            serializer.getMapping().put(short.class, numberSerializer);
            serializer.getMapping().put(Short.class, numberSerializer);
            serializer.getMapping().put(long.class, numberSerializer);
            serializer.getMapping().put(Long.class, numberSerializer);

//            DateSerializer dateSerializer = new DateSerializer();
//            serializer.getMapping().put(Date.class, dateSerializer);
            if (dateFormat != null) {
                serializer.setDateFormat(dateFormat);
            }

            serializer.write(object);

            return out.toString();
        } finally {
            out.close();
        }
	}
	
	
	/**
	 * fastjson 序列化器
	 * 用于将数字类型的对象转换成String输出。
	 * @author libin@zhongzhihui.com
	 *
	 */
	private class NumberSerializer implements ObjectSerializer{

		@Override
		public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features)
				throws IOException {
			SerializeWriter out = serializer.getWriter();
            if ( object == null ) {
                if ( out.isEnabled(SerializerFeature.WriteNullNumberAsZero) ) {
                    out.write('0');
                } else {
                    out.writeNull();
                }
                return;
            }
            out.writeString(String.valueOf(object));
		}
		
	}
	
	
//	private class DateSerializer implements ObjectSerializer {
//
//		
//		private static final String format = "yyyy-MM-dd HH:mm:ss";
//		@Override
//		public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features)
//				throws IOException {
//			SerializeWriter out = serializer.getWriter();
//            if ( object != null ) {
//            	String format = null;
//                if ( object instanceof Date ) {
//                	try {
//						Field field = object.getClass().getDeclaredField(fieldName.toString());
//						if(field!=null){
//							JSONField jsonField = field.getAnnotation(JSONField.class);
//							if(jsonField!=null){
//								format = jsonField.format();
//							}
//						}
//					} catch (SecurityException e) {
//						e.printStackTrace();
//					} catch (NoSuchFieldException e) {
//						e.printStackTrace();
//					}
//                	SimpleDateFormat sdf = new SimpleDateFormat(StringUtils.defaultIfEmpty(format, this.format));
//                    out.write(sdf.format(object));
//                    return;
//                }
//            }
//            out.writeString(String.valueOf(object));
//		}
//		
//	}
}
