package com.mapuni.gdydcaiji.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.esri.android.map.GraphicsLayer
import com.esri.android.map.LocationDisplayManager
import com.esri.android.map.ags.ArcGISLocalTiledLayer
import com.esri.android.map.event.OnSingleTapListener
import com.esri.android.runtime.ArcGISRuntime
import com.esri.core.geometry.Point
import com.esri.core.geometry.Polygon
import com.esri.core.map.Graphic
import com.esri.core.symbol.SimpleFillSymbol
import com.esri.core.symbol.SimpleMarkerSymbol
import com.mapuni.gdydcaiji.GdydApplication
import com.mapuni.gdydcaiji.R
import com.mapuni.gdydcaiji.database.greendao.TBuildingInfoDao
import com.mapuni.gdydcaiji.database.greendao.TPoiInfoDao
import com.mapuni.gdydcaiji.database.greendao.TSocialInfoDao
import com.mapuni.gdydcaiji.database.greendao.TVillageInfoDao
import kotlinx.android.synthetic.main.activity_collection.*
import org.greenrobot.eventbus.EventBus
import kotlin.collections.ArrayList


class CollectionActivity : AppCompatActivity(),View.OnClickListener,OnSingleTapListener,View.OnTouchListener {



    var mapfilePath = ""

    //poi0，楼宇采集1，采集面2，村采集3,
    //除2都是点
    //4特殊，范围选择点
    var currentCode=0
    var targetCode=-1

    val pointPloygon=ArrayList<Point>()
    lateinit var graphicsLayer: GraphicsLayer
    lateinit  var alertDialog:AlertDialog

    //poi跳转请求码
    private val requestCode_poi:Int=10001
    //面跳转请求码
    val requestCode_ploygon:Int=10002

    var tolerance:Int=100

