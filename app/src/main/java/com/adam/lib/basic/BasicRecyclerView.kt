package com.adam.lib.basic

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView

/**
 * Created By Adam on 2020/11/30
 */
class BasicRecyclerView : RecyclerView{
    constructor(context: Context): super(context)
    constructor(context: Context, attrs: AttributeSet?): super(context, attrs)
    constructor(context: Context , attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    override fun canScrollHorizontally(direction: Int): Boolean {
        return super.canScrollHorizontally(direction)
    }
}