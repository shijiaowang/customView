package com.wangyang.divviewlibrary.view.mapview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;

/**
 * Created by wangyang on 2017/6/1.
 * email:1440214507@qq.com
 * 交互处理类，判断 是否触摸在当前位置，绘制颜色，绘制路径静等
 */

public class PathItem {
    private Path path;
    private boolean isChecked;
    private Region region;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public PathItem(Path path) {
        this.path = path;
    }

    /**
     * 是否触摸当前位置
     * @param x
     * @param y
     * @return
     */
    public boolean isTouch(int x,int y){
        if (region==null) {
            RectF rectF = new RectF();
            path.computeBounds(rectF, false);
            region = new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
            region.setPath(path,region);
        }
        return region.contains(x,y);
    }

    /**
     * 绘制图形
     * @param canvas
     * @param isChecked 是否是点击的
     */
    public void draw(Canvas canvas, Paint paint, boolean isChecked){
        paint.reset();
        if (isChecked){
            paint.setColor(Color.RED);
        }else {
            paint.setColor(Color.YELLOW);
        }
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(path, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);
        canvas.drawPath(path, paint);
    }
}
