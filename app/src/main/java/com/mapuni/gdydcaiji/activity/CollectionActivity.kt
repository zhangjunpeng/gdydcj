package com.mapuni.gdydcaiji.activity

import android.annotation.TargetApi
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.*
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.transition.Transition
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.*
import com.esri.android.map.GraphicsLayer
import com.esri.android.map.LocationDisplayManager
import com.esri.android.map.ags.ArcGISLocalTiledLayer
import com.esri.android.map.event.OnPanListener
import com.esri.android.map.event.OnSingleTapListener
import com.esri.android.map.event.OnZoomListener
import com.esri.android.runtime.ArcGISRuntime
import com.esri.core.geometry.*
import com.esri.core.map.Graphic
import com.esri.core.symbol.PictureMarkerSymbol
import com.esri.core.symbol.SimpleFillSymbol
import com.esri.core.symbol.SimpleLineSymbol
import com.esri.core.symbol.SimpleMarkerSymbol
import com.google.gson.annotations.Until
import com.mapuni.gdydcaiji.BZRecyAdapter
import com.mapuni.gdydcaiji.GdydApplication
import com.mapuni.gdydcaiji.GraphicListAdapter
import com.mapuni.gdydcaiji.R
import com.mapuni.gdydcaiji.bean.*
import com.mapuni.gdydcaiji.database.greendao.*
import com.mapuni.gdydcaiji.service.CopyService
import com.mapuni.gdydcaiji.utils.*
import com.xw.repo.BubbleSeekBar
import kotlinx.android.synthetic.main.activity_collection.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.File
import java.util.*


class CollectionActivity : AppCompatActivity(), View.OnClickListener, OnSingleTapListener, View.OnTouchListener, OnZoomListener, OnPanListener,BubbleSeekBar.OnProgressChangedListener {


    private var mIsLoading: Boolean = false

    override fun postPointerMove(v: Float, v1: Float, v2: Float, v3: Float) {


    }

    private var mStartX: Float = 0f
    private var mStartY: Float = 0f


    override fun prePointerMove(p0: Float, p1: Float, p2: Float, p3: Float) {

        if (mStartX == 0f && mStartY == 0f) {
            mStartX = p0
            mStartY = p1
        }

    }

    override fun prePointerUp(p0: Float, p1: Float, p2: Float, p3: Float) {
    }

    override fun postPointerUp(v: Float, v1: Float, v2: Float, v3: Float) {

        val distance = Math.sqrt(((v2 - mStartX) * (v2 - mStartX) +
                (v3 - mStartY) * (v3 - mStartY)).toDouble())
        if (distance < 150) {
            mStartX = 0f
            mStartY = 0f
            return
        }
        mStartX = 0f
        mStartY = 0f
        if (!mIsLoading) {
            mIsLoading = true
            graphicsLayer.removeAll()
            localGraphicsLayer.removeAll()
            graphicName.removeAll()
            upDateGraphic()
        }
    }


//    var mapfilePath = ""

    //点采集0，线采集1，采集面2
    //3特殊，范围选择点
    private var currentCode = 0
    private var targetCode = -1

    private val pointPloygon = ArrayList<Point>()
    private val pointPloyline= ArrayList<Point>()

    private var graphicsLayer: GraphicsLayer= GraphicsLayer()
    private var tempGraphicLayer: GraphicsLayer= GraphicsLayer()
    private var localGraphicsLayer: GraphicsLayer = GraphicsLayer()
    var graphicName: GraphicsLayer = GraphicsLayer()
    lateinit var alertDialog: AlertDialog


    var tolerance: Int = 20

    private var mExitTime: Long = 0
    private var mapFileName: String? = null
    private var mapFilePath: String? = null


    //数据库操作对象

    lateinit var tbLineDao: TbLineDao
    lateinit var tbPointDao: TbPointDao
    lateinit var tbSurfaceDao: TbSurfaceDao

    private lateinit var lineInfoList: List<TbLine>
    private lateinit var pointList: List<TbPoint>
    private lateinit var surfaceList:List<TbSurface>


