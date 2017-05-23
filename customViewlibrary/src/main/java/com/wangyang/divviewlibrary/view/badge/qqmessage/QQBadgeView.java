package com.wangyang.divviewlibrary.view.badge.qqmessage;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

import com.wangyang.divviewlibrary.R;
import com.wangyang.divviewlibrary.utils.LogUtils;

/**
 * Created by wangyang on 2017/5/23.
 * email:1440214507@qq.com
 * qq消息小红点
 * 使用方式：同一个地方放一个占位textview，点击，然后添加这个view在window
 */

public class QQBadgeView extends View {

    private int bgColor;
    private int textColor;
    private int normalRadius;
    private int normalRadiusCopy;
    private int dragRadius;
    private int dragLimit;
    private Path path;
    private  PointF normalPoint=null;
    private PointF dragPoint;
    private PointF controlBezier = new PointF();//中间的控制点
    //四个点，两队起始点和终止点
    private PointF normalTop = new PointF();
    private PointF normalBottom = new PointF();
    private PointF dragTop = new PointF();
    private PointF dragBottom = new PointF();
    QQBadgeDismissInterface qqBadgeDismissInterface;
    private int currentIndex = 0;
    private int dismissCount;
    private int dismissDuration;

    public void setQqBadgeDismissInterface(QQBadgeDismissInterface qqBadgeDismissInterface) {
        this.qqBadgeDismissInterface = qqBadgeDismissInterface;
    }

    private float badgeTextSize;

