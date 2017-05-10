package com.wangyang.divviewlibrary.view.slidcursor;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.wangyang.divviewlibrary.R;


/**
 * Created by wangyang on 2017/5/10.
 * email:1440214507@qq.com
 * 索引view
 */
public class SlideCursorView extends View {
    private static char[] words = new char[28];

    static {
        words[0] = '*';
        words[27] = '#';
        char start = 'A';
        for (int i = 1; i <= 26; i++) {
            words[i] = start++;
        }
    }

    private int width;
    private int wordHeight;//一个单词的高度
    private Paint wordPaint;
    private Rect textMesure;
    private float preY = -1f;
    private Paint selectPaint;
    private char currentWord;
    private int selectBackgroundColor;
    private int normalBackgroundColor;

    public SlideCursorView(Context context) {
        this(context, null);
    }

    public SlideCursorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideCursorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        float dimension = getResources().getDimension(R.dimen.x12);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SlideCursorView);
        int normalTextColor = typedArray.getColor(R.styleable.SlideCursorView_normalTextColor, Color.DKGRAY);
        selectBackgroundColor = typedArray.getColor(R.styleable.SlideCursorView_selectBackgroundColor, Color.parseColor("#21969696"));
        normalBackgroundColor = typedArray.getColor(R.styleable.SlideCursorView_normalBackgroundColor, Color.TRANSPARENT);
        float normalTextSize = typedArray.getDimension(R.styleable.SlideCursorView_normaTextSize, dimension);
        int selectTextColor = typedArray.getColor(R.styleable.SlideCursorView_selectTextColor, Color.GREEN);
        float selectTextSize = typedArray.getDimension(R.styleable.SlideCursorView_selectTextSize, dimension);

        typedArray.recycle();

        wordPaint = new Paint();
        wordPaint.setAntiAlias(true);
        wordPaint.setTextSize(normalTextSize);
        wordPaint.setColor(normalTextColor);
        selectPaint = new Paint();
        selectPaint.setAntiAlias(true);
        selectPaint.setTextSize(selectTextSize);
        selectPaint.setColor(selectTextColor);
        textMesure = new Rect();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < words.length; i++) {
            String currentWord = String.valueOf(words[i]);
            wordPaint.getTextBounds(currentWord, 0, currentWord.length(), textMesure);
            int textWidth = textMesure.width();
            int textHeight = textMesure.height();
            float x = (width - textWidth) / 2f;
            float y = (i + 1) * wordHeight - (wordHeight - textHeight) / 2f;
            if (currentWord.equals(String.valueOf(this.currentWord))) {
                canvas.drawText(currentWord, x, y, selectPaint);
            } else {
                canvas.drawText(currentWord, x, y, wordPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setBackgroundColor(selectBackgroundColor);
            case MotionEvent.ACTION_MOVE:
                float y = event.getY();
                currentWord = words[floatToInt(y)];
                if ((wordSelectChangeListener != null && canUseListener(y)) || (wordSelectChangeListener != null && preY == -1f)) {
                    if (floatToInt(y) < words.length && floatToInt(y) > 0) {
                        wordSelectChangeListener.wordChange(currentWord);
                    }
                }
                preY = y;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                setBackgroundColor(normalBackgroundColor);
                if (wordSelectChangeListener != null) {
                    wordSelectChangeListener.wordCancel();
                }
                currentWord = 0;
                break;

        }
        invalidate();
        return true;
    }

    /**
     * 是否触发listener
     *
     * @param y
     */
    private boolean canUseListener(float y) {
        if (floatToInt(preY) == floatToInt(y)) {
            return false;
        }
        return true;
    }

    private int floatToInt(float y) {
        return (int) (y / wordHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        wordHeight = h / words.length;
    }

    private WordSelectChangeListener wordSelectChangeListener;


    public interface WordSelectChangeListener {
        void wordChange(char word);

        void wordCancel();
    }

    public void setWordSelectChangeListener(WordSelectChangeListener wordSelectChangeListener) {
        this.wordSelectChangeListener = wordSelectChangeListener;
    }
}