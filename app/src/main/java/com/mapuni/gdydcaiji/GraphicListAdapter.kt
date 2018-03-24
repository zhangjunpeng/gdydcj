package com.mapuni.gdydcaiji

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mapuni.gdydcaiji.activity.BuildingDetail
import com.mapuni.gdydcaiji.activity.PoiDetail
import com.mapuni.gdydcaiji.activity.VillageDetail
import com.mapuni.gdydcaiji.bean.TBuildingInfo
import com.mapuni.gdydcaiji.bean.TPoiInfo
import com.mapuni.gdydcaiji.bean.TVillageInfo

/**
 * Created by zjp on 2018/3/23.
 * mail:zhangjunpeng92@163.com
 */
class GraphicListAdapter(context:Context,list:List<Map<String,Any>>): RecyclerView.Adapter<GraphicListAdapter.Viewholder>() {
    private var infoList:List<Map<String,Any>> = list

    private val context: Context=context

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): Viewholder {
        val view=LayoutInflater.from(context).inflate(R.layout.item_list_showgraphicinfo,parent,false)
        return Viewholder(view)
    }

    override fun getItemCount(): Int {
        return infoList.size
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
         val obj=infoList[position]["obj"]
        when(obj){
            is TBuildingInfo->{
                holder.name.text= obj.name
            }
            is TPoiInfo->{
                holder.name.text= obj.name
            }
            is TVillageInfo->{
                holder.name.text= obj.name
            }
        }
        holder.itemView.setOnClickListener {
            val intent=Intent()
            when(obj){
                is TBuildingInfo->{
                    intent.setClass(context,BuildingDetail::class.java)
                    intent.putExtra("resultBean",obj)
                }
                is TPoiInfo->{
                    intent.setClass(context,PoiDetail::class.java)
                    intent.putExtra("resultBean",obj)
                }
                is TVillageInfo->{
                    intent.setClass(context,VillageDetail::class.java)
                    intent.putExtra("resultBean",obj)
                }
            }
            context.startActivity(intent)
        }
    }

    inner class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name:TextView = itemView.findViewById(R.id.name_item_showgraphic)
    }
}