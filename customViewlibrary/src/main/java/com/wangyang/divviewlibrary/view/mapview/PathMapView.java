package com.wangyang.divviewlibrary.view.mapview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.wangyang.divviewlibrary.R;
import com.wangyang.divviewlibrary.utils.LogUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by wangyang on 2017/6/1.
 * email:1440214507@qq.com
 * 地图view，path绘制地图，可交互，点击事件处理等等
 */

public class PathMapView extends View {
    private List<PathItem> pathItems;
    private Paint paint;
    private boolean isParseComplete;
    private GestureDetector gestureDetector;
    private float scale = 1.0f;
    private PathItem selectItem;
    private RectF rectF;

    public PathMapView(Context context) {
        this(context, null);
    }

    public PathMapView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PathMapView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        paint = new Paint();
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                if (pathItems != null) {
                    for (PathItem pathItem : pathItems) {
                        if (pathItem.isTouch((int) (e.getX() / scale), (int) (e.getY() / scale))) {
                            selectItem = pathItem;
                            invalidate();
                            break;
                        }
                    }
                }
                return true;
            }
        });
        initSvg();


    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.scale(scale, scale);
        if (pathItems != null && isParseComplete) {
            for (PathItem pathItem : pathItems) {
                pathItem.draw(canvas, paint, selectItem == pathItem);
            }
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    /**
     * 初始化svg资源
     */
    private void initSvg() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                    InputStream inputStream = getResources().openRawResource(R.raw.china);
                    Document document = documentBuilder.parse(inputStream);
                    Element documentElement = document.getDocumentElement();
                    final NodeList path = documentElement.getElementsByTagName("path");
                    int length = path.getLength();
                    pathItems = new ArrayList<>(length);
                    PathItem pathItem;
                    float left = -1;
                    float top = -1;
                    float right = -1;
                    float bottom = -1;
                    rectF = new RectF();
                    for (int i = 0; i < length; i++) {
                        Element element = (Element) path.item(i);
                        String attribute = element.getAttribute("android:pathData");
                        Path pathFromPathData = PathParser.createPathFromPathData(attribute);
                        if (pathFromPathData != null) {
                            pathItem = new PathItem(pathFromPathData);
                            pathItems.add(pathItem);
                            pathFromPathData.computeBounds(rectF, false);
                            left = left == -1 ? rectF.left : Math.min(left, rectF.left);
                            top = top == -1 ? rectF.top : Math.min(top, rectF.top);
                            right = right == -1 ? rectF.right : Math.max(right, rectF.right);
                            bottom = bottom == -1 ? rectF.bottom : Math.max(bottom, rectF.bottom);
                        }

                    }
                    rectF.left = left;
                    rectF.top = top;
                    rectF.right = right;
                    rectF.bottom = bottom;
                    //绘制
                    isParseComplete = true;
                    post(new Runnable() {
                        @Override
                        public void run() {
                            requestLayout();
                            invalidate();
                        }
                    });
                } catch (Exception e) {
                    isParseComplete = false;
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width;
        int height;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY && heightMode== MeasureSpec.EXACTLY){
           float widthScale = widthSize/rectF.width();
           float heightScale = heightSize/rectF.height();
            width=widthSize;
            height=heightSize;
            scale=Math.min(widthScale,heightScale);
        }else if (widthMode==MeasureSpec.EXACTLY){
            scale = widthSize/rectF.width();
            width=widthSize;
            height= (int) (rectF.height()*scale);
        }else if (heightMode==MeasureSpec.EXACTLY){
            scale = heightSize/rectF.height();
            height=heightSize;
            width= (int) (rectF.width()*scale);
        }else {
            height= (int) rectF.height();
            width= (int) rectF.width();
            scale=1.0f;
        }
        setMeasuredDimension(MeasureSpec.makeMeasureSpec(width,MeasureSpec.EXACTLY),MeasureSpec.makeMeasureSpec(height,MeasureSpec.EXACTLY));

    }

}
