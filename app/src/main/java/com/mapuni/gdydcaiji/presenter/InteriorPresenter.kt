package com.mapuni.gdydcaiji.presenter

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
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
import com.esri.core.symbol.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import com.mapuni.gdydcaiji.GdydApplication
import com.mapuni.gdydcaiji.R
import com.mapuni.gdydcaiji.activity.*
import com.mapuni.gdydcaiji.adapter.GraphicListAdapter
import com.mapuni.gdydcaiji.adapter.MapChoseListAdapter
import com.mapuni.gdydcaiji.bean.*
import com.mapuni.gdydcaiji.database.greendao.InLineDao
import com.mapuni.gdydcaiji.database.greendao.InPointDao
import com.mapuni.gdydcaiji.database.greendao.InSurfaceDao
import com.mapuni.gdydcaiji.net.RetrofitFactory
import com.mapuni.gdydcaiji.net.RetrofitService
import com.mapuni.gdydcaiji.utils.*
import okhttp3.MediaType
import okhttp3.RequestBody
import org.greenrobot.greendao.query.LazyList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.util.*

/**
 * Created by zjp on 2018/4/16.
 * mail:zhangjunpeng92@163.com
 */
class InteriorPresenter(context: Activity, mapView: MapView) : ZhiJianInterface {

    var point_bz_array: ArrayList<Boolean>
    var line_bz_array: ArrayList<Boolean>
    var surface_bz_array: ArrayList<Boolean>
    var pointPloygon: ArrayList<Point>
    var pointPloyline: ArrayList<Point>

    var targetCode: Int = 0
    var currentCode: Int = 0


    private val context: Context = context
    private val mapView: MapView = mapView


    //数据库操作对象

    var inLineDao: InLineDao = GdydApplication.instances.daoSession.inLineDao
    var inPointDao: InPointDao = GdydApplication.instances.daoSession.inPointDao
    var inSurfaceDao: InSurfaceDao = GdydApplication.instances.daoSession.inSurfaceDao


    var lineInfoList: LazyList<InLine>? = null
    var pointList: LazyList<InPoint>? = null
    var surfaceList: LazyList<InSurface>? = null

    private lateinit var tbPointList1: MutableList<InPoint>
    private lateinit var tbPointList2: MutableList<InPoint>
    private lateinit var tbLineList1: MutableList<InLine>
    private lateinit var tbLineList2: MutableList<InLine>
    private lateinit var tbSurfaceList1: MutableList<InSurface>
    private lateinit var tbSurfaceList2: MutableList<InSurface>

    //显示的点线面

    var infoList: ArrayList<Map<String, Any>>? = null
    var infoMap: HashMap<Int, Any> = HashMap()

    private var updataNum = 0


    //graphiclayer定义
    private var graphicsLayer: GraphicsLayer = GraphicsLayer()
    private var tempGraphicLayer: GraphicsLayer = GraphicsLayer()
    private var localGraphicsLayer: GraphicsLayer = GraphicsLayer()
    var graphicName: GraphicsLayer = GraphicsLayer()

    private var mIsLoading: Boolean = false

    private var grahicGonUid: Int = 0

    private var tempGraphicID: Int = 0

    private var currentPoi: InPoint? = null

    var currentPoiColor: Int = -1
    var currentLineColor: Int = -1
    var currentSurfaceColor: Int = -1

    //    var dateStartTime: String
//    var dateStopTime: String
    private val fileDirNames = ArrayList<String>()
    private val fileDirPaths = ArrayList<String>()
    private val filePath = PathConstant.UPLOAD_DATA + "/upload.txt"

    init {
        initShowGraphicDialog()

//        dateStartTime = DateUtil.getCurrentDateByOffset(DateUtil.YMD, Calendar.DATE, -2)
//        dateStopTime = DateUtil.getCurrentDate(DateUtil.YMD)

        initShowMapListDialog()
        infoMap = HashMap()
        point_bz_array = ArrayList<Boolean>()
        line_bz_array = ArrayList<Boolean>()
        surface_bz_array = ArrayList<Boolean>()
        pointPloygon = ArrayList()
        pointPloyline = ArrayList()

        for (i in 0 until 12) {
            point_bz_array.add(false)
        }
//        point_bz_array[0] = true

        for (i in 0 until 4) {
            line_bz_array.add(false)
        }
//        line_bz_array[0] = true

        for (i in 0 until 7) {
            surface_bz_array.add(false)
        }
//        surface_bz_array[0] = true
    }


