package com.mapuni.gdydcaiji.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import com.esri.android.map.GraphicsLayer
import com.esri.android.map.LocationDisplayManager
import com.esri.android.map.ags.ArcGISLocalTiledLayer
import com.mapuni.gdydcaiji.R
import kotlinx.android.synthetic.main.activity_collection.*


class CollectionActivity : AppCompatActivity() {


    var mapfilePath = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection)

        mapfilePath = Environment.getExternalStorageDirectory().absolutePath+"/map/" + "/layers"
        initMapView()
        initListener()
    }

    private fun initListener() {
        dingwei_collect.setOnClickListener {
            val locationDisplayManager = mapview_collect.locationDisplayManager
            locationDisplayManager.autoPanMode=(LocationDisplayManager.AutoPanMode.LOCATION)
            locationDisplayManager.start()
        }

    }

    fun initMapView() {

        //test
        //        mapfilePath= "file://"+ Environment.getExternalStorageDirectory()
        //                + "/mapuni/map/bjtest/layers";

        val layer = ArcGISLocalTiledLayer(mapfilePath)
        mapview_collect.addLayer(layer)
        var graphicsLayer = GraphicsLayer()
        mapview_collect.addLayer(graphicsLayer, 1)

    }
}
