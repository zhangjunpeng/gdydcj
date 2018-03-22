package com.mapuni.gdydcaiji.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import com.esri.android.map.GraphicsLayer
import com.esri.android.map.LocationDisplayManager
import com.esri.android.map.ags.ArcGISLocalTiledLayer
import com.esri.android.runtime.ArcGISRuntime
import com.esri.core.geometry.Point
import com.esri.core.geometry.Polygon
import com.esri.core.map.Graphic
import com.esri.core.symbol.PictureMarkerSymbol
import com.esri.core.symbol.SimpleFillSymbol
import com.esri.core.symbol.SimpleMarkerSymbol
import com.mapuni.gdydcaiji.R
import com.mapuni.gdydcaiji.utils.ScreenUtils
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

        ArcGISRuntime.setClientId("uK0DxqYT0om1UXa9")//加入arcgis研发验证码
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
        poi_collect.setOnClickListener(this)
        louyu_collect.setOnClickListener(this)
        newploygon_collect.setOnClickListener(this)
        jiaotong_collect.setOnClickListener(this)
        tianjia_collect.setOnClickListener(this)

        baocun_collect.setOnClickListener(this)
        houtui_collect.setOnClickListener(this)
        chanel_collect.setOnClickListener(this)

    }

    fun initMapView() {

        val layer = ArcGISLocalTiledLayer(mapfilePath)
        mapview_collect.addLayer(layer)
        graphicsLayer = GraphicsLayer()
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
                        addPointInmap()
                R.id.houtui_collect->{
                        ploygonBack()
                }
                R.id.baocun_collect->{

                }
                R.id.cancel_action->{
                    graphicsLayer.removeGraphic(grahicGonUid)
                    pointPloygon.clear()

                }
            }
        }

    }

    private fun addPointInmap() {
        when(currentCode){
            0->{

            }
            1->{

            }
            2->{
                addPolygonInMap()
            }
            3->{

            }
        }

    }

    private fun ploygonBack() {
        graphicsLayer.removeGraphic(grahicGonUid)
        if (pointPloygon.size>0){
            pointPloygon.remove(pointPloygon.last())
            drawGon(pointPloygon)
        }

    }

    private fun addPolygonInMap() {
        val centerPoint=mapview_collect.center
        pointPloygon.add(centerPoint)

        drawGon(pointPloygon)
//            val layer=mapview_collect.layers[1]
//            if (layer is GraphicsLayer){
//                layer.removeAll()
//                val polygon=Polygon()
//
//            }

    }

    private fun beginCountryCollect() {
        if (currentCode==2&&pointPloygon.size > 2){
            showConfirmDiaolog()
        }else{
            currentCode=targetCode
            upDateView()

        }


    }

    private fun beginpolygonCollect() {
        currentCode=targetCode
        upDateView()

    }

    private fun beginLouyuCollect() {
        if (currentCode==2&&pointPloygon.size > 2){
            showConfirmDiaolog()
        }else{
            currentCode=targetCode
            upDateView()
        }

    }

    private fun beginPOICollect() {
        if (currentCode==2&&pointPloygon.size > 2){
            showConfirmDiaolog()
        }else{
            currentCode=targetCode
            upDateView()

        }
    }

    private fun upDateView(){
        when(currentCode){
            0,1,3->{
                linear_tools_collect.visibility=View.INVISIBLE
                tianjia_collect.visibility=View.VISIBLE
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
        builder.setPositiveButton("确定") { dialog, _ ->
            //跳转保存页
            dialog.dismiss()


        }.setNegativeButton("取消") { dialog, _ ->
//            cleanNotSave()
            dialog.dismiss()
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


    var grahicGonUid: Int=0
    fun drawGon(pointList: ArrayList<Point>) {
        if (pointList.size==0){
            return
        }
        if (pointList.size == 1) {
            grahicGonUid = addPointInMap(pointList.get(0))
        } else {
            val fillSymbol = SimpleFillSymbol(Color.argb(100, 255, 0, 0))
            val polygon = Polygon()
            polygon.startPath(pointList[0])
            for (i in 1 until pointList.size) {
                polygon.lineTo(pointList[i])
            }
            graphicsLayer.removeGraphic(grahicGonUid)

            grahicGonUid = graphicsLayer.addGraphic(Graphic(polygon, fillSymbol))
        }
    }

    fun addPointInMap(point: Point): Int {
        val simpleMarkerSymbol=SimpleMarkerSymbol(Color.RED,5,SimpleMarkerSymbol.STYLE.CIRCLE)
        val graphic = Graphic(point, simpleMarkerSymbol)
        return graphicsLayer.addGraphic(graphic)
    }

}