    fun drawGon(pointList: ArrayList<Point>): Int {
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

    fun drawline(pointPloyline: ArrayList<Point>): Int {
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

    fun addPointInMap(point: Point) {
        when (currentCode) {
            0 -> {

                val intent1 = Intent(context, InteriorPoiDetail::class.java)
                if (currentPoi != null) {
                    //移点
                    intent1.putExtra("resultBm", currentPoi!!.bm)
                    currentPoi = null
                }
                intent1.putExtra("lat", point.y)
                intent1.putExtra("lng", point.x)

                addOnePointInMap(point)

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


    private fun addPolyLineInMap(point: Point) {
        pointPloyline.add(point)
        grahicGonUid = drawline(pointPloyline)
    }


    private fun addPolygonInMap(point: Point) {
        //开始小区采集
        pointPloygon.add(point)
        grahicGonUid = drawGon(pointPloygon)
    }

    inner class UPDateGraphicTask : AsyncTask<String, Void, Polygon>() {
        override fun doInBackground(vararg params: String?): Polygon {
            val currentPloygon = mapView.extent
            val leftTopP = currentPloygon.getPoint(0)
            val rightTopP = currentPloygon.getPoint(1)
            val leftBottomP = currentPloygon.getPoint(2)
            //外业
            pointList = inPointDao.queryBuilder().where(
                    InPointDao.Properties.Lng.between(leftTopP.x, rightTopP.x),
                    InPointDao.Properties.Lat.between(leftTopP.y, leftBottomP.y)
//                    InPointDao.Properties.Id.isNull,
//                    InPointDao.Properties.Opttime.between(DateUtil.getDateByFormat("$dateStartTime 00:00:00", DateUtil.YMDHMS), DateUtil.getDateByFormat("$dateStopTime 24:00:00", DateUtil.YMDHMS))
            ).listLazy()
            lineInfoList = inLineDao.queryBuilder().listLazy()
            surfaceList = inSurfaceDao.queryBuilder().listLazy()

            return currentPloygon
        }

        override fun onPostExecute(result: Polygon) {
            updateGraphicInLocal(result)
            super.onPostExecute(result)
        }

    }

    fun addNameInMap(point: Point, name: String, currentTvColor: Int) {

        if (name.isEmpty()) {
            return
        }
        if ("samsung" == android.os.Build.BRAND) {
            val tv = TextView(context)
            tv.text = name
            tv.textSize = 8f
            tv.setTextColor(currentTvColor)
            tv.isDrawingCacheEnabled = true
            tv.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
            tv.layout(0, 0, tv.measuredWidth, tv.measuredHeight)
            val bitmap = Bitmap.createBitmap(tv.drawingCache)
//        千万别忘最后一步
            tv.destroyDrawingCache()
            val picturSymbol = PictureMarkerSymbol(BitmapDrawable(context.resources, bitmap))
            picturSymbol.offsetY = 10f
            val nameGraphic = Graphic(point, picturSymbol)
            graphicName.addGraphic(nameGraphic)
        } else {
            val textSymbol = TextSymbol(10, name, currentTvColor)
            textSymbol.offsetY = 6f
            val nameGraphic = Graphic(point, textSymbol)
            graphicName.addGraphic(nameGraphic)
        }
    }


    private fun updateGraphicInLocal(currentPloygon: Polygon) {
        for (info: InPoint in pointList as LazyList) {
            val point = Point(info.lng, info.lat)
            var simpleMarkerSymbol: SimpleMarkerSymbol = if (info.flag == 0 && info.id != null) {
                SimpleMarkerSymbol(Color.RED, 10, SimpleMarkerSymbol.STYLE.CIRCLE)
            } else {
                SimpleMarkerSymbol(Color.BLUE, 10, SimpleMarkerSymbol.STYLE.CIRCLE)
            }
            var graphic = Graphic(point, simpleMarkerSymbol)
            val uid = localGraphicsLayer.addGraphic(graphic)
            infoMap[uid] = info
            val name = getPointName(info)
            addNameInMap(point, name, currentPoiColor)
        }

        for (info: InLine in lineInfoList as LazyList) {
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
            var simpleLineSymbol = if (info.flag == 0 && info.id != null) {
                //下载下来的内业数据且未做更改
                SimpleLineSymbol(Color.RED, 2f, SimpleLineSymbol.STYLE.SOLID)
            } else {
                SimpleLineSymbol(Color.BLUE, 2f, SimpleLineSymbol.STYLE.SOLID)
            }
            val graphic = Graphic(polyline, simpleLineSymbol)
            val uid = localGraphicsLayer.addGraphic(graphic)
            infoMap[uid] = info
            val name = getLineName(info)

            val tEnvelope = Envelope()
            polyline.queryEnvelope(tEnvelope)
            val tPoint = tEnvelope.center

            addNameInMap(tPoint, name, currentLineColor)

        }

        for (info: InSurface in surfaceList as LazyList) {
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

            var fillSymbol = if (info.flag == 0 && info.id != null) {
                //下载下来的内业数据且未做更改
                SimpleFillSymbol(Color.argb(100, 255, 0, 0))
            } else {
                SimpleFillSymbol(Color.argb(100, 0, 0, 255))
            }
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
            addNameInMap(tPoint, name, currentSurfaceColor)


        }

        backUpAfterMover()

        mapView.invalidate()
        mIsLoading = false
        pointList?.close()
        lineInfoList?.close()
        surfaceList?.close()
    }

    private fun getPointName(info: InPoint): String {
        var name = ""
        val string_poi = listOf(info.name, info.lytype, info.lyxz, info.fl, info.dz, info.lyxz, info.dy, info.lxdh, info.dj, info.lycs, info.lyzhs, info.opttime.toString())
        for (i in 0 until point_bz_array.size) {
            if (point_bz_array[i] && string_poi[i].isNotEmpty()) {
                name += string_poi[i] + "/"
            }
        }
        return name.dropLast(1)
    }

    private fun getLineName(info: InLine): String {
        var name = ""
        val string_poi = listOf(info.name, info.sfz, info.zdz, info.opttime.toString())
        for (i in 0 until line_bz_array.size) {
            if (line_bz_array[i] && string_poi[i].isNotEmpty()) {
                name += string_poi[i] + "/"
            }
        }
        return name.dropLast(1)
    }

    private fun getSurfaceName(info: InSurface): String {
        var name = ""
        val string_poi = listOf(info.name, info.xqdz, info.fl, info.wyxx, info.lxdh, info.lds, info.opttime.toString())
        for (i in 0 until surface_bz_array.size) {
            if (surface_bz_array[i] && string_poi[i].isNotEmpty()) {
                name += string_poi[i] + "/"
            }
        }
        return name.dropLast(1)
    }

    fun updateGraphic() {
//        pointPloyline.clear()
//        pointPloygon.clear()
        tempGraphicLayer.removeAll()
        graphicName.removeAll()
        localGraphicsLayer.removeAll()
        graphicsLayer.removeAll()
        UPDateGraphicTask().execute("")

    }

    fun onCompleteBZ(eventBZ: EventBZ) {
        when (eventBZ.type) {
            0 -> {
                point_bz_array = eventBZ.booleanArrayList
                currentPoiColor = eventBZ.tvColor
            }
            1 -> {
                line_bz_array = eventBZ.booleanArrayList
                currentLineColor = eventBZ.tvColor
            }
            2 -> {
                surface_bz_array = eventBZ.booleanArrayList
                currentSurfaceColor = eventBZ.tvColor
            }
            else -> {

            }
        }
        updateGraphic()
    }

    fun deleteInfo(eventDeleteInfo: EventDeleteInfo) {
        for (obj in eventDeleteInfo.deleteList) {
            when (obj) {
                is InPoint -> {
                    inPointDao.delete(obj)
                }
                is InLine -> {
                    inLineDao.delete(obj)
                }
                is InSurface -> {
                    inSurfaceDao.delete(obj)
                }

            }
        }
        updateGraphic()
        showGrahipcListDialog.dismiss()
    }


    fun initMapview(mapFilePath: String) {
        val layer = ArcGISLocalTiledLayer("file://$mapFilePath/layers")
        mapView.addLayer(layer)
        mapView.addLayer(graphicsLayer)
        mapView.addLayer(tempGraphicLayer)
        mapView.addLayer(localGraphicsLayer)
        mapView.addLayer(graphicName)


    }


    private fun ploygonBack() {
        graphicsLayer.removeGraphic(grahicGonUid)
        if (pointPloygon.size > 0) {
            pointPloygon.remove(pointPloygon.last())
            grahicGonUid = drawGon(pointPloygon)
        }
    }

    fun backUpAfterMover() {
//        updateGraphic()
        if (currentCode == 1) {
            grahicGonUid = drawline(pointPloyline)
        }
        if (currentCode == 2) {
            grahicGonUid = drawGon(pointPloygon)
        }
    }

    fun singleTapOnCollection(v: Float, v1: Float, tolerance: Int) {
        val center = mapView.toMapPoint(v, v1)
        when (currentCode) {
            0 -> {
                val intent1 = Intent(context, InteriorPoiDetail::class.java)
                if (currentPoi != null) {
                    //移点
//                    currentPoi!!.lat = center.y
//                    currentPoi!!.lng = center.x
//                    intent1.putExtra("resultBean", currentPoi)
                    intent1.putExtra("resultBm", currentPoi!!.bm)
                    currentPoi = null
                }
                intent1.putExtra("lat", center.y)
                intent1.putExtra("lng", center.x)

                addOnePointInMap(center)

                context.startActivity(intent1)

            }
            1 -> {
                addPolyLineInMap(center)
            }
            2 -> {
                addPolygonInMap(center)
            }
            3 -> {
                getGraphics(v, v1, tolerance)
            }

        }

    }


    private lateinit var showGrahipcListDialog: Dialog
    private lateinit var recyclerView: RecyclerView
    private lateinit var tv_title: TextView
    private fun initShowGraphicDialog() {
        showGrahipcListDialog = Dialog(context)
        showGrahipcListDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val contentView = LayoutInflater.from(context).inflate(R.layout.dialog_showgraphicinfo, null, false)
        showGrahipcListDialog.setContentView(contentView)
        showGrahipcListDialog.setCanceledOnTouchOutside(false)
        recyclerView = contentView.findViewById(R.id.recycler_dialog)
        tv_title = contentView.findViewById(R.id.tv_dialog_title)

        recyclerView.layoutManager = LinearLayoutManager(context)


    }

    private fun getGraphics(v: Float, v1: Float, tolerance: Int) {

        infoList = ArrayList()
        val symbol = SimpleMarkerSymbol(Color.parseColor("#501EE15E"), tolerance, SimpleMarkerSymbol.STYLE.CIRCLE)
        val point = mapView.toMapPoint(v, v1)
        val graphic = Graphic(point, symbol)
        tempGraphicID = tempGraphicLayer.addGraphic(graphic)
        val uids_local = localGraphicsLayer.getGraphicIDs(v, v1, tolerance, 50)

        var poiCount = 0
        if (uids_local.isEmpty()) {
            Toast.makeText(context, "选择范围内没有点", Toast.LENGTH_SHORT).show()
            val removeGraphic = RemoveGraphic()
            removeGraphic.execute("")
        } else {
            for (uid in uids_local) {
                val map = HashMap<String, Any>()
                val info = infoMap[uid]
                when (info) {
                    is InPoint -> {
                        map["obj"] = info
                        poiCount++
                    }
                    is InLine -> {
                        map["obj"] = info
                    }
                    is InSurface -> {
                        map["obj"] = info
                    }
                }
                (infoList as ArrayList).add(map)

            }


//            when(MODE){
//                6->{
            tv_title.text = "附近要素的名称(点数量: $poiCount)"
            val graphicListAdtaper = GraphicListAdapter(context, infoList as ArrayList, showGrahipcListDialog, -1)
            recyclerView.adapter = graphicListAdtaper

//                }
//                2->{
//                    val onlyShowAdapter =OnlyShowAdapter(context,infoList as ArrayList,showGrahipcListDialog)
//                    recyclerView.adapter=onlyShowAdapter
//                }
//            }
            showGrahipcListDialog.show()
            val removeGraphic = RemoveGraphic()
            removeGraphic.execute("")

        }

    }

    private lateinit var showMapListDialog: Dialog
    private lateinit var recyclerView_showmap: RecyclerView
    private fun initShowMapListDialog() {
        showMapListDialog = Dialog(context)
        showMapListDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val contentView = LayoutInflater.from(context).inflate(R.layout.dialog_showgraphicinfo, null, false)
        showMapListDialog.setContentView(contentView)
        showMapListDialog.setCanceledOnTouchOutside(false)
        recyclerView_showmap = contentView.findViewById(R.id.recycler_dialog)

        recyclerView_showmap.layoutManager = LinearLayoutManager(context)


    }

    inner class RemoveGraphic : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String?): String {
            Thread.sleep(1000)
            return ""
        }

        override fun onPostExecute(result: String?) {
            tempGraphicLayer.removeAll()
        }

    }

    fun baocunPopwindow() {
        if (currentCode == 1) {
            if (pointPloyline.size > 1) {
                var bj = ""
                for (point in pointPloyline) {
                    bj = bj + point.x.toString() + "," + point.y.toString() + ";"
                }
                bj.dropLast(1)
                val intent1 = Intent(context, InteriorLineDetail::class.java)
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
                val intent1 = Intent(context, InteriorSocialDetail::class.java)
                intent1.putExtra("bj", bj)
                context.startActivity(intent1)
            } else {
                ToastUtils.showShort("请在地图上选择点")
            }
        }
    }

    fun huituiPopWindow() {
        if (currentCode == 2) {
            ploygonBack()
        }
        if (currentCode == 1) {
            graphicsLayer.removeGraphic(grahicGonUid)
            if (pointPloyline.size > 0) {
                pointPloyline.remove(pointPloyline.last())
                grahicGonUid = drawline(pointPloyline)
            }
        }
    }

    fun quxiaoPopWindow() {
        if (currentCode == 1) {
            graphicsLayer.removeGraphic(grahicGonUid)
            pointPloyline.clear()
        }
        if (currentCode == 2) {
            graphicsLayer.removeGraphic(grahicGonUid)
            pointPloygon.clear()
        }
    }

    //在切换采集状态时清空
    fun deleteLineOrGon() {
        graphicsLayer.removeGraphic(grahicGonUid)
        pointPloyline.clear()
        pointPloygon.clear()
    }

    fun initDialogSize(bzDialog: Dialog) {

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

    fun getAllFiles() {
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
                    mapView.setVisibility(View.VISIBLE)
                    // 默认显示数组中的第一个文件      按字母顺序排列
                    val mapFilePath = allDirFiles[0].getAbsolutePath()
                    // 将选中的地图名字存入sp中
                    SPUtils.getInstance().put("checkedMap", allDirFiles[0].getName())
                    SPUtils.getInstance().put("checkedMapPath", allDirFiles[0].getAbsolutePath())
                    initMapview(mapFilePath)
                } else {
                    mapView.visibility = View.GONE
                    showNotHaveMapDialog()
                }
            }
        }
    }


    /**
     * 弹出没有地图Dialog
     */
    private fun showNotHaveMapDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("无地图文件，请先下载地图")
        builder.setPositiveButton("确定") { dialog, which -> context.startActivity(Intent(context, DownloadMapActivity::class.java)) }
        builder.setNegativeButton("取消", null)
        builder.show()
    }

