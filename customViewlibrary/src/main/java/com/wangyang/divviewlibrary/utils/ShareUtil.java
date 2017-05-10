package com.wangyang.divviewlibrary.utils;

/**
 * Created by wangyang on 2017/5/10.
 * email:1440214507@qq.com
 * 偏好工具类
 */


import android.content.Context;
import android.content.SharedPreferences;

public class ShareUtil {
    public static void putString(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(getShareName(), Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(key, value).apply();
    }

    private static String getShareName() {
        return "share_data";
    }

    public static int getInt(Context context, String key, int def) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(getShareName(), Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, def);
    }

    public static String getString(Context context, String key, String def) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(getShareName(), Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, def);
    }

    public static void putInt(Context context, String key, int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(getShareName(), Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(key, value).apply();
    }

    /**
     * 删除 share 保存的数据
     * @param context
     */
    public static void deleteData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(getShareName(), Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }
}