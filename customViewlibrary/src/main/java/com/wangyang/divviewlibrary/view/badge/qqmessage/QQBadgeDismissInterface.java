package com.wangyang.divviewlibrary.view.badge.qqmessage;

import android.graphics.Canvas;
import android.graphics.PointF;

/**
 * Created by wangyang on 2017/5/23.
 * email:1440214507@qq.com
 * 消失接口
 */

public interface QQBadgeDismissInterface {
    /**
     *
     * @param currentIndex 第几次执行消失动画
     * @param canvas  画布
     * @param dismissPoint 消失的具体位置
     */
     void onDrawDismiss(int currentIndex, Canvas canvas, PointF dismissPoint);
}
