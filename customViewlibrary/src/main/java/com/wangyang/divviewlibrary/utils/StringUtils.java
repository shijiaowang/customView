package com.wangyang.divviewlibrary.utils;

/**
 * Created by wangyang on 2017/5/10.
 * email:1440214507@qq.com
 * 处理字符串的工具类
 */

public class StringUtils {
    private StringUtils() {


    }

    /**
     * 判断字符串是否有值，如果为null或者是空字符串或者只有空格或者为"null"字符串，则返回true，否则则返回false
     */
    public static boolean isEmpty(String value) {
        return !(value != null && !"".equalsIgnoreCase(value.trim())
                && !"null".equalsIgnoreCase(value.trim()));
    }
    /**
     * 判断字符串是否有值，如果为null或者是空字符串或者只有空格或者为"null"字符串，则返回true，否则则返回false
     */
    public static boolean isEmptyNotNull(String value) {
        return !(value != null && !"".equalsIgnoreCase(value.trim()));
    }
    public static boolean isEqual(String value1,String value2){
        if (value1==null && value2==null){
            return true;
        }else if (value1==null || value2==null){
            return false;
        } else if (value1.equals(value2)){
            return true;
        }
        return false;
    }
}