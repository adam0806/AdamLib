package com.adam.lib.util

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import com.adam.lib.widget.DatePopupWindow
import com.adam.lib.widget.DropDownPopupWindow
import com.adam.lib.widget.MyDialog
import com.google.android.material.snackbar.Snackbar

/**
 * Created by lihu on 2017/10/17.全局上下文Toast
 * 需要先初始化init(context)
 */
object AlertUtils {
    var dialog: Dialog?= null
    var datePopupWindow: DatePopupWindow?= null
    var dropDownPopupWindow: DropDownPopupWindow?= null
    var context:Context ?= null
    fun init(context: Context){
        this.context = context
    }
    fun showToast(toast: String?, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, toast, duration).show()
    }

    fun showSnackbar(view: View?, content: String?, buttonStr: String?, clickListener: View.OnClickListener?) {
        Snackbar.make(view!!, content!!, Snackbar.LENGTH_LONG)
                .setAction(buttonStr, clickListener)
                .show()
    }

    fun showDialog(context: Context, title: String?, content: String?,
                   posBtnStr: String?,
                   negBtnStr: String?,
                   posListener: DialogInterface.OnClickListener?,
                   negListener: DialogInterface.OnClickListener?,
                   cancelable: Boolean = true) {
        val builder = AlertDialog.Builder(context)
        if(!title.isNullOrEmpty()){
            builder.setTitle(title)
        }
        if(!content.isNullOrEmpty()){
            builder.setMessage(content)
        }
        if(!posBtnStr.isNullOrEmpty()){
            builder.setPositiveButton(posBtnStr, posListener)
        }
        if(!negBtnStr.isNullOrEmpty()) {
            builder.setNegativeButton(negBtnStr, negListener)
        }
        builder.setCancelable(cancelable)
        dialog = builder.create()
        dialog!!.show()
    }
    fun showDialog(context: Context, contentView: View, cancelable: Boolean = true){
        var builder = MyDialog.Builder(context)
        builder.contentView = contentView
        builder.cancelable = cancelable
        dialog = builder.build()
        dialog!!.show()
    }
    fun showDropDownPopup(context: Context){
        dropDownPopupWindow = DropDownPopupWindow.getInstance(context)
        dropDownPopupWindow!!.showOnScreenTop()
    }
    fun showDatePicker(context: Context, onDateChangedListener: DatePicker.OnDateChangedListener?,
                       onTimeChangedListener: TimePicker.OnTimeChangedListener?, callback: DatePopupWindow.Callback?,
                       time : Long = TimeUtils.getNowMillis()){
        datePopupWindow = DatePopupWindow.getInstance(context)
        if(datePopupWindow!!.isShowing){
            datePopupWindow!!.dismiss()
        }
        if(onDateChangedListener != null) {
            datePopupWindow!!.setDatePicker(onDateChangedListener, time)
        }
        if(onTimeChangedListener != null) {
            datePopupWindow!!.setTimePicker(onTimeChangedListener, time)
        }
        if(callback != null){
            datePopupWindow!!.callback = callback
        }
        datePopupWindow!!.showScreenMiddle()
    }
    fun dismissPopupWindow(){
        if(datePopupWindow?.isShowing!!){
            datePopupWindow!!.dismiss()
        }
    }
    fun dismissDialog(){
        if(dialog?.isShowing!!) {
            dialog?.dismiss()
        }
    }
    fun clear(){
        dismissPopupWindow()
        dismissDialog()
    }
}