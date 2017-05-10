package com.wangyang.divviewlibrary.view.bezierlayout;

import android.animation.TypeEvaluator;
import android.graphics.PointF;

/**
 * Created by wangyang on 2017/5/4.
 * email:1440214507@qq.com
 * 贝塞尔曲线动画估值器
 */

public class BezierEvaluator implements TypeEvaluator<PointF> {
    private PointF point1;
    private PointF point2;

    public BezierEvaluator(PointF point1, PointF point2) {
        this.point1 = point1;
        this.point2 = point2;
    }
    @Override
    public PointF evaluate(float fraction, PointF startValue, PointF endValue) {
        PointF pointF = new PointF();
        float bezierFloat = 1 - fraction;
        //分别计算 x y 的坐标
        pointF.x= (float) (startValue.x*Math.pow(bezierFloat,3)+3*point1.x*fraction*Math.pow(bezierFloat,2)+
                        3*point2.x*Math.pow(fraction,2)*bezierFloat+endValue.x*Math.pow(fraction,3));
        pointF.y= (float) (startValue.y*Math.pow(bezierFloat,3)+3*point1.y*fraction*Math.pow(bezierFloat,2)+
                3*point2.y*Math.pow(fraction,2)*bezierFloat+endValue.y*Math.pow(fraction,3));
        return pointF;
    }

}
