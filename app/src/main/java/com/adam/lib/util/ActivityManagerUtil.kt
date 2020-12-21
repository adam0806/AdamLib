package com.adam.lib.util

import android.app.Activity
import java.util.*

/**
 * Created By Adam on 2020/7/15
 */
class ActivityManagerUtil private constructor() {
    val count: Int
        get() = activityStack!!.size

    /**
     * 添加Activity到堆栈
     */
    fun addActivity(activity: Activity) {
        if (activityStack == null) {
            activityStack = Stack()
        }
        val iterator = activityStack!!.iterator()
        while (iterator.hasNext()) {
            val type = iterator.next() as Activity
            if (type == activity) {
                iterator.remove()
            }
        }
        activityStack!!.add(activity)
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    fun currentActivity(): Activity? {
        return if (activityStack != null) {
            activityStack!!.lastElement()
        } else null
    }

    /**
     * 移除指定Activity
     *
     * @param activity
     */
    fun removeActivity(activity: Activity) {
        if (activityStack != null) {
            activityStack!!.remove(activity)
        }
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    fun finishActivity() {
        if (activityStack != null) {
            val activity = activityStack!!.lastElement()
            finishActivity(activity)
        }
    }

    /**
     * 结束指定的Activity
     */
    fun finishActivity(activity: Activity?) {
        if (activity != null && activityStack != null) {
            activityStack!!.remove(activity)
            activity.finish()
        }
    }

    /**
     * 结束指定类名的Activity
     */
    fun finishActivity(cls: Class<*>) {
        if (activityStack != null) {
            val iterator = activityStack!!.iterator()
            while (iterator.hasNext()) {
                val tempActivity = iterator.next() as Activity
                if (tempActivity.javaClass == cls) {
                    iterator.remove()
                    tempActivity.finish()
                    break
                }
            }
        }
    }

    /**
     * 结束所有Activity
     */
    fun finishAllActivity() {
        if (activityStack != null) {
            var i = 0
            val size = activityStack!!.size
            while (i < size) {
                if (null != activityStack!![i]) {
                    activityStack!![i].finish()
                }
                i++
            }
            activityStack!!.clear()
        }
    }

    fun getActivityStack(): Stack<Activity>? {
        return activityStack
    }

    /**
     * 退出应用程序
     */
    fun AppExit(isDowning: Boolean) {
        try {
            finishAllActivity()
            if (!isDowning) {
                System.exit(0)
            }
        } catch (e: Exception) {
        }

    }

    companion object {

        private var activityStack: Stack<Activity>? = Stack()
        private var instance: ActivityManagerUtil? = null

        val appManager: ActivityManagerUtil
            get() {
                if (instance == null) {
                    synchronized(ActivityManagerUtil::class.java) {
                        instance = ActivityManagerUtil()
                    }
                }
                return instance!!
            }
    }
}