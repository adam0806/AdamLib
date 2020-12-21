package com.adam.lib.basic

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

/**
 * Created By Adam on 2020/11/25
 */
class BasicViewPager : ViewPager{
    private var scrollable = true
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun onTouchEvent(arg0: MotionEvent): Boolean {
        try {
            if (scrollable){
                return super.onTouchEvent(arg0)
            }
            return false
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }

    override fun onInterceptTouchEvent(arg0: MotionEvent): Boolean {
        try {
            if (scrollable){
                return super.onTouchEvent(arg0)
            }
            return false
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }

    fun disableScroll() {
        scrollable = false
    }

    fun enableScroll() {
        scrollable = true
    }

    fun isScrollable() : Boolean{
        return scrollable
    }
}