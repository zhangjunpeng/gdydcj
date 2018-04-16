package com.mapuni.gdydcaiji.presenter

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.AsyncTask
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.esri.android.map.GraphicsLayer
import com.esri.android.map.MapView
import com.esri.android.map.ags.ArcGISLocalTiledLayer
import com.esri.core.geometry.Envelope
import com.esri.core.geometry.Point
import com.esri.core.geometry.Polygon
import com.esri.core.geometry.Polyline
import com.esri.core.map.Graphic
import com.esri.core.symbol.PictureMarkerSymbol
import com.esri.core.symbol.SimpleFillSymbol
import com.esri.core.symbol.SimpleLineSymbol
import com.esri.core.symbol.SimpleMarkerSymbol
import com.mapuni.gdydcaiji.GdydApplication
import com.mapuni.gdydcaiji.GraphicListAdapter
import com.mapuni.gdydcaiji.R
import com.mapuni.gdydcaiji.activity.LineDetail
import com.mapuni.gdydcaiji.activity.PoiDetail
import com.mapuni.gdydcaiji.activity.SocialDetail
import com.mapuni.gdydcaiji.bean.*
import com.mapuni.gdydcaiji.database.greendao.TbLineDao
import com.mapuni.gdydcaiji.database.greendao.TbPointDao
import com.mapuni.gdydcaiji.database.greendao.TbSurfaceDao
import com.mapuni.gdydcaiji.utils.ToastUtils
import java.util.ArrayList
import java.util.HashMap

/**
 * Created by zjp on 2018/4/13.
 * mail:zhangjunpeng92@163.com
 */
class WaiYePresenter(context: Context,mapView: MapView):WaiYeInterface{
    var point_bz_array: ArrayList<Boolean>
    var line_bz_array: ArrayList<Boolean>
    var surface_bz_array: ArrayList<Boolean>
    var pointPloygon: ArrayList<Point>
    var pointPloyline: ArrayList<Point>

    var targetCode: Int=0
    var currentCode: Int=0


    private val context:Context=context
    private val mapView:MapView=mapView


    //数据库操作对象

    var tbLineDao: TbLineDao= GdydApplication.instances.daoSession.tbLineDao
    var tbPointDao: TbPointDao= GdydApplication.instances.daoSession.tbPointDao
    var tbSurfaceDao: TbSurfaceDao= GdydApplication.instances.daoSession.tbSurfaceDao


    var lineInfoList: List<TbLine>?=null
    var pointList: List<TbPoint>?=null
    var surfaceList: List<TbSurface>?=null

    //显示的点线面

    var infoList: ArrayList<Map<String, Any>>?=null
    var infoMap: HashMap<Int, Any> = HashMap()



    //graphiclayer定义
    private var graphicsLayer: GraphicsLayer = GraphicsLayer()
    private var tempGraphicLayer: GraphicsLayer = GraphicsLayer()
    private var localGraphicsLayer: GraphicsLayer = GraphicsLayer()
    var graphicName: GraphicsLayer = GraphicsLayer()

    private var mIsLoading: Boolean=false

    private var grahicGonUid: Int=0

    private var tempGraphicID: Int = 0



    init {
        initShowGraphicDialog()

        infoMap = HashMap()
        point_bz_array=ArrayList<Boolean>()
        line_bz_array=ArrayList<Boolean>()
        surface_bz_array=ArrayList<Boolean>()
        pointPloygon= ArrayList()
        pointPloyline=ArrayList()

        for (i in 0 until 12) {
            point_bz_array.add(false)
        }
        point_bz_array[0]=true

        for (i in 0 until 4) {
            line_bz_array.add(false)
        }
        line_bz_array[0]=true

        for (i in 0 until 7) {
            surface_bz_array.add(false)
        }
        surface_bz_array[0]=true
    }