    fun setCurrentPoi(tbPoint: InPoint) {
        currentPoi = tbPoint
    }

    fun getCurrentPoi(): InPoint? {
        return currentPoi
    }

    fun setCurrentPoiNull() {
        currentPoi = null
    }

    fun createFile() {
        ThreadUtils.executeSubThread {
            //生成文件
            val gson = GsonBuilder().setDateFormat(DateUtil.YMDHMS).registerTypeAdapterFactory(NullStringToEmptyAdapterFactory<Any>()).excludeFieldsWithoutExposeAnnotation().create()
            val map = HashMap<String, Any>()

            //未上传,新增
            tbPointList1 = inPointDao.queryBuilder()
                    .where(InPointDao.Properties.Flag.eq(0), //新增未上传
                            InPointDao.Properties.Id.isNull)
                    .orderAsc(InPointDao.Properties.Opttime).list()
            map["tb_point"] = tbPointList1

            if (tbPointList1.size > 0) {
                updataNum += tbPointList1.size
            }
//                //未上传,修改（id不为空，flag=0）
            tbPointList2 = inPointDao.queryBuilder()
                    .where(InPointDao.Properties.Flag.eq(2))//修改未上传
                    .orderAsc(InPointDao.Properties.Opttime).list()
            map["tb_point_modify"] = tbPointList2

            if (tbPointList2.size > 0) {
                updataNum += tbPointList2.size
            }

            //未上传,新增
            tbLineList1 = inLineDao.queryBuilder()
                    .where(InLineDao.Properties.Flag.eq(0),
                            InLineDao.Properties.Id.isNull)
                    .orderAsc(InLineDao.Properties.Opttime).list()
//                String poiJson1 = gson.toJson(tbLineList1);
//                FileUtils.writeFile(PathConstant.UPLOAD_DATA + "/tb_line.txt", poiJson);

            map["tb_line"] = tbLineList1

            if (tbLineList1.size > 0) {
                updataNum += tbLineList1.size
            }

            //未上传,修改（id不为空，flag=0）
            tbLineList2 = inLineDao.queryBuilder()
                    .where(InLineDao.Properties.Flag.eq(2))
                    .orderAsc(InLineDao.Properties.Opttime).list()
//                String poiJson2 = gson.toJson(tbLineList2);
//                FileUtils.writeFile(PathConstant.UPLOAD_DATA + "/tb_line.txt", poiJson);

            map["tb_line_modify"] = tbLineList2

            if (tbLineList2.size > 0) {
                updataNum += tbLineList2.size
            }

            //未上传,新增
            tbSurfaceList1 = inSurfaceDao.queryBuilder()
                    .where(InSurfaceDao.Properties.Flag.eq(0),
                            InSurfaceDao.Properties.Id.isNull)
                    .orderAsc(InSurfaceDao.Properties.Opttime).list()
//                String socialJson1 = gson.toJson(tbSurfaceList1);
//                FileUtils.writeFile(PathConstant.UPLOAD_DATA + "/tb_surface.txt", socialJson);
            map["tb_surface"] = tbSurfaceList1

            if (tbSurfaceList1.size > 0) {
                updataNum += tbSurfaceList1.size
            }

            //未上传,修改（id不为空，flag=0）
            tbSurfaceList2 = inSurfaceDao.queryBuilder()
                    .where(InSurfaceDao.Properties.Flag.eq(2))
                    .orderAsc(InSurfaceDao.Properties.Opttime).list()
//                String socialJson2 = gson.toJson(tbSurfaceList2);
//                FileUtils.writeFile(PathConstant.UPLOAD_DATA + "/tb_surface.txt", socialJson);
            map["tb_surface_modify"] = tbSurfaceList2

            if (tbSurfaceList2.size > 0) {
                updataNum += tbSurfaceList2.size
            }

            val json = gson.toJson(map)
            FileUtils.writeFile(filePath, json)

            ThreadUtils.executeMainThread {
                //联网上传
                uploadData()
            }
        }
    }

