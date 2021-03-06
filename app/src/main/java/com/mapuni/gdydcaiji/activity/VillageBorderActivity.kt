package com.mapuni.gdydcaiji.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.esri.android.map.GraphicsLayer
import com.esri.android.map.ags.ArcGISLocalTiledLayer
import com.esri.core.geometry.*
import com.esri.core.map.Graphic
import com.esri.core.symbol.SimpleFillSymbol
import com.esri.core.symbol.SimpleMarkerSymbol
import com.mapuni.gdydcaiji.R
import com.mapuni.gdydcaiji.bean.EventBJ
import com.mapuni.gdydcaiji.utils.PathConstant
import com.mapuni.gdydcaiji.utils.SPUtils
import com.mapuni.gdydcaiji.utils.ThreadUtils
import com.mapuni.gdydcaiji.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_village_border.*
import org.greenrobot.eventbus.EventBus
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class VillageBorderActivity : AppCompatActivity() ,View.OnClickListener{


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_village_border)
        initMapView()

        tianjia_collect.setOnClickListener(this)
        baocun_collect.setOnClickListener(this)
        houtui_collect.setOnClickListener(this)
        chanel_collect.setOnClickListener(this)
        initData()
    }

    private fun initData(){
        val bj = intent.getStringExtra("bj") ?: return
        val points_array=bj.split(";")
        for (i in 0 until points_array.size) {
            val item=points_array[i]
            if (item.isEmpty()){
                continue
            }
            val points=item.split(",")
            val point = Point(points[0].toDouble(), points[1].toDouble())
            pointPloygon.add(point)
        }

        val fillSymbol = SimpleFillSymbol(Color.argb(100, 255, 0, 0))
        val polygon = Polygon()
        polygon.startPath(pointPloygon[0])
        for (i in 1 until pointPloygon.size) {
            polygon.lineTo(pointPloygon[i])
        }

        grahicGonUid=graphicsLayer.addGraphic(Graphic(polygon, fillSymbol))
    }

    private lateinit var mapFileName: String
    private lateinit var mapFilePath: String
    private var grahicGonUid: Int = 0


    private lateinit var graphicsLayer: GraphicsLayer

    private fun initMapView() {


        mapFileName = SPUtils.getInstance().getString("checkedMap", "")
        mapFilePath = SPUtils.getInstance().getString("checkedMapPath", "")
        if (!TextUtils.isEmpty(mapFileName) && !TextUtils.isEmpty(mapFilePath)
                && File(mapFilePath).exists()) {
            val layer = ArcGISLocalTiledLayer("file://$mapFilePath/layers")
            mapview_collect.addLayer(layer)
            graphicsLayer = GraphicsLayer()
            mapview_collect.addLayer(graphicsLayer, 1)


        } else {
            // 获取所有地图文件
            getAllFiles()
        }


    }

    private fun getAllFiles() {
        ThreadUtils.executeSubThread {
            val path = PathConstant.UNDO_ZIP_PATH
            val fileDir = File(path)
            if (!fileDir.exists()) {
                fileDir.mkdirs()
            }
            // 获得文件夹下所有文件夹和文件的名字
//            var allDirNames = fileDir.list()
            // 获得文件夹下所有文件夹和文件
            var allDirFiles = fileDir.listFiles()
            // 等待切换fragment动画完成
            //SystemClock.sleep(1000);
            ThreadUtils.executeMainThread {
                if (allDirFiles != null && allDirFiles.size != 0) {
                    mapview_collect.setVisibility(View.VISIBLE)
                    // 默认显示数组中的第一个文件      按字母顺序排列
                    mapFilePath = allDirFiles[0].absolutePath
                    // 将选中的地图名字存入sp中
                    SPUtils.getInstance().put("checkedMap", allDirFiles[0].getName())
                    SPUtils.getInstance().put("checkedMapPath", allDirFiles[0].getAbsolutePath())
                    val layer = ArcGISLocalTiledLayer("file://$mapFilePath/layers")
                    mapview_collect.addLayer(layer)
                    graphicsLayer = GraphicsLayer()
                    mapview_collect.addLayer(graphicsLayer, 1)

                } else {
                    mapview_collect.visibility = View.GONE
                    showNotHaveMapDialog()
                }
            }
        }

    }

    /**
     * 弹出没有地图Dialog
     */
    private fun showNotHaveMapDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("无地图文件，请先下载地图")
        builder.setPositiveButton("确定") { dialog, which -> startActivity(Intent(this, DownloadMapActivity::class.java)) }
        builder.setNegativeButton("取消", null)
        builder.show()
    }


    override fun onClick(v: View) {
        when(v.id){
            R.id.tianjia_collect ->
                addPolygonInMap()
            R.id.houtui_collect -> {
                ploygonBack()
            }
            R.id.baocun_collect -> {
                    var bj=""
                    for (point in pointPloygon) {
                        bj=bj+point.x.toString()+","+point.y.toString()+";"
                    }
                    bj.dropLast(1)
                    val eventBJ=EventBJ(bj)
                    EventBus.getDefault().post(eventBJ)
                    finish()

            }
            R.id.cancel_action -> {
                graphicsLayer.removeGraphic(grahicGonUid)
                pointPloygon.clear()
            }
        }
    }

    private val pointPloygon: ArrayList<Point> = ArrayList()

    private fun addPolygonInMap() {
        //开始小区采集
        val centerPoint = mapview_collect.center
        pointPloygon.add(centerPoint)
        drawGon(pointPloygon)
    }
    private fun drawGon(pointList: ArrayList<com.esri.core.geometry.Point>) {
        if (pointList.size == 0) {
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
        val simpleMarkerSymbol = SimpleMarkerSymbol(Color.RED, 10, SimpleMarkerSymbol.STYLE.CIRCLE)
        val graphic = Graphic(point, simpleMarkerSymbol)
        return graphicsLayer.addGraphic(graphic)
    }
    private fun ploygonBack() {
        graphicsLayer.removeGraphic(grahicGonUid)
        if (pointPloygon.size > 0) {
            pointPloygon.dropLast(1)
            drawGon(pointPloygon)
        }
    }
}
