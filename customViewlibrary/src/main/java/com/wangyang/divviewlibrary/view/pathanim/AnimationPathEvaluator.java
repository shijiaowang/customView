package com.wangyang.divviewlibrary.view.pathanim;

import android.animation.TypeEvaluator;
import android.graphics.PointF;

import com.wangyang.divviewlibrary.utils.LogUtils;

/**
 * Created by wangyang on 2017/5/10.
 * email:1440214507@qq.com
 * 动画路径估值器
 */

public class AnimationPathEvaluator implements TypeEvaluator<AnimationPoint> {
    private Float x;
    private Float y;
    private AnimationPoint animationPoint =new AnimationPoint();

    @Override
    public AnimationPoint evaluate(float fraction, AnimationPoint startValue, AnimationPoint endValue) {
        int type = endValue.getType();
        //转换后的进度
        float t;
        PointF[] points;
        switch (type) {
            case AnimationPoint.MOVE_TO:
                points=endValue.getPoints();
                //瞬间移动到某处
                x=points[0].x;
                y=points[0].y;
                break;
            case AnimationPoint.LINE_TO:
                PointF pointF = endValue.getPoints()[0];
                PointF lineStartPointF = getStartPointF(startValue);
                x = lineStartPointF.x+(pointF.x-lineStartPointF.x) * fraction;
                y = lineStartPointF.y+(pointF.y-lineStartPointF.y) * fraction;
                break;
            case AnimationPoint.QUAD_TO:
               t = 1 - fraction;
                points = endValue.getPoints();
                PointF quadStartPoint1 = getStartPointF(startValue);
                PointF quadPointF2 = points[0];
                PointF quadPointF3 = points[1];
                x = (float)(quadStartPoint1.x*Math.pow(t,2)+2*fraction* t *quadPointF2.x+Math.pow(fraction,2)*quadPointF3.x);
                y = (float)(quadStartPoint1.y*Math.pow(t,2)+2*fraction* t *quadPointF2.y+Math.pow(fraction,2)*quadPointF3.y);
                break;
            case AnimationPoint.CUBIC_TO://三阶贝塞尔曲线动画计算
                t = 1 - fraction;
                points = endValue.getPoints();
                PointF pointF1 = getStartPointF(startValue);
                PointF pointF2 = points[0];
                PointF pointF3 = points[1];
                PointF pointF4 = points[2];
                x = (float) (pointF1.x * Math.pow(t, 3) + 3 * pointF2.x * fraction * Math.pow(t, 2) +
                        3 * pointF3.x * Math.pow(fraction, 2) * t + pointF4.x * Math.pow(fraction, 3));
                y = (float) (pointF1.y * Math.pow(t, 3) + 3 * pointF2.y * fraction * Math.pow(t, 2) +
                        3 * pointF3.y * Math.pow(fraction, 2) * t + pointF4.y * Math.pow(fraction, 3));
                break;
            default:
                LogUtils.e("no this animation type");
                break;
        }
        animationPoint.setTarget(x,y);
        return animationPoint;
    }

    /**
     * 其他情况都返回最后一个点为endView的第一个点
     *
     * @param startValue 开始
     * @return 开始点
     */
    private PointF getStartPointF(AnimationPoint startValue) {
        PointF pointF = new PointF();
        if (startValue == null) {
            pointF.x = 0;
            pointF.y = 0;
            return pointF;
        }
        PointF[] points = startValue.getPoints();
        return points[points.length - 1];
    }
}
