package com.mapuni.gdydcaiji.presenter


import android.app.Dialog
import com.esri.core.geometry.Point
import com.mapuni.gdydcaiji.bean.EventBZ
import com.mapuni.gdydcaiji.bean.EventDeleteInfo

import java.util.ArrayList

/**
 * Created by zjp on 2018/4/13.
 * mail:zhangjunpeng92@163.com
 */
public interface WaiYeInterface{


//
//    var currentCode:Int
//    var targetCode:Int
//    var pointPloygon:ArrayList<Point>
//    var pointPloyline:ArrayList<Point>
//
//    var point_bz_array:ArrayList<Boolean>
//    var line_bz_array:ArrayList<Boolean>
//    var surface_bz_array:ArrayList<Boolean>


    fun initMapview(mapFilePath:String)
    fun drawGon(pointList: ArrayList<Point>):Int
    fun drawline(pointPloyline: ArrayList<Point>):Int
    fun updateGraphic()
    fun onCompleteBZ(eventBZ: EventBZ)
    fun deleteInfo(eventDeleteInfo: EventDeleteInfo)
    fun addPointInMap(point: Point)
    fun backUpAfterMover()
    fun singleTapOnCollection(v: Float, v1: Float,tolerance:Int)
    fun baocunPopwindow()
    fun huituiPopWindow()
    fun quxiaoPopWindow()
    fun initDialogSize(bzDialog: Dialog)
}