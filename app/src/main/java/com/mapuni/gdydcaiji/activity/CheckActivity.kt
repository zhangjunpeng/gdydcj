package com.mapuni.gdydcaiji.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.Gravity
import com.mapuni.gdydcaiji.R
import com.mapuni.gdydcaiji.presenter.CheckPresenter
import com.mapuni.gdydcaiji.presenter.WaiYePresenter
import com.mapuni.gdydcaiji.utils.SPUtils
import kotlinx.android.synthetic.main.activity_check.*
import java.io.File

class CheckActivity : AppCompatActivity() {

    lateinit var checkPresenter: CheckPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check)
        checkPresenter= CheckPresenter(this,mapview_check,recyler_check)
        initMapView()
        initData()
        initListener()
    }

    private fun initListener() {
        btn_menu.setOnClickListener {
            if (drawer.isDrawerOpen(Gravity.RIGHT)){
                drawer.closeDrawers()
            }else{
                drawer.openDrawer(Gravity.RIGHT)
            }
        }
        recyler_check.layoutManager = LinearLayoutManager(this)

    }
    private fun initData() {
        checkPresenter.UPDateGraphicTask()
    }
    private fun initMapView() {
        checkPresenter.initMap()
    }


}
