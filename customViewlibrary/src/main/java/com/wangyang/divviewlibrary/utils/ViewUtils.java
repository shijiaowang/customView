package com.wangyang.divviewlibrary.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

/**
 * Created by wangyang on 2017/5/12.
 * email:1440214507@qq.com
 * view转化为bitmap
 */

public class ViewUtils {
    /*view转化为bitmap*/
    public static Bitmap convertViewToBitmap(View view) {
        if (view==null || view.getWidth()<=0 || view.getHeight()<=0){
            throw  new IllegalArgumentException("the view is empty or width or height <0,suggest use this method while view is visible");
        }
        Bitmap bitmap= Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
}
