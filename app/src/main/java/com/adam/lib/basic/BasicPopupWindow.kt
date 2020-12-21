package com.adam.lib.basic

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import android.widget.PopupWindow
import com.adam.lib.util.WindowUtils
import com.adam.lib.util.WindowUtils.getScreenHeight
import com.adam.lib.util.WindowUtils.getScreenWidth

/**
 * Created By Adam on 2020/12/8
 */
abstract class BasicPopupWindow : PopupWindow{
    var context: Context
    var mContentView: View
    constructor(context: Context) : super(context){
        this.context = context
        mContentView = View.inflate(context, getLayoutResId(), null);
        width = LinearLayout.LayoutParams.WRAP_CONTENT
        height = LinearLayout.LayoutParams.WRAP_CONTENT
//        setBackgroundDrawable(ColorDrawable(0))
//        setAnimationStyle(getAnimationResourse());
        //        setAnimationStyle(getAnimationResourse());
        setBackgroundDrawable(ColorDrawable(0));    //透明背景, 去除黑色邊框
        isFocusable = true
        isOutsideTouchable = true
        contentView = mContentView
    }
    fun showAutoOrientation(anchorView: View, xOff: Int, yOff: Int) {
        val isNeedShowUp: Boolean = WindowUtils.calculatePopWindowPos(anchorView, mContentView).first
        val windowPos: IntArray = WindowUtils.calculatePopWindowPos(anchorView, mContentView).second
        mContentView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                autoAdjustArrowPos(isNeedShowUp)
                mContentView.viewTreeObserver.removeGlobalOnLayoutListener(this)
            }
        })
        windowPos[0] -= xOff
        if (isNeedShowUp) {
            windowPos[1] += yOff
        } else {
            windowPos[1] -= yOff
        }
        showAtLocation(anchorView, Gravity.TOP or Gravity.START, windowPos[0], windowPos[1])
    }
    open fun showOnScreenTop() {
        showAtLocation(mContentView, Gravity.TOP, 0, 0)
    }
    open fun showOnScreenBottom() {
        showAtLocation(mContentView, Gravity.BOTTOM, 0, 0)
    }
    open fun showScreenMiddle() {
        val windowHeight = getScreenHeight(context)
        val windowWidth = getScreenWidth(context)
        var xOff= windowWidth / 2 - WindowUtils.dip2px(context, width.toFloat()) / 2
        var yOff = windowHeight / 2 - WindowUtils.dip2px(context, height.toFloat()) / 2
        showAtLocation(mContentView, Gravity.BOTTOM, xOff, yOff)
    }

    open fun autoAdjustArrowPos(isNeedShowUp: Boolean){}
    abstract fun getLayoutResId(): Int
}