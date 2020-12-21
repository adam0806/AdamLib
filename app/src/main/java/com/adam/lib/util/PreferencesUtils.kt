package com.adam.lib.util

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils

/**
 * Created By Adam on 2020/12/9
 */
object PreferencesUtils {
    private lateinit var context: Context
    private var PREFERENCE_NAME = "adam.preference.ds"
    private var sharedPreferences: SharedPreferences? = null
    fun initSharedPreferenceName(context: Context, name: String) {
        PreferencesUtils.context = context
        if (!TextUtils.isEmpty(name) && PREFERENCE_NAME != name) {
            PREFERENCE_NAME = name
            sharedPreferences = null
        }
    }
    private fun sharedPreferences(context: Context): SharedPreferences? {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        }
        return sharedPreferences
    }
    fun putString(key: String?, value: String?): Boolean {
        val editor = sharedPreferences(context!!)!!.edit()
        editor.putString(key, value)
        return editor.commit()
    }
    fun getString(key: String?): String{
        return getString(key, null)
    }
    fun getString(key: String?, defaultValue: String?): String{
        return sharedPreferences(context!!)!!.getString(key, defaultValue) ?: ""
    }
    fun putInt(key: String?, value: Int): Boolean {
        val editor = sharedPreferences(context!!)!!.edit()
        editor.putInt(key, value)
        return editor.commit()
    }
    fun getInt(key: String?): Int {
        return getInt(key, -1)
    }
    fun getInt(key: String?, defaultValue: Int): Int {
        return sharedPreferences(context!!)!!.getInt(key, defaultValue)
    }
    fun putLong(key: String?, value: Long): Boolean {
        val editor = sharedPreferences(context!!)!!.edit()
        editor.putLong(key, value)
        return editor.commit()
    }
    fun getLong(key: String?): Long {
        return getLong(key, -1)
    }
    fun getLong(key: String?, defaultValue: Long): Long {
        return sharedPreferences(context!!)!!.getLong(key, defaultValue)
    }
    fun putFloat(key: String?, value: Float): Boolean {
        val editor = sharedPreferences(context!!)!!.edit()
        editor.putFloat(key, value)
        return editor.commit()
    }
    fun getFloat(key: String?): Float {
        return getFloat(key, -1f)
    }
    fun getFloat(key: String?, defaultValue: Float): Float {
        return sharedPreferences(context!!)!!.getFloat(key, defaultValue)
    }
    fun putBoolean(key: String?, value: Boolean): Boolean {
        val editor = sharedPreferences(context!!)!!.edit()
        editor.putBoolean(key, value)
        return editor.commit()
    }
    fun getBoolean(key: String?): Boolean {
        return getBoolean(key, false)
    }
    fun getBoolean(key: String?, defaultValue: Boolean): Boolean {
        return sharedPreferences(context!!)!!.getBoolean(key, defaultValue)
    }

    fun clear(context: Context?): Boolean {
        return sharedPreferences(context!!)!!.edit().clear().commit()
    }
}