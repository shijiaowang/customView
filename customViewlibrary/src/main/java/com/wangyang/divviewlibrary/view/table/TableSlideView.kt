package com.wangyang.divviewlibrary.view.table

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.ViewGroup
import com.wangyang.divviewlibrary.R

/**
 * Created by .wangyang on 2017/6/11
 * email：1440214507@qq.com
 * 可上下左右滑动的tableview
 */

class TableSlideView @JvmOverloads constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0) : ViewGroup(context, attrs, defStyleAttr) {
    constructor(context: Context) : this(context, null)

    var firstRowColor: Int
    lateinit var widths: IntArray//宽度集合
    lateinit var heights: IntArray//高度集合

    var adapter: IBaseTableAdapter? = null
        set(value) {
            field = value
            widths = IntArray(value!!.rowCount)
            heights = IntArray(value.columnCount)
        }

    init {
        setBackgroundColor(Color.YELLOW)
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.TableSlideView, defStyleAttr, 0)
        firstRowColor = attributes.getColor(R.styleable.TableSlideView_firstRowColor, Color.YELLOW)
        attributes.recycle()
        setBackgroundColor(firstRowColor)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (null == adapter) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        } else {
            val widthMode = MeasureSpec.getMode(widthMeasureSpec)
            val widthSize = MeasureSpec.getSize(widthMeasureSpec)
            val heightMode = MeasureSpec.getMode(heightMeasureSpec)
            val heightSize = MeasureSpec.getSize(heightMeasureSpec)
            var width: Int = 0
            var height: Int = 0
            val newAdapter = adapter!!
            val columnCount = newAdapter.columnCount
            val rowCount = newAdapter.rowCount
            //缓存每一行 和 每一列的高度->列数 和 行度都是从 0 开始
            for (i in 0..rowCount - 1) {
                widths[i] = newAdapter.getWidth(i)
            }
            for (i in 0..columnCount - 1) {
                heights[i] = newAdapter.getHeight(i)
            }
            when (widthMode) {
                MeasureSpec.AT_MOST -> width = Math.min(widthSize, sumOfArray(widths))
                MeasureSpec.EXACTLY -> {
                    width = widthSize
                    //如果数据宽度没有达到容器宽度,暂时不做处理
                }
                MeasureSpec.UNSPECIFIED->width=sumOfArray(widths)
            }
            when(heightMode){
                MeasureSpec.AT_MOST -> height = Math.min(heightSize, sumOfArray(heights))
                MeasureSpec.EXACTLY -> {
                    height = heightSize
                    //如果数据高度没有达到容器宽度,暂时不做处理
                }
                MeasureSpec.UNSPECIFIED->height=sumOfArray(heights)
            }
            setMeasuredDimension(width,height)

        }


    }

    /**
     * 计算 一个数组的总数
     */
    fun sumOfArray(widths: IntArray): Int {
        var sum = 0
        widths.forEach {
            sum += it
        }
        return sum
    }
}