    //数据库操作对象
    lateinit var tBuildingInfoDao:TBuildingInfoDao
    lateinit var tPoiInfoDao: TPoiInfoDao
    lateinit var tSocialInfoDao: TSocialInfoDao
    lateinit var tVillageInfoDao: TVillageInfoDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection)

        ArcGISRuntime.setClientId("uK0DxqYT0om1UXa9")//加入arcgis研发验证码
        mapfilePath = Environment.getExternalStorageDirectory().absolutePath+"/map/" + "/layers"
        initData()
        initMapView()
        initListener()
        upDateView()


    }

    private fun initData() {
        tBuildingInfoDao=GdydApplication.instances.daoSession.tBuildingInfoDao
        tPoiInfoDao=GdydApplication.instances.daoSession.tPoiInfoDao
        tSocialInfoDao=GdydApplication.instances.daoSession.tSocialInfoDao
        tVillageInfoDao=GdydApplication.instances.daoSession.tVillageInfoDao
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

    private fun initMapView() {

        val layer = ArcGISLocalTiledLayer(mapfilePath)
        mapview_collect.addLayer(layer)
        graphicsLayer = GraphicsLayer()
        mapview_collect.addLayer(graphicsLayer, 1)
        mapview_collect.onSingleTapListener = this
//        mapview_collect.setOnTouchListener(this)
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
        val center=mapview_collect.center
        when(currentCode){
            0->{
                val intent1=Intent(this,PoiDetail::class.java)
                intent1.putExtra("lat",center.x)
                intent1.putExtra("lng",center.y)
                startActivity(intent1)
            }
            1->{
                val intent1=Intent(this,BuildingDetail::class.java)
                intent1.putExtra("lat",center.x)
                intent1.putExtra("lng",center.y)
                startActivity(intent1)
            }
            2->{
                addPolygonInMap()
            }
            3->{
                val intent1=Intent(this,VillageDetail::class.java)
                intent1.putExtra("lat",center.x)
                intent1.putExtra("lng",center.y)
                startActivity(intent1)
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
        //开始小区采集
        val centerPoint=mapview_collect.center
        pointPloygon.add(centerPoint)
        drawGon(pointPloygon)
    }

    private fun beginCountryCollect() {
        //开始村采集

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
        //开始楼宇采集

        if (currentCode==2&&pointPloygon.size > 2){
            showConfirmDiaolog()
        }else{
            currentCode=targetCode
            upDateView()
        }

    }

    private fun beginPOICollect() {
        //开始poi采集
        if (currentCode==2&&pointPloygon.size > 2){
            showConfirmDiaolog()
        }else{
            currentCode=targetCode
            upDateView()
        }
    }

    private fun upDateView(){
        tianjia_collect.visibility=View.VISIBLE
        when(currentCode){
            0->{
                linear_tools_collect.visibility=View.INVISIBLE
                poi_collect.isSelected=true
                louyu_collect.isSelected=false
                newploygon_collect.isSelected=false
                jiaotong_collect.isSelected=false

            }
            1->{
                linear_tools_collect.visibility=View.INVISIBLE
                poi_collect.isSelected=false
                louyu_collect.isSelected=true
                newploygon_collect.isSelected=false
                jiaotong_collect.isSelected=false
            }
            2->{
                linear_tools_collect.visibility=View.VISIBLE
                poi_collect.isSelected=false
                louyu_collect.isSelected=false
                newploygon_collect.isSelected=true
                jiaotong_collect.isSelected=false
            }
            3->{
                linear_tools_collect.visibility=View.INVISIBLE
                poi_collect.isSelected=false
                louyu_collect.isSelected=false
                newploygon_collect.isSelected=false
                jiaotong_collect.isSelected=true
            }
        }
    }

    private fun showConfirmDiaolog(): AlertDialog {
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
            graphicsLayer.removeGraphic(grahicGonUid)
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


    private var grahicGonUid: Int=0
    private fun drawGon(pointList: ArrayList<Point>) {
        if (pointList.size==0){
            return
        }
        grahicGonUid = if (pointList.size == 1) {
            addPointInMap(pointList[0])
        } else {
            val fillSymbol = SimpleFillSymbol(Color.argb(100, 255, 0, 0))
            val polygon = Polygon()
            polygon.startPath(pointList[0])
            for (i in 1 until pointList.size) {
                polygon.lineTo(pointList[i])
            }
            graphicsLayer.removeGraphic(grahicGonUid)

            graphicsLayer.addGraphic(Graphic(polygon, fillSymbol))
        }
    }

    private fun addPointInMap(point: Point): Int {
        val simpleMarkerSymbol=SimpleMarkerSymbol(Color.RED,5,SimpleMarkerSymbol.STYLE.CIRCLE)
        val graphic = Graphic(point, simpleMarkerSymbol)
        return graphicsLayer.addGraphic(graphic)
    }

    override fun onSingleTap(v: Float, v1: Float) {
        singleTapOnCollection(v, v1)
    }


    private fun singleTapOnCollection(v:Float, v1:Float) {
        when(currentCode){
            0->{

            }
            1->{

            }
            2->{

            }
            3->{

            }
            4->{
            }
        }
        getGraphics(v,v1)

    }

    private var tempGraphicID:Int=0
    private fun getGraphics(v: Float, v1: Float) {
        val symbol=SimpleMarkerSymbol(Color.parseColor("#50AA0000"),tolerance,SimpleMarkerSymbol.STYLE.CIRCLE)
        val point=mapview_collect.toMapPoint(v,v1)
        val graphic=Graphic(point,symbol)
        tempGraphicID=  graphicsLayer.addGraphic(graphic)
        val uids = graphicsLayer.getGraphicIDs(v, v1, tolerance, 50)
        if (uids.isEmpty()){
            graphicsLayer.removeGraphic(tempGraphicID)
            Toast.makeText(this,"选择范围内没有点",Toast.LENGTH_SHORT).show()
        }else{

        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (resultCode==Activity.RESULT_OK){
            when(requestCode){
                requestCode_poi->{
                    val point=Point(data.getDoubleExtra("lat",0.0),data.getDoubleExtra("lng", 0.0))
                    addPointInMap(point)
                }
                requestCode_ploygon->{

                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (event.action==MotionEvent.ACTION_DOWN){
            Log.i("onTouchEvent","down")
        }
        if (event.action==MotionEvent.ACTION_UP){
            Log.i("onTouchEvent","up")

            graphicsLayer.removeGraphic(tempGraphicID)

        }
        return super.onTouchEvent(event)
    }


}
