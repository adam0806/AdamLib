package com.adam.lib.basic

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.Toolbar

/**
 * Created By Adam on 2020/11/20
 */
class BasicToolbar : Toolbar{
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
}