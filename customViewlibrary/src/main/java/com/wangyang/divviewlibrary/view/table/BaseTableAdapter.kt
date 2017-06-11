package com.wangyang.divviewlibrary.view.table

import android.view.View
import android.view.ViewGroup

/**
 * Created by Administrator on 2017/6/5 0005.
 * 适配器模式
 */

interface BaseTableAdapter {
    fun getRowCount():Int
    fun getColumnCount():Int

    fun getView(row: Int, comun: Int, convertView: View, parent: ViewGroup): View

    fun getWidth(column: Int): Int

    fun getHeight(row: Int): Int

    fun getItemViewType(row: Int, column: Int): Int

    val viewTypeCount: Int


    /**
     * scrollX+=x;
     * scrollY+=y;
     * if (needRelayout) {
     * return;
     * }
     * Log.i(TAG,"x   "+x+"   y  "+y +"  scrollX  "+scrollX+"  scrollY     "+scrollX+"   needRelayout  "+needRelayout);
     * scrollBounds();
     * Log.i(TAG,"================x   "+x+"   y  "+y +"  scrollX  "+scrollX+"  scrollY     "+scrollX+"  firstRow  "+firstRow+"   firstColumn "+firstColumn );

     * if (scrollX == 0) {
     * //什么都不做
     * //手指往左划
     * } else if (scrollX >0) {
     * Log.i(TAG,"<----------------------");
     * while (widths[firstColumn + 1] < scrollX) {
     * if (!rowViewList.isEmpty()) {
     * removeLeft();
     * }
     * //划出左边一列  则将scrollX  加1个宽度
     * scrollX-=widths[firstColumn+1];
     * firstColumn++;
     * }
     * //当第一列的宽度加上 剩余 列数 的总宽度   并且减去scrollX   如果大于了控件的总宽度  表明需要增加右边一列了
     * while (getFilledWidth() < width) {
     * addRight();
     * }
     * //手指往右划
     * }else {
     * Log.i(TAG,"-------------->");
     * //
     * while (!rowViewList.isEmpty()&&getFilledWidth() >= width && getFilledWidth() - widths[firstColumn + rowViewList.size()] >= width) {
     * //                移除右边的集合
     * removeRight();
     * }
     * //            //新老更替   将右边View的集合  先移除 后添加 ，这里仅仅是添加集合    没有添加到ViewGroup中
     * //            if (rowViewList.isEmpty()) {
     * //                while (scrollX < 0) {
     * ////                    第一列不再是之前的那一列  手指向右划   移除右边的View  添加左边的View  让后第一列 做减一操作
     * //                    firstColumn--;
     * ////                    scrollX要加上一个  最新一列的宽度  总数组是多一个  所以 最新一列  要加一  firstColumn+1
     * //                    scrollX+=widths[firstColumn+1];
     * //                }
     * //                while (getFilledWidth() < width) {
     * //                    addRight();
     * //                }
     * //            }else {
     * //在上面for循环中  会去加上一个 宽度   所以造成了scrollX  小于0的情况，
     * while (scrollX < 0) {
     * addLeft();
     * firstColumn--;
     * scrollX+=widths[firstColumn+1];
     * }
     * //            }

     * }

     * if (scrollY == 0) {

     * } else if (scrollY > 0) {
     * //当划出去的y 偏移量大于第一行的高度时   移除顶部
     * while (scrollY > heights[firstRow + 1]) {
     * if (!columnViewList.isEmpty()) {
     * removeTop();
     * }
     * scrollY-=heights[firstRow+1];
     * firstRow++;
     * }
     * while (getFilledWidth() < height) {
     * addBottom();
     * }
     * } else {
     * while (getFilledWidth() - heights[firstColumn + columnViewList.size()] >= height) {
     * removeBottom();
     * }
     * while (scrollY < 0) {
     * addTop();
     * firstRow--;
     * scrollY+=heights[firstRow+1];
     * }

     * }
     * repositionViews();
     */
}
