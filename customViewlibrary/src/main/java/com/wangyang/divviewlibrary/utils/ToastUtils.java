package com.wangyang.divviewlibrary.utils;

/**
 * Created by wangyang on 2017/5/4.
 * email:1440214507@qq.com
 * 吐司
 */
import android.widget.Toast;
public class ToastUtils {
    //工具类不让实例化
    private ToastUtils() {

    }
    public static Toast mToast = null;

    public static void showToast(String text) {
        if (mToast == null) {
            mToast = Toast.makeText(CommonUtils.getContext(), text, Toast.LENGTH_SHORT);
        }
        mToast.setText(text);
        mToast.show();
    }

    public static void showToast(int res) {
        showToast(CommonUtils.getString(res));
    }

    /**
     * 根据网络展示不同的消息
     * @param text 有网时需要显示的消息
     * @param networkText 没有网络需要显示的消息
     */
    public static void showNetToast(String text,String networkText) {
        if (!NetworkUtils.isNetworkConnected()) {
            ToastUtils.showToast(networkText);
        } else {
            ToastUtils.showToast(text);
        }
    }

}