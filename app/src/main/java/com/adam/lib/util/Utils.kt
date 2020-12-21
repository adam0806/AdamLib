package com.adam.lib.util

import android.app.Activity
import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.net.Uri
import android.os.Build
import android.util.Log


/**
 * Created By Adam on 2020/7/6
 */
class Utils {
    companion object {
        val IS_HUAWEI = "isHuawei" //华为
        val IS_XIAOMI = "isXiaomi" //小米
        val IS_OPPO = "isOppo"  //oppo
        val IS_VIVO = "isVivo" //vivo
        val IS_MEIZU = "isMeizu" //魅族
        val IS_SAMSUNG = "isSamsung" //三星
        val IS_LETV = "isLetv" //乐视
        val IS_SMARTISAN = "isSmartisan" //锤子
        
        fun getPackageName(context: Context): String? {
            try {
                val am = context
                        .getSystemService(Activity.ACTIVITY_SERVICE) as ActivityManager
                val cn = am.getRunningTasks(1)[0].topActivity
                return cn!!.packageName
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }
        fun getProcessName(context: Context): String?{
            var processNameString = ""
            val mActivityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            var pid = android.os.Process.myPid()
            for (appProcess in mActivityManager.runningAppProcesses) {
                if (appProcess.pid == pid) {
                    processNameString = appProcess.processName
                }
            }
            Log.i("BasicApplication", "pid=$pid, processName=$processNameString")
            return processNameString
        }
        fun isAppRunning(context: Context, packageName: String): Boolean {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val list = am.getRunningTasks(100)
            if (list.size <= 0) {
                return false
            }
            for (info in list) {
                if (info.baseActivity!!.packageName == packageName) {
                    return true
                }
            }
            return false
        }

        //判断手机厂商
        fun checkPhoneFirm(): String {
            val phoneState = Build.BRAND.toLowerCase() //获取手机厂商
            if (phoneState == "huawei" || phoneState == "honor")
                return IS_HUAWEI
            else if (phoneState == "xiaomi" && Build.BRAND != null)
                return IS_XIAOMI
            else if (phoneState == "oppo" && Build.BRAND != null)
                return IS_OPPO
            else if (phoneState == "vivo" && Build.BRAND != null)
                return IS_VIVO
            else if (phoneState == "meizu" && Build.BRAND != null)
                return IS_MEIZU
            else if (phoneState == "samsung" && Build.BRAND != null)
                return IS_SAMSUNG
            else if (phoneState == "letv" && Build.BRAND != null)
                return IS_LETV
            else if (phoneState == "smartisan" && Build.BRAND != null)
                return IS_SMARTISAN

            return ""
        }

        /**
         * 跳转到指定应用的首页
         */
        fun showActivity(packageName: String, context: Context) {
            val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            context.startActivity(intent)
        }

        /**
         * 跳转到指定应用的指定页面
         */
        fun showActivity(packageName: String, activityDir: String, context: Context) {
            val intent = Intent()
            intent.component = ComponentName(packageName, activityDir)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }

        //前往设置管理
        fun gotoWhiteListSetting(context: Context) {
            if (checkPhoneFirm().equals(IS_HUAWEI)) {
                try {
                    showActivity("com.huawei.systemmanager", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity", context)
                } catch (e: Exception) {
                    showActivity("com.huawei.systemmanager",
                            "com.huawei.systemmanager.optimize.bootstart.BootStartActivity", context)
                }

            } else if (checkPhoneFirm().equals(IS_XIAOMI)) {
                showActivity("com.miui.securitycenter",
                        "com.miui.permcenter.autostart.AutoStartManagementActivity", context)
            } else if (checkPhoneFirm().equals(IS_OPPO)) {
                //oppo:操作步骤：权限隐私 -> 自启动管理 -> 允许应用自启动
                try {
                    showActivity("com.coloros.phonemanager", context)
                } catch (e: Exception) {
                    try {
                        showActivity("com.oppo.safe", context)
                    } catch (e2: Exception) {
                        try {
                            showActivity("com.coloros.oppoguardelf", context)
                        } catch (e3: Exception) {
                            showActivity("com.coloros.safecenter", context)
                        }

                    }

                }

            } else if (checkPhoneFirm().equals(IS_VIVO)) {
                //vivo:操作步骤：权限管理 -> 自启动 -> 允许应用自启动
                showActivity("com.iqoo.secure", context)
            } else if (checkPhoneFirm().equals(IS_MEIZU)) {
                //魅族:操作步骤：权限管理 -> 后台管理 -> 点击应用 -> 允许后台运行
                showActivity("com.meizu.safe", context)
            } else if (checkPhoneFirm().equals(IS_SAMSUNG)) {
                //三星:操作步骤：自动运行应用程序 -> 打开应用开关 -> 电池管理 -> 未监视的应用程序 -> 添加应用
                try {
                    showActivity("com.samsung.android.sm_cn", context)
                } catch (e: Exception) {
                    showActivity("com.samsung.android.sm", context)
                }

            } else if (checkPhoneFirm().equals(IS_LETV)) {
                //乐视:操作步骤：自启动管理 -> 允许应用自启动
                showActivity("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity", context)
            } else if (checkPhoneFirm().equals(IS_SMARTISAN)) {
                //锤子:操作步骤：权限管理 -> 自启动权限管理 -> 点击应用 -> 允许被系统启动
                showActivity("com.smartisanos.security", context)
            }
        }

        /**
         * 检测是否安装支付宝
         * @param context
         * @return
         */
        fun isAliPayInstalled(context: Context): Boolean {
            val uri = Uri.parse("alipays://platformapi/startApp")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            val componentName = intent.resolveActivity(context.packageManager)
            return componentName != null
        }

        /**
         * 检测是否安装微信
         * @param context
         * @return
         */
        fun isWeixinAvilible(context: Context): Boolean {
            val packageManager = context.packageManager// 获取packagemanager
            val pinfo = packageManager.getInstalledPackages(0)// 获取所有已安装程序的包信息
            if (pinfo != null) {
                for (i in pinfo.indices) {
                    val pn = pinfo[i].packageName
                    if (pn == "com.tencent.mm") {
                        return true
                    }
                }
            }
            return false
        }
        fun isDebugVersion(context: Context): Boolean {
            try {
                val info = context.applicationInfo
                return info.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return false
        }

    }

}