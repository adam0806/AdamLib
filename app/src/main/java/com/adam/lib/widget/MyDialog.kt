package com.adam.lib.widget

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View

/**
 * Created By Adam on 2020/12/7
 */
class MyDialog : Dialog{
    var builder: Builder
    constructor(context: Context, builder: Builder, dialogStyle: Int) : super(context, dialogStyle) {
        this.builder = builder
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (builder.contentView != null) {
            setContentView(builder.contentView!!)
        }
        setCancelable(builder.cancelable)
    }

    class Builder(context: Context) {
        var context = context
        var contentView : View ? =null
        var dialogStyle : Int = 0
        var cancelable : Boolean = true
        fun build() : MyDialog{
            return MyDialog(context, this, dialogStyle)
        }
    }
}