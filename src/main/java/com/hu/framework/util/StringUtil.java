package com.hu.framework.util;

public class StringUtil {

    /**
     * 判断字符串是否为空
     * @param str
     * @return
     */
    public static boolean isNotEmpty(String str) {
        if (str == null || "".equals(str)) {
            return false;
        }
        return true;
    }
}