    override fun drawGon(pointList: ArrayList<Point>): Int {
        if (pointList.size == 0) {
            return -1
        }
        return if (pointList.size == 1) {
            addOnePointInMap(pointList[0])
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

    override fun drawline(pointPloyline: ArrayList<Point>): Int {
        if (pointPloyline.size == 1) run { return addOnePointInMap(pointPloyline[0]) } else if (pointPloyline.size > 1) {
            val polyline = Polyline()
            for (i in pointPloyline.indices) {
                if (i == 0) {
                    polyline.startPath(pointPloyline[0])
                } else {
                    polyline.lineTo(pointPloyline[i])
                }
            }
            graphicsLayer.removeGraphic(grahicGonUid)
            val simpleLineSymbol = SimpleLineSymbol(Color.RED, 2f)

            return graphicsLayer.addGraphic(Graphic(polyline, simpleLineSymbol))
        }
        return -1
    }

    override fun addPointInMap(point: Point) {
        when (currentCode) {
            0 -> {
                val intent1 = Intent(context, PoiDetail::class.java)
                intent1.putExtra("lat", point.y)
                intent1.putExtra("lng", point.x)

                context.startActivity(intent1)
            }
            1 -> {
                addPolyLineInMap(point)
            }
            2 -> {
                addPolygonInMap(point)
            }
        }
    }

    private fun addOnePointInMap(point: Point): Int {
        val simpleMarkerSymbol = SimpleMarkerSymbol(Color.RED, 10, SimpleMarkerSymbol.STYLE.CIRCLE)
        val graphic = Graphic(point, simpleMarkerSymbol)
        return graphicsLayer.addGraphic(graphic)
    }


    private fun addPolyLineInMap(point: Point ) {
        pointPloyline.add(point)
        grahicGonUid=drawline(pointPloyline)
    }


    private fun addPolygonInMap(point: Point ) {
        //开始小区采集
        pointPloygon.add(point)
        grahicGonUid=drawGon(pointPloygon)
    }

    inner class UPDateGraphicTask() : AsyncTask<String, Void, Polygon>() {
        override fun doInBackground(vararg params: String?): Polygon {
            val currentPloygon = mapView.extent
            val leftTopP = currentPloygon.getPoint(0)
            val rightTopP = currentPloygon.getPoint(1)
            val leftBottomP = currentPloygon.getPoint(2)
            pointList = tbPointDao.queryBuilder().where(TbPointDao.Properties.Lng.between(leftTopP.x, rightTopP.x)
                    , TbPointDao.Properties.Lat.between(leftTopP.y, leftBottomP.y)).list()
            lineInfoList = tbLineDao.loadAll()
            surfaceList = tbSurfaceDao.loadAll()
            pointList = tbPointDao.queryBuilder().where(TbPointDao.Properties.Lng.between(leftTopP.x, rightTopP.x)
                    , TbPointDao.Properties.Lat.between(leftTopP.y, leftBottomP.y)).list()
            lineInfoList = tbLineDao.loadAll()
            surfaceList = tbSurfaceDao.loadAll()

            return currentPloygon
        }

        override fun onPostExecute(result: Polygon) {
            updateGraphicInLocal(result)
            super.onPostExecute(result)
        }

    }

    private fun addNameInMap(point: Point, name: String) {

        val tv = TextView(context)
        if (name.isEmpty()) {
            return
        }
        tv.text = name
        tv.textSize = 8f
        tv.setTextColor(Color.WHITE)
        tv.isDrawingCacheEnabled = true
        tv.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
        tv.layout(0, 0, tv.measuredWidth, tv.measuredHeight)
        val bitmap = Bitmap.createBitmap(tv.drawingCache)
        //千万别忘最后一步
        tv.destroyDrawingCache()
        val picturSymbol = PictureMarkerSymbol(BitmapDrawable(context.resources, bitmap))
        picturSymbol.offsetY = 10f
        val nameGraphic = Graphic(point, picturSymbol)
        graphicName.addGraphic(nameGraphic)
    }


    private fun updateGraphicInLocal(currentPloygon: Polygon) {
        for (info: TbPoint in pointList as List) {
            val point = Point(info.lng, info.lat)
            val simpleMarkerSymbol = SimpleMarkerSymbol(Color.RED, 10, SimpleMarkerSymbol.STYLE.CIRCLE)
            val graphic = Graphic(point, simpleMarkerSymbol)
            val uid = localGraphicsLayer.addGraphic(graphic)
            infoMap[uid] = info
            val name = getPointName(info)
            addNameInMap(point, name)
        }

        for (info: TbLine in lineInfoList as List) {
            val bj = info.polyarrays
            val polyline = Polyline()
            val points_array = bj.split(";")
            for (i in 0 until points_array.size) {
                val item = points_array[i]
                if (item.isEmpty()) {
                    continue
                }
                val points = item.split(",")
                val point = Point(points[0].toDouble(), points[1].toDouble())
                if (i == 0) {
                    polyline.startPath(point)
                } else {
                    polyline.lineTo(point)
                }

            }
            val simpleLineSymbol = SimpleLineSymbol(Color.RED, 2f, SimpleLineSymbol.STYLE.SOLID)
            val graphic = Graphic(polyline, simpleLineSymbol)
            val uid = localGraphicsLayer.addGraphic(graphic)
            infoMap[uid] = info
            val name = getLineName(info)

            val tEnvelope = Envelope()
            polyline.queryEnvelope(tEnvelope)
            val tPoint = tEnvelope.center

            addNameInMap(tPoint, name)

        }

        for (info: TbSurface in surfaceList as List) {
            val bj = info.polyarrays
            val tempPointList = ArrayList<Point>()
            val points_array = bj.split(";")
            for (i in 0 until points_array.size) {
                val item = points_array[i]
                if (item.isEmpty()) {
                    continue
                }
                val points = item.split(",")
                val point = Point(points[0].toDouble(), points[1].toDouble())
                tempPointList.add(point)
            }

            val fillSymbol = SimpleFillSymbol(Color.argb(100, 255, 0, 0))
            val polygon = Polygon()
            polygon.startPath(tempPointList[0])
            for (i in 1 until tempPointList.size) {
                polygon.lineTo(tempPointList[i])
            }

            val uid = localGraphicsLayer.addGraphic(Graphic(polygon, fillSymbol))
            infoMap[uid] = info
            val tEnvelope = Envelope()
            polygon.queryEnvelope(tEnvelope)
            val tPoint = tEnvelope.center
            val name = getSurfaceName(info)
            addNameInMap(tPoint, name)


        }

        mapView.invalidate()
        mIsLoading = false
    }

    private fun getPointName(info: TbPoint): String {
        var name = ""
        val string_poi = listOf(info.name, info.lytype, info.lyxz, info.fl, info.dz, info.lyxz, info.dy, info.lxdh, info.dj, info.lycs, info.lyzhs, info.opttime.toString())
        for (i in 0 until point_bz_array.size) {
            if (point_bz_array[i] && string_poi[i].isNotEmpty()) {
                name += string_poi[i] + "/"
            }
        }
        return name.dropLast(1)
    }

    private fun getLineName(info: TbLine): String {
        var name = ""
        val string_poi = listOf(info.name, info.sfz, info.zdz, info.opttime.toString())
        for (i in 0 until line_bz_array.size) {
            if (line_bz_array[i] && string_poi[i].isNotEmpty()) {
                name += string_poi[i] + "/"
            }
        }
        return name.dropLast(1)
    }

    private fun getSurfaceName(info: TbSurface): String {
        var name = ""
        val string_poi = listOf(info.name, info.xqdz, info.fl, info.wyxx, info.lxdh, info.lds, info.opttime.toString())
        for (i in 0 until surface_bz_array.size) {
            if (surface_bz_array[i] && string_poi[i].isNotEmpty()) {
                name += string_poi[i] + "/"
            }
        }
        return name.dropLast(1)
    }
    override fun updateGraphic() {
        pointPloyline.clear()
        pointPloygon.clear()
        tempGraphicLayer.removeAll()
        graphicName.removeAll()
        localGraphicsLayer.removeAll()
        graphicsLayer.removeAll()
        UPDateGraphicTask().execute("")

    }

    override fun onCompleteBZ(eventBZ: EventBZ) {
        when (eventBZ.type) {
            0 -> {
                point_bz_array = eventBZ.booleanArrayList
            }
            1 -> {
                line_bz_array = eventBZ.booleanArrayList
            }
            2 -> {
                surface_bz_array = eventBZ.booleanArrayList
            }
            else -> {

            }
        }
       updateGraphic()
    }
    override fun deleteInfo(eventDeleteInfo: EventDeleteInfo) {
        for (obj in eventDeleteInfo.deleteList) {
            when (obj) {
                is TbPoint -> {
                    tbPointDao.delete(obj)
                }
                is TbLine -> {
                    tbLineDao.delete(obj)
                }
                is TbSurface -> {
                    tbSurfaceDao.delete(obj)
                }


            }
        }
        updateGraphic()
        showGrahipcListDialog.dismiss()
    }


    override fun initMapview(mapFilePath: String) {
        val layer = ArcGISLocalTiledLayer("file://$mapFilePath/layers")
        mapView.addLayer(layer)
        mapView.addLayer(graphicsLayer, 1)
        mapView.addLayer(tempGraphicLayer, 2)
        mapView.addLayer(localGraphicsLayer, 3)
        mapView.addLayer(graphicName, 4)
    }


    private fun ploygonBack() {
        graphicsLayer.removeGraphic(grahicGonUid)
        if (pointPloygon.size > 0) {
            pointPloygon.remove(pointPloygon.last())
            grahicGonUid=drawGon(pointPloygon)
        }
    }

    override fun backUpAfterMover() {
        updateGraphic()
        if (currentCode==1){
            grahicGonUid=drawline(pointPloyline)
        }
        if (currentCode==2){
            grahicGonUid=drawGon(pointPloygon)
        }
    }

    override fun singleTapOnCollection(v: Float, v1: Float,tolerance: Int) {
        val center = mapView.toMapPoint(v, v1)
        when (currentCode) {
            0 -> {
                addPointInMap(center)

                val intent1 = Intent(context, PoiDetail::class.java)
                intent1.putExtra("lat", center.y)
                intent1.putExtra("lng", center.x)

                context.startActivity(intent1)

            }
            1 -> {
                addPolyLineInMap(center)
            }
            2 -> {
                addPolygonInMap(center)
            }
            3 -> {
                getGraphics(v, v1,tolerance )
            }

        }

    }


    private lateinit var showGrahipcListDialog: Dialog
    private lateinit var recyclerView: RecyclerView
    private fun initShowGraphicDialog() {
        showGrahipcListDialog = Dialog(context)
        showGrahipcListDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val contentView = LayoutInflater.from(context).inflate(R.layout.dialog_showgraphicinfo, null, false)
        showGrahipcListDialog.setContentView(contentView)
        showGrahipcListDialog.setCanceledOnTouchOutside(false)
        recyclerView = contentView.findViewById(R.id.recycler_dialog)

        recyclerView.layoutManager = LinearLayoutManager(context)


    }

    private fun getGraphics(v: Float, v1: Float, tolerance:Int) {

        infoList = ArrayList()
        val symbol = SimpleMarkerSymbol(Color.parseColor("#501EE15E"), tolerance, SimpleMarkerSymbol.STYLE.CIRCLE)
        val point = mapView.toMapPoint(v, v1)
        val graphic = Graphic(point, symbol)
        tempGraphicID = tempGraphicLayer.addGraphic(graphic)
        val uids_local = localGraphicsLayer.getGraphicIDs(v, v1, tolerance, 50)

        if (uids_local.isEmpty()) {
            Toast.makeText(context, "选择范围内没有点", Toast.LENGTH_SHORT).show()
            val removeGraphic = RemoveGraphic()
            removeGraphic.execute("")
        } else {
            for (uid in uids_local) {
                val map = HashMap<String, Any>()
                val info = infoMap[uid]
                when (info) {
                    is TbPoint -> {
                        map["obj"] = info
                    }
                    is TbLine -> {
                        map["obj"] = info
                    }
                    is TbSurface -> {
                        map["obj"] = info
                    }
                }
                (infoList as ArrayList).add(map)

            }

            val graphicListAdtaper = GraphicListAdapter(context, infoList as ArrayList, showGrahipcListDialog)
            recyclerView.adapter = graphicListAdtaper
            showGrahipcListDialog.show()
            val removeGraphic = RemoveGraphic()
            removeGraphic.execute("")
        }

    }

    inner class RemoveGraphic() : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String?): String {
            Thread.sleep(1000)
            return ""
        }
        override fun onPostExecute(result: String?) {
            tempGraphicLayer.removeAll()
        }

    }

