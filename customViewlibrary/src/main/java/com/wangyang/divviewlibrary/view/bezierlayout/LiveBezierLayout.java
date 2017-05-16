package com.wangyang.divviewlibrary.view.bezierlayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Random;

/**
 * Created by wangyang on 2017/5/4.
 * email:1440214507@qq.com
 * 贝塞尔，直播送花 等等自定义view
 */

public class LiveBezierLayout extends RelativeLayout {
    Drawable[] drawables;
    Interpolator[] interpolators = new Interpolator[3];
    private LayoutParams layoutParams;
    Random random =new Random();
    private int viewHeight;
    private int viewWidth;
    private int defWidth;
    private int defHeight;
   private boolean useInterpolators=true;//是否使用差值器
    public LiveBezierLayout(Context context) {
        this(context,null);
    }

    public LiveBezierLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LiveBezierLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewHeight = getMeasuredHeight();
        viewWidth = getMeasuredWidth();

    }

    public Interpolator[] getInterpolators() {
        return interpolators;
    }

    public void setInterpolators(Interpolator[] interpolators) {
        this.interpolators = interpolators;
    }

    /**
     * 初始化
     * @param drawableIds 需要显示的图片id集合
     */
    public void init(int ...drawableIds) {
        if (drawableIds==null || drawableIds.length==0){
            throw  new IllegalArgumentException("params must have drawable ids");
        }
        drawables=new Drawable[drawableIds.length];
        for (int i = 0;i<drawableIds.length;i++){
            drawables[i]= getDrawable(drawableIds[i]);
        }
        interpolators[0]=new AccelerateInterpolator();
        interpolators[1]=new LinearInterpolator();
        interpolators[2]=new DecelerateInterpolator();
        defWidth = drawables[0].getIntrinsicWidth();
        defHeight = drawables[0].getIntrinsicHeight();
        layoutParams = new LayoutParams(defWidth, defHeight);
        layoutParams.addRule(CENTER_HORIZONTAL);
        layoutParams.addRule(ALIGN_PARENT_BOTTOM);
    }

    public boolean isUseInterpolators() {
        return useInterpolators && interpolators!=null && interpolators.length>0;
    }

    public void setUseInterpolators(boolean useInterpolators) {
        this.useInterpolators = useInterpolators;
    }

    private Drawable getDrawable(int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getResources().getDrawable(id,null);
        }else {
            return getResources().getDrawable(id);
        }
    }
    public void addView(){
        if (drawables==null || drawables.length==0){
            throw new NullPointerException("you must call init method before use addView and init method params length must > 0");
        }
        final ImageView imageView = new ImageView(getContext());
        imageView.setImageDrawable(drawables[random.nextInt(drawables.length)]);
        imageView.setLayoutParams(layoutParams);
        addView(imageView);
        //设置动画
        AnimatorSet animatorSet =getAnimator(imageView);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                //结束移除view
                removeView(imageView);
            }
        });
        animatorSet.start();
    }

    private AnimatorSet getAnimator(final ImageView imageView) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(imageView,"scaleX",0.4f,1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(imageView,"scaleY",0.4f,1.0f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(imageView,"alpha",0f,1.0f);
        AnimatorSet animatorSet =new AnimatorSet();
        animatorSet.playTogether(scaleX,scaleY,alpha);
        animatorSet.setDuration(300);
        //先后执行的动画集合
        AnimatorSet sequentiallySet =new AnimatorSet();
        //计算控制点
        PointF point0 =new PointF(viewWidth/2-defHeight/2,viewHeight-defHeight);
       PointF point1 = getPointF(viewHeight/2);
       PointF point2= getPointF(0);

        PointF point3 = new PointF();
        point3.y=0;
        point3.x=random.nextInt(viewWidth);
        BezierEvaluator bezierEvaluator = new BezierEvaluator(point1,point2);
        //设置 贝塞尔 动画
        ValueAnimator valueAnimator = ValueAnimator.ofObject(bezierEvaluator,point0,point3);
        if (isUseInterpolators()) {
            valueAnimator.setInterpolator(interpolators[random.nextInt(interpolators.length)]);
        }
        //监听动画
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                PointF animatedValue = (PointF) animation.getAnimatedValue();
                imageView.setX(animatedValue.x);
                imageView.setY(animatedValue.y);
                imageView.setAlpha(1-animation.getAnimatedFraction());
            }
        });
        valueAnimator.setDuration(3000);
        sequentiallySet.playSequentially(animatorSet,valueAnimator);
        return sequentiallySet;
    }
   public float getFloatNumber(int max){
       return random.nextInt(max);
   }
    private PointF getPointF(int addHeight) {
        PointF pointF =new PointF();
        pointF.x = random.nextInt(viewWidth/2);
        pointF.y = random.nextInt(viewHeight/2)+addHeight;
        return pointF;
    }
}
