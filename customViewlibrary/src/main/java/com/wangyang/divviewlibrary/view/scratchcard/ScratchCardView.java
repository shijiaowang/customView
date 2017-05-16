package com.wangyang.divviewlibrary.view.scratchcard;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.wangyang.divviewlibrary.R;
import com.wangyang.divviewlibrary.utils.StringUtils;

import java.lang.ref.SoftReference;

/**
 * Created by wangyang on 2017/5/16.
 * email:1440214507@qq.com
 * 刮刮卡效果
 */

public class ScratchCardView extends View {
    private SoftReference<Bitmap> softResult;//结果图片
    private SoftReference<Bitmap> softHover;//遮罩图片
    private String  resultText;//结果文字
    private int hoverColor;//遮罩颜色
    private int resultStringColor;
    private float resultStringSize;
    private Paint resultTextPaint;
    private Paint bitmapPaint;
    private Xfermode xfermode;
    private Path movePath;
    private float pathWidth;
    private float preX;
    private float preY;

    public ScratchCardView(Context context) {
        this(context,null);
    }

    public ScratchCardView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ScratchCardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //不关闭硬件加速会有问题
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        init(context,attrs);
        if (!StringUtils.isEmpty(resultText)){
            resultTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            resultTextPaint.setColor(resultStringColor);
            resultTextPaint.setTextSize(resultStringSize);
            resultTextPaint.setDither(true);
        }
        bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bitmapPaint.setDither(true);
        bitmapPaint.setColor(hoverColor);
        bitmapPaint.setStyle(Paint.Style.STROKE);
        bitmapPaint.setStrokeWidth(pathWidth);
        bitmapPaint.setStrokeCap(Paint.Cap.ROUND);
        xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT);
        movePath = new Path();
    }
    //初始化，拓展可设置参数供布局使用
    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ScratchCardView);
            int resultId = ta.getResourceId(R.styleable.ScratchCardView_resultBitmap,-1);
            int hoverId = ta.getResourceId(R.styleable.ScratchCardView_hoverBitmap,-1);
            hoverColor = ta.getColor(R.styleable.ScratchCardView_hoverBitmap, Color.parseColor("#e0e0e0"));
            resultStringColor = ta.getColor(R.styleable.ScratchCardView_resultStringColor, Color.RED);
            resultStringSize = ta.getFloat(R.styleable.ScratchCardView_resultStringSize,R.dimen.x12);
            pathWidth = ta.getFloat(R.styleable.ScratchCardView_pathWidth,20);
            resultText = ta.getString(R.styleable.ScratchCardView_resultString);
            Bitmap resultBitmap = getBitmap(resultId);
            if (resultBitmap!=null){
                softResult=new SoftReference<Bitmap>(resultBitmap);
            }
            Bitmap hoverBitmap = getBitmap(hoverId);
            if (hoverBitmap!=null){
                softHover=new SoftReference<Bitmap>(hoverBitmap);
            }
            ta.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (softNotNull(softResult)){
            //先绘制结果
            bitmapPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            canvas.drawBitmap(softResult.get(),0,0,bitmapPaint);
            if (!StringUtils.isEmpty(resultText)){//存在文字绘制文字
                float textWidth = resultTextPaint.measureText(resultText);
                Paint.FontMetrics fontMetrics = resultTextPaint.getFontMetrics();
                float textCenter = (fontMetrics.bottom-fontMetrics.top)/2;
                float x = (getWidth()-textWidth)/2;

                float y =getHeight()/2+textCenter-fontMetrics.bottom;
                canvas.drawText(resultText,x,y,resultTextPaint);
            }
            int saveCount = canvas.saveLayer(0, 0,getWidth(),getHeight(), bitmapPaint, Canvas.ALL_SAVE_FLAG);
            //绘制遮罩

            if (softNotNull(softHover)){
                canvas.drawBitmap(softHover.get(),0,0,bitmapPaint);
            }else {
                canvas.drawRect(0,0,getWidth(),getHeight(),bitmapPaint);
            }
            bitmapPaint.setXfermode(xfermode);
            bitmapPaint.setStyle(Paint.Style.STROKE);
            canvas.drawPath(movePath,bitmapPaint);
            bitmapPaint.setXfermode(null);
            canvas.restoreToCount(saveCount);

        }

    }

    private Bitmap getBitmap(int resultId) {
        return BitmapFactory.decodeResource(getResources(), resultId);
    }
    private boolean softNotNull(SoftReference<Bitmap> softReference){
        return softReference!=null && softReference.get()!=null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                preX = event.getX();
                preY = event.getY();
                movePath.moveTo(preX, preY);
               return  true;
            case MotionEvent.ACTION_MOVE:
                float endX = (preX+event.getX())/2;
                float endY = (preY+event.getY())/2;
                movePath.quadTo(preX,preY,endX,endY);
                preX = event.getX();
                preY =event.getY();
                break;
        }
        invalidate();
        return super.onTouchEvent(event);
    }
}
