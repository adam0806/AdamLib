package com.adam.lib.handler

import android.os.Message

/**
 * Created By Adam on 2020/6/24
 */
interface Callback{
    fun callback(msg: Message?)
}