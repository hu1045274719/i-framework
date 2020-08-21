package com.hu.framework.model;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @ClassName UrlMapping
 * @Description
 * @Author us
 * @Date 2020/8/12 15:11
 * @Version 1.0
 **/
public class URLMapping {

    private Object obj;
    private Method method;
    
    /**
     *  key 参数名称 value 参数类型
     */
    private Map<String, Class<?>> parameters;

    public URLMapping(Object obj, Method method, Map<String, Class<?>> parameters) {
        this.obj = obj;
        this.method = method;
        this.parameters = parameters;
    }

    public Object getObj() {
        return obj;
    }

    public Method getMethod() {
        return method;
    }

    public Map<String, Class<?>> getParameters() {
        return parameters;
    }
}