    private fun uploadData() {
        val map = HashMap<String, RequestBody>()

        val filePaths = ArrayList<String>()
        filePaths.add("/upload.txt")
        //        filePaths.add("/tb_line.txt");
        //        filePaths.add("/tb_surface.txt");

        var file: File
        for (i in filePaths.indices) {
            file = File(PathConstant.UPLOAD_DATA + filePaths[i])
            if (file.exists() && file.length() > 10) {
                val build = RequestBody.create(MediaType.parse("text/plain;charset=utf-8"), file)
                map["file\"; filename=\"" + file.name] = build
            }
        }

        if (updataNum == 0) {
            ToastUtils.showShort("没有新数据")
            return
        }

        val call = RetrofitFactory.create(RetrofitService::class.java).uploadMobileData(map)
        // Dialog
        val pd = ProgressDialog(context)
        pd.setMessage("正在上传...")
        // 点击对话框以外的地方无法取消
        pd.setCanceledOnTouchOutside(false)
        // 点击返回按钮无法取消
        pd.setCancelable(false)
        pd.setButton(DialogInterface.BUTTON_NEGATIVE, "取消") { dialog, which -> call.cancel() }
        pd.show()

        call.enqueue(object : Callback<UploadBean> {
            override fun onResponse(call: Call<UploadBean>, response: Response<UploadBean>) {
                LogUtils.d("onResponse" + response.body()!!)

                if (pd.isShowing) {
                    pd.dismiss()
                }
                val body = response.body()
                if (body == null) {
                    showResponseDialog("上传失败")
                    updataNum = 0
                    deleteUpdateFile()
                    return
                }
                processData(body)
            }

            override fun onFailure(call: Call<UploadBean>, t: Throwable) {
                t.printStackTrace()
                if (pd.isShowing) {
                    pd.dismiss()
                }
                if (!call.isCanceled) {
                    // 非点击取消
                    ToastUtils.showShort("网络错误")
                } else {
                    ToastUtils.showShort("上传取消")
                }
                updataNum = 0
                deleteUpdateFile()
            }
        })

    }

