package com.wangyang.divviewlibrary.view.waveview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import com.wangyang.divviewlibrary.R;
import com.wangyang.divviewlibrary.utils.LogUtils;

/**
 * Created by wangyang on 2017/5/28.
 * 水波纹view
 */

public class WaveView extends View {

    private int waveSpeed;
    private int waveHeight;
    private int waveLength;
    private int waveColor;
    private int height = 0;//增长的高度
    private boolean isDrawText;
    private int textColor;
    private float textSize;
    private Paint bgPaint;
    private Paint progressPaint;
    private Path wavePath;
    private int currentWidth;
    private int drawTime;
    private boolean isStop;

    public WaveView(Context context) {
        this(context,null);
    }

    public WaveView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public WaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(waveColor);
        bgPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        bgPaint.setDither(true);
        if (isDrawText){
            progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            progressPaint.setColor(textColor);
            progressPaint.setTextSize(textSize);
            progressPaint.setDither(true);
        }
        wavePath = new Path();
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.WaveView);
            waveSpeed = ta.getInteger(R.styleable.WaveView_waveSpeed, 20);
            waveHeight = ta.getInteger(R.styleable.WaveView_waveHeight, 60);
            waveLength = ta.getInteger(R.styleable.WaveView_waveLength, 300);//一个完整的波浪长度,波浪号
            waveColor = ta.getColor(R.styleable.WaveView_waveColor, Color.GREEN);
            isDrawText = ta.getBoolean(R.styleable.WaveView_isDrawProgress, false);
            textColor = ta.getColor(R.styleable.WaveView_waveProgressColor, Color.WHITE);
            textSize = ta.getColor(R.styleable.WaveView_waveTextSize, 24);
            drawTime = ta.getInteger(R.styleable.WaveView_drawTime, 60);
            currentWidth=-waveLength;
            ta.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        wavePath.reset();
        currentWidth+=waveSpeed;
        if (currentWidth>0){
            currentWidth=-waveLength;
        }

        wavePath.moveTo(currentWidth,getHeight()-height-waveHeight);
        for (int i = -waveLength;i<2*waveLength;i+=waveLength/2){
            wavePath.rQuadTo(waveLength/4,waveHeight,waveLength/2,0);
            wavePath.rQuadTo(waveLength/4,-waveHeight,waveLength/2,0);
        }
        wavePath.lineTo(getWidth(),getHeight());
        wavePath.lineTo(0,getHeight());
        wavePath.close();
        canvas.drawPath(wavePath,bgPaint);
        if (isDrawText){
            String progress = height/(getHeight()*1.0f)*100 +"%";
            float textWidth = progressPaint.measureText(progress);
            Paint.FontMetrics fontMetrics = progressPaint.getFontMetrics();
            float textCenter = (fontMetrics.bottom - fontMetrics.top)/2;
            canvas.drawText(progress,(getWidth()-textWidth)/2,(getHeight()-height)+height/2+textCenter-fontMetrics.bottom,progressPaint);
        }
        if (!isStop) {
            postInvalidateDelayed(drawTime);
        }


    }
    public void stopAnimation(){
        this.isStop = false;
    }
    public void startAnimation(){
        this.isStop = true;
        invalidate();
    }



    /**
     * 百分比
     * @param progress 1-100 =》0.1 -》1
     */
    public void setProgress(int progress){
        if (progress<0 || progress>100){
            LogUtils.e("Progress cannot > 100 or <0");
            return;
        }
        height = (int) (getHeight() * (progress/100f));
    }
}
