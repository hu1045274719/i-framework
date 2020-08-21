package com.hu.framework.util;

import com.hu.framework.annotation.Controller;
import com.hu.framework.annotation.RequestMapping;
import com.hu.framework.model.URLMapping;
import javassist.*;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import org.reflections.Reflections;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @ClassName ClassUtils
 * @Description 处理反射与注解相关信息的工具类
 * @Author us
 * @Date 2020/8/12 14:30
 * @Version 1.0
 **/
public class ClassUtil {

    public static Map<String, URLMapping> getURLMappings(String basePackage)
            throws IllegalAccessException, InstantiationException, NotFoundException {
        Reflections reflections = new Reflections(basePackage);
        // 获取标注了@Controller注解的类
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Controller.class);
        Map<String, URLMapping> mappings = new HashMap<>();
        for (Class<?> cls : classes) {
            String baseURI = "";
            // 检查类上是否有@RequestMapping注解
            RequestMapping baseMapping = cls.getDeclaredAnnotation(RequestMapping.class);
            // 如果类有有@RequestMapping
            if (baseMapping != null) {
                baseURI = baseURI + baseMapping.value();
            }
            // 通过反射创建该类的对象
            Object obj = cls.newInstance();
            // 获取类中所有的方法
            Method[] methods = cls.getDeclaredMethods();
            for (Method method : methods) {
                // 判断方法上是否有 @RequestMapping
                RequestMapping requestMapping
                        = method.getDeclaredAnnotation(RequestMapping.class);
                if (requestMapping != null) {
                    String requestURI = baseURI + requestMapping.value();
                    Map<String, Class<?>> parameter = new LinkedHashMap<>();
                    ClassPool pool = ClassPool.getDefault();
                    pool.insertClassPath(new ClassClassPath(cls));
                    CtMethod cm = pool.getMethod(cls.getName(), method.getName());
                    MethodInfo methodInfo = cm.getMethodInfo();
                    CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
                    // 获取所有的参数类型
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
                    if (attr != null) {
                        int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;
                        for (int i = 0; i < method.getParameterCount(); i++) {
                            String paramName = attr.variableName(i + pos);
                            Class<?> paramType = parameterTypes[i];
                            parameter.put(paramName,paramType);
                        }
                    }
                    mappings.put(requestURI, new URLMapping(obj, method, parameter));
                }
            }
        }
        return mappings;
    }
}
