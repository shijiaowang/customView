package com.wangyang.divviewlibrary.view.table.name

/**
 * Created by 动脑学院 - David
 * 日期： 2017/6/7 0007.
 * 做一家受人尊敬的企业，做一位受人尊重的老师
 */

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.database.DataSetObserver
import android.graphics.Canvas
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import android.widget.Scroller

import com.wangyang.divviewlibrary.view.table.BaseTableAdapter
import com.wangyang.divviewlibrary.view.table.Recycler

import java.util.ArrayList

import android.content.ContentValues.TAG

/**
 */
class TableView
/**
 * Constructor that is called when inflating a view from XML. This is called
 * when a view is being constructed from an XML file, supplying attributes
 * that were specified in the XML file. This version uses a default style of
 * 0, so the only attribute values applied are those in the Context's Theme
 * and the given AttributeSet.

 * The method onFinishInflate() will be called after all children have been
 * added.

 * @param context
 * *            The Context the view is running in, through which it can
 * *            access the current theme, resources, etc.
 * *
 * @param attrs
 * *            The attributes of the XML tag that is inflating the view.
 */
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : ViewGroup(context, attrs) {
    private var currentX: Int = 0
    private var currentY: Int = 0

    /**
     * Returns the adapter currently associated with this widget.

     * @return The adapter used to provide this view's content.
     */
    /**
     * Sets the data behind this TableFixHeaders.

     * @param adapter
     * *            The TableAdapter which is responsible for maintaining the data
     * *            backing this list and for producing a view to represent an
     * *            item in that data set.
     */
    var adapter: BaseTableAdapter? = null
        set(adapter) {
            field = adapter
            this.recycler = Recycler(adapter.viewTypeCount)
            scrollX = 0
            scrollY = 0
            firstColumn = 0
            firstRow = 0

            needRelayout = true
            requestLayout()
        }
    private var scrollX: Int = 0
    private var scrollY: Int = 0
    private var firstRow: Int = 0
    private var firstColumn: Int = 0
    private var widths: IntArray? = null
    private var heights: IntArray? = null

    private var headView: View? = null
    private val rowViewList: MutableList<View>
    private val columnViewList: MutableList<View>
    private val bodyViewTable: MutableList<List<View>>

    private var rowCount: Int = 0
    private var columnCount: Int = 0

    private var width: Int = 0
    private var height: Int = 0

    private var recycler: Recycler? = null
    private var needRelayout: Boolean = false
    private val minimumVelocity: Int
    private val maximumVelocity: Int

    private val flinger: Flinger

    private var velocityTracker: VelocityTracker? = null

    private val touchSlop: Int

    init {

        this.headView = null
        this.rowViewList = ArrayList<View>()
        this.columnViewList = ArrayList<View>()
        this.bodyViewTable = ArrayList<List<View>>()

        this.needRelayout = true
        this.flinger = Flinger(context)
        val configuration = ViewConfiguration.get(context)
        this.touchSlop = configuration.scaledTouchSlop
        this.minimumVelocity = configuration.scaledMinimumFlingVelocity
        this.maximumVelocity = configuration.scaledMaximumFlingVelocity

        this.setWillNotDraw(false)
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        val list: ListView
        var intercept = false
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                currentX = event.rawX.toInt()
                currentY = event.rawY.toInt()
            }
            MotionEvent.ACTION_MOVE -> {
                val x2 = Math.abs(currentX - event.rawX.toInt())
                val y2 = Math.abs(currentY - event.rawY.toInt())
                if (x2 > touchSlop || y2 > touchSlop) {
                    intercept = true
                }
            }
        }
        return intercept
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (velocityTracker == null) { // If we do not have velocity tracker
            velocityTracker = VelocityTracker.obtain() // then get one
        }
        velocityTracker!!.addMovement(event) // add this movement to it

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (!flinger.isFinished) { // If scrolling, then stop now
                    flinger.forceFinished()
                }
                currentX = event.rawX.toInt()
                currentY = event.rawY.toInt()
            }
            MotionEvent.ACTION_MOVE -> {
                val x2 = event.rawX.toInt()
                val y2 = event.rawY.toInt()
                val diffX = currentX - x2
                val diffY = currentY - y2
                currentX = x2
                currentY = y2
            }
            MotionEvent.ACTION_UP -> {
                val velocityTracker = this.velocityTracker
                velocityTracker!!.computeCurrentVelocity(1000, maximumVelocity.toFloat())
                val velocityX = velocityTracker.xVelocity.toInt()
                val velocityY = velocityTracker.yVelocity.toInt()

                if (Math.abs(velocityX) > minimumVelocity || Math.abs(velocityY) > minimumVelocity) {
                    flinger.start(actualScrollX, actualScrollY,
                            velocityX, velocityY, maxScrollX, maxScrollY)
                } else {
                    if (this.velocityTracker != null) { // If the velocity
                        this.velocityTracker!!.recycle() // recycle the
                        this.velocityTracker = null
                    }
                }
            }
        }
        return true
    }

    override fun scrollTo(x: Int, y: Int) {
        if (needRelayout) {
            scrollX = x
            firstColumn = 0

            scrollY = y
            firstRow = 0
        } else {
            scrollBy(x - sumArray(widths, 1, firstColumn) - scrollX, y - sumArray(heights, 1, firstRow) - scrollY)
        }
    }

    override fun scrollBy(x: Int, y: Int) {
        scrollX += x
        scrollY += y

        if (needRelayout) {
            return
        }

        /*
		 * TODO Improve the algorithm. Think big diagonal movements. If we are
		 * in the top left corner and scrollBy to the opposite corner. We will
		 * have created the views from the top right corner on the X part and we
		 * will have eliminated to generate the right at the Y.
		 */

        if (scrollX == 0) {
            // no op
        } else if (scrollX > 0) {
            while (widths!![firstColumn + 1] < scrollX) {
                if (!rowViewList.isEmpty()) {
                    removeLeft()
                }
                scrollX -= widths!![firstColumn + 1]
                firstColumn++
            }
            while (filledWidth < width) {
                addRight()
            }
        } else {
            while (!rowViewList.isEmpty() && filledWidth - widths!![firstColumn + rowViewList.size] >= width) {
                removeRight()
            }
            Log.i(TAG, "  scrollX  $scrollX  firstColumn  $firstColumn")
            while (0 > scrollX) {
                addLeft()
                firstColumn--
                scrollX += widths!![firstColumn + 1]
            }
        }
        if (scrollY == 0) {
            // no op
        } else if (scrollY > 0) {
            while (heights!![firstRow + 1] < scrollY) {
                if (!columnViewList.isEmpty()) {
                    removeTop()
                }
                scrollY -= heights!![firstRow + 1]
                firstRow++
            }
            while (filledHeight < height) {
                addBottom()
            }
        } else {
            while (!columnViewList.isEmpty() && filledHeight - heights!![firstRow + columnViewList.size] >= height) {
                removeBottom()
            }

            while (0 > scrollY) {
                addTop()
                firstRow--
                scrollY += heights!![firstRow + 1]
            }
        }

        repositionViews()
        awakenScrollBars()
    }

    /*
     * The expected value is: percentageOfViewScrolled * computeHorizontalScrollRange()
     */
    override fun computeHorizontalScrollExtent(): Int {
        val tableSize = (width - widths!![0]).toFloat()
        val contentSize = (sumArray(widths) - widths!![0]).toFloat()
        val percentageOfVisibleView = tableSize / contentSize

        return Math.round(percentageOfVisibleView * tableSize)
    }

    /*
     * The expected value is between 0 and computeHorizontalScrollRange() -
computeHorizontalScrollExtent()
     */
    override fun computeHorizontalScrollOffset(): Int {
        val maxScrollX = (sumArray(widths) - width).toFloat()
        val percentageOfViewScrolled = actualScrollX / maxScrollX
        val maxHorizontalScrollOffset = width - widths!![0] -
                computeHorizontalScrollExtent()

        return widths!![0] + Math.round(percentageOfViewScrolled * maxHorizontalScrollOffset)
    }

    /*
     * The base measure
     */
    override fun computeHorizontalScrollRange(): Int {
        return width
    }

    /*
     * The expected value is: percentageOfViewScrolled * computeVerticalScrollRange()
     */
    override fun computeVerticalScrollExtent(): Int {
        val tableSize = (height - heights!![0]).toFloat()
        val contentSize = (sumArray(heights) - heights!![0]).toFloat()
        val percentageOfVisibleView = tableSize / contentSize

        return Math.round(percentageOfVisibleView * tableSize)
    }

    /*
     * The expected value is between 0 and computeVerticalScrollRange() -
computeVerticalScrollExtent()
     */
    override fun computeVerticalScrollOffset(): Int {
        val maxScrollY = (sumArray(heights) - height).toFloat()
        val percentageOfViewScrolled = actualScrollY / maxScrollY
        val maxHorizontalScrollOffset = height - heights!![0] -
                computeVerticalScrollExtent()

        return heights!![0] + Math.round(percentageOfViewScrolled * maxHorizontalScrollOffset)
    }

    /*
     * The base measure
     */
    override fun computeVerticalScrollRange(): Int {
        return height
    }

    val actualScrollX: Int
        get() = scrollX + sumArray(widths, 1, firstColumn)

    val actualScrollY: Int
        get() = scrollY + sumArray(heights, 1, firstRow)

    private val maxScrollX: Int
        get() = Math.max(0, sumArray(widths) - width)

    private val maxScrollY: Int
        get() = Math.max(0, sumArray(heights) - height)

    private val filledWidth: Int
        get() = widths!![0] + sumArray(widths, firstColumn + 1, rowViewList.size) - scrollX

    private val filledHeight: Int
        get() = heights!![0] + sumArray(heights, firstRow + 1, columnViewList.size) - scrollY

    private fun addLeft() {
        addLeftOrRight(firstColumn - 1, 0)
    }

    private fun addTop() {
        addTopAndBottom(firstRow - 1, 0)
    }

    private fun addRight() {
        val size = rowViewList.size
        addLeftOrRight(firstColumn + size, size)
    }

    private fun addBottom() {
        val size = columnViewList.size
        addTopAndBottom(firstRow + size, size)
    }

    private fun addLeftOrRight(column: Int, index: Int) {
        var view = obtainView(-1, column, widths!![column + 1], heights!![0])
        rowViewList.add(index, view)

        var i = firstRow
        for (list in bodyViewTable) {
            view = obtainView(i, column, widths!![column + 1], heights!![i + 1])
            list.add(index, view)
            i++
        }
    }

    private fun addTopAndBottom(row: Int, index: Int) {
        var view = obtainView(row, -1, widths!![0], heights!![row + 1])
        columnViewList.add(index, view)

        val list = ArrayList<View>()
        val size = rowViewList.size + firstColumn
        for (i in firstColumn..size - 1) {
            view = obtainView(row, i, widths!![i + 1], heights!![row + 1])
            list.add(view)
        }
        bodyViewTable.add(index, list)
    }

    private fun removeLeft() {
        removeLeftOrRight(0)
    }

    private fun removeTop() {
        removeTopOrBottom(0)
    }

    private fun removeRight() {
        removeLeftOrRight(rowViewList.size - 1)
    }

    private fun removeBottom() {
        removeTopOrBottom(columnViewList.size - 1)
    }

    private fun removeLeftOrRight(position: Int) {
        removeView(rowViewList.removeAt(position))
        for (list in bodyViewTable) {
            removeView(list.removeAt(position))
        }
    }

    private fun removeTopOrBottom(position: Int) {
        removeView(columnViewList.removeAt(position))
        val remove = bodyViewTable.removeAt(position)
        for (view in remove) {
            removeView(view)
        }
    }

    override fun removeView(view: View) {
        super.removeView(view)
        val typeView = view.getTag(R.id.tag_type_view) as Int
        recycler!!.addViewToRecycle(view, typeView)
    }


    private fun repositionViews() {
        var left: Int
        var top: Int
        var right: Int
        var bottom: Int
        var i: Int

        left = widths!![0] - scrollX
        i = firstColumn
        for (view in rowViewList) {
            right = left + widths!![++i]
            view.layout(left, 0, right, heights!![0])
            left = right
        }

        top = heights!![0] - scrollY
        i = firstRow
        for (view in columnViewList) {
            bottom = top + heights!![++i]
            view.layout(0, top, widths!![0], bottom)
            top = bottom
        }

        top = heights!![0] - scrollY
        i = firstRow
        for (list in bodyViewTable) {
            bottom = top + heights!![++i]
            left = widths!![0] - scrollX
            var j = firstColumn
            for (view in list) {
                right = left + widths!![++j]
                view.layout(left, top, right, bottom)
                left = right
            }
            top = bottom
        }
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)

        val w: Int
        val h: Int

        if (this.adapter != null) {
            this.rowCount = this.adapter!!.getRowCount()
            this.columnCount = this.adapter!!.getColmunCount()

            widths = IntArray(columnCount + 1)
            for (i in -1..columnCount - 1) {
                widths[i + 1] += this.adapter!!.getWidth(i)
            }
            heights = IntArray(rowCount + 1)
            for (i in -1..rowCount - 1) {
                heights[i + 1] += this.adapter!!.getHeight(i)
            }

            if (widthMode == View.MeasureSpec.AT_MOST) {
                w = Math.min(widthSize, sumArray(widths))
            } else if (widthMode == View.MeasureSpec.UNSPECIFIED) {
                w = sumArray(widths)
            } else {
                w = widthSize
                val sumArray = sumArray(widths)
                if (sumArray < widthSize) {
                    val factor = widthSize / sumArray.toFloat()
                    for (i in 1..widths!!.size - 1) {
                        widths[i] = Math.round(widths!![i] * factor)
                    }
                    widths[0] = widthSize - sumArray(widths, 1, widths!!.size - 1)
                }
            }

            if (heightMode == View.MeasureSpec.AT_MOST) {
                h = Math.min(heightSize, sumArray(heights))
            } else if (heightMode == View.MeasureSpec.UNSPECIFIED) {
                h = sumArray(heights)
            } else {
                h = heightSize
            }
        } else {
            if (heightMode == View.MeasureSpec.AT_MOST || widthMode == View.MeasureSpec.UNSPECIFIED) {
                w = 0
                h = 0
            } else {
                w = widthSize
                h = heightSize
            }
        }

        if (firstRow >= rowCount || maxScrollY - actualScrollY < 0) {
            firstRow = 0
            scrollY = Integer.MAX_VALUE
        }
        if (firstColumn >= columnCount || maxScrollX - actualScrollX < 0) {
            firstColumn = 0
            scrollX = Integer.MAX_VALUE
        }

        setMeasuredDimension(w, h)
    }

    private fun sumArray(array: IntArray, firstIndex: Int = 0, count: Int = array.size): Int {
        var count = count
        var sum = 0
        count += firstIndex
        for (i in firstIndex..count - 1) {
            sum += array[i]
        }
        return sum
    }

    @SuppressLint("DrawAllocation")
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (needRelayout || changed) {
            needRelayout = false
            resetTable()

            if (this.adapter != null) {
                width = r - l
                height = b - t

                var left: Int
                var top: Int
                var right: Int
                var bottom: Int

                right = Math.min(width, sumArray(widths))
                bottom = Math.min(height, sumArray(heights))
                //已经绘制了第一个
                headView = makeAndSetup(-1, -1, 0, 0, widths!![0], heights!![0])
                //left不能从0开始
                left = widths!![0] - scrollX
                //填充第一行  橘红色的部分
                run {
                    var i = firstColumn
                    while (i < columnCount && left < width) {
                        right = left + widths!![i + 1]
                        val view = makeAndSetup(-1, i, left, 0, right,
                                heights!![0])
                        rowViewList.add(view)
                        //循环  赋值
                        left = right
                        i++

                    }
                }

                top = heights!![0] - scrollY
                //        填充绿色的部分
                run {
                    var i = firstRow
                    while (i < rowCount && top < height) {
                        bottom = top + heights!![i + 1]
                        val view = makeAndSetup(i, -1, 0, top, widths!![0],
                                bottom)
                        columnViewList.add(view)
                        top = bottom
                        i++
                    }
                }
                //top被改变
                top = heights!![0] - scrollY
                //改动  firstRow
                var i = firstRow
                while (i < rowCount && top < height) {
                    bottom = top + heights!![i + 1]
                    left = widths!![0] - scrollX
                    val list = ArrayList<View>()
                    var j = firstColumn
                    while (j < columnCount && left < width) {
                        right = left + widths!![j + 1]
                        val view = makeAndSetup(i, j, left, top,
                                right, bottom)
                        list.add(view)
                        left = right
                        j++
                    }
                    bodyViewTable.add(list)
                    top = bottom
                    i++
                }
            }
        }
    }

    private fun scrollBounds() {
        scrollX = scrollBounds(scrollX, firstColumn, widths, width)
        scrollY = scrollBounds(scrollY, firstRow, heights, height)
    }

    private fun scrollBounds(desiredScroll: Int, firstCell: Int, sizes: IntArray, viewSize: Int): Int {
        var desiredScroll = desiredScroll
        if (desiredScroll == 0) {
            // no op
        } else if (desiredScroll < 0) {
            desiredScroll = Math.max(desiredScroll, -sumArray(sizes, 1, firstCell))
        } else {
            desiredScroll = Math.min(desiredScroll, Math.max(0, sumArray(sizes,
                    firstCell + 1, sizes.size - 1 - firstCell) + sizes[0] - viewSize))
        }
        return desiredScroll
    }

    private fun adjustFirstCellsAndScroll() {
        var values: IntArray

        values = adjustFirstCellsAndScroll(scrollX, firstColumn, widths)
        scrollX = values[0]
        firstColumn = values[1]

        values = adjustFirstCellsAndScroll(scrollY, firstRow, heights)
        scrollY = values[0]
        firstRow = values[1]
    }

    private fun adjustFirstCellsAndScroll(scroll: Int, firstCell: Int, sizes: IntArray): IntArray {
        var scroll = scroll
        var firstCell = firstCell
        if (scroll == 0) {
            // no op
        } else if (scroll > 0) {
            while (sizes[firstCell + 1] < scroll) {
                firstCell++
                scroll -= sizes[firstCell]
            }
        } else {
            while (scroll < 0) {
                scroll += sizes[firstCell]
                firstCell--
            }
        }
        return intArrayOf(scroll, firstCell)
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private fun setAlpha(imageView: ImageView, alpha: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            imageView.alpha = alpha
        } else {
            imageView.setAlpha(Math.round(alpha * 255))
        }
    }


    private fun resetTable() {
        headView = null
        rowViewList.clear()
        columnViewList.clear()
        bodyViewTable.clear()

        removeAllViews()
    }

    private fun makeAndSetup(row: Int, column: Int, left: Int, top: Int, right: Int, bottom: Int): View {
        val view = obtainView(row, column, right - left, bottom - top)
        view.layout(left, top, right, bottom)
        return view
    }

    override fun drawChild(canvas: Canvas, child: View, drawingTime: Long): Boolean {
        val ret: Boolean

        val row = child.getTag(R.id.tag_row) as Int
        val column = child.getTag(R.id.tag_column) as Int
        // row == null => Shadow view
        if (row == null || row === -1 && column === -1) {
            ret = super.drawChild(canvas, child, drawingTime)
        } else {
            canvas.save()
            if (row === -1) {
                canvas.clipRect(widths!![0], 0, canvas.width, canvas.height)
            } else if (column === -1) {
                canvas.clipRect(0, heights!![0], canvas.width,
                        canvas.height)
            } else {
                canvas.clipRect(widths!![0], heights!![0], canvas.width,
                        canvas.height)
            }

            ret = super.drawChild(canvas, child, drawingTime)
            canvas.restore()
        }
        return ret
    }

    private fun obtainView(row: Int, colmun: Int, width: Int, height: Int): View {
        //得到当前控件的类型
        val itemType = this.adapter!!.getItemViewType(row, colmun)
        //从回收池 拿到一个View
        val reclyView = recycler!!.getViewByRecycle(itemType)
        //reclyView 可能为空
        val view = this.adapter!!.getView(row, colmun, reclyView!!, this) ?: throw RuntimeException("view  不能为空")
//View不可能为空
        view.setTag(R.id.tag_type_view, itemType)
        view.setTag(R.id.tag_column, colmun)
        view.setTag(R.id.tag_row, row)
        view.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY))
        addTableView(view, row, colmun)
        return view
    }

    private fun addTableView(view: View, row: Int, column: Int) {
        if (row == -1 && column == -1) {
            addView(view, childCount - 1)
        } else if (row == -1 || column == -1) {
            addView(view, childCount - 2)
        } else {
            addView(view, 0)
        }
    }

    private inner class TableAdapterDataSetObserver : DataSetObserver() {

        override fun onChanged() {
            needRelayout = true
            requestLayout()
        }

        override fun onInvalidated() {
            // Do nothing
        }
    }

    // http://stackoverflow.com/a/6219382/842697
    private inner class Flinger internal constructor(context: Context) : Runnable {
        private val scroller: Scroller

        private var lastX = 0
        private var lastY = 0

        init {
            scroller = Scroller(context)
        }

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

        internal val isFinished: Boolean
            get() = scroller.isFinished

        internal fun forceFinished() {
            if (!scroller.isFinished) {
                scroller.forceFinished(true)
            }
        }
    }
}
/**
 * Simple constructor to use when creating a view from code.

 * @param context
 * *            The Context the view is running in, through which it can
 * *            access the current theme, resources, etc.
 */
