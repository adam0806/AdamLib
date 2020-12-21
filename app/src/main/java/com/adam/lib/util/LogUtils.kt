package com.adam.lib.util

import android.text.TextUtils
import android.text.format.DateFormat
import android.util.Log
import java.io.PrintWriter
import java.io.StringWriter
import java.io.Writer

/**
 * Created by woniu on 16/12/20.
 */
object LogUtils {
    /**
     * 允許打印的DEBUG標籤
     */
    val DETAULT = 0
    val LIFECYCLE = 1   //BasicActivity, BasicFragment
    val ENABLE_DEBUG_TAGS = intArrayOf(DETAULT, LIFECYCLE)

    /**
     * isWrite:用于开关是否吧日志写入txt文件中
     */
    var isWrite = false

    /**
     * isDebug :是用来控制，是否打印日志
     */
    var isDeBug = false

    /**
     * 存放日志文件的所在路径
     */
    private const val DIRPATH = "/log"

    /**
     * 存放日志的文本名
     */
    private const val LOGNAME = "log.txt"

    /**
     * 设置时间的格式
     */
    private const val INFORMAT = "yyyy-MM-dd HH:mm:ss"

    /**
     * VERBOSE日志形式的标识符
     */
    const val VERBOSE = 5

    /**
     * DEBUG日志形式的标识符
     */
    const val DEBUG = 4

    /**
     * INFO日志形式的标识符
     */
    const val INFO = 3

    /**
     * WARN日志形式的标识符
     */
    const val WARN = 2

    /**
     * ERROR日志形式的标识符
     */
    const val ERROR = 1

    /**
     * 把异常用来输出日志的综合方法
     *
     * @param @param tag 日志标识
     * @param @param throwable 抛出的异常
     * @param @param type 日志类型
     * @return void 返回类型
     * @throws
     */
    fun log(tag: String, throwable: Throwable, type: Int) {
        log(tag, exToString(throwable), type)
    }

    /**
     * 用来输出日志的综合方法（文本内容）
     *
     * @param @param tag 日志标识
     * @param @param msg 要输出的内容
     * @param @param type 日志类型
     * @return void 返回类型
     * @throws
     */
    fun log(tag: String, msg: String, type: Int) {
        when (type) {
            VERBOSE -> v(tag, msg) // verbose等级
            DEBUG -> d(tag, msg) // debug等级
            INFO -> i(tag, msg) // info等级
            WARN -> w(tag, msg) // warn等级
            ERROR -> e(tag, msg) // error等级
            else -> {
            }
        }
    }

    /**
     * verbose等级的日志输出
     *
     * @param tag 日志标识
     * @param msg 要输出的内容
     * @return void 返回类型
     * @throws
     */
    fun v(tag: String, msg: String) {
        v(tag, msg, DETAULT)
    }

    fun v(tag: String, msg: String, enableTag: Int) {
        // 是否开启日志输出
        if (isDeBug && debugAble(enableTag)) {
            Log.v(tag, msg)
        }
        // 是否将日志写入文件
        if (isWrite && debugAble(enableTag)) {
            write(tag, msg)
        }
    }

    /**
     * debug等级的日志输出
     *
     * @param tag 标识
     * @param msg 内容
     * @return void 返回类型
     * @throws
     */
    fun d(tag: String, msg: String) {
        d(tag, msg, DETAULT)
    }

    fun d(tag: String, msg: String, enableTag: Int) {
        if (isDeBug && debugAble(enableTag)) {
            Log.d(tag, msg)
        }
        if (isWrite && debugAble(enableTag)) {
            write(tag, msg)
        }
    }

    fun debugAble(type: Int): Boolean {
        for (enableTag in ENABLE_DEBUG_TAGS) {
            if (type == enableTag) {
                return true
            }
        }
        return false
    }

    /**
     * info等级的日志输出
     *
     * @param tag 标识
     * @param msg 内容
     * @return void 返回类型
     * @throws
     */
    fun i(tag: String, msg: String) {
        i(tag, msg, DETAULT)
    }

    fun i(tag: String, msg: String, enableTag: Int) {
        if (isDeBug && debugAble(enableTag)) {
            Log.i(tag, msg)
        }
        if (isWrite && debugAble(enableTag)) {
            write(tag, msg)
        }
    }

    /**
     * warn等级的日志输出
     *
     * @param tag 标识
     * @param msg 内容
     * @return void 返回类型
     * @throws
     */
    fun w(tag: String, msg: String) {
        w(tag, msg, DETAULT)
    }

    fun w(tag: String, msg: String, enableTag: Int) {
        if (isDeBug && debugAble(enableTag)) {
            Log.w(tag, msg)
        }
        if (isWrite && debugAble(enableTag)) {
            write(tag, msg)
        }
    }

    /**
     * error等级的日志输出
     *
     * @param tag 标识
     * @param msg 内容
     * @return void 返回类型
     */
    fun e(tag: String, msg: String) {
        e(tag, msg, DETAULT)
    }

    fun e(tag: String, msg: String, enableTag: Int) {
        if (isDeBug && debugAble(enableTag)) {
            Log.w(tag, msg)
        }
        if (isWrite && debugAble(enableTag)) {
            write(tag, msg)
        }
    }

    /**
     * 用于把日志内容写入制定的文件
     *
     * @param @param tag 标识
     * @param @param msg 要输出的内容
     * @return void 返回类型
     * @throws
     */
    fun write(tag: String, msg: String) {
        val path: String = FileUtils.createMkdirsAndFiles(DIRPATH, LOGNAME)
        if (TextUtils.isEmpty(path)) {
            return
        }
        val log = (DateFormat.format(INFORMAT, System.currentTimeMillis())
                .toString() + tag
                + "========>>"
                + msg
                + "\n=================================分割线=================================")
        FileUtils.write2File(path, log, true)
    }

    /**
     * 用于把日志内容写入制定的文件
     *
     *
     * 标签
     *
     * @param ex 异常
     */
    fun write(ex: Throwable) {
        write("", exToString(ex))
    }

    /**
     * 把异常信息转化为字符串
     *
     * @param ex 异常信息
     * @return 异常信息字符串
     */
    private fun exToString(ex: Throwable): String {
        val writer: Writer = StringWriter()
        val printWriter = PrintWriter(writer)
        ex.printStackTrace(printWriter)
        printWriter.close()
        return writer.toString()
    }

    val LINE_SEPARATOR = System.getProperty("line.separator") /*public static void printLine(String tag, boolean isTop) {
        if (isTop) {
            Log.d(tag, "╔═══════════════════════════════════════════════════════════════════════════════════════");
        } else {
            Log.d(tag, "╚═══════════════════════════════════════════════════════════════════════════════════════");
        }
    }*/
    /**
     * 格式化输出Json
     *
     * @param tag
     * @param msg
     * @param headString
     */
    /* public static void printJson(String tag, String msg, String headString) {
        String message;

        try {
            if (msg.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(msg);
                message = jsonObject.toString(4);//最重要的方法，就一行，返回格式化的json字符串，其中的数字4是缩进字符数
            } else if (msg.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(msg);
                message = jsonArray.toString(4);
            } else {
                message = msg;
            }
        } catch (JSONException e) {
            message = msg;
        }

        printLine(tag, true);
        message = headString + LINE_SEPARATOR + message;
        String[] lines = message.split(LINE_SEPARATOR);
        for (String line : lines) {
            Log.d(tag, "║ " + line);
        }
        printLine(tag, false);
    }*/
}