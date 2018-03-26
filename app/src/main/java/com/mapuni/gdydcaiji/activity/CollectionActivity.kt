package com.mapuni.gdydcaiji.activity

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
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
import com.esri.core.symbol.SimpleMarkerSymbol
import com.google.gson.annotations.Until
import com.mapuni.gdydcaiji.BZRecyAdapter
import com.mapuni.gdydcaiji.GdydApplication
import com.mapuni.gdydcaiji.GraphicListAdapter
import com.mapuni.gdydcaiji.R
import com.mapuni.gdydcaiji.bean.*
import com.mapuni.gdydcaiji.database.greendao.TBuildingInfoDao
import com.mapuni.gdydcaiji.database.greendao.TPoiInfoDao
import com.mapuni.gdydcaiji.database.greendao.TSocialInfoDao
import com.mapuni.gdydcaiji.database.greendao.TVillageInfoDao
import com.mapuni.gdydcaiji.service.CopyService
import com.mapuni.gdydcaiji.utils.*
import kotlinx.android.synthetic.main.activity_collection.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.File
import java.util.*


class CollectionActivity : AppCompatActivity(), View.OnClickListener, OnSingleTapListener, View.OnTouchListener, OnZoomListener, OnPanListener {
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
            localGraphicsLayer.removeAll()
            graphicName.removeAll()
            upDateGraphic()
        }
    }


