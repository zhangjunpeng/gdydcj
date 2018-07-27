package com.mapuni.gdydcaiji.adapter

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.mapuni.gdydcaiji.R
import com.mapuni.gdydcaiji.activity.LineDetail
import com.mapuni.gdydcaiji.activity.PoiDetail
import com.mapuni.gdydcaiji.activity.SocialDetail
import com.mapuni.gdydcaiji.bean.TbLine
import com.mapuni.gdydcaiji.bean.TbPoint
import com.mapuni.gdydcaiji.bean.TbSurface

/**
 * Created by zjp on 2018/4/16.
 * mail:zhangjunpeng92@163.com
 */
class OnlyShowAdapter(context: Context, list: List<Map<String, Any>>,dialog: Dialog): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var infoList: List<Map<String, Any>> = list

    private val context: Context = context
    private val dialog:Dialog=dialog

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_list_showgraphicinfo, parent, false)
        return Viewholder(view)
    }

    override fun getItemCount(): Int {
        return  infoList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {

        holder as Viewholder
        val obj = infoList[position]["obj"]
        when (obj) {
            is TbPoint -> {
                holder.name.text = obj.name
            }
            is TbLine -> {
                holder.name.text = obj.name
            }
            is TbSurface -> {
                holder.name.text = obj.name
            }

        }
        holder.itemView.setOnClickListener {
            val intent = Intent()
            when (obj) {
                is TbPoint -> {
                    intent.setClass(context, PoiDetail::class.java)
                    intent.putExtra("resultBean", obj)
                }
                is TbLine -> {
                    intent.setClass(context, LineDetail::class.java)
                    intent.putExtra("resultBean", obj)
                }
                is TbSurface -> {
                    intent.setClass(context, SocialDetail::class.java)
                    intent.putExtra("resultBean", obj)
                }

            }
            context.startActivity(intent)
            if (dialog != null && dialog.isShowing) {
                dialog.dismiss()
            }
        }

        holder.isdetet.visibility=View.INVISIBLE
    }

    inner class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView = itemView.findViewById(R.id.name_item_showgraphic)
        var isdetet: CheckBox =itemView.findViewById(R.id.check_isdelete)
    }
}