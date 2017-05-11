package com.wangyang.divviewlibrary.view.neonlights;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.util.AttributeSet;
import android.widget.TextView;

import com.wangyang.divviewlibrary.R;
import com.wangyang.divviewlibrary.utils.LogUtils;

/**
 * Created by wangyang on 2017/5/11.
 * email:1440214507@qq.com
 * 霓虹灯 textview，跑马灯
 */

@SuppressLint("AppCompatCustomView")
public class NeonLightsTextView extends TextView {
    //是否单行移动
    private boolean isSingleMove;
    //移动的速度
    private int moveSpeed;
    private int neonLightsNumber;
    private int neonLightsColor;
    private int[] colors;//颜色
    private LinearGradient linearGradient;
    private float textWidth;
    private float singleLineWidth;//单行宽度
    private LineTextMessage[] lineTextMessages;
    private float currentTranslate = 0;
    private Matrix translateMatrix;
    private float neonLigthsWidth;

    private int currentLine;
    private float lastWidth;
    private float rightMoveLimit;
    private boolean isReversal;
    private int reversalNumber=1;
    private String preText;


    public NeonLightsTextView(Context context) {
        this(context, null);
    }

    public NeonLightsTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NeonLightsTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NeonLightsTextView);
        isSingleMove = typedArray.getBoolean(R.styleable.NeonLightsTextView_isSingleMove, true);
        neonLightsColor = typedArray.getColor(R.styleable.NeonLightsTextView_neonLightsColor, Color.BLACK);
        moveSpeed = typedArray.getInteger(R.styleable.NeonLightsTextView_moveSpeed, 15);
        //需要跑马灯的文字个数
        neonLightsNumber = typedArray.getInteger(R.styleable.NeonLightsTextView_neonLightsNumber, 5);
        isReversal = typedArray.getBoolean(R.styleable.NeonLightsTextView_isReversal, true);
        typedArray.recycle();
        translateMatrix = new Matrix();

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        calculateShader();

    }

    /**
     * 重新计算位置，text变更
     */
    private void calculateShader() {
        String text = getText().toString();
        if (text.equals(preText)){
            return;
        }
        //总宽度
        textWidth = getPaint().measureText(text);
        Rect rect = new Rect();
        getLineBounds(0, rect);
        singleLineWidth = rect.width();

        //计算跑马灯的文字宽度
        neonLigthsWidth = textWidth / text.length() * neonLightsNumber;
        rightMoveLimit = singleLineWidth + neonLigthsWidth;
        int currentTextColor = getCurrentTextColor();
        //从最外面进入，可以看到第一个开始跑
        if (colors == null) {
            colors = new int[]{currentTextColor, neonLightsColor, currentTextColor};
        }
        //单行走马灯效果
        if (isSingleMove && getLineCount() > 1) {
            lineTextMessages = new LineTextMessage[getLineCount()];
            Layout layout = getLayout();
            for (int i = 0; i < getLineCount(); i++) {
                rect = new Rect();
                getLineBounds(i, rect);
                int lineStart = layout.getLineStart(i);
                int lineEnd = layout.getLineEnd(i);
                String lineText = text.substring(lineStart, lineEnd);
                if (i == getLineCount() - 1) {
                    lastWidth = getPaint().measureText(lineText);//最后一行的宽度
                }
                lineTextMessages[i] = new LineTextMessage(rect, lineText);
            }
        }
        changeShader();
        preText =text;
    }

    @Override
    public void setTextColor(@ColorInt int color) {
        super.setTextColor(color);
        if (colors != null) {
            colors[0] = colors[colors.length - 1] = color;
            changeShader();
        }
    }

    /**
     * 改变Shader
     */
    private void changeShader() {
        linearGradient = new LinearGradient(-neonLigthsWidth, 0, 0, 0, colors, null, Shader.TileMode.CLAMP);
        getPaint().setShader(linearGradient);
    }

    public void setColors(int[] colors) {
        this.colors = colors;
        changeShader();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (isSingleMove && lineTextMessages != null && lineTextMessages.length > 1) {
            getPaint().setShader(null);
            //canvas.save();
            //LineTextMessage lineTextMessage = lineTextMessages[currentLine];
            //canvas.clipRect(lineTextMessage.getRect());
            super.onDraw(canvas);
            //canvas.restore();
            LineTextMessage lineTextMessage = lineTextMessages[currentLine];
            Rect rect = lineTextMessage.getRect();
            getPaint().setShader(linearGradient);
            Paint.FontMetrics fontMetrics = getPaint().getFontMetrics();
            canvas.drawText(lineTextMessage.getText(), rect.left, rect.bottom - fontMetrics.bottom, getPaint());

        } else {
            super.onDraw(canvas);
        }
        moveTranslate();
        postInvalidateDelayed(50);
    }

    private void moveTranslate() {
        currentTranslate += moveSpeed;
        if (currentTranslate > rightMoveLimit || currentTranslate < 1) {
            if (isReversal) {//反转显示
                moveSpeed = -moveSpeed;
            } else {//不反转
                currentTranslate = 1;
            }
            if (isSingleMove) {//对最后一行进行特殊处理
                currentLine = isReversal ? getReversalLine() : (currentLine + 1) % lineTextMessages.length;
                rightMoveLimit = currentLine == lineTextMessages.length - 1 ? lastWidth + neonLigthsWidth : singleLineWidth + neonLigthsWidth;
                currentTranslate = currentLine == lineTextMessages.length - 1 ? getReversalTranslate() : currentTranslate;
            }
        }
        translateMatrix.setTranslate(currentTranslate, 0);
        linearGradient.setLocalMatrix(translateMatrix);
    }
   //如果不是反转模式，
    private float getReversalTranslate() {
        return isReversal?lastWidth + neonLigthsWidth:1;
    }

    /**
     * 获取反转走马灯 对应的行数
     * @return
     */
    public int getReversalLine() {
        if (currentLine==lineTextMessages.length-1 || (currentLine==0 && reversalNumber==-1)){
            reversalNumber=-reversalNumber;
        }
        return currentLine+reversalNumber;
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        if (colors!=null) {//防止 xml 设置的时候重复执行
            calculateShader();
        }
    }
}
