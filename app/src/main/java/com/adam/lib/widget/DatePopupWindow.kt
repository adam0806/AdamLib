package com.adam.lib.widget

import android.content.Context
import android.view.View
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import com.adam.lib.R
import com.adam.lib.basic.BasicPopupWindow
import com.adam.lib.util.TimeUtils
import java.lang.StringBuilder
import java.util.*

/**
 * Created By Adam on 2020/12/8
 */
class DatePopupWindow : BasicPopupWindow{
    var tv_cancel:TextView
    var tv_confirm:TextView
    var tv_title:TextView
    var datepicker:DatePicker ? = null
    var timepicker: TimePicker? = null
    var callback: Callback ? = null
    companion object{
        private var instance: DatePopupWindow? = null
        fun getInstance(context: Context): DatePopupWindow =
                instance ?: synchronized(this){
                    instance ?: DatePopupWindow(context).also { instance = it }
                }
    }
    interface Callback{
        fun confirm(time: Long, timeString: String, year: Int?, monthOfYear: Int?, dayOfMonth: Int?, hour: Int?, minue: Int?)
    }
    private constructor(context: Context) : super(context){
        tv_cancel = mContentView.findViewById(R.id.tv_cancel)
        tv_cancel.setOnClickListener {
            dismiss()
        }
        tv_confirm = mContentView.findViewById(R.id.tv_confirm)
        tv_confirm.setOnClickListener {
            dismiss()
            callback?.confirm(getTimeMilli(), getTimeString(),
                    datepicker?.year, datepicker?.month, datepicker?.dayOfMonth, timepicker?.hour, timepicker?.minute)
        }
        tv_title = mContentView.findViewById(R.id.tv_title)
        datepicker = mContentView.findViewById(R.id.datepicker)
        timepicker = mContentView.findViewById(R.id.timepicker)
        datepicker?.visibility = View.GONE
        timepicker?.visibility = View.GONE
    }
    override fun getLayoutResId(): Int {
        return R.layout.popup_date
    }
    fun setTime(time: Long){
        var calendar = Calendar.getInstance()
        calendar.time = Date(time)
        var hour = calendar.get(Calendar.HOUR)
        var minue = calendar.get(Calendar.MINUTE)
        timepicker?.hour = hour
        timepicker?.minute = minue
    }
    fun setTimePicker(onTimeChangedListener: TimePicker.OnTimeChangedListener, time: Long = TimeUtils.getNowMillis()){
        timepicker?.visibility = View.VISIBLE
        timepicker?.setIs24HourView(true);
        setTime(time)
        tv_title.text = getTimeString()
        timepicker?.setOnTimeChangedListener { view, hourOfDay, minute ->
            tv_title.text = getTimeString()
            onTimeChangedListener.onTimeChanged(view, hourOfDay, minute)
        }
    }
    fun getTimeString() : String{
        var builder = StringBuilder()
        if(datepicker != null){
            builder.append(datepicker!!.year)
            builder.append("-")
            builder.append(datepicker!!.month)
            builder.append("-")
            builder.append(datepicker!!.dayOfMonth)
            builder.append(" ")
        }
        if(timepicker != null) {
            builder.append(timepicker!!.hour)
            builder.append(":")
            builder.append(timepicker!!.minute)
        }
        return builder.toString()
    }
    fun getTimeMilli() : Long{
        var calendar = Calendar.getInstance()
        if(datepicker != null) {
            if (timepicker != null){
                calendar.set(datepicker!!.year, datepicker!!.month, datepicker!!.dayOfMonth, timepicker!!.hour, timepicker!!.minute)
            }else{
                calendar.set(datepicker!!.year, datepicker!!.month, datepicker!!.dayOfMonth)
            }
        }
        return calendar.time.time
    }
    fun setDatePicker(onDateChangedListener: DatePicker.OnDateChangedListener, time: Long = TimeUtils.getNowMillis()){
        if(datepicker != null) {
            datepicker!!.visibility = View.VISIBLE
            var calendar = Calendar.getInstance()
            calendar.time = Date(time)
            var year = calendar.get(Calendar.YEAR)
            var month = calendar.get(Calendar.MONTH)
            var day = calendar.get(Calendar.DAY_OF_MONTH)
            datepicker!!.init(year, month, day) { view, year, monthOfYear, dayOfMonth ->
                tv_title.text = getTimeString()
                onDateChangedListener.onDateChanged(view, year, monthOfYear, dayOfMonth)
            }
        }
    }
}