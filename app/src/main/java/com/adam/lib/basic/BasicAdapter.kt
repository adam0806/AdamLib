package com.adam.lib.basic

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adam.lib.widget.MyOnItemClickListener

/**
 * Created By Adam on 2020/11/26
 */
abstract class BasicAdapter<T, VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>(){
    protected var list:MutableList<T> = ArrayList()
    private lateinit var onClickListener: MyOnItemClickListener<T>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val inflater = LayoutInflater.from(parent.context)
        val v = createView(inflater, parent, viewType)
        val holder = createHolder(v, viewType)
        return holder;
    }
    fun setOnItemClickListener(onClickListener: MyOnItemClickListener<T>){
        this.onClickListener = onClickListener
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        var data = list[position]
        bindData(holder, data, position)
        holder.itemView.setOnClickListener{
            View.OnClickListener { v->
                onClickListener.onItemClick(position, data)
            }
        }
    }

    fun append(list:MutableList<T>){
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    abstract fun createView(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): View
    abstract fun createHolder(v: View, viewType: Int): VH
    abstract fun bindData(holder: VH, data: T, position: Int)
}