    //显示的点线面


    lateinit var infoList: ArrayList<Map<String, Any>>
    lateinit var infoMap: HashMap<Int, Any>

    private lateinit var graphicListAdtaper: GraphicListAdapter

    private lateinit var showGrahipcListDialog: Dialog
    private lateinit var recyclerView: RecyclerView

    private lateinit var bzDialog: Dialog
    private lateinit var recyclerView_bz: RecyclerView

    private var manager: SensorManager? = null
    private val listener = SensorListener()

    lateinit var instance: Activity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection)
        instance = this
        PermissionUtils.requestAllPermission(this)

        ArcGISRuntime.setClientId("uK0DxqYT0om1UXa9")//加入arcgis研发验证码
//        mapfilePath = Environment.getExternalStorageDirectory().absolutePath+"/map/" + "/layers"
        EventBus.getDefault().register(this)

        val intent = Intent(this, CopyService::class.java)
        startService(intent)

        //获取系统服务（SENSOR_SERVICE)返回一个SensorManager 对象 
        manager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        initMapView()
        initShowGraphicDialog()
        initBZDialog()
        initToolsPopWindow()
        initData()
        initListener()
        upDateView()


    }

    private fun initData() {
        tbLineDao=GdydApplication.instances.daoSession.tbLineDao
        tbPointDao=GdydApplication.instances.daoSession.tbPointDao
        tbSurfaceDao=GdydApplication.instances.daoSession.tbSurfaceDao
        infoMap = HashMap()



        for (i in 0 until 12){
            point_bz_array.add(false)
        }
        for (i in 0 until 7){
            surface_bz_array.add(false)
        }
        for (i in 0 until 4){
            line_bz_array.add(false)
        }

        point_bz_array[0]=true
        surface_bz_array[0]=true
        line_bz_array[0]=true

    }


    private fun initListener() {
        dingwei_collect.setOnClickListener {
            val locationDisplayManager = mapview_collect.locationDisplayManager
            locationDisplayManager.autoPanMode = (LocationDisplayManager.AutoPanMode.LOCATION)

            if (locationDisplayManager.isStarted){
                locationDisplayManager.stop()
            }else{
                locationDisplayManager.start()
            }

        }
        poi_collect.setOnClickListener(this)
        line_collect.setOnClickListener(this)
        newploygon_collect.setOnClickListener(this)
        tianjia_collect.setOnClickListener(this)


        selectpoint_collect.setOnClickListener(this)

        iv_amplify.setOnClickListener(this)
        iv_reduce.setOnClickListener(this)
        btn_menu.setOnClickListener(this)


        seek_collect.onProgressChangedListener=this


    }

    private fun initMapView() {


        mapFileName = SPUtils.getInstance().getString("checkedMap", "")
        mapFilePath = SPUtils.getInstance().getString("checkedMapPath", "")
        if (!TextUtils.isEmpty(mapFileName) && !TextUtils.isEmpty(mapFilePath)
                && File(mapFilePath).exists()) {
            val layer = ArcGISLocalTiledLayer("file://$mapFilePath/layers")
            mapview_collect.addLayer(layer)

            mapview_collect.addLayer(graphicsLayer, 1)
            mapview_collect.addLayer(tempGraphicLayer, 2)
            mapview_collect.addLayer(localGraphicsLayer, 3)
            mapview_collect.addLayer(graphicName, 4)

        } else {
            // 获取所有地图文件
            getAllFiles()
        }

        mapview_collect.onSingleTapListener = this
        mapview_collect.onZoomListener = this
        mapview_collect.onPanListener = this
        mapview_collect.isShowMagnifierOnLongPress = true
        mapview_collect.setAllowMagnifierToPanMap(true)
    }

    override fun onClick(v: View?) {
        if (v is View) {
            when (v.id) {
                R.id.poi_collect -> {
                    targetCode = 0
                    beginPOICollect()
                }
                R.id.line_collect-> {
                    targetCode = 1
                    beigonLineCollect()
                }
                R.id.newploygon_collect -> {
                    targetCode = 2
                    beginpolygonCollect()
                }
                R.id.tianjia_collect ->
                    addPointInmap()
                R.id.selectpoint_collect -> {
                    targetCode = 3
                    beiginSelectPoint()
                }
                R.id.iv_amplify -> {
                    mapview_collect.zoomin()
                }
                R.id.iv_reduce -> {
                    // 分辨率放大
                    mapview_collect.zoomout()

                }
                R.id.btn_menu -> {
                    showMenuPop(v)
                }

            }
        }

    }

    private fun beigonLineCollect() {
        //开始范围选择
        if (currentCode == 2 && pointPloygon.size > 2) {
            showConfirmDiaolog()
        } else {
            currentCode = targetCode
            upDateView()
        }
    }


    private fun addPointInmap(point:Point=mapview_collect.center) {
        when (currentCode) {
            0 -> {
                val intent1 = Intent(this, PoiDetail::class.java)
                intent1.putExtra("lat", point.y)
                intent1.putExtra("lng", point.x)

                startActivity(intent1)
            }
            1 -> {
                addPolyLineInMap(point)
            }
            2 -> {
                addPolygonInMap(point)
            }
        }

    }

    private fun addPolyLineInMap(point:Point=mapview_collect.center) {
        pointPloyline.add(point)
        drawline(pointPloyline)
    }

    private fun drawline(pointPloyline: ArrayList<Point>) {
        if (pointPloyline.size == 1) run { grahicGonUid = addPointInMap(pointPloyline[0]) } else if (pointPloyline.size > 1) {
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

            grahicGonUid= graphicsLayer.addGraphic(Graphic(polyline, simpleLineSymbol))
        }
    }


    private fun ploygonBack() {
        graphicsLayer.removeGraphic(grahicGonUid)
        if (pointPloygon.size > 0) {
            pointPloygon.remove(pointPloygon.last())
            drawGon(pointPloygon)
        }
    }

    private fun addPolygonInMap(point:Point=mapview_collect.center) {
        //开始小区采集
        pointPloygon.add(point)
        drawGon(pointPloygon)
    }

    private fun beginCountryCollect() {
        //开始村采集

        if (currentCode == 2 && pointPloygon.size > 2) {
            showConfirmDiaolog()
        } else {
            currentCode = targetCode
            upDateView()

        }


    }

    private fun beginpolygonCollect() {
        if (currentCode==1&&pointPloyline.size>1){
            showConfirmDiaolog()
        }else {
            currentCode = targetCode
            upDateView()
        }
    }

    private fun beginLouyuCollect() {
        //开始楼宇采集

        if (currentCode == 2 && pointPloygon.size > 2) {
            showConfirmDiaolog()
        } else {
            currentCode = targetCode
            upDateView()
        }

    }

    private fun beginPOICollect() {
        //开始范围选择
        if (currentCode==1&&pointPloyline.size>1){
            showConfirmDiaolog()

        }else if (currentCode == 2 && pointPloygon.size > 2) {
            showConfirmDiaolog()
        } else {
            currentCode = targetCode
            upDateView()
        }
    }

    private fun beiginSelectPoint() {
        if (currentCode==1&&pointPloyline.size>1){
            showConfirmDiaolog()
        }else if (currentCode == 2 && pointPloygon.size > 2) {
            showConfirmDiaolog()
        } else {
            currentCode = targetCode
            upDateView()
        }
    }

    private fun upDateView() {
        tianjia_collect.visibility = View.VISIBLE
        when (currentCode) {
            0 -> {

                if (popupWindow.isShowing){
                    popupWindow.dismiss()
                }

                poi_collect.isSelected = true
                line_collect.isSelected = false
                newploygon_collect.isSelected = false
                selectpoint_collect.isSelected = false

                seek_collect.visibility = View.INVISIBLE
            }
            1 -> {

                popupWindow.showAtLocation(findViewById(R.id.main), Gravity.BOTTOM,0,0)

                poi_collect.isSelected = false
                line_collect.isSelected = true
                newploygon_collect.isSelected = false
                selectpoint_collect.isSelected = false

                seek_collect.visibility = View.INVISIBLE

            }
            2 -> {

                popupWindow.showAtLocation(findViewById(R.id.main), Gravity.BOTTOM,0,0)

                poi_collect.isSelected = false
                line_collect.isSelected = false
                newploygon_collect.isSelected = true
                selectpoint_collect.isSelected = false

                seek_collect.visibility = View.INVISIBLE

            }
            3 -> {
                if (popupWindow.isShowing){
                    popupWindow.dismiss()
                }
                poi_collect.isSelected = false
                line_collect.isSelected = false
                newploygon_collect.isSelected = false
                selectpoint_collect.isSelected = true
                seek_collect.visibility = View.VISIBLE

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
            pointPloyline.clear()
            graphicsLayer.removeGraphic(grahicGonUid)
            currentCode = targetCode
            when (targetCode) {
                0 -> {
                    beginPOICollect()
                }
                1 -> {
                    beigonLineCollect()
                }
                2 -> {
                    beginpolygonCollect()
                }
                3 -> {
                    beiginSelectPoint()
                }
            }
        }

        alertDialog = builder.create()
        alertDialog.show()
        return alertDialog
    }


    private var grahicGonUid: Int = 0
    private fun drawGon(pointList: ArrayList<Point>) {
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

    override fun onSingleTap(v: Float, v1: Float) {
        singleTapOnCollection(v, v1)
    }


    private fun singleTapOnCollection(v: Float, v1: Float) {
        val center=mapview_collect.toMapPoint(v,v1)
        when (currentCode) {
            0 -> {
                val intent1 = Intent(this, PoiDetail::class.java)
                intent1.putExtra("lat", center.y)
                intent1.putExtra("lng", center.x)

                startActivity(intent1)
            }
            1 -> {
                addPolyLineInMap(center)
            }
            2 -> {
                addPolygonInMap(center)
            }
            3 -> {
                getGraphics(v, v1)
            }

        }

    }


    private var tempGraphicID: Int = 0
    private fun getGraphics(v: Float, v1: Float) {

        infoList = ArrayList()
        val symbol = SimpleMarkerSymbol(Color.parseColor("#501EE15E"), tolerance, SimpleMarkerSymbol.STYLE.CIRCLE)
        val point = mapview_collect.toMapPoint(v, v1)
        val graphic = Graphic(point, symbol)
        tempGraphicID = tempGraphicLayer.addGraphic(graphic)
        val uids_local = localGraphicsLayer.getGraphicIDs(v, v1, tolerance, 50)

        if (uids_local.isEmpty()) {
            Toast.makeText(this, "选择范围内没有点", Toast.LENGTH_SHORT).show()
            val removeGraphic=RemoveGraphic()
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
                infoList.add(map)

            }

            graphicListAdtaper = GraphicListAdapter(instance, infoList, showGrahipcListDialog)
            recyclerView.adapter = graphicListAdtaper
            showGrahipcListDialog.show()
            val removeGraphic=RemoveGraphic()
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

    private fun addNameInMap(point: Point, name: String) {

        val tv = TextView(this)
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
        val picturSymbol = PictureMarkerSymbol(BitmapDrawable(resources, bitmap))
        picturSymbol.offsetY = 10f
        val nameGraphic = Graphic(point, picturSymbol)
        graphicName.addGraphic(nameGraphic)
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            Log.i("onTouchEvent", "down")
        }
        if (event.action == MotionEvent.ACTION_UP) {
            Log.i("onTouchEvent", "up")

            graphicsLayer.removeGraphic(tempGraphicID)

        }
        return super.onTouchEvent(event)
    }

    override fun preAction(p0: Float, p1: Float, p2: Double) {

    }

    override fun postAction(p0: Float, p1: Float, p2: Double) {
        localGraphicsLayer.removeAll()
        graphicName.removeAll()
        upDateGraphic()
    }

    private fun upDateGraphic() {
        UPDateeGraphic().execute("")
    }

    inner class UPDateeGraphic() : AsyncTask<String, Void, Polygon>(){
        override fun doInBackground(vararg params: String?): Polygon {
            val currentPloygon = mapview_collect.extent
            val leftTopP = currentPloygon.getPoint(0)
            val rightTopP = currentPloygon.getPoint(1)
            val leftBottomP = currentPloygon.getPoint(2)
            pointList=tbPointDao.queryBuilder().where(TbPointDao.Properties.Lng.between(leftTopP.x, rightTopP.x)
                    , TbPointDao.Properties.Lat.between(leftTopP.y, leftBottomP.y)).list()
            lineInfoList=tbLineDao.loadAll()
            surfaceList=tbSurfaceDao.loadAll()
            pointList=tbPointDao.queryBuilder().where(TbPointDao.Properties.Lng.between(leftTopP.x, rightTopP.x)
                    , TbPointDao.Properties.Lat.between(leftTopP.y, leftBottomP.y)).list()
            lineInfoList=tbLineDao.loadAll()
            surfaceList=tbSurfaceDao.loadAll()

            return currentPloygon
        }

        override fun onPostExecute(result: Polygon) {
            updateGraphicInLocal(result)
            super.onPostExecute(result)
        }

    }

    private fun updateGraphicInLocal(currentPloygon: Polygon) {
        for (info: TbPoint in pointList) {
            val point = Point(info.lng, info.lat)
            val simpleMarkerSymbol = SimpleMarkerSymbol(Color.RED, 10, SimpleMarkerSymbol.STYLE.CIRCLE)
            val graphic = Graphic(point, simpleMarkerSymbol)
            val uid = localGraphicsLayer.addGraphic(graphic)
            infoMap[uid] = info
            val name=getPointName(info)
            addNameInMap(point, name)
        }

        for (info:TbLine in lineInfoList){
            val bj = info.polyarrays
            val polyline=Polyline()
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
            val simpleLineSymbol=SimpleLineSymbol(Color.RED,2f,SimpleLineSymbol.STYLE.SOLID)
            val graphic = Graphic(polyline, simpleLineSymbol)
            val uid = localGraphicsLayer.addGraphic(graphic)
            infoMap[uid] = info
            val name=getLineName(info)

            val tEnvelope = Envelope()
            polyline.queryEnvelope(tEnvelope)
            val tPoint = tEnvelope.center

            addNameInMap(tPoint, name)

        }

        for (info: TbSurface in surfaceList) {
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
                val name=getSurfaceName(info)
                addNameInMap(tPoint, name)


        }

        mapview_collect.invalidate()
        mIsLoading = false
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
                    mapFilePath = allDirFiles[0].getAbsolutePath()
                    // 将选中的地图名字存入sp中
                    SPUtils.getInstance().put("checkedMap", allDirFiles[0].getName())
                    SPUtils.getInstance().put("checkedMapPath", allDirFiles[0].getAbsolutePath())
                    val layer = ArcGISLocalTiledLayer("file://$mapFilePath/layers")
                    mapview_collect.addLayer(layer)
                    graphicsLayer = GraphicsLayer()
                    mapview_collect.addLayer(graphicsLayer, 1)
                    mapview_collect.addLayer(tempGraphicLayer, 2)
                    mapview_collect.addLayer(localGraphicsLayer, 3)
                    mapview_collect.addLayer(graphicName, 4)
                } else {
                    mapview_collect.setVisibility(View.GONE)
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

    @Subscribe
    fun onEventMainThread(eventBean: EventBean) {
        if ("download".equals(eventBean.beanStr))
        // 下载成功
            getAllFiles()
    }

    private fun showMenuPop(view: View?) {
        val inflate = LayoutInflater.from(this).inflate(R.layout.ppw_menu, null, false)
        val tvUploadData = inflate.findViewById<TextView>(R.id.tv_upload)
        val tvChooseMap = inflate.findViewById<TextView>(R.id.tv_choosemap)
        val tvCopyDb = inflate.findViewById<TextView>(R.id.tv_copy_bd)
        val tv_bz:TextView=inflate.findViewById(R.id.tv_bz)

        val ppw = PopupWindow(inflate,ViewGroup.LayoutParams.WRAP_CONTENT, ScreenUtils.dp2px(this, 150F), true)
        ppw.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.white)))
        ppw.isOutsideTouchable = true
        ppw.isTouchable = true

        tvUploadData.setOnClickListener {
            startActivity(Intent(this, UploadDataActivity::class.java))
            ppw.dismiss()
        }

        tvChooseMap.setOnClickListener {
            startActivity(Intent(this, ChooseMapActivity::class.java))
            ppw.dismiss()
        }

        tvCopyDb.setOnClickListener {
            ppw.dismiss()
            ThreadUtils.executeSubThread {
                FileUtils.copyFile(GdydApplication.getInstances().db.path, PathConstant.DATABASE_PATH + "/sport.db") { true }
                ThreadUtils.executeMainThread {
                    ToastUtils.showShort("备份成功")
                }
            }
        }
        tv_bz.setOnClickListener {
            ppw.dismiss()

            bzDialog.show()
            bzRecyAdapter_point=BZRecyAdapter(this,0,point_bz_array)
            bzRecyAdapter_line=BZRecyAdapter(this,1,line_bz_array)
            bzRecyAdapter_surface=BZRecyAdapter(this,2,surface_bz_array)

            recyclerView_bz.adapter=bzRecyAdapter_point
        }

        //获取点击View的坐标
        val location = IntArray(2)
        view!!.getLocationOnScreen(location)
        val y = location[1] - ppw.height
        ppw.showAtLocation(view, Gravity.NO_GRAVITY, location[0], y)
    }
    private lateinit var bzRecyAdapter_point: BZRecyAdapter
    private lateinit var bzRecyAdapter_line: BZRecyAdapter
    private lateinit var bzRecyAdapter_surface: BZRecyAdapter


    private fun initBZDialog() {
        bzDialog = Dialog(instance)
        bzDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val contentView = LayoutInflater.from(instance).inflate(R.layout.dialog_bz, null, false)
        bzDialog.setContentView(contentView)
        bzDialog.setCanceledOnTouchOutside(false)
        recyclerView_bz = contentView.findViewById(R.id.recycler_dialog_bz)
        val spinner:Spinner=contentView.findViewById(R.id.spinener_bz_dialog)
        val mItems = listOf("POI标注","线标注","面标注")
        val adapter = ArrayAdapter(this, R.layout.item_spinner_dialog, R.id.tv_type, mItems)
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown)
        spinner.adapter = adapter
        spinner.onItemSelectedListener=object :AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position){
                    0->{
                        recyclerView_bz.adapter=bzRecyAdapter_point
                        bzRecyAdapter_point.notifyDataSetChanged()
                    }
                    1->{
                        recyclerView_bz.adapter=bzRecyAdapter_line
                        bzRecyAdapter_line.notifyDataSetChanged()
                    }
                    2->{
                        recyclerView_bz.adapter=bzRecyAdapter_surface
                        bzRecyAdapter_surface.notifyDataSetChanged()
                    }

                }
            }
        }

        recyclerView_bz.layoutManager = LinearLayoutManager(this)

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.repeatCount == 0) {
            exit()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    /**
     * 点击两次退出
     *
     * @return
     */
    fun exit() {
        if (System.currentTimeMillis() - mExitTime > 2000) {
            ToastUtils.showShort("再点一次退出")
            mExitTime = System.currentTimeMillis()
        } else {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        val intent = Intent(this, CopyService::class.java)
        stopService(intent)
    }


    private fun initShowGraphicDialog() {
        showGrahipcListDialog = Dialog(instance)
        showGrahipcListDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val contentView = LayoutInflater.from(instance).inflate(R.layout.dialog_showgraphicinfo, null, false)
        showGrahipcListDialog.setContentView(contentView)
        showGrahipcListDialog.setCanceledOnTouchOutside(false)
        recyclerView = contentView.findViewById(R.id.recycler_dialog)

        recyclerView.layoutManager = LinearLayoutManager(this)


    }

    override fun onResume() {
        super.onResume()
        /**
         * 获取方向传感器
         * 通过SensorManager对象获取相应的Sensor类型的对象
         */
        val magneticSensor = manager!!.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        val accelerometerSensor = manager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        manager!!.registerListener(listener, magneticSensor, SensorManager.SENSOR_DELAY_GAME)
        manager!!.registerListener(listener, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME)
        
        initDialogSize()
    }


    private fun initDialogSize() {
        val dialogWindow = showGrahipcListDialog.window
        val dialogWindow_bz = bzDialog.window

        /*
       * 将对话框的大小按屏幕大小的百分比设置
       */
        val lp = dialogWindow.attributes // 获取对话框当前的参数值
        val lp_bz=dialogWindow_bz.attributes
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        lp.height =LinearLayout.LayoutParams.WRAP_CONTENT
        lp.width = (dm.widthPixels * 0.75).toInt()// 宽度设置为屏幕的0.65
        lp_bz.height =LinearLayout.LayoutParams.WRAP_CONTENT
        lp_bz.width = (dm.widthPixels * 0.75).toInt()// 宽度设置为屏幕的0.65
        dialogWindow.attributes = lp
        dialogWindow_bz.attributes = lp_bz

    }

    @Subscribe
    fun onDeleteInfo(eventDeleteInfo: EventDeleteInfo){
        for (obj in eventDeleteInfo.deleteList){
            when(obj){

                is TbPoint->{
                    tbPointDao.delete(obj)
                }
                is TbLine->{
                    tbLineDao.delete(obj)
                }
                is TbSurface->{
                    tbSurfaceDao.delete(obj)
                }


            }
        }
        localGraphicsLayer.removeAll()
        graphicsLayer.removeAll()
        graphicName.removeAll()
        upDateGraphic()
        showGrahipcListDialog.dismiss()
    }



    private var point_bz_array= ArrayList<Boolean>()
    private var line_bz_array= ArrayList<Boolean>()
    private var surface_bz_array=ArrayList<Boolean>()

    @Subscribe
    fun onChangeMap(event: EventChangeMap) {
        finish()
    }
    @Subscribe
    fun onCompleteBZ(eventBZ: EventBZ) {
        when(eventBZ.type){
            0->{
                point_bz_array=eventBZ.booleanArrayList
            }
            1->{
                line_bz_array=eventBZ.booleanArrayList
            }
            2->{
                surface_bz_array=eventBZ.booleanArrayList
            }
            else->{

            }
        }
        graphicName.removeAll()
        localGraphicsLayer.removeAll()
        graphicsLayer.removeAll()
        upDateGraphic()
        bzDialog.dismiss()
    }



    private fun getPointName(info: TbPoint):String{
        var name=""
        val string_poi= listOf(info.name,info.lytype,info.lyxz,info.fl,info.dz,info.lyxz,info.dy,info.lxdh,info.dj,info.lycs,info.lyzhs,info.opttime.toString())
        for (i in 0 until point_bz_array.size){
            if (point_bz_array[i]&&string_poi[i].isNotEmpty()){
                name += string_poi[i]+"/"
            }
        }
        return name.dropLast(1)
    }
    private fun getLineName(info: TbLine):String{
        var name=""
        val string_poi= listOf(info.name,info.sfz,info.zdz,info.opttime.toString())
        for (i in 0 until line_bz_array.size){
            if (line_bz_array[i]&&string_poi[i].isNotEmpty()){
                name += string_poi[i]+"/"
            }
        }
        return name.dropLast(1)
    }
    private fun getSurfaceName(info: TbSurface):String{
        var name=""
        val string_poi= listOf(info.name,info.xqdz,info.fl,info.wyxx,info.lxdh,info.lds,info.opttime.toString())
        for (i in 0 until surface_bz_array.size){
            if (surface_bz_array[i]&&string_poi[i].isNotEmpty()){
                name += string_poi[i]+"/"
            }
        }
        return name.dropLast(1)
    }

    @Subscribe
    fun onUPDataGraphic(eventUpdate: EvevtUpdate) {
        pointPloygon.clear()
        tempGraphicLayer.removeAll()
        graphicName.removeAll()
        localGraphicsLayer.removeAll()
        graphicsLayer.removeAll()
        upDateGraphic()
    }


    override fun onPause() {
        //应用不在前台时候销毁掉监听器 
        manager!!.unregisterListener(listener)
        super.onPause()
    }

    private inner class SensorListener : SensorEventListener {

        private var predegree = 0f
        internal var accelerometerValues = FloatArray(3)
        internal var magneticValues = FloatArray(3)


        override fun onSensorChanged(event: SensorEvent) {

            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                accelerometerValues = event.values
            } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                magneticValues = event.values
            }
            val R = FloatArray(9)
            val values = FloatArray(3)
            SensorManager.getRotationMatrix(R, null, accelerometerValues, magneticValues)
            SensorManager.getOrientation(R, values)
            val degree = Math.toDegrees(values[0].toDouble()).toFloat()//旋转角度
            /**动画效果 */
            val animation = RotateAnimation(predegree, -degree,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
            animation.duration = 200
            iv_compass.startAnimation(animation)
            predegree = -degree

        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

        }
    }


    override fun getProgressOnActionUp(bubbleSeekBar: BubbleSeekBar?, progress: Int, progressFloat: Float) {
    }

    override fun getProgressOnFinally(bubbleSeekBar: BubbleSeekBar?, progress: Int, progressFloat: Float) {
    }

    override fun onProgressChanged(bubbleSeekBar: BubbleSeekBar?, progress: Int, progressFloat: Float) {
        tolerance = progress
    }

    private lateinit var popupWindow: PopupWindow

    private fun initToolsPopWindow(){

        val contentView=LayoutInflater.from(this).inflate(R.layout.popwindow_tools,null,false)
        popupWindow=PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        popupWindow.animationStyle = R.style.mypopwindow_anim_style// 设置动画

        popupWindow.setBackgroundDrawable(BitmapDrawable())
        //点击外部消失
//        popupWindow.isOutsideTouchable = true
        //设置可以点击
        popupWindow.isTouchable = true
        //进入退出的动画
        val baocun=popupWindow.contentView.findViewById<TextView>(R.id.baocun_collect)
        val quxiao=popupWindow.contentView.findViewById<TextView>(R.id.chanel_collect)
        val houtui=popupWindow.contentView.findViewById<TextView>(R.id.houtui_collect)
        baocun.setOnClickListener {
            if (currentCode==1){
                if (pointPloyline.size > 1) {
                    var bj = ""
                    for (point in pointPloyline) {
                        bj = bj + point.x.toString() + "," + point.y.toString() + ";"
                    }
                    bj.dropLast(1)
                    val intent1 = Intent(this@CollectionActivity, LineDetail::class.java)
                    intent1.putExtra("bj", bj)
                    startActivity(intent1)
                } else {
                    ToastUtils.showShort("请在地图上选择点")
                }
            }
            if (currentCode==2) {
                if (pointPloygon.size > 2) {
                    var bj = ""
                    for (point in pointPloygon) {
                        bj = bj + point.x.toString() + "," + point.y.toString() + ";"
                    }
                    bj.dropLast(1)
                    val intent1 = Intent(this@CollectionActivity, SocialDetail::class.java)
                    intent1.putExtra("bj", bj)
                    startActivity(intent1)
                } else {
                    ToastUtils.showShort("请在地图上选择点")
                }
            }
        }
        quxiao.setOnClickListener {
            if (currentCode==1){
                graphicsLayer.removeGraphic(grahicGonUid)
                pointPloyline.clear()
            }
            if (currentCode==2){
                graphicsLayer.removeGraphic(grahicGonUid)
                pointPloygon.clear()
            }
        }
        houtui.setOnClickListener {
            if (currentCode==2){
                ploygonBack()
            }
            if (currentCode==1){
                graphicsLayer.removeGraphic(grahicGonUid)
                if (pointPloyline.size > 0) {
                    pointPloyline.remove(pointPloyline.last())
                    drawline(pointPloyline)
                }
            }
        }

    }
}
