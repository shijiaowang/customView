package com.wangyang.divviewlibrary.view.magnifier;

/**
 * Created by wangyang on 2017/5/12.
 * email:1440214507@qq.com
 * 放大镜view
 *  支持放大，缩小 支持 固定放大镜，支持移动放大镜 支持设置放大镜的位置，等比例缩放
 *  支持设置放大倍数，放大镜大小
 *  支持 设置view，bitmap,res资源为底图
 */

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.wangyang.divviewlibrary.R;
import com.wangyang.divviewlibrary.utils.BitmapUtils;
import com.wangyang.divviewlibrary.utils.CommonUtils;
import com.wangyang.divviewlibrary.utils.LogUtils;
import com.wangyang.divviewlibrary.utils.ViewUtils;

import java.lang.ref.SoftReference;

/**
 * Created by wangyang on 2017/5/12.
 * email:1440214507@qq.com
 * 放大镜view
 */
public class MagnifierView extends View {
    private final Path mPath = new Path();
    private final Matrix matrix = new Matrix();
    private SoftReference<Bitmap> softBitmap;
    // 放大镜的半径

    private int radius = 80;
    // 放大倍数

    private float factor = 2;
    private int mCurrentX, mCurrentY;
    private boolean isMove;
    private float scanle;


    public MagnifierView(Context context) {
        this(context,null);
    }

    public MagnifierView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MagnifierView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }


    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MagnifierView);
        isMove = typedArray.getBoolean(R.styleable.MagnifierView_magnifierCanMove, true);
        radius =typedArray.getInteger(R.styleable.MagnifierView_magnifierRadius,80);
        float paddingLeft = typedArray.getDimension(R.styleable.MagnifierView_magnifierPaddingLeft,0);
        float paddingTop = typedArray.getDimension(R.styleable.MagnifierView_magnifierPaddingTop,0);
        factor = typedArray.getFloat(R.styleable.MagnifierView_magnifierScale,2);
        typedArray.recycle();
        mPath.addCircle(radius+paddingLeft, radius+paddingTop, radius, Path.Direction.CW);
        matrix.setScale(factor, factor);
    }

    public void setImageRes(@DrawableRes int res){
       Bitmap bitmap = BitmapFactory.decodeResource(getResources(),res);
        if (bitmap==null){
            throw new Resources.NotFoundException("cannot find this Resources");
        }
        resetBitmap(bitmap);
        requestLayout();
    }

    private void resetBitmap(Bitmap bitmap) {
        if (softIsNotNull()) {
            softBitmap.get().recycle();
        }
        softBitmap=new SoftReference<Bitmap>(bitmap);
    }

    public void setBitmap(Bitmap bitmap){
        if (bitmap==null){
            LogUtils.e("this bitmap is null");
            return;
        }
        resetBitmap(bitmap);
        requestLayout();
    }
    public void setView(View view){
       // softBitmap=new SoftReference<Bitmap>(bitmap);
        Bitmap bitmap = ViewUtils.convertViewToBitmap(view);
        if (bitmap==null){
            LogUtils.e("get bitmap is fail");
            return;
        }
        resetBitmap(bitmap);
        requestLayout();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mCurrentX = (int) event.getX();
        mCurrentY = (int) event.getY();
        invalidate();
        return true;
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
        }else if(softIsNotNull()){
            Bitmap bitmap = softBitmap.get();
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int mayWidth = widthMode == MeasureSpec.EXACTLY?widthSize:CommonUtils.getScreenWidthPixels(getContext());
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

    private float getMinScale(int width, int height, int mayWidth, int mayHeight) {
        float widthScale=mayWidth*1.0f/width;
        float heightScale = mayHeight*1.0f/height;
        return Math.min(widthScale, heightScale);
    }

    private boolean softIsNotNull() {
        return null!=softBitmap && softBitmap.get()!=null;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 底图
       if (softIsNotNull()) {
          //不支持padding
           canvas.drawBitmap(softBitmap.get(), 0,0, null);
           //移动放大镜
           if (isMove) {
               canvas.translate(mCurrentX - radius, mCurrentY - radius);
           }
           // 剪切
           canvas.clipPath(mPath);
           // 移动canves到相应的位置，是反方向的
           canvas.translate(getTranslate(mCurrentX), getTranslate(mCurrentY));
           //画放大后的图
           canvas.drawBitmap(softBitmap.get(), matrix, null);
       }
    }

    private float getTranslate(int number) {
        return radius - number * factor;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
       if (softIsNotNull()){
            int bitWidth = softBitmap.get().getWidth();
            int bitHeight = softBitmap.get().getHeight();
            if (bitHeight == h && bitWidth==w){
                return;
            }
           Bitmap bitmap = softBitmap.get();
           float minScale = getMinScale(bitWidth, bitHeight, w, h);
           Bitmap newBitmap = BitmapUtils.resizeImage(bitmap, bitWidth * minScale, bitHeight * minScale);
           resetBitmap(newBitmap);
        }
    }
}