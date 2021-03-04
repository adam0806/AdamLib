package com.adam.lib.basic

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.adam.lib.handler.MyHandler
import com.adam.lib.util.ActivityManagerUtil
import com.adam.lib.util.LogUtils
import java.lang.ref.WeakReference

/**
 * Created By Adam on 2020/7/2
 */
abstract class BasicActivity : AppCompatActivity(){
    protected var TAG = javaClass.simpleName
    val MESSAGE_MSG_UPDATE = 3
    val MESSAGE_PROGRESS_UPDATE = 4
    var mFragmenTags:MutableList<String>? = null

    abstract fun getBackgrounResource(): Int
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogUtils.d(TAG, "Lifecycle, onCreate: $TAG", LogUtils.LIFECYCLE)
        getWindow().getDecorView().setBackgroundResource(getBackgrounResource())
        ActivityManagerUtil.appManager.addActivity(this)
        MyHandler.activityReference = WeakReference(this)
        mFragmenTags = ArrayList()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        LogUtils.d(TAG, "Lifecycle, onActivityResult: $TAG", LogUtils.LIFECYCLE)
        if(!supportFragmentManager.fragments.isNullOrEmpty()) {
            for (fragment in supportFragmentManager.fragments){
                fragment.onActivityResult(requestCode, resultCode, data)
            }
        }
    }
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        LogUtils.d(TAG, "Lifecycle, onAttachedToWindow: $TAG", LogUtils.LIFECYCLE)
    }

    override fun onStart() {
        super.onStart()
        LogUtils.d(TAG, "Lifecycle, onStart: $TAG", LogUtils.LIFECYCLE)
    }

    override fun onRestart() {
        super.onRestart()
        LogUtils.d(TAG, "Lifecycle, onRestart: $TAG", LogUtils.LIFECYCLE)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        LogUtils.d(TAG, "Lifecycle, onRestoreInstanceState: $TAG", LogUtils.LIFECYCLE)
    }
    override fun onResume() {
        super.onResume()
        LogUtils.d(TAG, "Lifecycle, onResume: $TAG", LogUtils.LIFECYCLE)
    }
    override fun onPause() {
        super.onPause()
        LogUtils.d(TAG, "Lifecycle, onPause: $TAG", LogUtils.LIFECYCLE)
    }

    override fun onStop() {
        super.onStop()
        LogUtils.d(TAG, "Lifecycle, onStop: $TAG", LogUtils.LIFECYCLE)
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtils.d(TAG, "Lifecycle, onDestroy: $TAG", LogUtils.LIFECYCLE)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LogUtils.d(TAG, "Lifecycle, onConfigurationChanged $TAG, orientation: "+newConfig.orientation)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        LogUtils.d(TAG, "Lifecycle, onSaveInstanceState $TAG")
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        LogUtils.d(TAG, "Lifecycle, onDetachedFromWindow: $TAG", LogUtils.LIFECYCLE)
    }
}