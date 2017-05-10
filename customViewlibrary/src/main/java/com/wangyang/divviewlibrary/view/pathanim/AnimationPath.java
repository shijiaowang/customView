package com.wangyang.divviewlibrary.view.pathanim;
import android.graphics.PointF;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangyang on 2017/5/10.
 * email:1440214507@qq.com
 * 自定义动画路径类
 */

public class AnimationPath {
    private List<AnimationPoint> animationPoints;

    public List<AnimationPoint> getAnimationPoints() {
        return animationPoints;
    }

    public void setAnimationPoints(List<AnimationPoint> animationPoints) {
        this.animationPoints = animationPoints;
    }

    public static class Builder{
        private List<AnimationPoint> points=new ArrayList<>();
        public Builder moveTo(float x0,float y0){
            points.add(AnimationPoint.moveTo(new PointF(x0,y0)));
            return this;
        }
        public Builder lineTo(float x0,float y0){
            points.add(AnimationPoint.lineTo(new PointF(x0,y0)));
            return this;
        }
        public Builder quadTo(float x0,float y0,float x1,float y1){
            points.add(AnimationPoint.quadTo(new PointF(x0,y0),new PointF(x1,y1)));
            return this;
        }
        public Builder cubicTo(float x0,float y0,float x1,float y1,float x2,float y2){
            points.add(AnimationPoint.cubicTo(new PointF(x0,y0),new PointF(x1,y1),new PointF(x2,y2)));
            return this;
        }
        public AnimationPath build(){
            AnimationPath animationPath = new AnimationPath();
            animationPath.setAnimationPoints(points);
            return animationPath;
        }
    }
}
