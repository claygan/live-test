package com.province.platform.injection;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.province.platform.utils.DefenseUtil;


/**
 * sql防注入HttpservletRequestWrapper包装类
 * @author saleson
 *
 */
public class SqlHttpServletRequestWrapper extends HttpServletRequestWrapper {

	
	private Map<String, String[]> parameters;
    private String[][] keyValues;

    public SqlHttpServletRequestWrapper(HttpServletRequest request, String[][] keyValues) {
        super(request);
        this.keyValues = keyValues;
        initParameters();
    }

    @Override
    public String getParameter(String name) {
        String[] strs = parameters.get(name);
        if (strs == null || strs.length == 0) {
            return null;
        }
        return strs[0];
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public Map getParameterMap() {
        return Collections.unmodifiableMap(this.parameters);
    }

    @Override
    public String[] getParameterValues(String name) {
        return this.parameters.get(name);
    }
    
    private void initParameters(){
    	parameters = new HashMap<String, String[]>();
        Map<String, String[]> params = (Map<String, String[]>) super.getParameterMap();
        Iterator<String> iter = params.keySet().iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            String[] valueAry = (String[]) params.get(key);
            String[] values = new String[valueAry.length];
            for (int i = 0; i < valueAry.length; i++) {
                values[i] = replaceSqlKey(valueAry[i]);
                if(DefenseUtil.sql_inj(values[i])){
                	values[i] = DefenseUtil.dealSqlStr(values[i]);
                }
            }
            parameters.put(key, values);
        }
    }
    
    
    public String replaceSqlKey(String str) {
        String retStr = str;
        for (int i = 0, size=keyValues.length; i < size; i++) {
            String [] words = keyValues[i];
            if (str.indexOf(words[0]) >= 0) {
                retStr = retStr.replaceAll(words[0], words[1]);
            }
        }
        return retStr;
    }
}
