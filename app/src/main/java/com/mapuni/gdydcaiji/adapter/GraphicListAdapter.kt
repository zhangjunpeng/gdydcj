package com.mapuni.gdydcaiji.adapter

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mapuni.gdydcaiji.R
import com.mapuni.gdydcaiji.activity.*
import com.mapuni.gdydcaiji.bean.*
import com.mapuni.gdydcaiji.utils.DateUtil
import com.mapuni.gdydcaiji.utils.FileUtils
import com.mapuni.gdydcaiji.utils.PathConstant
import org.greenrobot.eventbus.EventBus
import java.util.HashMap

/**
 * Created by zjp on 2018/3/23.
 * mail:zhangjunpeng92@163.com
 */
class GraphicListAdapter(context: Context, list: List<Map<String, Any>>, dialog: Dialog, MODE: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var infoList: List<Map<String, Any>> = list

    private val context: Context = context

    private val dialog: Dialog = dialog

    private val deleteList = ArrayList<Any>()

    private val MODE: Int = MODE

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            1 -> {
                val view = LayoutInflater.from(context).inflate(R.layout.item_list_showgraphicinfo, parent, false)
                return Viewholder(view)
            }
            -1 -> {
                val view = LayoutInflater.from(context).inflate(R.layout.item_deletebutton, parent, false)

                return DeleteView(view)
            }
            else -> {
                val view = LayoutInflater.from(context).inflate(R.layout.item_list_showgraphicinfo, parent, false)

                return Viewholder(view)

            }
        }

    }

    override fun getItemCount(): Int {
        return infoList.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < infoList.size) {
            1
        } else {
            -1
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position < infoList.size) {
            holder as Viewholder
            val obj = infoList[position]["obj"]

            when (obj) {
                is TbPoint -> {
//                    if (MODE == 2 && obj.authcontent)
                    holder.name.text = obj.name + "(点" + if ((MODE == 2 || MODE == 8) && !TextUtils.isEmpty(obj.authcontent)) "/已质检)" else ")"
                }
                is TbLine -> {
                    holder.name.text = obj.name + "(线" + if ((MODE == 2 || MODE == 8) && !TextUtils.isEmpty(obj.authcontent)) "/已质检)" else ")"
                }
                is TbSurface -> {
                    holder.name.text = obj.name + "(面" + if ((MODE == 2 || MODE == 8) && !TextUtils.isEmpty(obj.authcontent)) "/已质检)" else ")"
                }

            }
            holder.itemView.setOnClickListener {
                val intent = Intent()
                when (obj) {
                    is TbPoint -> {
                        intent.setClass(context, PoiDetail::class.java)
                        intent.putExtra("resultBm", obj.bm)
                    }
                    is TbLine -> {
                        intent.setClass(context, LineDetail::class.java)
                        intent.putExtra("resultBm", obj.bm)
                    }
                    is TbSurface -> {
                        intent.setClass(context, SocialDetail::class.java)
                        intent.putExtra("resultBm", obj.bm)
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
        } else {
            holder as DeleteView
            holder.deleteButton.setOnClickListener {
                val eventDeleteInfo = EventDeleteInfo()
                eventDeleteInfo.deleteList = deleteList
                EventBus.getDefault().post(eventDeleteInfo)

            }
        }
    }

    inner class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView = itemView.findViewById(R.id.name_item_showgraphic)
        var isdetet: CheckBox = itemView.findViewById(R.id.check_isdelete)
    }

    inner class DeleteView(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var deleteButton: Button = itemView.findViewById(R.id.button_delete_item)
    }
}