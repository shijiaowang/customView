package com.wangyang.divviewlibrary.view.pathanim;

import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.Interpolator;

import com.wangyang.divviewlibrary.utils.LogUtils;

/**
 * Created by wangyang on 2017/5/10.
 * email:1440214507@qq.com
 * 动画路径管理执行类,如果需要设置复杂的ObjectAnimator 属性，可自行使用ObjectAnimator
 */

public class AnimationManager {
    //执行动画的view
    private View targetView;
    private AnimatorListenerAdapter animaListener;
    private ValueAnimator.AnimatorUpdateListener updaListener;

    public AnimationManager(View targetView){
        this.targetView = targetView;
    }
    public void setAnimaListener(AnimatorListenerAdapter animaListener){
        this.animaListener = animaListener;
    }
    public void setUpdaListener(ValueAnimator.AnimatorUpdateListener updaListener){

        this.updaListener = updaListener;
    }
    public void startAnimation(AnimationPath animationPath, int duration){
        startAnimation(animationPath,duration,null);
    }
    public void startAnimation(AnimationPath animationPath, int duration, Interpolator interpolator){
        if (animationPath==null){
            LogUtils.e("animationPath is null");
            return;
        }
        ObjectAnimator objectAnimator = ObjectAnimator.ofObject(this,"animChange",new AnimationPathEvaluator(),animationPath.getAnimationPoints().toArray());
        objectAnimator.setDuration(duration);
        if (animaListener!=null){
            objectAnimator.addListener(animaListener);
        }
        if (updaListener!=null){
            objectAnimator.addUpdateListener(updaListener);
        }
        if (interpolator!=null){
            objectAnimator.setInterpolator(interpolator);
        }
        objectAnimator.start();
    }
    //反射执行的动画
    public void setAnimChange(AnimationPoint animationPoint){
        if (targetView==null){
            LogUtils.e("targetView is null,anim not doing");
            return;
        }
        targetView.setTranslationX(animationPoint.x);
        targetView.setTranslationY(animationPoint.y);
    }

}
