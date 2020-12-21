package com.adam.lib.widget

import android.content.Context
import android.view.View
import android.widget.TextView
import com.adam.lib.R
import com.adam.lib.basic.BasicPopupWindow
/**
 * Created By Adam on 2020/12/8
 */
class DropDownPopupWindow : BasicPopupWindow {
    var tv_arrow_up: TextView
    var tv_arrow_down: TextView
    companion object{
        private var instance: DropDownPopupWindow? = null
        fun getInstance(context: Context): DropDownPopupWindow =
                instance ?: synchronized(this){
                    instance ?: DropDownPopupWindow(context).also { instance = it }
                }
    }

    constructor(context: Context) : super(context){
        tv_arrow_up = mContentView.findViewById(R.id.tv_arrow_up)
        tv_arrow_down = mContentView.findViewById(R.id.tv_arrow_down)
    }
    override fun getLayoutResId(): Int {
        return R.layout.popup_list
    }
    override fun autoAdjustArrowPos(isNeedShowUp: Boolean) {
        if (tv_arrow_up != null && tv_arrow_down != null) {
            tv_arrow_up.setVisibility(if (isNeedShowUp) View.INVISIBLE else View.VISIBLE)
            tv_arrow_down.setVisibility(if (isNeedShowUp) View.VISIBLE else View.INVISIBLE)
        }
    }
}