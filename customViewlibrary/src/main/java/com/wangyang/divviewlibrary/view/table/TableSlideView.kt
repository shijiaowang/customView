package com.wangyang.divviewlibrary.view.table

import android.content.Context
import android.util.AttributeSet
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.Scroller

/**
 * Created by .wangyang on 2017/6/11
 * email：1440214507@qq.com
 * 可上下左右滑动的tableview
 */

class TableSlideView @JvmOverloads constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0) : ViewGroup(context, attrs, defStyleAttr) {
    constructor(context: Context) : this(context, null)

    private lateinit var widths: IntArray//宽度集合
    private lateinit var heights: IntArray//高度集合
    private var needLayout: Boolean = false //是否需要重绘制
    private var headerView: View? = null//第一行第一列的view
    private var firstRowViews: ArrayList<View> = ArrayList(1)//第一行可见view
    private var firstColumnViews: ArrayList<View> = ArrayList() //第一列可见view
    private var bodyViews: ArrayList<ArrayList<View>> = ArrayList()//其他可见view
    private var minimumVelocity: Int = -1 //最小速度
    private var maximumVelocity: Int = -1 //最大速度
    private var touchSlop: Int = -1 //最小移动单位
    private lateinit var velocityTracker: VelocityTracker//速度相关
    private lateinit var recycler: Recycler//回收池
    private var flinger: Flinger
    private var viewWidth: Int = 0// view的宽度
    private var viewHeight: Int = 0// view的高度
    private var slideX: Int = 0// 水平滑动的距离
    private var slideY: Int = 0// 垂直滑动的距离
    private var rowCount: Int = 0//行数
    private var columnCount: Int = 0//列数

    var adapter: IBaseTableAdapter? = null
        set(value) {
            field = value
            slideX =0
            slideY =0
            widths = IntArray(value!!.rowCount)
            heights = IntArray(value.columnCount)
            needLayout = true
            rowCount=value.rowCount
            columnCount=value.columnCount
        }

    init {
        this.flinger = Flinger(context)
        val configuration = ViewConfiguration.get(context)
        this.touchSlop = configuration.scaledTouchSlop
        this.minimumVelocity = configuration.scaledMinimumFlingVelocity
        this.maximumVelocity = configuration.scaledMaximumFlingVelocity
        needLayout = true
        setWillNotDraw(false)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (needLayout || changed) {
            needLayout = false
            resetTableView()
            if (adapter != null) {
                viewWidth = r - l
                viewHeight = b - t
                //绘制第一个view
                var left=0
                var top=0
                var right=0
                var bottom=0
                headerView=makeView(0,0,left,top,widths[0],heights[0])
                left=widths[0] - slideX
                //摆放第一行
                run {
                    var i=0
                    //测绘可见个数，不可见的不做处理
                    while (i<columnCount && left<viewWidth){
                        i++
                        right=left+widths[i]
                        val rowView = makeView(0, i, left, 0, right, heights[0])
                        firstRowViews.add(rowView)
                        left=right
                    }
                }
                left=0
                //摆放第一列


            }
        }
    }

    private fun  makeView(row: Int, column: Int, left: Int, top: Int, right: Int, bottom: Int): View {

    }

    //重置
    private fun resetTableView() {
        headerView = null
        firstRowViews.clear()
        firstColumnViews.clear()
        bodyViews.clear()
        removeAllViews()
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
    internal inner class Flinger(context: Context) : Runnable {
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

        internal val isFinshed: Boolean
            get() = scroller.isFinished

        internal fun forceFinished() {
            if (!scroller.isFinished) {
                scroller.forceFinished(true)
            }
        }


    }
}
