package com.wangyang.divviewlibrary.view.table

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.Scroller
import com.wangyang.divviewlibrary.R

/**
 * Created by .wangyang on 2017/6/11
 * email：1440214507@qq.com
 * 可上下左右滑动的tableview
 */

class TableSlideView @JvmOverloads constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0) : ViewGroup(context, attrs, defStyleAttr) {
    constructor(context: Context) : this(context, null)

    var firstRowColor: Int
    private lateinit var widths: IntArray//宽度集合
    private lateinit var heights: IntArray//高度集合
    private var needLayout: Boolean = false //是否需要重绘制
    private lateinit var headerView: View//第一行第一列的view
    private var firstRowViews: List<View> = emptyList() //第一行可见view
    private var firstColumnViews: List<View> = emptyList() //第一列可见view
    private var bodyViews: List<List<View>> = emptyList() //其他可见view
    private  var minimumVelocity:Int=-1 //最小速度
    private  var maximumVelocity:Int=-1 //最大速度
    private  var touchSlop:Int=-1 //最小移动单位
    private lateinit var  velocityTracker:VelocityTracker//速度相关
    private lateinit var flinger:Flinger

    var adapter: IBaseTableAdapter? = null
        set(value) {
            field = value
            widths = IntArray(value!!.rowCount)
            heights = IntArray(value.columnCount)
            needLayout = true
        }

    init {
        this.flinger = Flinger(context)
        val configuration = ViewConfiguration.get(context)
        this.touchSlop = configuration.scaledTouchSlop
        this.minimumVelocity = configuration.scaledMinimumFlingVelocity
        this.maximumVelocity = configuration.scaledMaximumFlingVelocity
        setWillNotDraw(false)
        setBackgroundColor(Color.YELLOW)
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.TableSlideView, defStyleAttr, 0)
        firstRowColor = attributes.getColor(R.styleable.TableSlideView_firstRowColor, Color.YELLOW)
        attributes.recycle()
        setBackgroundColor(firstRowColor)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (needLayout || changed) {

        }
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
                MeasureSpec.UNSPECIFIED -> width = sumOfArray(widths)
            }
            when (heightMode) {
                MeasureSpec.AT_MOST -> height = Math.min(heightSize, sumOfArray(heights))
                MeasureSpec.EXACTLY -> {
                    height = heightSize
                    //如果数据高度没有达到容器宽度,暂时不做处理
                }
                MeasureSpec.UNSPECIFIED -> height = sumOfArray(heights)
            }
            setMeasuredDimension(width, height)

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
    //惯性滑动
    internal inner class Flinger (context: Context):Runnable{
        private val scroller: Scroller = Scroller(context)
        private var lastX = 0
        private var lastY = 0
        //开始惯性滑动
        internal fun start(initX: Int, initY: Int, initialVelocityX: Int, initialVelocityY: Int, maxX: Int, maxY: Int) {
            scroller.fling(initX, initY, initialVelocityX, initialVelocityY, 0, maxX,
                    0, maxY)

            lastX = initX
            lastY = initY
            post(this)
        }
        override fun run() {
            if (scroller.isFinished) {
                return
            }

            val more = scroller.computeScrollOffset()
            val x = scroller.currX
            val y = scroller.currY
            val diffX = lastX - x
            val diffY = lastY - y
            if (diffX != 0 || diffY != 0) {
                scrollBy(diffX, diffY)
                lastX = x
                lastY = y
            }

            if (more) {
                post(this)
            }
        }
        internal val isFinshed:Boolean
             get() = scroller.isFinished
        internal fun forceFinished() {
            if (!scroller.isFinished) {
                scroller.forceFinished(true)
            }
        }


    }
}
