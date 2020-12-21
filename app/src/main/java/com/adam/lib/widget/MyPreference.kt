package com.adam.lib.widget

import android.content.Context
import android.content.SharedPreferences

/**
 * 不推薦, 多進程操作可能數據丟失, 推薦contentprovider
 * Created By Adam on 2020/7/14
 */
class MyPreference {
    companion object{
        private var PREFERENCE_NAME = "com.adam.preference"
        private var sharedPreferences: SharedPreferences? = null
        fun sharedPreferences(context: Context): SharedPreferences {
            if (sharedPreferences == null) {
                //Context.MODE_PRIVATE(應用內使用), MODE_MULTI_PROCESS
                sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_MULTI_PROCESS)
            }
            return sharedPreferences!!
        }

        fun putString(context: Context, key: String, value: String): Boolean {
            val editor = sharedPreferences(context).edit()
            editor.putString(key, value)
            return editor.commit()
        }

        fun getString(context: Context, key: String): String? {
            return getString(context, key, null)
        }

        fun getString(context: Context, key: String, defaultValue: String?): String? {
            return sharedPreferences(context).getString(key, defaultValue)
        }
    }
}