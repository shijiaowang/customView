package com.wangyang.divviewlibrary.view.table

import android.view.View
import android.view.ViewGroup

/**
 * Created by .wangyang on 2017/6/11
 * email：1440214507@qq.com
 * SlideTableView的滑动适配器接口
 */
interface IBaseTableAdapter {
    val rowCount:Int//行数量
    val columnCount:Int//列数量
    val itemViewTypeCount:Int //子类类型数量
    fun getItemViewType(row:Int,column:Int):Int //获取子类的类型
    fun getView(row: Int,column: Int,convertView: View,parent: ViewGroup):View //获取view
    fun getWidth(row: Int):Int //获取当前行的宽度
    fun getHeight(column: Int):Int //获取当前列的高度

}