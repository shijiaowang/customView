package com.wangyang.divviewlibrary.view.heartelectric;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.wangyang.divviewlibrary.R;
import com.wangyang.divviewlibrary.utils.BitmapUtils;
import com.wangyang.divviewlibrary.utils.CommonUtils;

import java.lang.ref.SoftReference;

/**
 * Created by wangyang on 2017/5/15.
 * email:1440214507@qq.com
 * 心电图view
 */

public class HeartElectricView extends View {

    private int resId;//心电图资源id
    private SoftReference<Bitmap> heartSoft;
    private int bitmapWidth;
    private int bitmapHeight;
    private Paint heartPaint;
    private Xfermode xfermode;
    private int heartBgColor;
    private int heartColor;
    private long heartTime;
    private float heartSpeed;
    private boolean isStart=true;//默认开启动画

    private float currentDistance = 0;

    public HeartElectricView(Context context) {
        this(context,null);
    }

    public HeartElectricView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public HeartElectricView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
        heartPaint = new Paint();
        heartPaint.setAntiAlias(true);
        heartPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        heartPaint.setColor(Color.WHITE);
        xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
        initBitmap();
    }

    private void initBitmap() {
        Bitmap heartBitmap = BitmapFactory.decodeResource(getResources(), resId);
        resetBitmap(heartBitmap);
    }

    private void resetBitmap(Bitmap heartBitmap) {
        if (heartBitmap!=null){
            if (softNotNull()){
                heartSoft.get().recycle();
            }
            bitmapWidth = heartBitmap.getWidth();
            bitmapHeight = heartBitmap.getHeight();
            heartSoft = new SoftReference<Bitmap>(heartBitmap);
        }
    }

    /**
     * 设置图片
     * @param resId
     */
    public void setResId(@DrawableRes int resId){
        this.resId=resId;
        initBitmap();
        requestLayout();
    }
    //初始化，拓展可设置参数供布局使用
    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.HeartElectricView);
            resId = ta.getResourceId(R.styleable.HeartElectricView_heartMap,-1);
            heartBgColor = ta.getColor(R.styleable.HeartElectricView_heartBgColor, Color.TRANSPARENT);
            heartColor = ta.getColor(R.styleable.HeartElectricView_heartColor, Color.GREEN);
            heartTime = ta.getInteger(R.styleable.HeartElectricView_heartTime, 20);
            heartSpeed = ta.getFloat(R.styleable.HeartElectricView_heartSpeed, 14);
            ta.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //不支持padding
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY && heightMode== MeasureSpec.EXACTLY){
            super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        }else if(softNotNull()){
            Bitmap bitmap = heartSoft.get();
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int mayWidth = widthMode == MeasureSpec.EXACTLY?widthSize: CommonUtils.getScreenWidthPixels(getContext());
            int mayHeight =heightMode== MeasureSpec.EXACTLY?heightSize:CommonUtils.getScreenHeightPixels(getContext());
            if (width>mayWidth || height>mayHeight){
                //如果有一个超过最大宽度，进行压缩
                float minScale = getMinScale(width, height, mayWidth, mayHeight);
                width= widthMode == MeasureSpec.EXACTLY?mayWidth:(int) (width* minScale);
                height=heightMode== MeasureSpec.EXACTLY?heightSize:(int) (height* minScale);
            }
            setMeasuredDimension(width,height);
        }else {
            super.onMeasure(0, 0);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (softNotNull()){
            canvas.drawColor(heartBgColor);
            int i = canvas.saveLayer(0, 0, getWidth(), getHeight(),heartPaint, Canvas.ALL_SAVE_FLAG);
            heartPaint.setColor(heartColor);
            canvas.drawRect(bitmapWidth-currentDistance,0,bitmapWidth,bitmapHeight,heartPaint);
            heartPaint.setXfermode(xfermode);
            canvas.drawBitmap(heartSoft.get(),0,0,heartPaint);
            canvas.restoreToCount(i);
            heartPaint.setXfermode(null);
            if (isStart){
                currentDistance=currentDistance>bitmapWidth?-1:currentDistance+heartSpeed;
                postInvalidateDelayed(heartTime);
            }
        }

    }
    private boolean softNotNull(){
        return heartSoft!=null && heartSoft.get()!=null;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (softNotNull()){
            bitmapWidth = heartSoft.get().getWidth();
            bitmapHeight = heartSoft.get().getHeight();
            if (bitmapWidth == h && bitmapHeight==w){
                return;
            }
            Bitmap bitmap = heartSoft.get();
            float minScale = getMinScale(bitmapWidth, bitmapHeight, w, h);
            Bitmap newBitmap = BitmapUtils.resizeImage(bitmap, bitmapWidth * minScale, bitmapHeight * minScale);
            resetBitmap(newBitmap);
        }
    }
    public void isStart(boolean isStart){
        if (this.isStart==isStart){
            return;
        }
        this.isStart=isStart;
        postInvalidate();
    }
    /*开始*/
    public void start(){
        isStart(true);
    }
    /*停止*/
    public void stop(){
        isStart(false);
    }
    private float getMinScale(int width, int height, int mayWidth, int mayHeight) {
        float widthScale=mayWidth*1.0f/width;
        float heightScale = mayHeight*1.0f/height;
        return Math.min(widthScale, heightScale);
    }

}
