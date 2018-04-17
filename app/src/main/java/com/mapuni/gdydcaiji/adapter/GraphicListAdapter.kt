package com.mapuni.gdydcaiji.adapter

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import com.mapuni.gdydcaiji.R
import com.mapuni.gdydcaiji.activity.*
import com.mapuni.gdydcaiji.bean.*
import org.greenrobot.eventbus.EventBus

/**
 * Created by zjp on 2018/3/23.
 * mail:zhangjunpeng92@163.com
 */
class GraphicListAdapter(context: Context, list: List<Map<String, Any>>, dialog: Dialog) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var infoList: List<Map<String, Any>> = list

    private val context: Context = context

    private val dialog: Dialog = dialog

    private val deleteList=ArrayList<Any>()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        when(viewType){
            1->{
                val view = LayoutInflater.from(context).inflate(R.layout.item_list_showgraphicinfo, parent, false)
                return Viewholder(view)
            }
            -1->{
                val view = LayoutInflater.from(context).inflate(R.layout.item_deletebutton, parent, false)

                return DeleteView(view)
            }
            else->{
                val view = LayoutInflater.from(context).inflate(R.layout.item_list_showgraphicinfo, parent, false)

                return Viewholder(view)

            }
        }

    }

    override fun getItemCount(): Int {
        return infoList.size+1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position<infoList.size){
            1
        }else{
            -1
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position<infoList.size) {
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
            holder.isdetet.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    if (obj != null) {
                        deleteList.add(obj)
                    }
                } else {
                    if (deleteList.contains(obj)) {
                        deleteList.remove(obj)
                    }
                }
            }
        }else{
            holder as DeleteView
            holder.deleteButton.setOnClickListener{
                val eventDeleteInfo=EventDeleteInfo()
                eventDeleteInfo.deleteList=deleteList
                EventBus.getDefault().post(eventDeleteInfo)

            }
        }
    }

    inner class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView = itemView.findViewById(R.id.name_item_showgraphic)
        var isdetet:CheckBox=itemView.findViewById(R.id.check_isdelete)
    }
    inner class DeleteView(itemView: View):RecyclerView.ViewHolder(itemView){
        var deleteButton:Button =itemView.findViewById(R.id.button_delete_item)
    }
}