//    var mapfilePath = ""

    //poi0，楼宇采集1，采集面2，村采集3,
    //除2都是点
    //4特殊，范围选择点
    private var currentCode = 0
    private var targetCode = -1

    private val pointPloygon = ArrayList<Point>()
    private lateinit var graphicsLayer: GraphicsLayer
    private lateinit var tempGraphicLayer: GraphicsLayer
    private var localGraphicsLayer: GraphicsLayer = GraphicsLayer()
    var graphicName: GraphicsLayer = GraphicsLayer()
    lateinit var alertDialog: AlertDialog

    //poi跳转请求码
    private val requestCode_poi: Int = 10001
    //面跳转请求码
    val requestCode_ploygon: Int = 10002
    var tolerance: Int = 20

    private var mExitTime: Long = 0
    private var mapFileName: String? = null
    private var mapFilePath: String? = null


    //数据库操作对象
    private lateinit var tBuildingInfoDao: TBuildingInfoDao
    private lateinit var tPoiInfoDao: TPoiInfoDao
    private lateinit var tSocialInfoDao: TSocialInfoDao
    private lateinit var tVillageInfoDao: TVillageInfoDao

    //显示的点线面
    private lateinit var buildingInfoList: List<TBuildingInfo>
    private lateinit var pointInfoList: List<TPoiInfo>
    private lateinit var socialInfoList: List<TSocialInfo>
    private lateinit var villageInfoList: List<TVillageInfo>


    lateinit var infoList: ArrayList<Map<String, Any>>
    lateinit var infoMap: HashMap<Int, Any>

    private lateinit var graphicListAdtaper: GraphicListAdapter

    private lateinit var showGrahipcListDialog: Dialog
    private lateinit var recyclerView: RecyclerView

    private lateinit var bzDialog: Dialog
    private lateinit var recyclerView_bz: RecyclerView

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

        seek_collect.progress = tolerance
        initMapView()
        initShowGraphicDialog()
        initBZDialog()

        initData()
        initListener()
        upDateView()


    }

    private fun initData() {
        tBuildingInfoDao = GdydApplication.instances.daoSession.tBuildingInfoDao
        tPoiInfoDao = GdydApplication.instances.daoSession.tPoiInfoDao
        tSocialInfoDao = GdydApplication.instances.daoSession.tSocialInfoDao
        tVillageInfoDao = GdydApplication.instances.daoSession.tVillageInfoDao

        infoMap = HashMap()

        for (i in 0 until 7){
            poi_bz_array.add(false)
        }
        poi_bz_array[0]=true
        for (i in 0 until 9){
            building_bz_array.add(false)

        }
        for (i in 0 until 6){
            social_bz_array.add(false)

        }
        for (i in 0 until 2){
            village_bz_array.add(false)

        }
        poi_bz_array[0]=true
        building_bz_array[0]=true
        social_bz_array[0]=true
        village_bz_array[0]=true


    }


    private fun initListener() {
        dingwei_collect.setOnClickListener {
            val locationDisplayManager = mapview_collect.locationDisplayManager
            locationDisplayManager.autoPanMode = (LocationDisplayManager.AutoPanMode.LOCATION)
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
        selectpoint_collect.setOnClickListener(this)

        iv_amplify.setOnClickListener(this)
        iv_reduce.setOnClickListener(this)
        btn_menu.setOnClickListener(this)




        seek_collect.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
//                val toast= Toast.makeText(this@CollectionActivity,progress.toString(),Toast.LENGTH_SHORT)
//                toast.cancel()
//                toast.show()

                ToastUtile.showText(this@CollectionActivity, progress.toString())
                tolerance = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

    }

    private fun initMapView() {


        mapFileName = SPUtils.getInstance().getString("checkedMap", "")
        mapFilePath = SPUtils.getInstance().getString("checkedMapPath", "")
        if (!TextUtils.isEmpty(mapFileName) && !TextUtils.isEmpty(mapFilePath)
                && File(mapFilePath).exists()) {
            val layer = ArcGISLocalTiledLayer("file://$mapFilePath/layers")
            mapview_collect.addLayer(layer)
            graphicsLayer = GraphicsLayer()
            tempGraphicLayer = GraphicsLayer()
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
    }

    override fun onClick(v: View?) {
        if (v is View) {
            when (v.id) {
                R.id.poi_collect -> {
                    targetCode = 0
                    beginPOICollect()
                }
                R.id.louyu_collect -> {
                    targetCode = 1
                    beginLouyuCollect()
                }
                R.id.newploygon_collect -> {
                    targetCode = 2

                    beginpolygonCollect()
                }
                R.id.jiaotong_collect -> {
                    targetCode = 3

                    beginCountryCollect()
                }
                R.id.tianjia_collect ->
                    addPointInmap()
                R.id.houtui_collect -> {
                    ploygonBack()
                }
                R.id.baocun_collect -> {
                    if (pointPloygon.size > 2) {
                        var bj = ""
                        for (point in pointPloygon) {
                            bj = bj + point.x.toString() + "," + point.y.toString() + ";"
                        }
                        bj.dropLast(1)
                        val intent1 = Intent(this@CollectionActivity, SocialDetail::class.java)
                        intent1.putExtra("bj", bj)
                        startActivityForResult(intent1, requestCode_ploygon)
                    } else {
                        ToastUtils.showShort("请在地图上选择点")
                    }

                }
                R.id.chanel_collect -> {
                    graphicsLayer.removeGraphic(grahicGonUid)
                    pointPloygon.clear()
                }
                R.id.selectpoint_collect -> {
                    targetCode = 4
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


    private fun addPointInmap() {
        val center = mapview_collect.center
        when (currentCode) {
            0 -> {
                val intent1 = Intent(this, PoiDetail::class.java)
                intent1.putExtra("lat", center.y)
                intent1.putExtra("lng", center.x)

                startActivityForResult(intent1, requestCode_poi)
            }
            1 -> {
                val intent1 = Intent(this, BuildingDetail::class.java)
                intent1.putExtra("lat", center.y)
                intent1.putExtra("lng", center.x)
                startActivityForResult(intent1, requestCode_poi)

            }
            2 -> {
                addPolygonInMap()
            }
            3 -> {
                val intent1 = Intent(this, VillageDetail::class.java)
                intent1.putExtra("lat", center.y)
                intent1.putExtra("lng", center.x)
                startActivityForResult(intent1, requestCode_poi)

            }
        }

    }


    private fun ploygonBack() {
        graphicsLayer.removeGraphic(grahicGonUid)
        if (pointPloygon.size > 0) {
            pointPloygon.remove(pointPloygon.last())
            drawGon(pointPloygon)
        }
    }

    private fun addPolygonInMap() {
        //开始小区采集
        val centerPoint = mapview_collect.center
        pointPloygon.add(centerPoint)
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
        currentCode = targetCode
        upDateView()

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
        if (currentCode == 2 && pointPloygon.size > 2) {
            showConfirmDiaolog()
        } else {
            currentCode = targetCode
            upDateView()
        }
    }

    private fun beiginSelectPoint() {
        //开始poi采集
        if (currentCode == 2 && pointPloygon.size > 2) {
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
                linear_tools_collect.visibility = View.INVISIBLE
                poi_collect.isSelected = true
                louyu_collect.isSelected = false
                newploygon_collect.isSelected = false
                jiaotong_collect.isSelected = false
                selectpoint_collect.isSelected = false

                seek_collect.visibility = View.INVISIBLE
            }
            1 -> {
                linear_tools_collect.visibility = View.INVISIBLE
                poi_collect.isSelected = false
                louyu_collect.isSelected = true
                newploygon_collect.isSelected = false
                jiaotong_collect.isSelected = false
                selectpoint_collect.isSelected = false

                seek_collect.visibility = View.INVISIBLE

            }
            2 -> {
                linear_tools_collect.visibility = View.VISIBLE
                poi_collect.isSelected = false
                louyu_collect.isSelected = false
                newploygon_collect.isSelected = true
                jiaotong_collect.isSelected = false
                selectpoint_collect.isSelected = false

                seek_collect.visibility = View.INVISIBLE

            }
            3 -> {
                linear_tools_collect.visibility = View.INVISIBLE
                poi_collect.isSelected = false
                louyu_collect.isSelected = false
                newploygon_collect.isSelected = false
                jiaotong_collect.isSelected = true
                selectpoint_collect.isSelected = false

                seek_collect.visibility = View.INVISIBLE

            }
            4 -> {
                linear_tools_collect.visibility = View.INVISIBLE
                poi_collect.isSelected = false
                louyu_collect.isSelected = false
                newploygon_collect.isSelected = false
                jiaotong_collect.isSelected = false
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
            graphicsLayer.removeGraphic(grahicGonUid)
            currentCode = targetCode
            when (targetCode) {
                0 -> {
                    beginPOICollect()
                }
                1 -> {
                    beginLouyuCollect()
                }
                3 -> {
                    beginCountryCollect()
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

                startActivityForResult(intent1, requestCode_poi)
            }
            1 -> {
                val intent1 = Intent(this, BuildingDetail::class.java)
                intent1.putExtra("lat", center.y)
                intent1.putExtra("lng", center.x)
                startActivityForResult(intent1, requestCode_poi)

            }
            2 -> {
                getPloygon(v, v1)

            }
            3 -> {
                val intent1 = Intent(this, VillageDetail::class.java)
                intent1.putExtra("lat", center.y)
                intent1.putExtra("lng", center.x)
                startActivityForResult(intent1, requestCode_poi)

            }
            4 -> {
                getGraphics(v, v1)
            }

        }

    }

    private fun getPloygon(v: Float, v1: Float) {

    }

    private var tempGraphicID: Int = 0
    private fun getGraphics(v: Float, v1: Float) {

        infoList = ArrayList()
        val symbol = SimpleMarkerSymbol(Color.parseColor("#50AA0000"), tolerance, SimpleMarkerSymbol.STYLE.CIRCLE)
        val point = mapview_collect.toMapPoint(v, v1)
        val graphic = Graphic(point, symbol)
        tempGraphicID = tempGraphicLayer.addGraphic(graphic)
        val uids_local = localGraphicsLayer.getGraphicIDs(v, v1, tolerance, 50)

        if (uids_local.isEmpty()) {
            Toast.makeText(this, "选择范围内没有点", Toast.LENGTH_SHORT).show()
            tempGraphicLayer.removeGraphic(tempGraphicID)
        } else {
            //            pointInfoList_select=tPoiInfoDao.queryBuilder().where(TPoiInfoDao.Properties)
            //            for (uid in uids_graphic) {


            //                val graphic = graphicsLayer.getGraphic(uid)
            //                if (graphic.geometry.type == Geometry.Type.POINT) {
            //                    val point = graphic.geometry as Point
            //                    val mutableList_poi = tPoiInfoDao.queryBuilder().where(TPoiInfoDao.Properties.Lng.eq(point.x), TPoiInfoDao.Properties.Lat.eq(point.y)).list()
            //                    if (mutableList_poi.size > 0) {
            //                        pointInfoList_select.add(mutableList_poi[0])
            //                        val map = HashMap<String, Any>()
            //                        map["obj"] = mutableList_poi[0]
            //                        infoList.add(map)
            //                    }
            //                    val mutableList_build = tBuildingInfoDao.queryBuilder().where(TBuildingInfoDao.Properties.Lng.eq(point.x), TBuildingInfoDao.Properties.Lat.eq(point.y)).list()
            //                    if (mutableList_build.size > 0) {
            //                        buildingInfoList_select.add(mutableList_build[0])
            //                        val map = HashMap<String, Any>()
            //                        map["obj"] = mutableList_build[0]
            //                        infoList.add(map)
            //                    }
            //                    val mutableList_village = tVillageInfoDao.queryBuilder().where(TVillageInfoDao.Properties.Lng.eq(point.x), TVillageInfoDao.Properties.Lat.eq(point.y)).list()
            //                    if (mutableList_village.size > 0) {
            //                        villageInfoList_select.add(mutableList_village[0])
            //                        val map = HashMap<String, Any>()
            //                        map["obj"] = mutableList_village[0]
            //                        infoList.add(map)
            //                    }
            //                }
            //            }
            for (uid in uids_local) {
                val map = HashMap<String, Any>()
                val info = infoMap[uid]
                when (info) {
                    is TVillageInfo -> {
                        map["obj"] = info
                    }
                    is TBuildingInfo -> {
                        map["obj"] = info
                    }
                    is TPoiInfo -> {
                        map["obj"] = info
                    }
                    is TSocialInfo -> {
                        map["obj"] = info
                    }
                }
                infoList.add(map)

            }

            graphicListAdtaper = GraphicListAdapter(instance, infoList, showGrahipcListDialog)
            recyclerView.adapter = graphicListAdtaper
            showGrahipcListDialog.show()
            tempGraphicLayer.removeGraphic(tempGraphicID)

        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                requestCode_poi -> {
                    if (data is Intent) {
                        val obj = data.getSerializableExtra("obj")
                        var point = Point()
                        var name = ""
                        when (obj) {
                            is TPoiInfo -> {
                                point = Point(obj.lng, obj.lat)
//                                name = obj.name
                                name=getPOIShowName(obj)
                            }
                            is TBuildingInfo -> {
                                point = Point(obj.lng, obj.lat)
//                                name = obj.name
                                name=getBuildingShowName(obj)
                            }
                            is TVillageInfo -> {
                                point = Point(obj.lng, obj.lat)
//                                name = obj.name
                                name=getVillageShowName(obj)
                            }
                        }
                        val uid = addPointInMap(point)
                        infoMap[uid] = obj
                        addNameInMap(point, name)
                    }

                }
                requestCode_ploygon -> {
                    pointPloygon.clear()
                    if (localGraphicsLayer != null) {
                        localGraphicsLayer.removeAll()
                    }
                    graphicName.removeAll()
                    upDateGraphic()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
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
        if (localGraphicsLayer != null) {
            localGraphicsLayer.removeAll()
        }
        graphicName.removeAll()
        upDateGraphic()
    }

    private fun upDateGraphic() {
        val currentPloygon = mapview_collect.extent
        val leftTopP = currentPloygon.getPoint(0)
        val rightTopP = currentPloygon.getPoint(1)
        val leftBottomP = currentPloygon.getPoint(2)

        buildingInfoList = tBuildingInfoDao.queryBuilder().where(TBuildingInfoDao.Properties.Lng.between(leftTopP.x, rightTopP.x)
                , TBuildingInfoDao.Properties.Lat.between(leftTopP.y, leftBottomP.y)).list()
        pointInfoList = tPoiInfoDao.queryBuilder().where(TPoiInfoDao.Properties.Lng.between(leftTopP.x, rightTopP.x)
                , TPoiInfoDao.Properties.Lat.between(leftTopP.y, leftBottomP.y)).list()
//        LogUtils.d(pointInfoList[0].lat.toString()+"---"+pointInfoList[0].lng.toString())
        socialInfoList = tSocialInfoDao.loadAll()
        villageInfoList = tVillageInfoDao.queryBuilder().where(TVillageInfoDao.Properties.Lng.between(leftTopP.x, rightTopP.x)
                , TVillageInfoDao.Properties.Lat.between(leftTopP.y, leftBottomP.y)).list()
        updateGraphicInLocal(currentPloygon)

    }

    private fun updateGraphicInLocal(currentPloygon: Polygon) {
        for (info: TBuildingInfo in buildingInfoList) {
            val point = Point(info.lng, info.lat)
            val simpleMarkerSymbol = SimpleMarkerSymbol(Color.RED, 10, SimpleMarkerSymbol.STYLE.CIRCLE)
            val graphic = Graphic(point, simpleMarkerSymbol)
            val uid = localGraphicsLayer.addGraphic(graphic)
            infoMap[uid] = info
            val name=getBuildingShowName(info)
            addNameInMap(point, name)
        }
        for (info: TVillageInfo in villageInfoList) {
            val point = Point(info.lng, info.lat)
            val simpleMarkerSymbol = SimpleMarkerSymbol(Color.RED, 10, SimpleMarkerSymbol.STYLE.CIRCLE)
            val graphic = Graphic(point, simpleMarkerSymbol)
            val uid = localGraphicsLayer.addGraphic(graphic)
            infoMap[uid] = info
            val name=getVillageShowName(info)
            addNameInMap(point, name)

        }
        for (info: TSocialInfo in socialInfoList) {
            val bj = info.bj
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

            if (GeometryEngine.contains(currentPloygon, polygon, SpatialReference.create(SpatialReference.WKID_WGS84))) {
                val uid = localGraphicsLayer.addGraphic(Graphic(polygon, fillSymbol))
                infoMap[uid] = info
                val tEnvelope = Envelope()
                polygon.queryEnvelope(tEnvelope)
                val tPoint = tEnvelope.center
                val name=getSocialShowName(info)
                addNameInMap(tPoint, name)
            }


        }
        for (info: TPoiInfo in pointInfoList) {
            val point = Point(info.lng, info.lat)
            val simpleMarkerSymbol = SimpleMarkerSymbol(Color.RED, 10, SimpleMarkerSymbol.STYLE.CIRCLE)
            val graphic = Graphic(point, simpleMarkerSymbol)
            val uid = localGraphicsLayer.addGraphic(graphic)
            infoMap[uid] = info
            val name=getPOIShowName(info)
            addNameInMap(point, name)

        }
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

        val ppw = PopupWindow(inflate, view!!.getWidth(), ScreenUtils.dp2px(this, 150F), true)
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
            bzRecyAdapter_poi=BZRecyAdapter(this,0,poi_bz_array)
            bzRecyAdapter_building=BZRecyAdapter(this,1,building_bz_array)
            bzRecyAdapter_social=BZRecyAdapter(this,2,social_bz_array)
            bzRecyAdapter_village=BZRecyAdapter(this,3,village_bz_array)

            recyclerView_bz.adapter=bzRecyAdapter_poi
        }

        //获取点击View的坐标
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        val y = location[1] - ppw.height
        ppw.showAtLocation(view, Gravity.NO_GRAVITY, location[0], y)
    }
    private lateinit var bzRecyAdapter_poi: BZRecyAdapter
    private lateinit var bzRecyAdapter_building: BZRecyAdapter
    private lateinit var bzRecyAdapter_social: BZRecyAdapter
    private lateinit var bzRecyAdapter_village: BZRecyAdapter


    private fun initBZDialog() {
        bzDialog = Dialog(instance)
        bzDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val contentView = LayoutInflater.from(instance).inflate(R.layout.dialog_bz, null, false)
        bzDialog.setContentView(contentView)
        bzDialog.setCanceledOnTouchOutside(false)
        recyclerView_bz = contentView.findViewById(R.id.recycler_dialog_bz)
        val spinner:Spinner=contentView.findViewById(R.id.spinener_bz_dialog)
        val mItems = listOf("POI标注","建筑物标注","小区等标注","村庄标注")
        val adapter = ArrayAdapter(this, R.layout.item_spinner_dialog, R.id.tv_type, mItems)
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown)
        spinner.adapter = adapter
        spinner.onItemSelectedListener=object :AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position){
                    0->{
                        recyclerView_bz.adapter=bzRecyAdapter_poi
                        bzRecyAdapter_poi.notifyDataSetChanged()
                    }
                    1->{
                        recyclerView_bz.adapter=bzRecyAdapter_building
                        bzRecyAdapter_building.notifyDataSetChanged()
                    }
                    2->{
                        recyclerView_bz.adapter=bzRecyAdapter_social
                        bzRecyAdapter_social.notifyDataSetChanged()
                    }
                    3->{
                        recyclerView_bz.adapter=bzRecyAdapter_village
                        bzRecyAdapter_village.notifyDataSetChanged()
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
                is TPoiInfo->{
                    tPoiInfoDao.delete(obj)
                }
                is TBuildingInfo->{
                    tBuildingInfoDao.delete(obj)
                }
                is TSocialInfo->{
                    tSocialInfoDao.delete(obj)
                }
                is TVillageInfo->{
                    tVillageInfoDao.delete(obj)
                }

            }
        }
        localGraphicsLayer.removeAll()
        graphicsLayer.removeAll()
        graphicName.removeAll()
        upDateGraphic()
        showGrahipcListDialog.dismiss()
    }

    private var poi_bz_array= ArrayList<Boolean>()
    private var building_bz_array= ArrayList<Boolean>()
    private var social_bz_array=ArrayList<Boolean>()
    private var village_bz_array=ArrayList<Boolean>()

    @Subscribe
    fun onCompleteBZ(eventBZ: EventBZ) {
        when(eventBZ.type){
            0->{
                poi_bz_array=eventBZ.booleanArrayList
            }
            1->{
                building_bz_array=eventBZ.booleanArrayList
            }
            2->{
                social_bz_array=eventBZ.booleanArrayList
            }
            3->{
                village_bz_array=eventBZ.booleanArrayList
            }else->{

            }
        }
        graphicName.removeAll()
        localGraphicsLayer.removeAll()
        graphicsLayer.removeAll()
        upDateGraphic()
        bzDialog.dismiss()
    }

    private fun getPOIShowName(poiInfo: TPoiInfo):String{
        var name=""
        val string_poi= listOf(poiInfo.name,poiInfo.forthad,poiInfo.fifad,poiInfo.sixad,poiInfo.sevenad,poiInfo.mjdj,poiInfo.sslymc)
        for (i in 0 until poi_bz_array.size){
            if (poi_bz_array[i]&&string_poi[i].isNotEmpty()){
                name += string_poi[i]+"/"
            }
        }
        return name.dropLast(1)

    }
    private fun getBuildingShowName(info: TBuildingInfo):String{
        var name=""
        val string_poi= listOf(info.name,info.forthad,info.fifad,info.sixad,info.lytype,info.lyxz,info.lyfl,info.lycs,info.lyzhs)
        for (i in 0 until building_bz_array.size){
            if (building_bz_array[i]&&string_poi[i].isNotEmpty()){
                name += string_poi[i]+"/"
            }
        }
        return name.dropLast(1)
    }
    private fun getSocialShowName(info: TSocialInfo):String{
        var name=""
        val string_poi= listOf(info.name,info.forthad,info.fifad,info.xqdz,info.wyxx,info.lxdh)
        for (i in 0 until social_bz_array.size){
            if (social_bz_array[i]&&string_poi[i].isNotEmpty()){
                name += string_poi[i]+"/"
            }
        }
        return name.dropLast(1)
    }
    private fun getVillageShowName(info: TVillageInfo):String{
        var name=""
        val string_poi= listOf(info.name,info.dz)
        for (i in 0 until social_bz_array.size){
            if (social_bz_array[i]&&string_poi[i].isNotEmpty()){
                name += string_poi[i]+"/"
            }
        }
        return name.dropLast(1)
    }

    @Subscribe
    fun onUPDataGraphic(eventUpdate: EvevtUpdate) {
        graphicName.removeAll()
        localGraphicsLayer.removeAll()
        graphicsLayer.removeAll()
        upDateGraphic()
    }

}
