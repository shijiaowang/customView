package com.wangyang.divviewlibrary.view.radarview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.wangyang.divviewlibrary.R;

/**
 * Created by wangyang on 2017/5/15.
 * email:1440214507@qq.com
 * 雷达组件
 */

public class RadarView extends View{

    private boolean isScan = true;
    //设置默认宽高，雷达一般都是圆形，所以我们下面取宽高会去Math.min(宽,高)
    private final int DEFAULT_LENGTH = 200;
    //雷达的半径
    private float mRadarRadius = DEFAULT_LENGTH /2f;
    //雷达画笔
    private Paint mRadarPaint;
    //雷达底色画笔
    private Paint mRadarBg;
    //雷达圆圈的个数，默认4个
    private int mCircleNum = 4;
    //雷达线条的颜色，默认为白色
    private int mCircleColor = Color.WHITE;
    //雷达圆圈背景色
    private int mRadarBgColor = Color.BLACK;
    //paintShader
    private Shader mRadarShader;

    private Matrix matrix;
    //角度
    private float rotate;
    private float currentRoate;

    private int rotateTime;//多久旋转一次

    //雷达扫描时候的起始和终止颜色
    private int mStartColor = 0x0000ff00;

    private int mEndColor = 0xaa00ff00;
    public static  Handler mHandler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    public RadarView(Context context) {
        this(context,null);
    }

    public RadarView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RadarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
        mRadarShader = new SweepGradient(0,0,mStartColor,mEndColor);
        mRadarPaint = new Paint();
        mRadarPaint.setColor(mCircleColor);
        mRadarPaint.setStyle(Paint.Style.STROKE);
        mRadarPaint.setDither(true);
        mRadarPaint.setAntiAlias(true);
        mRadarBg = new Paint();
        mRadarBg.setColor(mRadarBgColor);
        mRadarBg.setDither(true);
        mRadarBg.setAntiAlias(true);
        mRadarBg.setStyle(Paint.Style.FILL_AND_STROKE);
        matrix= new Matrix();
    }
    //初始化，拓展可设置参数供布局使用
    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RadarView);
            mStartColor = ta.getColor(R.styleable.RadarView_startColor, mStartColor);
            mEndColor = ta.getColor(R.styleable.RadarView_endColor, mEndColor);
            mRadarBgColor = ta.getColor(R.styleable.RadarView_backgroundColor, mRadarBgColor);
            mCircleColor = ta.getColor(R.styleable.RadarView_lineColor, mCircleColor);
            mCircleNum = ta.getInteger(R.styleable.RadarView_circleNum, mCircleNum);
            rotateTime = ta.getInteger(R.styleable.RadarView_onceTime, 20);
            rotate = ta.getFloat(R.styleable.RadarView_scanRotate, 3.0f);
            isScan = ta.getBoolean(R.styleable.RadarView_isStartScan, false);
            ta.recycle();
        }
    }

   /*暂不支持padding*/
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width=0,height=0;
        if (widthMode == MeasureSpec.EXACTLY && heightMode ==MeasureSpec.EXACTLY){
            width=widthSize;
            height=heightSize;
        }else if (widthMode == MeasureSpec.EXACTLY || heightMode ==MeasureSpec.EXACTLY){
            width=height=widthMode == MeasureSpec.EXACTLY?widthSize:heightSize;
        }else {
            width= DEFAULT_LENGTH;
            height= DEFAULT_LENGTH;
        }
        setMeasuredDimension(width,height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRadarRadius = Math.min(w,h)/2f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mRadarBg.setShader(null);
        //先移动画布，圆心
        canvas.translate(mRadarRadius,mRadarRadius);
        //画背景
        canvas.drawCircle(0,0,mRadarRadius,mRadarBg);
        //画圆圈
        for (int i = 1; i <=mCircleNum; i++) {
            canvas.drawCircle(0,0,(float) (i * 1.0 / mCircleNum * mRadarRadius),mRadarPaint);
        }
        //画上十字相交线
        canvas.drawLine(0,-mRadarRadius,0,mRadarRadius,mRadarPaint);
        canvas.drawLine(-mRadarRadius,0,mRadarRadius,0,mRadarPaint);

        canvas.concat(matrix);
        mRadarBg.setShader(mRadarShader);
        canvas.drawCircle(0,0,mRadarRadius,mRadarBg);
        if (isScan) {
            postInvalidateDelayed(rotateTime);
            currentRoate+=rotate;
            matrix.setRotate(currentRoate,0,0);
        }
    }
    public void isScan(boolean isScan){
        if (this.isScan==isScan){
            return;
        }
        this.isScan=isScan;
        postInvalidate();
    }
    /*开始扫描*/
    public void startScan(){
        isScan(true);
    }
    /*停止扫描*/
    public void stopScan(){
        isScan(false);
    }
}
