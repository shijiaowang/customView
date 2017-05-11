package com.wangyang.divviewlibrary.view.neonlights;

import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by wangyang on 2017/5/11.
 * email:1440214507@qq.com
 * 每一行文字的信息
 */

public class LineTextMessage {
    private Rect rect;
    private String text;

    public LineTextMessage(Rect rect, String text) {
        this.rect = rect;
        this.text = text;
    }

    public Rect getRect() {

        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
