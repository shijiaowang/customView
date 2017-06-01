package com.wangyang.divviewlibrary.view.mapview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.wangyang.divviewlibrary.R;
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
    private float scale=1.3f;
    private PathItem selectItem;

    public PathMapView(Context context) {
        this(context,null);
    }

    public PathMapView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public PathMapView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        paint = new Paint();
        gestureDetector = new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onDown(MotionEvent e) {
                if (pathItems!=null){
                    for (PathItem pathItem : pathItems) {
                        if (pathItem.isTouch((int)(e.getX()/scale),(int)(e.getY()/scale))){
                            selectItem=pathItem;
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
        canvas.scale(scale,scale);
        if (pathItems!=null && isParseComplete){
            for (PathItem pathItem : pathItems) {
                pathItem.draw(canvas,paint,selectItem==pathItem);
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
                 long l = System.currentTimeMillis();
                 try {
                     DocumentBuilderFactory documentBuilderFactory=DocumentBuilderFactory.newInstance();
                     DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                     InputStream inputStream = getResources().openRawResource(R.raw.china);
                     Document document = documentBuilder.parse(inputStream);
                     Element documentElement = document.getDocumentElement();
                     NodeList path = documentElement.getElementsByTagName("path");
                     int length = path.getLength();
                     pathItems = new ArrayList<>(length);
                     PathItem pathItem;
                     for (int i=0;i<length;i++){
                         Element element = (Element) path.item(i);
                         String attribute = element.getAttribute("android:pathData");
                         pathItem= new PathItem(PathParser.createPathFromPathData(attribute));
                         pathItems.add(pathItem);
                     }
                     //绘制
                     isParseComplete=true;
                     postInvalidate();
                 } catch (Exception e) {
                     isParseComplete=false;
                     e.printStackTrace();
                 }
             }
         }).start();
    }
}
