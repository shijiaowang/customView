package com.wangyang.divviewlibrary.utils;

/**
 * Created by wangyang on 2017/5/10.
 * email:1440214507@qq.com
 * 关闭流
 */


import java.io.Closeable;
import java.io.IOException;

public class IOUtils {
    /**
     * 关闭流
     */
    //此Closeable是所有可关闭类的父类，所以可关闭任何可关闭的
    public static boolean close(Closeable io) {
        if (io != null) {
            try {
                io.close();
            } catch (IOException e) {
                LogUtils.e(e);
            }
        }
        return true;
    }
}