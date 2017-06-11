package com.wangyang.divviewlibrary.view.table

import android.view.View
import java.util.*

/**
 * Created by .wangyang on 2017/6/11
 * email：1440214507@qq.com
 * 可回收的View回收池
 */
class Recycler(viewTypeCount:Int) {
    private val views: Array<Stack<View>?>
    init {
        if (viewTypeCount<=0){
            throw Exception("viewTypeCount cannot <=0")
        }
        views= arrayOfNulls(size = viewTypeCount)
        for (i in 0..viewTypeCount-1){
            views[i]=Stack<View>()
        }
    }

    /**
     * 添加到回收池
     */
    fun addViewToRecycle(view:View,type:Int){
        views[type]?.push(view)
    }

    /**
     * 从回收池获取
     */
    fun getViewByRecycle(type: Int):View?{
        try {
            val view: View? = views[type]?.pop()
            return view
        }catch (e:Exception){
            return null
        }
    }
}