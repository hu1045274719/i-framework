package com.hu.framework.servlet;

import com.alibaba.fastjson.JSON;
import com.hu.framework.model.URLMapping;
import com.hu.framework.util.ClassUtil;
import com.hu.framework.util.DateTimeConverter;
import com.hu.framework.util.StringUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @ClassName DispatcherServlet
 * @Description
 * @Author us
 * @Date 2020/8/12 15:48
 * @Version 1.0
 **/
public class DispatcherServlet extends HttpServlet {

    // 请求前缀
    private static final String REQUEST_PREFIX = "prefix";
    // 请求后缀
    private static final String REQUEST_SUFFIX = "suffix";
    //controller包路径
    private static final String BASE_PACKAGE = "basePackage";

    private String prefix = "/WEB-INF/jsp";
    private String suffix = ".jsp";
    private String basePackage = "com.hu";
    private static Map<String, URLMapping> mappings = new HashMap<>();

    // 可以处理的数据类型
    private Set<Class<?>> allParamTypes = new HashSet<>();

    @Override
    public void init(ServletConfig config){
        ConvertUtils.register(new DateTimeConverter(), LocalDate.class);
        ConvertUtils.register(new DateTimeConverter(), LocalDateTime.class);
        try {
            if (StringUtil.isNotEmpty(config.getInitParameter(REQUEST_PREFIX))) {
                prefix = config.getInitParameter(REQUEST_PREFIX);
            }
            if (StringUtil.isNotEmpty(config.getInitParameter(REQUEST_SUFFIX))) {
                suffix = config.getInitParameter(REQUEST_SUFFIX);
            }
            if (StringUtil.isNotEmpty(config.getInitParameter(BASE_PACKAGE))) {
                basePackage = config.getInitParameter(BASE_PACKAGE);
            }
            mappings = ClassUtil.getURLMappings(basePackage);

            allParamTypes.add(java.lang.Short.class);
            allParamTypes.add(java.lang.Byte.class);
            allParamTypes.add(java.lang.Integer.class);
            allParamTypes.add(java.lang.Long.class);
            allParamTypes.add(java.lang.Float.class);
            allParamTypes.add(java.lang.Double.class);
            allParamTypes.add(java.lang.Boolean.class);
            allParamTypes.add(java.lang.Character.class);
            allParamTypes.add(java.lang.String.class);
            allParamTypes.add(java.time.LocalDate.class);
            allParamTypes.add(java.time.LocalDateTime.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        try {
            // 获取到请求的URI
            String requestURI = request.getRequestURI();
            // 获取目标URI
            URLMapping urlMapping
                    = mappings.get(requestURI.replace(request.getContextPath(), ""));
            // 请求的路径不存在
            if (urlMapping == null) {
                response.setStatus(404);
                response.getWriter().write("请求的路径不存在：404 " + requestURI);
                return;
            }
            // 获取目标对象
            Object obj = urlMapping.getObj();
            // 获取目标方法
            Method method = urlMapping.getMethod();
            // 获取目标方法的参数信息 参数名称  参数值
            Map<String, Class<?>> parameter = urlMapping.getParameters();
            // 存储请求参数的值
            Object[] params = new Object[method.getParameterCount()];
            int index = 0;
            for (String key : parameter.keySet()) {
                // 请求参数的名称
                String paramName = key;
                // 请求参数的值 一个参数一个值
                String paramValue = request.getParameter(paramName);
                // 请求参数的值 一个参数多个值
                String[] paramValues = request.getParameterValues(paramName);
                // 请求参数的类型
                Class<?> paramType = parameter.get(key);
                // 请求参数如果是数组，数组数据的数据类型
                Class<?> componentType = paramType.getComponentType();

                if (paramType == HttpServletRequest.class) {
                    params[index++] = request;
                    continue;
                }
                if (paramType == HttpServletResponse.class) {
                    params[index++] = response;
                    continue;
                }
                if (paramValue != null) { // 数据类型转换的处理
                    // 基本数据类型转换开始
                    if (paramType == String.class) {
                        params[index++] = paramValue;
                        continue;
                    }
                    if (paramType == Byte.class) {
                        params[index++] = Byte.parseByte(paramValue);
                        continue;
                    }
                    if (paramType == Short.class) {
                        params[index++] = Short.parseShort(paramValue);
                        continue;
                    }
                    if (paramType == Integer.class) {
                        params[index++] = Integer.parseInt(paramValue);
                        continue;
                    }
                    if (paramType == Long.class) {
                        params[index++] = Long.parseLong(paramValue);
                        continue;
                    }
                    if (paramType == Double.class) {
                        params[index++] = Double.parseDouble(paramValue);
                        continue;
                    }
                    if (paramType == Float.class) {
                        params[index++] = Float.parseFloat(paramValue);
                        continue;
                    }
                    if (paramType == Boolean.class) {
                        params[index++] = Boolean.parseBoolean(paramValue);
                        continue;
                    }
                    if (paramType == Character.class) {
                        params[index++] = paramValue.toCharArray()[0];
                        continue;
                    }
                    // 基本数据类型转换结束

                    // 日期类型处理开始
                    if (paramType == LocalDate.class) {
                        params[index++] = LocalDate.parse(paramValue, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        continue;
                    }
                    if (paramType == LocalDateTime.class) {
                        params[index++] = LocalDateTime.parse(paramValue, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        continue;
                    }
                    // 日期类型处理结束

                    // 数组数据类型处理开始
                    if (paramValues != null && componentType != null && paramValues.length > 0) {
                        Integer length = paramValues.length;
                        if (componentType == String.class) {
                            List<String> list = new ArrayList<>();
                            for (int i = 0; i < length; i++) {
                                String[] arr = paramValues[i].split(",");
                                for(String value : arr) {
                                    list.add(value);
                                }
                            }
                            String[] array = new String[list.size()];
                            params[index++] = list.toArray(array);
                            continue;
                        }
                        if (componentType == Byte.class) {
                            List<Byte> list = new ArrayList<>();
                            for (int i = 0; i < length; i++) {
                                String[] arr = paramValues[i].split(",");
                                for(String value : arr) {
                                    list.add(Byte.parseByte(value));
                                }
                            }
                            Byte[] array = new Byte[list.size()];
                            params[index++] = list.toArray(array);
                            continue;
                        }
                        if (componentType == Short.class) {
                            List<Short> list = new ArrayList<>();
                            for (int i = 0; i < length; i++) {
                                String[] arr = paramValues[i].split(",");
                                for(String value : arr) {
                                    list.add(Short.parseShort(value));
                                }
                            }
                            Short[] array = new Short[list.size()];
                            params[index++] = list.toArray(array);
                            continue;
                        }
                        if (componentType == Integer.class) {
                            List<Integer> list = new ArrayList<>();
                            for (int i = 0; i < length; i++) {
                                String[] arr = paramValues[i].split(",");
                                for(String value : arr) {
                                    list.add(Integer.parseInt(value));
                                }
                            }
                            Integer[] array = new Integer[list.size()];
                            params[index++] = list.toArray(array);
                            continue;
                        }
                        if (componentType == Long.class) {
                            List<Long> list = new ArrayList<>();
                            for (int i = 0; i < length; i++) {
                                String[] arr = paramValues[i].split(",");
                                for(String value : arr) {
                                    list.add(Long.parseLong(value));
                                }
                            }
                            Long[] array = new Long[list.size()];
                            params[index++] = list.toArray(array);
                            continue;
                        }
                        if (componentType == Double.class) {
                            List<Double> list = new ArrayList<>();
                            for (int i = 0; i < length; i++) {
                                String[] arr = paramValues[i].split(",");
                                for(String value : arr) {
                                    list.add(Double.parseDouble(value));
                                }
                            }
                            Double[] array = new Double[list.size()];
                            params[index++] = list.toArray(array);
                            continue;
                        }
                        if (componentType == Float.class) {
                            List<Float> list = new ArrayList<>();
                            for (int i = 0; i < length; i++) {
                                String[] arr = paramValues[i].split(",");
                                for(String value : arr) {
                                    list.add(Float.parseFloat(value));
                                }
                            }
                            Float[] array = new Float[list.size()];
                            params[index++] = list.toArray(array);
                            continue;
                        }
                        if (componentType == Boolean.class) {
                            List<Boolean> list = new ArrayList<>();
                            for (int i = 0; i < length; i++) {
                                String[] arr = paramValues[i].split(",");
                                for(String value : arr) {
                                    list.add(Boolean.parseBoolean(value));
                                }
                            }
                            Boolean[] array = new Boolean[list.size()];
                            params[index++] = list.toArray(array);
                            continue;
                        }
                        if (componentType == Character.class) {
                            List<Character> list = new ArrayList<>();
                            for (int i = 0; i < length; i++) {
                                String[] arr = paramValues[i].split(",");
                                for(String value : arr) {
                                    list.add(value.toCharArray()[0]);
                                }
                            }
                            Character[] array = new Character[list.size()];
                            params[index++] = list.toArray(array);
                            continue;
                        }
                        // 基本数据类型转换结束

                        // 日期类型处理开始
                        if (componentType == LocalDate.class) {
                            List<LocalDate> list = new ArrayList<>();
                            for (int i = 0; i < length; i++) {
                                String[] arr = paramValues[i].split(",");
                                for(String value : arr) {
                                    list.add(LocalDate.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                                }
                            }
                            LocalDate[] array = new LocalDate[list.size()];
                            params[index++] = list.toArray(array);
                            continue;
                        }
                        if (componentType == LocalDateTime.class) {
                            List<LocalDateTime> list = new ArrayList<>();
                            for (int i = 0; i < length; i++) {
                                String[] arr = paramValues[i].split(",");
                                for(String value : arr) {
                                    list.add(LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                                }
                            }
                            LocalDateTime[] array = new LocalDateTime[list.size()];
                            params[index++] = list.toArray(array);
                            continue;
                        }
                        // 日期类型处理结束
                    }
                    // 数组数据类型处理结束
                }
                // 处理其它情况
                if (!allParamTypes.contains(paramType) && componentType == null) {
                    try {
                        Object paramObj = paramType.newInstance();
                        BeanUtils.populate(paramObj, request.getParameterMap());
                        params[index++] = paramObj;
                    } catch (Exception e) {
                        params[index++] = null;
                    }
                }
            }
            Object modelView = method.invoke(obj, params);
            // 如果方法有返回值
            if (modelView != null) {
                // 方法的返回值是字符串
                if (modelView instanceof String) {
                    String viewName = (String) modelView;
                    // 重定向
                    if (viewName.startsWith("redirect:")) {
                        response.sendRedirect(request.getContextPath() + viewName.replace("redirect:", ""));
                    }
                    // 请求转发
                    else {
                        request.getRequestDispatcher(prefix + "/" + viewName + suffix).forward(request, response);
                    }
                }
                // 方法的返回值是其它类型 按JSON数据处理
                else {
                    response.setContentType("application/json;charset=UTF-8");
                    // 将 modelView 转换成JSON字符串，打印到浏览器
                    String jsonResult = JSON.toJSONString(modelView);
                    response.getWriter().write(jsonResult);
                }
            }
        } catch (Exception e) {
            response.setStatus(500);
            response.getWriter().println("服务器内部错误500：");
            e.printStackTrace(response.getWriter()); // 打印到浏览器
            e.printStackTrace();    // 打印到控制台
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}

