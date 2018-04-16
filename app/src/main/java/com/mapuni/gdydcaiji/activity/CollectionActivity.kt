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
import com.mapuni.gdydcaiji.presenter.WaiYeInterface
import com.mapuni.gdydcaiji.presenter.WaiYePresenter
import com.mapuni.gdydcaiji.service.CopyService
import com.mapuni.gdydcaiji.utils.*
import com.xw.repo.BubbleSeekBar
import kotlinx.android.synthetic.main.activity_collection.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.File
import java.util.*


class CollectionActivity : AppCompatActivity(), View.OnClickListener, OnSingleTapListener, OnZoomListener, OnPanListener, BubbleSeekBar.OnProgressChangedListener {


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
            waiYeInterface.backUpAfterMover()
        }
    }


    lateinit var alertDialog: AlertDialog

    private var tolerance: Int = 20

    private var mExitTime: Long = 0
    private var mapFileName: String? = null
    private var mapFilePath: String=""

    private lateinit var bzDialog: Dialog
    private lateinit var recyclerView_bz: RecyclerView

    private var manager: SensorManager? = null
    private val listener = SensorListener()

    lateinit var instance: Activity

    private lateinit var waiYeInterface: WaiYePresenter

    private lateinit var bzRecyAdapter_point: BZRecyAdapter
    private lateinit var bzRecyAdapter_line: BZRecyAdapter
    private lateinit var bzRecyAdapter_surface: BZRecyAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection)
        instance = this
        PermissionUtils.requestAllPermission(this)

        ArcGISRuntime.setClientId("uK0DxqYT0om1UXa9")//加入arcgis研发验证码
        EventBus.getDefault().register(this)

        val intent = Intent(this, CopyService::class.java)
        startService(intent)
        //获取系统服务（SENSOR_SERVICE)返回一个SensorManager 对象 
        manager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        waiYeInterface=WaiYePresenter(this,mapview_collect)

        initMapView()
        initBZDialog()
        initToolsPopWindow()
        initListener()
        upDateView()
    }

    private fun initListener() {
        dingwei_collect.setOnClickListener {
            val locationDisplayManager = mapview_collect.locationDisplayManager
            locationDisplayManager.autoPanMode = (LocationDisplayManager.AutoPanMode.LOCATION)

            if (locationDisplayManager.isStarted) {
                locationDisplayManager.stop()
            } else {
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


        seek_collect.onProgressChangedListener = this


    }

    private fun initMapView() {


        mapFileName = SPUtils.getInstance().getString("checkedMap", "")
        mapFilePath = SPUtils.getInstance().getString("checkedMapPath", "")
        if (!TextUtils.isEmpty(mapFileName) && !TextUtils.isEmpty(mapFilePath)
                && File(mapFilePath).exists()) {
            waiYeInterface.initMapview(mapFilePath)
//            val layer = ArcGISLocalTiledLayer("file://$mapFilePath/layers")
//            mapview_collect.addLayer(layer)
//            mapview_collect.addLayer(graphicsLayer, 1)
//            mapview_collect.addLayer(tempGraphicLayer, 2)
//            mapview_collect.addLayer(localGraphicsLayer, 3)
//            mapview_collect.addLayer(graphicName, 4)
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
                    waiYeInterface.targetCode = 0
                    beginPOICollect()
                }
                R.id.line_collect -> {
                    waiYeInterface.targetCode = 1
                    beigonLineCollect()
                }
                R.id.newploygon_collect -> {
                    waiYeInterface.targetCode = 2
                    beginpolygonCollect()
                }
                R.id.tianjia_collect ->{
                    waiYeInterface.addPointInMap(mapview_collect.center)
                }
                R.id.selectpoint_collect -> {
                    waiYeInterface.targetCode = 3
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
        if (waiYeInterface.currentCode == 2 && waiYeInterface.pointPloygon.size > 2) {
            showConfirmDiaolog()
        } else {
            waiYeInterface.currentCode = waiYeInterface.targetCode
            upDateView()
        }
    }


//    private fun addPointInmap(point: Point = mapview_collect.center) {
//        when (currentCode) {
//            0 -> {
//                val intent1 = Intent(this, PoiDetail::class.java)
//                intent1.putExtra("lat", point.y)
//                intent1.putExtra("lng", point.x)
//                startActivity(intent1)
//            }
//            1 -> {
//                addPolyLineInMap(point)
//            }
//            2 -> {
//                addPolygonInMap(point)
//            }
//        }
//
//    }

//    private fun addPolyLineInMap(point: Point = mapview_collect.center) {
//        pointPloyline.add(point)
//        drawline(pointPloyline)
//    }

//    private fun drawline(pointPloyline: ArrayList<Point>) {
//        if (pointPloyline.size == 1) run { grahicGonUid = addPointInMap(pointPloyline[0]) } else if (pointPloyline.size > 1) {
//            val polyline = Polyline()
//            for (i in pointPloyline.indices) {
//                if (i == 0) {
//                    polyline.startPath(pointPloyline[0])
//                } else {
//                    polyline.lineTo(pointPloyline[i])
//                }
//            }
//            graphicsLayer.removeGraphic(grahicGonUid)
//            val simpleLineSymbol = SimpleLineSymbol(Color.RED, 2f)
//
//            grahicGonUid = graphicsLayer.addGraphic(Graphic(polyline, simpleLineSymbol))
//        }
//    }


//    private fun ploygonBack() {
//        graphicsLayer.removeGraphic(grahicGonUid)
//        if (pointPloygon.size > 0) {
//            pointPloygon.remove(pointPloygon.last())
//            drawGon(pointPloygon)
//        }
//    }

//    private fun addPolygonInMap(point: Point = mapview_collect.center) {
//        //开始小区采集
//        pointPloygon.add(point)
//        drawGon(pointPloygon)
//    }


    private fun beginpolygonCollect() {
        if (waiYeInterface.currentCode == 1 && waiYeInterface.pointPloyline.size > 1) {
            showConfirmDiaolog()
        } else {
            waiYeInterface.currentCode = waiYeInterface.targetCode
            upDateView()
        }
    }



    private fun beginPOICollect() {
        //开始范围选择
        if (waiYeInterface.currentCode == 1 && waiYeInterface.pointPloyline.size > 1) {
            showConfirmDiaolog()

        } else if (waiYeInterface.currentCode == 2 && waiYeInterface.pointPloygon.size > 2) {
            showConfirmDiaolog()
        } else {
            waiYeInterface.currentCode = waiYeInterface.targetCode
            upDateView()
        }
    }

    private fun beiginSelectPoint() {
        if (waiYeInterface.currentCode == 1 && waiYeInterface.pointPloyline.size > 1) {
            showConfirmDiaolog()
        } else if (waiYeInterface.currentCode == 2 && waiYeInterface.pointPloygon.size > 2) {
            showConfirmDiaolog()
        } else {
            waiYeInterface.currentCode = waiYeInterface.targetCode
            upDateView()
        }
    }

    private fun upDateView() {
        tianjia_collect.visibility = View.VISIBLE
        when (waiYeInterface.currentCode) {
            0 -> {

                if (popupWindow.isShowing) {
                    popupWindow.dismiss()
                }

                poi_collect.isSelected = true
                line_collect.isSelected = false
                newploygon_collect.isSelected = false
                selectpoint_collect.isSelected = false

                seek_collect.visibility = View.INVISIBLE
            }
            1 -> {

                popupWindow.showAtLocation(findViewById(R.id.main), Gravity.BOTTOM, 0, 0)

                poi_collect.isSelected = false
                line_collect.isSelected = true
                newploygon_collect.isSelected = false
                selectpoint_collect.isSelected = false

                seek_collect.visibility = View.INVISIBLE

            }
            2 -> {

                popupWindow.showAtLocation(findViewById(R.id.main), Gravity.BOTTOM, 0, 0)

                poi_collect.isSelected = false
                line_collect.isSelected = false
                newploygon_collect.isSelected = true
                selectpoint_collect.isSelected = false

                seek_collect.visibility = View.INVISIBLE

            }
            3 -> {
                if (popupWindow.isShowing) {
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
            waiYeInterface.updateGraphic()
            when (waiYeInterface.targetCode) {
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


//    private var grahicGonUid: Int = 0
//    private fun drawGon(pointList: ArrayList<Point>) {
//        if (pointList.size == 0) {
//            return
//        }
//        grahicGonUid = if (pointList.size == 1) {
//            addPointInMap(pointList[0])
//        } else {
//            val fillSymbol = SimpleFillSymbol(Color.argb(100, 255, 0, 0))
//            val polygon = Polygon()
//            polygon.startPath(pointList[0])
//            for (i in 1 until pointList.size) {
//                polygon.lineTo(pointList[i])
//            }
//            graphicsLayer.removeGraphic(grahicGonUid)
//
//            graphicsLayer.addGraphic(Graphic(polygon, fillSymbol))
//        }
//    }
//
//    private fun addPointInMap(point: Point): Int {
//        val simpleMarkerSymbol = SimpleMarkerSymbol(Color.RED, 10, SimpleMarkerSymbol.STYLE.CIRCLE)
//        val graphic = Graphic(point, simpleMarkerSymbol)
//        return graphicsLayer.addGraphic(graphic)
//    }

    override fun onSingleTap(v: Float, v1: Float) {
        singleTapOnCollection(v, v1)
    }


    private fun singleTapOnCollection(v: Float, v1: Float) {
        waiYeInterface.singleTapOnCollection(v,v1,tolerance)
    }




//    private fun addNameInMap(point: Point, name: String) {
//
//        val tv = TextView(this)
//        if (name.isEmpty()) {
//            return
//        }
//        tv.text = name
//
//        tv.textSize = 8f
//        tv.setTextColor(Color.WHITE)
//        tv.isDrawingCacheEnabled = true
//        tv.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
//        tv.layout(0, 0, tv.measuredWidth, tv.measuredHeight)
//        val bitmap = Bitmap.createBitmap(tv.drawingCache)
//        //千万别忘最后一步
//        tv.destroyDrawingCache()
//        val picturSymbol = PictureMarkerSymbol(BitmapDrawable(resources, bitmap))
//        picturSymbol.offsetY = 10f
//        val nameGraphic = Graphic(point, picturSymbol)
//        graphicName.addGraphic(nameGraphic)
//    }



    override fun preAction(p0: Float, p1: Float, p2: Double) {

    }

    override fun postAction(p0: Float, p1: Float, p2: Double) {
        waiYeInterface.updateGraphic()
    }




//    private fun updateGraphicInLocal(currentPloygon: Polygon) {
//        for (info: TbPoint in pointList) {
//            val point = Point(info.lng, info.lat)
//            val simpleMarkerSymbol = SimpleMarkerSymbol(Color.RED, 10, SimpleMarkerSymbol.STYLE.CIRCLE)
//            val graphic = Graphic(point, simpleMarkerSymbol)
//            val uid = localGraphicsLayer.addGraphic(graphic)
//            infoMap[uid] = info
//            val name = getPointName(info)
//            addNameInMap(point, name)
//        }
//
//        for (info: TbLine in lineInfoList) {
//            val bj = info.polyarrays
//            val polyline = Polyline()
//            val points_array = bj.split(";")
//            for (i in 0 until points_array.size) {
//                val item = points_array[i]
//                if (item.isEmpty()) {
//                    continue
//                }
//                val points = item.split(",")
//                val point = Point(points[0].toDouble(), points[1].toDouble())
//                if (i == 0) {
//                    polyline.startPath(point)
//                } else {
//                    polyline.lineTo(point)
//                }
//
//            }
//            val simpleLineSymbol = SimpleLineSymbol(Color.RED, 2f, SimpleLineSymbol.STYLE.SOLID)
//            val graphic = Graphic(polyline, simpleLineSymbol)
//            val uid = localGraphicsLayer.addGraphic(graphic)
//            infoMap[uid] = info
//            val name = getLineName(info)
//
//            val tEnvelope = Envelope()
//            polyline.queryEnvelope(tEnvelope)
//            val tPoint = tEnvelope.center
//
//            addNameInMap(tPoint, name)
//
//        }
//
//        for (info: TbSurface in surfaceList) {
//            val bj = info.polyarrays
//            val tempPointList = ArrayList<Point>()
//            val points_array = bj.split(";")
//            for (i in 0 until points_array.size) {
//                val item = points_array[i]
//                if (item.isEmpty()) {
//                    continue
//                }
//                val points = item.split(",")
//                val point = Point(points[0].toDouble(), points[1].toDouble())
//                tempPointList.add(point)
//            }
//
//            val fillSymbol = SimpleFillSymbol(Color.argb(100, 255, 0, 0))
//            val polygon = Polygon()
//            polygon.startPath(tempPointList[0])
//            for (i in 1 until tempPointList.size) {
//                polygon.lineTo(tempPointList[i])
//            }
//
//            val uid = localGraphicsLayer.addGraphic(Graphic(polygon, fillSymbol))
//            infoMap[uid] = info
//            val tEnvelope = Envelope()
//            polygon.queryEnvelope(tEnvelope)
//            val tPoint = tEnvelope.center
//            val name = getSurfaceName(info)
//            addNameInMap(tPoint, name)
//
//
//        }
//
//        mapview_collect.invalidate()
//        mIsLoading = false
//    }


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
//                    val layer = ArcGISLocalTiledLayer("file://$mapFilePath/layers")
//                    mapview_collect.addLayer(layer)
//                    graphicsLayer = GraphicsLayer()
//                    mapview_collect.addLayer(graphicsLayer, 1)
//                    mapview_collect.addLayer(tempGraphicLayer, 2)
//                    mapview_collect.addLayer(localGraphicsLayer, 3)
//                    mapview_collect.addLayer(graphicName, 4)
                    waiYeInterface.initMapview(mapFilePath)
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
        val tv_zj = inflate.findViewById<TextView>(R.id.tv_zj)
        val tv_bz: TextView = inflate.findViewById(R.id.tv_bz)

        val ppw = PopupWindow(inflate, ViewGroup.LayoutParams.WRAP_CONTENT, ScreenUtils.dp2px(this, 180F), true)
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

        tv_zj.setOnClickListener {
            var roleid = SPUtils.getInstance().getString("roleid")
            if ("2".equals(roleid)) {
                //质检
                startActivity(Intent(this, QCListActivity::class.java))
            } else if ("6".equals(roleid)) {
                //外业
                startActivity(Intent(this, CaijiQCResultActivity::class.java))
            }
            
            ppw.dismiss()
        }
        tv_bz.setOnClickListener {
            ppw.dismiss()

            bzDialog.show()
            bzRecyAdapter_point = BZRecyAdapter(this, 0, waiYeInterface.point_bz_array)
            bzRecyAdapter_line = BZRecyAdapter(this, 1,waiYeInterface.line_bz_array)
            bzRecyAdapter_surface = BZRecyAdapter(this, 2, waiYeInterface.surface_bz_array)

            recyclerView_bz.adapter = bzRecyAdapter_point
        }

        //获取点击View的坐标
        val location = IntArray(2)
        view!!.getLocationOnScreen(location)
        val y = location[1] - ppw.height
        ppw.showAtLocation(view, Gravity.NO_GRAVITY, location[0], y)
    }




    private fun initBZDialog() {
        bzDialog = Dialog(instance)
        bzDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val contentView = LayoutInflater.from(instance).inflate(R.layout.dialog_bz, null, false)
        bzDialog.setContentView(contentView)
        bzDialog.setCanceledOnTouchOutside(false)
        recyclerView_bz = contentView.findViewById(R.id.recycler_dialog_bz)
        val spinner: Spinner = contentView.findViewById(R.id.spinener_bz_dialog)
        val mItems = listOf("POI标注", "线标注", "面标注")
        val adapter = ArrayAdapter(this, R.layout.item_spinner_dialog, R.id.tv_type, mItems)
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> {
                        recyclerView_bz.adapter = bzRecyAdapter_point
                        bzRecyAdapter_point.notifyDataSetChanged()
                    }
                    1 -> {
                        recyclerView_bz.adapter = bzRecyAdapter_line
                        bzRecyAdapter_line.notifyDataSetChanged()
                    }
                    2 -> {
                        recyclerView_bz.adapter = bzRecyAdapter_surface
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


//    private fun initShowGraphicDialog() {
//        showGrahipcListDialog = Dialog(instance)
//        showGrahipcListDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        val contentView = LayoutInflater.from(instance).inflate(R.layout.dialog_showgraphicinfo, null, false)
//        showGrahipcListDialog.setContentView(contentView)
//        showGrahipcListDialog.setCanceledOnTouchOutside(false)
//        recyclerView = contentView.findViewById(R.id.recycler_dialog)
//
//        recyclerView.layoutManager = LinearLayoutManager(this)
//
//    }

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

        waiYeInterface.initDialogSize(bzDialog)
    }




    @Subscribe
    fun onDeleteInfo(eventDeleteInfo: EventDeleteInfo) {
        waiYeInterface.deleteInfo(eventDeleteInfo)
//        showGrahipcListDialog.dismiss()
    }




    @Subscribe
    fun onChangeMap(event: EventChangeMap) {
        finish()
    }

    @Subscribe
    fun onCompleteBZ(eventBZ: EventBZ) {
        waiYeInterface.onCompleteBZ(eventBZ)
        bzDialog.dismiss()
    }

    @Subscribe
    fun onUPDataGraphic(eventUpdate: EvevtUpdate) {
        waiYeInterface.updateGraphic()
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

    private fun initToolsPopWindow() {

        val contentView = LayoutInflater.from(this).inflate(R.layout.popwindow_tools, null, false)
        popupWindow = PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        popupWindow.animationStyle = R.style.mypopwindow_anim_style// 设置动画

        popupWindow.setBackgroundDrawable(BitmapDrawable())
        //点击外部消失
//        popupWindow.isOutsideTouchable = true
        //设置可以点击
        popupWindow.isTouchable = true
        //进入退出的动画
        val baocun = popupWindow.contentView.findViewById<TextView>(R.id.baocun_collect)
        val quxiao = popupWindow.contentView.findViewById<TextView>(R.id.chanel_collect)
        val houtui = popupWindow.contentView.findViewById<TextView>(R.id.houtui_collect)
        baocun.setOnClickListener {
            waiYeInterface.baocunPopwindow()
        }
        quxiao.setOnClickListener {
           waiYeInterface.quxiaoPopWindow()
        }
        houtui.setOnClickListener {
           waiYeInterface.huituiPopWindow()
        }

    }
}
