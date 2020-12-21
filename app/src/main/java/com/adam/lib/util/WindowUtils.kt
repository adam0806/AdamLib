package com.adam.lib.util

import android.content.Context
import android.util.Pair
import android.util.TypedValue
import android.view.View

/**
 * Created By Adam on 2020/12/8
 */
object WindowUtils {

    fun getScreenHeight(context: Context): Int {
        return context.resources.displayMetrics.heightPixels
    }
    fun getScreenWidth(context: Context): Int {
        return context.resources.displayMetrics.widthPixels
    }
    fun dip2px(context: Context?, dipValue: Float): Int {
        if (context == null) {
            return dipValue.toInt()
        }
        val scale = context.resources.displayMetrics.density
        return (dipValue * scale + 0.5f).toInt()
    }
    fun px2dip(context: Context?, pxValue: Float): Int {
        if (context == null) {
            return pxValue.toInt()
        }
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }
    fun sp2px(context: Context?, sp: Float): Float {
        if (context == null) {
            return sp
        }
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.resources .displayMetrics)
    }
    fun px2sp(context: Context?, px: Int): Float {
        if (context == null) {
            return px.toFloat()
        }
        val scaledDensity = context.resources.displayMetrics.scaledDensity
        return px / scaledDensity
    }

    fun calculatePopWindowPos(anchorView: View, contentView: View): Pair<Boolean, IntArray> {
        val windowPos = IntArray(2)
        val anchorLoc = IntArray(2)
        // 获取锚点View在屏幕上的左上角坐标位置
        anchorView.getLocationOnScreen(anchorLoc)
        val anchorHeight = anchorView.height
        // 获取屏幕的高宽
        val screenHeight: Int = getScreenHeight(anchorView.context)
        val screenWidth: Int = getScreenWidth(anchorView.context)
        // 测量contentView
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        // 计算contentView的高宽
        val windowHeight = contentView.measuredHeight
        val windowWidth = contentView.measuredWidth
        // 判断需要向上弹出还是向下弹出显示（（屏幕高度-锚点y点位置-锚点高度=下方距离）<弹框高度）
        val isNeedShowUp = screenHeight - anchorLoc[1] - anchorHeight < windowHeight
        //显示在锚点上面
        if (isNeedShowUp) {
            windowPos[0] = screenWidth - windowWidth
            windowPos[1] = anchorLoc[1] - windowHeight
        } else {
            windowPos[0] = screenWidth - windowWidth
            windowPos[1] = anchorLoc[1] + anchorHeight
        }
        return Pair.create(isNeedShowUp, windowPos)
    }
}