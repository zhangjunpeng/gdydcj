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
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
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
import com.mapuni.gdydcaiji.bean.*
import com.mapuni.gdydcaiji.database.greendao.TbLineDao
import com.mapuni.gdydcaiji.database.greendao.TbPointDao
import com.mapuni.gdydcaiji.database.greendao.TbSurfaceDao
import com.mapuni.gdydcaiji.net.RetrofitFactory
import com.mapuni.gdydcaiji.net.RetrofitService
import com.mapuni.gdydcaiji.utils.*
import kotlinx.android.synthetic.main.activity_collection.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*

/**
 * Created by zjp on 2018/4/16.
 * mail:zhangjunpeng92@163.com
 */
class CheckPresenter(context: Activity, mapView: MapView,recyler_check:RecyclerView):ZhiJianInterface {

    private val mapView:MapView=mapView
    private val context:Activity=context
    private val recyler:RecyclerView=recyler_check

    private val adapter=RecylerAdapter()


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

        recyler.adapter=adapter


    }

    fun updataGraphic(){
        graphicName.removeAll()
        graphicsLayer.removeAll()
        UPDateGraphicTask().execute("")
    }
    inner class UPDateGraphicTask : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String?): String {
            pointList = tbPointDao.queryBuilder().where(
                    TbPointDao.Properties.Authcontent.isNotNull,
                    TbPointDao.Properties.Flag.eq("0"),
                    TbPointDao.Properties.Id.isNotNull).build().list()
            lineInfoList = tbLineDao.queryBuilder().where(
                    TbLineDao.Properties.Authcontent.isNotNull,
                    TbLineDao.Properties.Flag.eq("0"),
                    TbLineDao.Properties.Id.isNotNull).build().list()
            surfaceList = tbSurfaceDao.queryBuilder().where(
                    TbSurfaceDao.Properties.Authcontent.isNotNull,
                    TbSurfaceDao.Properties.Flag.eq("0"),
                    TbSurfaceDao.Properties.Id.isNotNull).build().list()
            return "done"
        }

        override fun onPostExecute(result: String) {
            graphicInfoList.clear()
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

            adapter.notifyDataSetChanged()
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
                context.startActivity(intent)

            }

        }

        inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var name: TextView = itemView.findViewById(R.id.name_item_showgraphic)
            var isdetet: CheckBox =itemView.findViewById(R.id.check_isdelete)
        }

    }


    fun insertData2DB(filePath:String) {
        var fileTemp: FileInputStream? = null
        try {

            fileTemp = FileInputStream(File(filePath))
            val length = fileTemp!!.available()
            val buffer = ByteArray(length)
            fileTemp.read(buffer)
            val json = String(buffer)
            val gson = GsonBuilder().setDateFormat(DateUtil.YMDHMS).create()
            val downloadBean = gson.fromJson(json, DownloadBean::class.java)
            val tb_points = downloadBean.tb_point
            val tb_lines = downloadBean.tb_line
            val tb_surfaces = downloadBean.tb_surface

            var updateSize = 0

            if (tb_points != null && tb_points.size > 0) {
                        tbPointDao.insertOrReplaceInTx(tb_points)
                        updateSize += tb_points.size

            }
            if (tb_lines != null && tb_lines.size > 0) {
                tbLineDao.insertOrReplaceInTx(tb_lines)
                updateSize += tb_lines.size
            }
            if (tb_surfaces != null && tb_surfaces.size > 0) {
                        tbSurfaceDao.insertOrReplaceInTx(tb_surfaces)
                        updateSize += tb_surfaces.size
            }

            val size = tbPointDao.queryBuilder()
                    .where(TbPointDao.Properties.Flag.eq(0), //未上传
                            TbPointDao.Properties.Authflag.eq("1"))//错误
                    .list().size
            val size1 = tbLineDao.queryBuilder()
                    .where(TbLineDao.Properties.Flag.eq(0), //未上传
                            TbLineDao.Properties.Authflag.eq("1"))//错误
                    .list().size
            val size2 = tbSurfaceDao.queryBuilder()
                    .where(TbSurfaceDao.Properties.Flag.eq(0), //未上传
                            TbSurfaceDao.Properties.Authflag.eq("1"))//错误
                    .list().size

            val totalSize = size + size1 + size2

            val finalUpdateSize = updateSize
            ThreadUtils.executeMainThread {
                val dialog=Dialog(context)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                val textView=TextView(context)
                textView.text="需要修改数据总数：$totalSize\n新增数据数:$finalUpdateSize"
                dialog.setContentView(textView)
                dialog.show()

                updataGraphic()
            }

//            ThreadUtils.executeMainThread { tvResult.setText("需要修改数据总数：$totalSize\n新增数据数:$finalUpdateSize") }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private lateinit var tbPointList2: MutableList<TbPoint>
    private lateinit var tbLineList2: MutableList<TbLine>
    private lateinit var tbSurfaceList2: MutableList<TbSurface>

    fun createFile(){
        ThreadUtils.executeSubThread {
            //生成文件
            val gson = GsonBuilder().setDateFormat(DateUtil.YMDHMS).registerTypeAdapterFactory(NullStringToEmptyAdapterFactory<Any>()).excludeFieldsWithoutExposeAnnotation().create()
            val map = HashMap<String, Any>()

            //                //未上传,修改（id不为空，flag=0）
            tbPointList2 = tbPointDao.queryBuilder()
                    .where(TbPointDao.Properties.Flag.eq(0), //未上传
                            TbPointDao.Properties.Id.isNotNull,
                            TbPointDao.Properties.Authflag.eq(getFlagByUser()))
                    .orderAsc(TbPointDao.Properties.Opttime).list()
            //                String buildingJson2 = gson.toJson(tbPointList2);
            map["tb_point_modify"] = tbPointList2



            //未上传,修改（id不为空，flag=0）
            tbLineList2 = tbLineDao.queryBuilder()
                    .where(TbLineDao.Properties.Flag.eq(0),
                            TbLineDao.Properties.Id.isNotNull,
                            TbLineDao.Properties.Authflag.eq(getFlagByUser()))
                    .orderAsc(TbLineDao.Properties.Opttime).list()
            //                String poiJson2 = gson.toJson(tbLineList2);
            //                FileUtils.writeFile(PathConstant.UPLOAD_DATA + "/tb_line.txt", poiJson);

            map["tb_line_modify"] = tbLineList2


            //未上传,修改（id不为空，flag=0）
            tbSurfaceList2 = tbSurfaceDao.queryBuilder()
                    .where(TbSurfaceDao.Properties.Flag.eq(0),
                            TbSurfaceDao.Properties.Id.isNotNull,
                            TbSurfaceDao.Properties.Authflag.eq(getFlagByUser()))
                    .orderAsc(TbSurfaceDao.Properties.Opttime).list()
            //                String socialJson2 = gson.toJson(tbSurfaceList2);
            //                FileUtils.writeFile(PathConstant.UPLOAD_DATA + "/tb_surface.txt", socialJson);
            map["tb_surface_modify"] = tbSurfaceList2

            val json = gson.toJson(map)
            FileUtils.writeFile(PathConstant.UPLOAD_DATA + "/upload.txt", json)


            ThreadUtils.executeMainThread {
                //联网上传
                uploadData()
            }
        }
    }

    fun getFlagByUser(): String {
        var flag = ""
        val roleid = SPUtils.getInstance().getString("roleid")
        if (roleid == "6") {
            //外业
            flag = "0"
        } else if (roleid == "2") {
            //质检
            flag = "1"
        }
        return flag
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



        val call = RetrofitFactory.create(RetrofitService::class.java).upload(map)
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

                    return
                }
                processData(body)
            }

            override fun onFailure(call: Call<UploadBean>, t: Throwable) {
                t.printStackTrace()
                if (!call.isCanceled) {
                    // 非点击取消
                    //LogUtils.d(t.getMessage());
                    if (pd.isShowing) {
                        pd.dismiss()
                    }
                    ToastUtils.showShort("网络错误")
                } else {
                    ToastUtils.showShort("上传取消")
                }
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
            ToastUtile.showText(context,"上传成功\n" )
            ThreadUtils.executeSubThread { updateData() }

        } else {
            showResponseDialog("上传失败")
        }
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
        updataGraphic()
    }

    private fun updateSurface() {

        if (tbSurfaceList2 != null && tbSurfaceList2.size > 0) {
            for (i in tbSurfaceList2.indices) {
                tbSurfaceList2[i].flag = 1
            }
            tbSurfaceDao.updateInTx(tbSurfaceList2)

        }
    }

    private fun updateLine() {


        if (tbLineList2 != null && tbLineList2.size > 0) {
            for (i in tbLineList2.indices) {
                tbLineList2[i].flag = 1
            }
            tbLineDao.updateInTx(tbLineList2)
        }
    }

    private fun updatePoi() {


        if (tbPointList2 != null && tbPointList2.size > 0) {
            for (i in tbPointList2.indices) {
                tbPointList2[i].flag = 1
            }
            tbPointDao.updateInTx(tbPointList2)
        }
    }

}