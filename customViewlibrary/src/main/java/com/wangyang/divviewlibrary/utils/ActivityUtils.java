package com.wangyang.divviewlibrary.utils;

/**
 * Created by wangyang on 2017/5/10.
 * email:1440214507@qq.com
 * 退出 activity工具类
 */


import android.app.Activity;

import java.util.LinkedList;
import java.util.List;


public class ActivityUtils {
    private List<Activity> mList;
    private static ActivityUtils instance;

    private ActivityUtils() {
    }

    public synchronized static ActivityUtils getInstance() {
        if (null == instance) {
            instance = new ActivityUtils();
        }
        return instance;
    }

    // add Activity
    public void addActivity(Activity activity) {
        if (mList == null) mList = new LinkedList<>();
        mList.add(activity);
    }

    public void exit() {
        if (mList == null || mList.size() == 0) return;
        try {
            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mList.clear();
    }

}