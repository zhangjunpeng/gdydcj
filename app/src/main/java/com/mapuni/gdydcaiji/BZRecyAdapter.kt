package com.mapuni.gdydcaiji

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

/**
 * Created by zjp on 2018/3/26.
 * mail:zhangjunpeng92@163.com
 */
class BZRecyAdapter(type:Int): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    init {

    }
    val property_poi= listOf("名称")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(null)
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

    }
    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

    }
}