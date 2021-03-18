package com.adam.lib.basic

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.LinearLayout
import com.adam.lib.callback.Callback

/**
 * Created By Adam on 2020/8/5
 */
class BasicLinearLayout : LinearLayout{
    var callback: Callback? = null
    var intercept = false
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        var consume = super.dispatchTouchEvent(event)
        callback?.callback("LinearLayout($tag), dispatchTouchEvent: $consume, action:  ${event?.action}")
        return consume
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        var consume = super.onTouchEvent(event)
        callback?.callback("LinearLayout($tag), onTouchEvent: $consume, action:  ${event?.action}")
        return consume
    }

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        var consume = intercept || super.onTouchEvent(event)
        callback?.callback("LinearLayout($tag), onInterceptTouchEvent: $consume, action:  ${event?.action}")
        return consume
    }
}