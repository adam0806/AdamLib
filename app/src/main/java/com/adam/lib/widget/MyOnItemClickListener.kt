package com.adam.lib.widget

/**
 * Created By Adam on 2020/11/27
 */
interface MyOnItemClickListener<T> {
    fun onItemClick(position: Int, data:T)
}