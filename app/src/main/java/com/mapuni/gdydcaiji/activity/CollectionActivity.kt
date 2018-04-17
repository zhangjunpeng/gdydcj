package com.mapuni.gdydcaiji.activity

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.*
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.*
import com.esri.android.map.LocationDisplayManager
import com.esri.android.map.event.OnPanListener
import com.esri.android.map.event.OnSingleTapListener
import com.esri.android.map.event.OnZoomListener
import com.esri.android.runtime.ArcGISRuntime
import com.mapuni.gdydcaiji.GdydApplication
import com.mapuni.gdydcaiji.R
import com.mapuni.gdydcaiji.adapter.BZRecyAdapter
import com.mapuni.gdydcaiji.adapter.PpwAdapter
import com.mapuni.gdydcaiji.bean.*
import com.mapuni.gdydcaiji.presenter.WaiYePresenter
import com.mapuni.gdydcaiji.service.CopyService
import com.mapuni.gdydcaiji.utils.*
import com.xw.repo.BubbleSeekBar
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
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
    private var mapFilePath: String = ""

    private lateinit var bzDialog: Dialog
    private lateinit var recyclerView_bz: RecyclerView

    private var manager: SensorManager? = null
    private val listener = SensorListener()

    lateinit var instance: Activity

    private lateinit var waiYeInterface: WaiYePresenter

    private lateinit var bzRecyAdapter_point: BZRecyAdapter
    private lateinit var bzRecyAdapter_line: BZRecyAdapter
    private lateinit var bzRecyAdapter_surface: BZRecyAdapter


    //质检模式为1
    //外业采集模式为0
    private var MODE: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection)
        PermissionUtils.requestAllPermission(this)
        instance = this

        MODE = SPUtils.getInstance().getString("roleid").toInt()
        ArcGISRuntime.setClientId("uK0DxqYT0om1UXa9")//加入arcgis研发验证码
        EventBus.getDefault().register(this)

        val intent = Intent(this, CopyService::class.java)
        startService(intent)
        //获取系统服务（SENSOR_SERVICE)返回一个SensorManager 对象 
        manager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        waiYeInterface = WaiYePresenter(this, mapview_collect)

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
        } else {
            // 获取所有地图文件
            waiYeInterface.getAllFiles()
        }

        mapview_collect.onSingleTapListener = this
        mapview_collect.onZoomListener = this
        mapview_collect.onPanListener = this
        mapview_collect.isShowMagnifierOnLongPress = true
        mapview_collect.setAllowMagnifierToPanMap(true)

        if (MODE==2){
            mapview_collect.maxScale=5.0
        }

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
                R.id.tianjia_collect -> {
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

    override fun onSingleTap(v: Float, v1: Float) {
        singleTapOnCollection(v, v1)
    }


    private fun singleTapOnCollection(v: Float, v1: Float) {
        waiYeInterface.singleTapOnCollection(v, v1, tolerance)
    }


    override fun preAction(p0: Float, p1: Float, p2: Double) {

    }

    override fun postAction(p0: Float, p1: Float, p2: Double) {
        waiYeInterface.updateGraphic()
    }





    @Subscribe
    fun onEventMainThread(eventBean: EventBean) {
        if ("download".equals(eventBean.beanStr))
        // 下载成功
            waiYeInterface.getAllFiles()
    }

    private fun showMenuPop(view: View?) {
        val inflate = LayoutInflater.from(this).inflate(R.layout.ppw_menu2, null, false)
        val ryPpw = inflate.findViewById<RecyclerView>(R.id.ry_ppw)

        ryPpw.setLayoutManager(LinearLayoutManager(this))
        ryPpw.setHasFixedSize(true)
        ryPpw.setNestedScrollingEnabled(false)
        ryPpw.addItemDecoration(HorizontalDividerItemDecoration.Builder(this)
                .size(1)
                .colorResId(R.color.gray_line)
                .build())//添加分隔线

        val data = ArrayList<String>()
        data.add("上传数据")
        data.add("选择地图")
        data.add("备份")
        data.add("标注设置")
//        data.add("质检")

        var adapter = PpwAdapter(R.layout.item_ppw, data)
        ryPpw.setAdapter(adapter)

        var roleid = SPUtils.getInstance().getString("roleid")
        if ("2".equals(roleid)) {
            //质检
            data.add("下载数据")
            data.add("删除数据")
        } else if ("6".equals(roleid)) {
            //外业
            data.add("纠错")
        }

        val ppw = PopupWindow(inflate, ViewGroup.LayoutParams.WRAP_CONTENT, ScreenUtils.dp2px(this, (37 * data.size - 1).toFloat()), true)
        ppw.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.white)))
        ppw.isOutsideTouchable = true
        ppw.isTouchable = true

        adapter.setOnItemClickListener { adapter, view, position ->
            ppw.dismiss()
            when (position) {
                0 -> {
                    startActivity(Intent(this, UploadDataActivity::class.java))
                }
                1 -> {
                    startActivity(Intent(this, ChooseMapActivity::class.java))
                }
                2 -> {
                    ThreadUtils.executeSubThread {
                        FileUtils.copyFile(GdydApplication.getInstances().db.path, PathConstant.DATABASE_PATH + "/sport.db") { true }
                        ThreadUtils.executeMainThread {
                            ToastUtils.showShort("备份成功")
                        }
                    }
                }
                3 -> {
                    bzDialog.show()
                    bzRecyAdapter_point = BZRecyAdapter(this, 0, waiYeInterface.point_bz_array)
                    bzRecyAdapter_line = BZRecyAdapter(this, 1, waiYeInterface.line_bz_array)
                    bzRecyAdapter_surface = BZRecyAdapter(this, 2, waiYeInterface.surface_bz_array)

                    recyclerView_bz.adapter = bzRecyAdapter_point
                }
                4 -> {
                    if ("2".equals(roleid)) {
                        //质检
                        startActivity(Intent(this, QCListActivity::class.java))
                    } else if ("6".equals(roleid)) {
                        //外业
                        startActivity(Intent(this, CaijiQCResultActivity::class.java))
                    }
                }
                5 -> {
                    //质检删除数据
                    ThreadUtils.executeSubThread {

                        if (DbUtils().noUpdateNum == 0) {
                            DbUtils().deleteData()
                            ThreadUtils.executeMainThread {
                                ToastUtils.showShort("删除成功")
                                waiYeInterface.updateGraphic()
                            }
                        } else {
                            ThreadUtils.executeMainThread {
                                showWarnDialog()
                            }
                        }

                    }
                }

            }
        }

        //获取点击View的坐标
        val location = IntArray(2)
        view!!.getLocationOnScreen(location)
        val y = location[1] - ppw.height
        ppw.showAtLocation(view, Gravity.NO_GRAVITY, location[0], y)
    }

    private fun showWarnDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("提示")
                .setCancelable(false)
                .setMessage("当前有未上传的质检数据，是否仍然删除")
                .setPositiveButton("确定") { dialog, which ->
                    ThreadUtils.executeSubThread {
                        DbUtils().deleteData()
                        ThreadUtils.executeMainThread {
                            ToastUtils.showShort("删除成功")
                            waiYeInterface.updateGraphic()
                        }
                    }
                }
                .setNegativeButton("取消", null)
        builder.show()
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
