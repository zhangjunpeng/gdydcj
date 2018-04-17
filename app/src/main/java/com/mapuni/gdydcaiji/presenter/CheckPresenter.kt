package com.mapuni.gdydcaiji.presenter

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.AsyncTask
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.esri.android.map.GraphicsLayer
import com.esri.android.map.MapView
import com.esri.android.map.ags.ArcGISLocalTiledLayer
import com.esri.android.map.event.OnSingleTapListener
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
import com.mapuni.gdydcaiji.R
import com.mapuni.gdydcaiji.activity.DownloadMapActivity
import com.mapuni.gdydcaiji.activity.LineDetail
import com.mapuni.gdydcaiji.activity.PoiDetail
import com.mapuni.gdydcaiji.activity.SocialDetail
import com.mapuni.gdydcaiji.bean.TbLine
import com.mapuni.gdydcaiji.bean.TbPoint
import com.mapuni.gdydcaiji.bean.TbSurface
import com.mapuni.gdydcaiji.database.greendao.TbLineDao
import com.mapuni.gdydcaiji.database.greendao.TbPointDao
import com.mapuni.gdydcaiji.database.greendao.TbSurfaceDao
import com.mapuni.gdydcaiji.utils.PathConstant
import com.mapuni.gdydcaiji.utils.SPUtils
import com.mapuni.gdydcaiji.utils.ThreadUtils
import kotlinx.android.synthetic.main.activity_collection.*
import java.io.File
import java.util.ArrayList
import java.util.HashMap

/**
 * Created by zjp on 2018/4/16.
 * mail:zhangjunpeng92@163.com
 */
class CheckPresenter(context: Activity, mapView: MapView,recyler_check:RecyclerView):ZhiJianInterface {

    private val mapView:MapView=mapView
    private val context:Activity=context
    private val recyler:RecyclerView=recyler_check


    fun initShowZhiJianDialog(){

    }

    //数据库操作对象

    var tbLineDao: TbLineDao = GdydApplication.instances.daoSession.tbLineDao
    var tbPointDao: TbPointDao = GdydApplication.instances.daoSession.tbPointDao
    var tbSurfaceDao: TbSurfaceDao = GdydApplication.instances.daoSession.tbSurfaceDao


    var lineInfoList: List<TbLine>?=null
    var pointList: List<TbPoint>?=null
    var surfaceList: List<TbSurface>?=null

    private var mapFileName: String=""
    private var mapFilePath: String = ""

    private val graphicsLayer: GraphicsLayer = GraphicsLayer()
    private val graphicName: GraphicsLayer = GraphicsLayer()

    var infoMap: HashMap<Int, Any> = HashMap()
    var graphicInfoList:ArrayList<Map<String,Any>> = ArrayList()


    fun initMap(){
        mapFileName = SPUtils.getInstance().getString("checkedMap", "")
        mapFilePath = SPUtils.getInstance().getString("checkedMapPath", "")
        if (!TextUtils.isEmpty(mapFileName) && !TextUtils.isEmpty(mapFilePath)
                && File(mapFilePath).exists()) {
            val layer = ArcGISLocalTiledLayer("file://$mapFilePath/layers")
            mapView.addLayer(layer)
            mapView.addLayer(graphicsLayer, 1)
            mapView.addLayer(graphicName, 2)

        } else {
            // 获取所有地图文件
            getAllFiles()
        }


    }

    fun updataGraphic(){
        graphicsLayer.removeAll()
        UPDateGraphicTask().execute("")
    }
    inner class UPDateGraphicTask : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String?): String {
            pointList = tbPointDao.queryBuilder().where(TbPointDao.Properties.Authcontent.isNotNull).build().list()
            lineInfoList = tbLineDao.queryBuilder().where(TbLineDao.Properties.Authcontent.isNotNull).build().list()
            surfaceList = tbSurfaceDao.queryBuilder().where(TbSurfaceDao.Properties.Authcontent.isNotNull).build().list()
            return "done"
        }

        override fun onPostExecute(result: String) {
            for (info: TbPoint in pointList as List) {
                val point = Point(info.lng, info.lat)
                val simpleMarkerSymbol= SimpleMarkerSymbol(Color.BLUE, 10, SimpleMarkerSymbol.STYLE.CIRCLE)
                var graphic = Graphic(point, simpleMarkerSymbol)
                val uid = graphicsLayer.addGraphic(graphic)
                infoMap[uid] = info
//                val name = getPointName(info)
                val name =info.name

                addNameInMap(point,name)

                val map=HashMap<String,Any>()
                map["uid"]=uid
                map["obj"]=info
                graphicInfoList.add(map)

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
                val uid = graphicsLayer.addGraphic(graphic)
                infoMap[uid] = info
//                val name = getLineName(info)
                val name =info.name

                val tEnvelope = Envelope()
                polyline.queryEnvelope(tEnvelope)
                val tPoint = tEnvelope.center

                addNameInMap(tPoint, name)
                val map=HashMap<String,Any>()
                map["uid"]=uid
                map["obj"]=info
                graphicInfoList.add(map)
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

                val uid = graphicsLayer.addGraphic(Graphic(polygon, fillSymbol))
                infoMap[uid] = info
                val tEnvelope = Envelope()
                polygon.queryEnvelope(tEnvelope)
                val tPoint = tEnvelope.center
                val name =info.name
                addNameInMap(tPoint, name)

                val map=HashMap<String,Any>()
                map["uid"]=uid
                map["obj"]=info
                graphicInfoList.add(map)
            }

            recyler.adapter=RecylerAdapter()
            super.onPostExecute(result)


        }

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
                    mapFilePath = allDirFiles[0].absolutePath
                    // 将选中的地图名字存入sp中
                    SPUtils.getInstance().put("checkedMap", allDirFiles[0].getName())
                    SPUtils.getInstance().put("checkedMapPath", allDirFiles[0].getAbsolutePath())
                    val layer = ArcGISLocalTiledLayer("file://$mapFilePath/layers")
                    mapView.addLayer(layer)
                    mapView.addLayer(graphicsLayer, 1)
                    mapView.addLayer(graphicName, 2)
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



    fun addNameInMap(point: Point, name: String) {

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

    inner class RecylerAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.item_list_showgraphicinfo, parent, false)
            return MyViewHolder(view)
        }

        override fun getItemCount(): Int {
            return graphicInfoList.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
            holder as MyViewHolder
            holder.isdetet.visibility=View.INVISIBLE
            val info= graphicInfoList[position]["obj"]
            val uid=graphicInfoList[position]["uid"]
            when(info){
                is TbPoint->{
                    holder.name.text=info.name
                }
                is TbLine->{
                    holder.name.text=info.name
                }
                is TbSurface->{
                    holder.name.text=info.name
                }
            }

            holder.itemView.setOnClickListener {
                graphicsLayer.setSelectedGraphics(intArrayOf(uid as Int),true)
                val intent = Intent()
                when(info){
                    is TbPoint -> {
                        intent.setClass(context, PoiDetail::class.java)
                        intent.putExtra("resultBean", info)
                    }
                    is TbLine -> {
                        intent.setClass(context, LineDetail::class.java)
                        intent.putExtra("resultBean", info)
                    }
                    is TbSurface -> {
                        intent.setClass(context, SocialDetail::class.java)
                        intent.putExtra("resultBean", info)
                    }
                }

            }

        }

        inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var name: TextView = itemView.findViewById(R.id.name_item_showgraphic)
            var isdetet: CheckBox =itemView.findViewById(R.id.check_isdelete)
        }

    }


}