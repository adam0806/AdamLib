package com.adam.lib.util

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created By Adam on 2020/12/8
 */
object TimeUtils{
    /**
     * 取當前millis
     */
    fun getNowMillis(): Long{
        return Date().time
    }
    /**
     * 根據format, date取millis
     */
    fun getMillisByDateFormat(format: String, date:String) : Long{
        return SimpleDateFormat(format).parse(date).time
    }

    /**
     * 根據format, date取時間String
     */
    fun getStringByDateFormat(format: String, date:String) : String{
        return SimpleDateFormat(format).format(getMillisByDateFormat(format, date))
    }
    /**
     * 根據format, millis取時間String
     */
    fun getStringByLongFormat(format: String, second: Long) : String{
        return SimpleDateFormat(format).format(Date(second))
    }

    /**
     * 判斷date1是否在date2之前
     */
    fun isDateBefore(format: String, date1: String, date2: String) : Boolean{
        var long1 = getMillisByDateFormat(format, date1)
        var long2 = getMillisByDateFormat(format, date2)
        return long1 < long2
    }

    fun isToday(time: Long) : Boolean{
        var now = getNowMillis()
        var todayStartStr = getStringByLongFormat("yyyy-mm-dd", now)
        var todayStartLong = getMillisByDateFormat("yyyy-mm-dd", todayStartStr)
        var todayEnd = todayStartLong + 86400
        return time in todayStartLong until todayEnd
    }
}