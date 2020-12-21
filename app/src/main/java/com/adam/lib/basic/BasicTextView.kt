package com.adam.lib.basic

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.TextView
import com.adam.lib.callback.Callback
/**
 * Created By Adam on 2020/8/5
 */
class BasicTextView : TextView{
    var callback: Callback? = null
    var intercept = false
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        callback?.callback("TextView($text), dispatchTouchEvent, ${event?.action}")
        return super.dispatchTouchEvent(event)//true表示, 自己不消費
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        callback?.callback("TextView($text), onTouchEvent, ${event?.action}")
        return intercept || super.onTouchEvent(event)
    }
}