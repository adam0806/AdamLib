package com.adam.lib.basic

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adam.lib.R
import com.adam.lib.widget.MyItemDecoration
import com.adam.lib.widget.MyOnItemClickListener
import kotlinx.android.synthetic.main.recyclerview.*


/**
 * Created By Adam on 2020/11/25
 */
abstract class BasicListFragment<T, VH : RecyclerView.ViewHolder> : BasicFragment(), MyOnItemClickListener<T> {
    private lateinit var adapter: BasicAdapter<T, VH>
    private lateinit var list: MutableList<T>
    protected lateinit var onClickListener: MyOnItemClickListener<T>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.layoutManager = getLayoutManager()
        adapter = createAdapter()
        adapter.setOnItemClickListener( object : MyOnItemClickListener<T>{
            override fun onItemClick(position: Int, data: T) {
                this@BasicListFragment.onItemClick(position, data)
            }
        })
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(MyItemDecoration(3))
    }
    override fun getLayoutResId(): Int {
        return R.layout.recyclerview
    }

    abstract fun createAdapter(): BasicAdapter<T, VH>
    fun getLayoutManager(): RecyclerView.LayoutManager {
        var layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = RecyclerView.VERTICAL
        return layoutManager
    }

    fun setList(list: MutableList<T>) {
        adapter.append(list)
    }
    fun setOnItemClickListener(onClickListener: MyOnItemClickListener<T>){
        this.onClickListener = onClickListener
    }
    override fun onItemClick(position: Int, data: T) {
        onClickListener.onItemClick(position, data)
    }
}