package com.wangyang.divviewlibrary.view.progress;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.wangyang.divviewlibrary.R;

/**
 * Created by wangyang on 2017/5/10.
 * email:1440214507@qq.com
 * 自定义中间显示进度的圆形进度条
 */

public class CircleProgressView extends View {

    private int max;
    private int roundColor;
    private int roundProgressColor;
    private float roundWidth;
    private boolean textShow;
    private Paint textPaint;
    private boolean roundBgShow;
    private Paint roundPaint;
    private float center;
    private float roundRadius;
    private RectF rectF = new RectF();
    private int progress=0;
    private int preProgress=-1;

    public CircleProgressView(Context context) {
        this(context,null);
    }

    public CircleProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CircleProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CircleProgressView);
        max = typedArray.getInteger(R.styleable.CircleProgressView_maxProgress, 100);
        roundColor = typedArray.getColor(R.styleable.CircleProgressView_roundBackgroundColor, Color.RED);
        roundProgressColor = typedArray.getColor(R.styleable.CircleProgressView_roundProgressColor, Color.BLUE);
        int textColor = typedArray.getColor(R.styleable.CircleProgressView_progressTextColor, Color.parseColor("#646464"));
        float textSize = typedArray.getDimension(R.styleable.CircleProgressView_progressTextSize, getResources().getDimension(R.dimen.x12));
        roundWidth = typedArray.getDimension(R.styleable.CircleProgressView_roundWidth, 10);
        textShow = typedArray.getBoolean(R.styleable.CircleProgressView_textShow, true);
        roundBgShow = typedArray.getBoolean(R.styleable.CircleProgressView_roundBgShow, true);
        typedArray.recycle();

        textPaint = new Paint();
        textPaint.setTextSize(textSize);
        textPaint.setColor(textColor);
        textPaint.setDither(true);
        textPaint.setAntiAlias(true);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        roundPaint = new Paint();
        roundPaint.setStrokeWidth(roundWidth);
        roundPaint.setAntiAlias(true);
        roundPaint.setDither(true);
        roundPaint.setStyle(Paint.Style.STROKE);
        roundPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public void setMax(int max) {
        this.max = max;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //绘制圆环
        if (roundBgShow){
            roundPaint.setColor(roundColor);
            canvas.drawCircle(center, center,roundRadius,roundPaint);
        }
        if (textShow){
            String precent = (progress*1.0f/max *100)+"%";
            //计算出baseline 的位置
            Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
            float textCenter = (fontMetrics.bottom-fontMetrics.top)/2;
            //减去文字的一半
            float textRadius = textPaint.measureText(precent)/2;
            canvas.drawText(precent,center-textRadius,center+textCenter-fontMetrics.bottom,textPaint);
        }
        //绘制进度圆弧
        roundPaint.setColor(roundProgressColor);
        canvas.drawArc(rectF,0,(progress*1f/max) *360,false,roundPaint);
    }
    public void setProgress(int progress){
        if (progress==preProgress){
            return;
        }
        if (progress<0){
            throw  new IllegalArgumentException("progress shuold > 0");
        }
        if (progress>max){
            progress=max;
        }
        this.progress=progress;
        postInvalidate();
        preProgress = progress;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        center = Math.min(w/2,h/2);
        roundRadius = center - roundWidth/2;
        rectF.set(center-roundRadius,center-roundRadius,center+roundRadius,center+roundRadius);
    }
}
