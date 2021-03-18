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
        callback?.callback("LinearLayout($tag), dispatchTouchEvent, action:  ${event?.action}")
        var consume = super.dispatchTouchEvent(event)
        callback?.callback("LinearLayout($tag), dispatchTouchEvent, consume: $consume, action:  ${event?.action}")
        return consume
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        callback?.callback("LinearLayout($tag), onTouchEvent, action:  ${event?.action}")
        var consume = super.onTouchEvent(event)
        callback?.callback("LinearLayout($tag), onTouchEvent, consume: $consume, action:  ${event?.action}")
        return consume
    }

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        callback?.callback("LinearLayout($tag), onInterceptTouchEvent, action:  ${event?.action}")
        var consume = intercept || super.onTouchEvent(event)
        callback?.callback("LinearLayout($tag), onInterceptTouchEvent, consume: $consume, action:  ${event?.action}")
        return consume
    }
}