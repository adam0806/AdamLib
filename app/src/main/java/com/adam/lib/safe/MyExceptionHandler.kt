package com.adam.lib.safe

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.util.Log
import com.adam.lib.util.ActivityManagerUtil
import java.io.PrintWriter
import java.io.StringWriter
import java.lang.ref.WeakReference
import java.util.*
import android.os.Looper
import com.adam.lib.widget.MyPreference
import java.lang.Exception


/**
 * Created By Adam on 2020/8/19
 */
enum class MyExceptionHandler{
    INSTANCE;
    companion object{
        private val MAX_LOG_SIZE = 5000
        private var minTimeBetweenCrashesMs = 3000
        private val INTENT_ACTION_ERROR_ACTIVITY = "com.adam.lib.safe.ErrorActivity"
        private val SHARED_PREFERENCES_FILE = "custom_activity_on_crash"
        private val SHARED_PREFERENCES_FIELD_TIMESTAMP = "last_crash_timestamp"
        private val TAG = javaClass.simpleName
        var enableCustomException = false
        private var lastActivityCreated = WeakReference<Activity>(null)
    }

    var context: Context? = null
    private var oldHandler: Thread.UncaughtExceptionHandler? = null

    fun install(context: Context){
        oldHandler = Thread.getDefaultUncaughtExceptionHandler()
        this.context = context
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            Log.d("MyExceptionHandler", "MyExceptionHandler, uncaughtException")

            val errorReport: String = getErrorReport(e)
            saveLogSharePreference(errorReport)

            if(hasCrashedInTheLastSeconds(context) || !enableCustomException){
                oldHandler?.uncaughtException(t, e)
                return@setDefaultUncaughtExceptionHandler
            }else{
                setLastCrashTimestamp(context, Date().time)

                var errorActivityClass = guessErrorActivityClass(context)
                if(isStackTraceLikelyConflictive(e, errorActivityClass)){
                    Log.e(TAG, "Your application class or your error activity have crashed, the custom activity will not be launched!")
                    if (oldHandler != null) {
                        oldHandler?.uncaughtException(t, e)
                        return@setDefaultUncaughtExceptionHandler
                    }
                }else {
                    try {
                        goToErrorPage(errorReport, errorActivityClass)
                    }catch (e:Exception){
                        e.printStackTrace()
                    }finally {
                        closeProcess()
                    }
                }
            }
        }
    }
    private fun isStackTraceLikelyConflictive(throwable: Throwable, activityClass: Class<out Activity>): Boolean {
        var throwable: Throwable? = throwable
        throwable = throwable?.cause
        while (throwable != null){
            val stackTrace = throwable.stackTrace
            for (element in stackTrace) {
                if (element.className == "android.app.ActivityThread" && element.methodName == "handleBindApplication" || element.className == activityClass.name) {
                    return true
                }
            }
            throwable = throwable?.cause
        }
        return false
    }
    private fun getErrorActivityClassWithIntentFilter(context: Context): Class<out Activity>? {
        val searchedIntent = Intent().setAction(INTENT_ACTION_ERROR_ACTIVITY).setPackage(context.packageName)
        val resolveInfos = context.packageManager.queryIntentActivities(searchedIntent,
                PackageManager.GET_RESOLVED_FILTER)

        if (resolveInfos != null && resolveInfos.size > 0) {
            val resolveInfo = resolveInfos[0]
            try {
                return Class.forName(resolveInfo.activityInfo.name) as Class<out Activity>
            } catch (e: ClassNotFoundException) {
                //Should not happen, print it to the log!
                Log.e(TAG, "Failed when resolving the error activity class via intent filter, stack trace follows!", e)
            }

        }

        return null
    }
    private fun guessErrorActivityClass(context: Context): Class<out Activity> {
        var resolvedActivityClass: Class<out Activity>?

        //If action is defined, use that
        resolvedActivityClass = getErrorActivityClassWithIntentFilter(context)

        //Else, get the default error activity
        if (resolvedActivityClass == null) {
            resolvedActivityClass = ErrorActivity::class.java
        }

        return resolvedActivityClass
    }
    private fun getLastCrashTimestamp(context: Context): Long {
        return context.getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE).getLong(SHARED_PREFERENCES_FIELD_TIMESTAMP, -1)
    }

    private fun setLastCrashTimestamp(context: Context, timestamp: Long) {
        context.getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE).edit().putLong(SHARED_PREFERENCES_FIELD_TIMESTAMP, timestamp).commit()
    }
    private fun hasCrashedInTheLastSeconds(context: Context): Boolean {
        val lastTimestamp = getLastCrashTimestamp(context)
        val currentTimestamp = Date().time

        return lastTimestamp <= currentTimestamp && currentTimestamp - lastTimestamp < minTimeBetweenCrashesMs
    }
    fun getErrorReport(e: Throwable): String{
        val stackTrace = StringWriter()
        e?.printStackTrace(PrintWriter(stackTrace))
        return "*** CAUSE OF ERROR ***\n" +
                stackTrace.toString() + "\n\n"
        "*** DEVICE INFORMATION ***\n" +
                "Brand: " + Build.BRAND + "\n"
        "Device: " + Build.DEVICE + "\n"
        "Model: " + Build.MODEL + "\n"
        "Id: " + Build.ID + "\n"
        "Product: " + Build.PRODUCT + "\n\n"
        "*** FIRMWARE ***\n" +
                "SDK: " + Build.VERSION.SDK + "\n"
        "Release: " + Build.VERSION.RELEASE + "\n"
        "Incremental: " +  Build.VERSION.INCREMENTAL + "\n"
    }
    fun saveLogSharePreference(errorReport: String){
        var oldException = MyPreference.getString(context!!, "Exception", "");
        var size = if(MAX_LOG_SIZE > errorReport.length) errorReport.length else MAX_LOG_SIZE
        MyPreference.putString(context!!, "Exception", oldException+"\n"+errorReport.substring(0, size));
    }
    fun goToErrorPage(errorReport: String, errorActivityClass: Class<out Activity>){
        Handler(Looper.getMainLooper()).post {
            invokeErrorActivity(errorReport, errorActivityClass)
        }
    }
    fun invokeErrorActivity(errorReport: String, errorActivityClass: Class<out Activity>){
        //todo 目前只能重啟, 沒法跳轉錯誤頁
        var intent = Intent(context, errorActivityClass::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra("text", errorReport)
        context?.startActivity(intent)
        ActivityManagerUtil.appManager.finishAllActivity()

//        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT)
//        val mgr = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        mgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, System.currentTimeMillis() + 500, pendingIntent)
    }
    fun closeProcess(){
        android.os.Process.killProcess(android.os.Process.myPid())
        System.exit(10);
    }
}