    private int number;
    private Paint paint;
    private int backDuartion;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
        postInvalidate();
    }

    private QQBadgeState state = QQBadgeState.NORMAL;

    public QQBadgeView(Context context) {
        this(context, null);
    }

    public QQBadgeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QQBadgeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setDither(true);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setTextSize(badgeTextSize);
        path = new Path();
        number = 990;
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.QQBadgeView);
            bgColor = ta.getColor(R.styleable.QQBadgeView_badgeBackgroundColor, Color.RED);
            textColor = ta.getColor(R.styleable.QQBadgeView_badgeTextColor, Color.WHITE);
            normalRadius = ta.getInteger(R.styleable.QQBadgeView_badgeNormalRadius, 20);
            normalRadiusCopy=normalRadius;
            badgeTextSize = ta.getFloat(R.styleable.QQBadgeView_badgeTextSize, 24);
            dragRadius = ta.getInteger(R.styleable.QQBadgeView_badgeNormalRadius, 30);
            dragLimit = ta.getInteger(R.styleable.QQBadgeView_badgeNormalRadius, 300);
            backDuartion = ta.getInteger(R.styleable.QQBadgeView_badgeDragBackAnimDuration, 500);
            dismissCount = ta.getInteger(R.styleable.QQBadgeView_badgeDragDismissCount, 5);
            dismissDuration = ta.getInteger(R.styleable.QQBadgeView_badgeDragDismissAnimDuration, 200);
            ta.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        path.reset();
        if (number <= 0) return;
        paint.setColor(bgColor);
        //画中间的圆
        if (state == QQBadgeState.NORMAL) {
            drawDragCircle(canvas);
        } else if (state == QQBadgeState.DRAGGING) {
            path.moveTo(normalTop.x, normalTop.y);
            path.quadTo(controlBezier.x, controlBezier.y, dragTop.x, dragTop.y);
            path.lineTo(dragBottom.x, dragBottom.y);
            path.quadTo(controlBezier.x, controlBezier.y, normalBottom.x, normalBottom.y);
            path.close();
            canvas.drawPath(path, paint);
            canvas.drawCircle(normalPoint.x, normalPoint.y, normalRadius, paint);
            drawDragCircle(canvas);
        }else if (state==QQBadgeState.DISCONNECT){
            drawDragCircle(canvas);
        }else if (state==QQBadgeState.DISMISS){
            if (qqBadgeDismissInterface!=null){
                qqBadgeDismissInterface.onDrawDismiss(currentIndex,canvas,dragPoint);
            }
        }

    }

    private void drawDragCircle(Canvas canvas) {
        canvas.drawCircle(dragPoint.x, dragPoint.y, dragRadius, paint);
        String text = String.valueOf(number > 99 ? "99+" : number < 0 ? 0 : number);
        paint.setColor(textColor);
        float textWidth = paint.measureText(text);
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float textCenter = (fontMetrics.bottom - fontMetrics.top) / 2;
        canvas.drawText(text, dragPoint.x - textWidth / 2, dragPoint.y + textCenter - fontMetrics.bottom, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                float x = event.getX();
                float y = event.getY();
                if (x > normalPoint.x - dragRadius && x < normalPoint.x + dragRadius && y > normalPoint.y - dragRadius && y < normalPoint.y + dragRadius) {
                    dragPoint.x = x;
                    dragPoint.y = y;
                    state = QQBadgeState.DRAGGING;
                    return true;
                }
                return false;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();
                float moveY = event.getY();
                dragPoint.x = moveX;
                dragPoint.y = moveY;
               if (state==QQBadgeState.DRAGGING) {
                   float distanceX = dragPoint.x - normalPoint.x;
                   float distanceY = dragPoint.y - normalPoint.y;
                   float skew = (float) Math.hypot(distanceX,distanceY);//求的斜边
                   calculationCirclePoint(distanceX,distanceY,skew);

                   if (skew>dragLimit){
                       state=QQBadgeState.DISCONNECT;
                   }
               }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (state==QQBadgeState.DRAGGING){//拖拽需要回归位置
                    startBackAnim();
                }else if (state==QQBadgeState.DISCONNECT){
                   startDismissAnim();

                } else {
                    reset();
                }
                break;
        }
        invalidate();
        return super.onTouchEvent(event);
    }

    /**
     * 开启消失动画
     */
    private void startDismissAnim() {
        state = QQBadgeState.DISMISS;
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0,dismissCount);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setDuration(dismissDuration);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentIndex= (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                reset();
                setVisibility(GONE);
            }
        });
        valueAnimator.start();
    }

    //计算重点控制点
    private void calculationControl(float distanceX, float distanceY, float skew) {
        controlBezier.x = (distanceX) / 2 + normalPoint.x;
        controlBezier.y = (distanceY) / 2 + normalPoint.y;
        if (skew>dragLimit/8) {//到达一定距离中心圆开始变小
            float scale = skew / dragLimit;
            if (scale < 0.6) {
                normalRadius = (int) (normalRadiusCopy*(1 - scale));//中心圆逐渐变小最低0.6
            }
        }
    }

    //恢复动画
    private void startBackAnim() {
        ValueAnimator valueAnimator = ValueAnimator.ofObject(new PointFEvaluator(),dragPoint,normalPoint);
        valueAnimator.setDuration(backDuartion);
        valueAnimator.setInterpolator(new OvershootInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                dragPoint = (PointF) animation.getAnimatedValue();
                float distanceX = dragPoint.x - normalPoint.x;
                float distanceY = dragPoint.y - normalPoint.y;
                float skew = (float) Math.hypot(distanceX,distanceY);//求的斜边
                calculationCirclePoint(distanceX,distanceY,skew);
                invalidate();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                reset();
            }
        });
        valueAnimator.start();
    }

    private void reset() {
        state = QQBadgeState.NORMAL;
        dragPoint.x = normalPoint.x;
        dragPoint.y = normalPoint.y;
        normalRadius=normalRadiusCopy;
    }

    //计算斜切点
    private void calculationCirclePoint(float distanceX, float distanceY, float skew) {

        float cosTheta = distanceX / skew;
        float sinTheta = distanceY / skew;
        //计算四个点的位置
        normalTop.x = normalPoint.x - sinTheta*normalRadius;
        normalTop.y = normalPoint.y + cosTheta*normalRadius;
        normalBottom.x = normalPoint.x + sinTheta*normalRadius;
        normalBottom.y = normalPoint.y - cosTheta*normalRadius;
        dragTop.x = dragPoint.x - sinTheta*dragRadius;
        dragTop.y = dragPoint.y + cosTheta*dragRadius;
        dragBottom.x = dragPoint.x + sinTheta*dragRadius;
        dragBottom.y = dragPoint.y - cosTheta*dragRadius;
        calculationControl(distanceX, distanceY, skew);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        LogUtils.e("onSizeChanged"+w +"-"+h);
        initPoint(w, h);
    }

    private void initPoint(int w, int h) {
        if (normalPoint == null) {
            normalPoint = new PointF(w / 2, h / 2);
        }
        if (dragPoint == null) {
            dragPoint = new PointF(w / 2, h / 2);
        }
    }
    public class PointFEvaluator implements TypeEvaluator<PointF> {

        @Override
        public PointF evaluate(float fraction, PointF startValue, PointF endValue) {
            float x = startValue.x + (fraction * (endValue.x - startValue.x));
            float y = startValue.y + (fraction * (endValue.y - startValue.y));
            return new PointF(x, y);
        }
    }
}
