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
import com.mapuni.gdydcaiji.activity.LineDetail
import com.mapuni.gdydcaiji.activity.PoiDetail
import com.mapuni.gdydcaiji.activity.SocialDetail
import com.mapuni.gdydcaiji.bean.*
import org.greenrobot.eventbus.EventBus

class MapChoseListAdapter(context: Context, list1: ArrayList<String>,list2: ArrayList<String>, dialog: Dialog) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val fileDirNames = list1
    private val fileDirPaths = list2

    private val context: Context = context

    private val dialog: Dialog = dialog

    private val showMapList=ArrayList<String>()

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
        return fileDirNames.size+1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position<fileDirNames.size){
            1
        }else{
            -1
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position<fileDirNames.size) {
            holder as Viewholder
            val filepath=fileDirPaths[position]
            holder.name.text= fileDirNames[position]

            holder.isdetet.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    showMapList.add(filepath)
                } else {
                    if (showMapList.contains(filepath)) {
                        showMapList.remove(filepath)
                    }
                }
            }
        }else{
            holder as DeleteView
            holder.deleteButton.text="确定"
            holder.deleteButton.setOnClickListener{
                val eventShowMap=EventShowMap()
                eventShowMap.filePahts=showMapList
                EventBus.getDefault().post(eventShowMap)
                dialog.dismiss()
            }
        }
    }

    inner class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView = itemView.findViewById(R.id.name_item_showgraphic)
        var isdetet: CheckBox =itemView.findViewById(R.id.check_isdelete)
    }
    inner class DeleteView(itemView: View): RecyclerView.ViewHolder(itemView){
        var deleteButton: Button =itemView.findViewById(R.id.button_delete_item)
    }
}