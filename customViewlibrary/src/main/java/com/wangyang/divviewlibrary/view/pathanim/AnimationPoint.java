package com.wangyang.divviewlibrary.view.pathanim;

import android.graphics.PointF;



/**
 * Created by wangyang on 2017/5/10.
 * email:1440214507@qq.com
 * 途中经过的点
 */

public class AnimationPoint {
    public static final int MOVE_TO=1;//初始点
    public static final int LINE_TO=2;//移动到某个位置
    public static final int QUAD_TO=3;//二阶贝塞尔曲线
    public static final int CUBIC_TO=4;//三阶贝塞尔曲线
    private  int type;//类型
    private  PointF[] points;//存放的点集合
    //这是方便给估值器使用的值
    public float x;//目标x,
    public float y;//目标 y

    public int getType() {
        return type;
    }
    public PointF[] getPoints() {
        return points;
    }
    public AnimationPoint(){

    }
    private AnimationPoint(int type,PointF... points){
        if (points.length==0){
            throw  new IllegalArgumentException("points can,t empty");
        }
        this.type = type;
        this.points = points;
    }
    private AnimationPoint(int type, PointF points){
        this.type = type;
        this.points = new PointF[]{points};
    }
    //第一次设置为初始点，之后设置则是跳跃点。将看不到任何动画，但是会占用时间
    public static AnimationPoint moveTo( PointF pointF){
        if (pointF==null){
            throw  new IllegalArgumentException("pointF can,t empty");
        }
        return new AnimationPoint(MOVE_TO,pointF);
    }
    //直线移动到某处
    public static AnimationPoint lineTo(PointF pointF){
        if (pointF==null){
            throw  new IllegalArgumentException("pointF can,t empty");
        }
        return new AnimationPoint(LINE_TO,pointF);
    }
   //二阶贝塞尔曲线
    public static AnimationPoint quadTo(PointF pointF1,PointF pointF2){
        if (pointF1==null || pointF2==null){
            throw  new IllegalArgumentException("pointF1 or pointF2 can,t empty");
        }
        return new AnimationPoint(QUAD_TO,pointF1,pointF2);
    }
    //三阶贝塞尔曲线
    public static AnimationPoint cubicTo( PointF pointF1, PointF pointF2, PointF pointF3){
        if (pointF1==null || pointF2==null || pointF3==null){
            throw  new IllegalArgumentException("pointF1 or pointF2 or pointF3 can,t empty");
        }
        return new AnimationPoint(CUBIC_TO,pointF1,pointF2,pointF3);
    }
    public void setTarget(float x,float y){
        this.x=x;
        this.y=y;
    }
}