    /**
     * 处理数据
     *
     * @param body
     */
    private fun processData(body: UploadBean) {
        if (body.isStatus) {
            showResponseDialog("上传成功\n总数：$updataNum")
            ThreadUtils.executeSubThread { updateData() }

        } else {
            showResponseDialog("上传失败")
        }
        updataNum = 0
        deleteUpdateFile()
    }

    private lateinit var dialog: AlertDialog

    private fun showResponseDialog(message: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("提示")
                .setCancelable(false)
                .setMessage(message)
                .setPositiveButton("确定", null)
        dialog = builder.create()
        dialog.show()
    }

    /**
     * 将flag标记为1
     */
    private fun updateData() {
        updatePoi()
        updateLine()
        updateSurface()
        updateGraphic()
    }

    private fun updateSurface() {

        if (tbSurfaceList1 != null && tbSurfaceList1.size > 0) {
            for (i in tbSurfaceList1.indices) {
                tbSurfaceList1[i].flag = 1
            }
            inSurfaceDao.updateInTx(tbSurfaceList1)

        }

        if (tbSurfaceList2 != null && tbSurfaceList2.size > 0) {
            for (i in tbSurfaceList2.indices) {
                tbSurfaceList2[i].flag = 1
            }
            inSurfaceDao.updateInTx(tbSurfaceList2)

        }
    }

