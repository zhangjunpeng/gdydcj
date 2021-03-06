package com.mapuni.gdydcaiji.adapter

import android.app.Activity
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import cn.qqtheme.framework.picker.ColorPicker
import com.mapuni.gdydcaiji.R
import com.mapuni.gdydcaiji.bean.EventBZ
import com.mapuni.gdydcaiji.utils.Utils.init
import org.greenrobot.eventbus.EventBus

/**
 * Created by zjp on 2018/3/26.
 * mail:zhangjunpeng92@163.com
 */
class BZRecyAdapter(context: Context, type: Int, currentTvColor: Int, checkedArray: ArrayList<Boolean>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val context = context
    private val checkedArray = checkedArray
    private val type = type
    lateinit var property: List<String>
    private var currentTvColor: Int = currentTvColor

    init {
        when (type) {
            0 -> {
                property = listOf("名称", "楼宇类型", "楼宇性质", "分类", "地址", "单元号", "电话", "等级", "楼宇层数", "楼宇住户数", "采集时间")
            }
            1 -> {
                property = listOf("名称", "起始站", "终点站", "采集时间")
            }
            2 -> {
                property = listOf("名称", "地址", "分类", "物业信息", "联系电话", "楼栋数", "采集时间")
            }

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            1 -> {
                val view = LayoutInflater.from(context).inflate(R.layout.item_list_showgraphicinfo, parent, false)
                return Viewholder(view)
            }
            -1 -> {
                val view = LayoutInflater.from(context).inflate(R.layout.item_color_buttom, parent, false)

                return DeleteView(view)
            }
            else -> {
                val view = LayoutInflater.from(context).inflate(R.layout.item_list_showgraphicinfo, parent, false)

                return Viewholder(view)

            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < property.size) {
            1
        } else {
            -1
        }
    }

    override fun getItemCount(): Int {
        return property.size + 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position < property.size) {
            holder as Viewholder
            holder.name.text = property[position]
            holder.isdetet.isChecked = checkedArray[position]
            holder.isdetet.setOnCheckedChangeListener { _, isChecked ->
                checkedArray[position] = isChecked
            }
        } else {
            holder as DeleteView
            holder.tvColor.setBackgroundColor(currentTvColor)
            holder.tvColor.setOnClickListener {
                var picker = ColorPicker(context as Activity?)
                picker.setInitColor(currentTvColor)
                picker.setOnColorPickListener { pickedColor ->
                    currentTvColor = pickedColor
                    holder.tvColor.setBackgroundColor(pickedColor)
                }
                picker.show()
            }
            holder.deleteButton.text = "确定"
            holder.deleteButton.setOnClickListener {
                val eventBZ = EventBZ()
                eventBZ.booleanArrayList = checkedArray
                eventBZ.type = type
                eventBZ.tvColor = currentTvColor
                EventBus.getDefault().post(eventBZ)
            }
        }

    }

    inner class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView = itemView.findViewById(R.id.name_item_showgraphic)
        var isdetet: CheckBox = itemView.findViewById(R.id.check_isdelete)
    }

    inner class DeleteView(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvColor = itemView.findViewById<TextView>(R.id.tv_color)
        var deleteButton: Button = itemView.findViewById(R.id.button_delete_item)
    }
}