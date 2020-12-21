package com.adam.lib.handler

import android.app.Activity
import android.os.Handler
import android.os.Message
import java.lang.ref.WeakReference

/**
 * Created By Adam on 2020/6/24
 */
//java的static class => kotlin的object(此类下的所有对象和函数，都是静态, 所有方法都为静态方法，如工具类、常量池)
object MyHandler : Handler() {
    lateinit var activityReference: WeakReference<Activity>
    lateinit var callback: com.adam.lib.handler.Callback
    override fun handleMessage(msg: Message) {
        var activity = activityReference.get()
        if(activity == null){
            return
        }
        callback.callback(msg)
    }
}