    private fun updateLine() {
        if (tbLineList1 != null && tbLineList1.size > 0) {
            for (i in tbLineList1.indices) {
                tbLineList1[i].flag = 1
            }
            inLineDao.updateInTx(tbLineList1)
        }

        if (tbLineList2 != null && tbLineList2.size > 0) {
            for (i in tbLineList2.indices) {
                tbLineList2[i].flag = 1
            }
            inLineDao.updateInTx(tbLineList2)
        }
    }

    private fun updatePoi() {

        if (tbPointList1 != null && tbPointList1.size > 0) {
            for (i in tbPointList1.indices) {
                tbPointList1[i].flag = 1
            }
            inPointDao.updateInTx(tbPointList1)
        }

        if (tbPointList2 != null && tbPointList2.size > 0) {
            for (i in tbPointList2.indices) {
                tbPointList2[i].flag = 1
            }
            inPointDao.updateInTx(tbPointList2)
        }
    }

    inner class NullStringToEmptyAdapterFactory<T> : TypeAdapterFactory {
        override fun <T> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
            val rawType = type.rawType as Class<T>
            return if (rawType != String::class.java) {
                null
            } else StringNullAdapter() as TypeAdapter<T>
        }
    }

    inner class StringNullAdapter : TypeAdapter<String>() {
        @Throws(IOException::class)
        override fun read(reader: JsonReader): String {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull()
                return ""
            }
            return reader.nextString()
        }

        @Throws(IOException::class)
        override fun write(writer: JsonWriter, value: String?) {
            if (value == null) {
                writer.nullValue()
                return
            }
            writer.value(value)
        }
    }

    fun searchFile() {
        ThreadUtils.executeSubThread {
            val path = PathConstant.UNDO_ZIP_PATH
            val fileDir = File(path)
            if (!fileDir.exists()) {
                fileDir.mkdirs()
            }
            // 获得文件夹下所有文件夹和文件的名字
            val fileDirs = fileDir.list()
            // 获得文件夹下所有文件夹和文件
            val files = fileDir.listFiles()
            //添加之前先清空集合
            fileDirNames.clear()
            fileDirPaths.clear()
            //抛出java.lang.UnsupportedOperationException
            val tempList = Arrays.asList(*fileDirs!!)
            fileDirNames.addAll(tempList)
            for (file in files!!) {
                fileDirPaths.add(file.absolutePath)
            }

            ThreadUtils.executeMainThread {
                //                adapter.notifyDataSetChanged()
                recyclerView_showmap.adapter = MapChoseListAdapter(context, fileDirNames, fileDirPaths, showMapListDialog)
                showMapListDialog.show()
            }
        }
    }

    //删除上传文件
    private fun deleteUpdateFile() {
        if (File(filePath).exists())
            File(filePath).delete()
    }

}