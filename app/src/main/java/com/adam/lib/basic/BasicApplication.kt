package com.adam.lib.basic

import android.app.Application
import com.adam.lib.util.AlertUtils
import com.adam.lib.util.LogUtils
import com.adam.lib.util.PreferencesUtils
import com.adam.lib.util.Utils


/**
 * Created By Adam on 2020/7/2
 */
abstract class BasicApplication : Application(){
    //通用初始化
    override fun onCreate() {
        super.onCreate()
        Utils.getProcessName(applicationContext)

        AlertUtils.init(this)

        if (Utils.isDebugVersion(this)) {
            LogUtils.isDeBug = true
        }
        PreferencesUtils.initSharedPreferenceName(this, packageName)
    }
}