    override fun baocunPopwindow() {
        if (currentCode == 1) {
            if (pointPloyline.size > 1) {
                var bj = ""
                for (point in pointPloyline) {
                    bj = bj + point.x.toString() + "," + point.y.toString() + ";"
                }
                bj.dropLast(1)
                val intent1 = Intent(context, LineDetail::class.java)
                intent1.putExtra("bj", bj)
                context.startActivity(intent1)
            } else {
                ToastUtils.showShort("请在地图上选择点")
            }
        }
        if (currentCode == 2) {
            if (pointPloygon.size > 2) {
                var bj = ""
                for (point in pointPloygon) {
                    bj = bj + point.x.toString() + "," + point.y.toString() + ";"
                }
                bj.dropLast(1)
                val intent1 = Intent(context, SocialDetail::class.java)
                intent1.putExtra("bj", bj)
                context.startActivity(intent1)
            } else {
                ToastUtils.showShort("请在地图上选择点")
            }
        }
    }

    override fun huituiPopWindow() {
        if (currentCode == 2) {
            ploygonBack()
        }
        if (currentCode == 1) {
            graphicsLayer.removeGraphic(grahicGonUid)
            if (pointPloyline.size > 0) {
                pointPloyline.remove(pointPloyline.last())
                grahicGonUid=drawline(pointPloyline)
            }
        }
    }

    override fun quxiaoPopWindow() {
        if (currentCode == 1) {
            graphicsLayer.removeGraphic(grahicGonUid)
            pointPloyline.clear()
        }
        if (currentCode == 2) {
            graphicsLayer.removeGraphic(grahicGonUid)
            pointPloygon.clear()
        }
    }

    override fun initDialogSize(bzDialog: Dialog) {

        val dialogWindow = showGrahipcListDialog.window
        val dialogWindow_bz = bzDialog.window

        /*
       * 将对话框的大小按屏幕大小的百分比设置
       */
        val lp = dialogWindow.attributes // 获取对话框当前的参数值
        val lp_bz = dialogWindow_bz.attributes
        val dm = DisplayMetrics()

        (context as Activity).windowManager.defaultDisplay.getMetrics(dm)
        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT
        lp.width = (dm.widthPixels * 0.75).toInt()// 宽度设置为屏幕的0.65
        lp_bz.height = LinearLayout.LayoutParams.WRAP_CONTENT
        lp_bz.width = (dm.widthPixels * 0.75).toInt()// 宽度设置为屏幕的0.65
        dialogWindow.attributes = lp
        dialogWindow_bz.attributes = lp_bz
    }
}