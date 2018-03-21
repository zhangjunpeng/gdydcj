package com.mapuni.gdydcaiji.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import com.esri.android.map.GraphicsLayer
import com.esri.android.map.LocationDisplayManager
import com.esri.android.map.ags.ArcGISLocalTiledLayer
import com.esri.core.geometry.Point
import com.esri.core.geometry.Polygon
import com.esri.core.map.Graphic
import com.mapuni.gdydcaiji.R
import kotlinx.android.synthetic.main.activity_collection.*
import java.util.*


class CollectionActivity : AppCompatActivity(),View.OnClickListener {


    var mapfilePath = ""

    //poi0，楼宇采集1，采集面2，寸采集3
    //除2都是点
    var currentCode=0
    var targetCode=-1

    val pointPloygon=ArrayList<Point>()
    lateinit var graphicsLayer: GraphicsLayer

    lateinit  var alertDialog:AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection)

        mapfilePath = Environment.getExternalStorageDirectory().absolutePath+"/map/" + "/layers"
        initMapView()
        initListener()
        upDateView()
    }

    private fun initListener() {
        dingwei_collect.setOnClickListener {
            val locationDisplayManager = mapview_collect.locationDisplayManager
            locationDisplayManager.autoPanMode=(LocationDisplayManager.AutoPanMode.LOCATION)
            locationDisplayManager.start()
        }

    }

    fun initMapView() {

        val layer = ArcGISLocalTiledLayer(mapfilePath)
        mapview_collect.addLayer(layer)
        var graphicsLayer = GraphicsLayer()
        mapview_collect.addLayer(graphicsLayer, 1)

    }

    override fun onClick(v: View?) {
        if (v is View){
            when(v.id){
                R.id.poi_collect->{
                    targetCode=0
                    beginPOICollect()
                }
                R.id.louyu_collect->{
                    targetCode=1
                    beginLouyuCollect()
                }
                R.id.newploygon_collect->{
                    targetCode=2

                    beginpolygonCollect()
                }
                R.id.jiaotong_collect->{
                    targetCode=3

                    beginCountryCollect()
                }
                R.id.tianjia_collect->
                        addPolygonInMap()
            }
        }

    }

    private fun addPolygonInMap() {
        val centerPoint=mapview_collect.center
        pointPloygon.add(centerPoint)
        if (pointPloygon.size>2){
            val layer=mapview_collect.layers[1]
            if (layer is GraphicsLayer){
                layer.removeAll()
                val polygon=Polygon()

            }
        }
    }

    private fun beginCountryCollect() {
        if (currentCode==2&&pointPloygon.size > 2){
            showConfirmDiaolog()
        }else{
            currentCode=targetCode
        }


    }

    private fun beginpolygonCollect() {
        currentCode=targetCode
    }

    private fun beginLouyuCollect() {
        if (currentCode==2&&pointPloygon.size > 2){
            showConfirmDiaolog()
        }else{
            currentCode=targetCode
        }

    }

    private fun beginPOICollect() {
        if (currentCode==2&&pointPloygon.size > 2){
            showConfirmDiaolog()
        }else{
            currentCode=targetCode
        }
    }

    private fun upDateView(){
        when(currentCode){
            0,1,3->{
                linear_tools_collect.visibility=View.INVISIBLE
                tianjia_collect.visibility=View.INVISIBLE
            }
            2->{
                linear_tools_collect.visibility=View.VISIBLE
                tianjia_collect.visibility=View.VISIBLE
            }
        }
    }

    fun showConfirmDiaolog(): AlertDialog {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("提示")
        builder.setMessage("是否保存？")
        builder.setPositiveButton("确定") { dialog, which ->
            //跳转保存页

        }.setNegativeButton("取消") { dialog, which ->
//            cleanNotSave()
            alertDialog.dismiss()
            pointPloygon.clear()
            currentCode=targetCode
            when(targetCode){
                0->{
                    beginPOICollect()
                }
                1->{
                    beginLouyuCollect()
                }
                3->{
                    beginCountryCollect()
                }
            }
        }

        alertDialog = builder.create()
        alertDialog.show()
        return alertDialog
    }

}
