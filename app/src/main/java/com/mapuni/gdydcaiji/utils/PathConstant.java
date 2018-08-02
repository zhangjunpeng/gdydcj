package com.mapuni.gdydcaiji.utils;

import android.os.Environment;

/**
 * Created by yf on 2018/3/19.
 */

public class PathConstant {

    public static final String SD_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();
    // 应用SD卡中的根路径
    public static final String ROOT_PATH = SD_ROOT + "/com/mapuni/gdydcaiji";
    // 解压地图路径
    public static final String UNDO_ZIP_PATH = ROOT_PATH + "/localmap";
    // 下载地图路径
    public static final String DOWNLOAD_MAP_PATH = ROOT_PATH + "/dowloadmap";
    // 下载数据路径
    public static final String DOWNLOAD_DATA_PATH = ROOT_PATH + "/dowloaddata";
    // 数据库路径
    public static final String DATABASE_PATH = ROOT_PATH + "/database";
    // ZIP路径
    public static final String ZIP_PATH = ROOT_PATH + "/data";
    // 临时图片路径
    public static final String IMAGE_PATH_CACHE = ROOT_PATH + "/chcheimg";
    // 压缩后图片路径
    public static final String IMAGE_PATH = ROOT_PATH + "/img";
    // 上传数据路径
    public static final String UPLOAD_DATA = ROOT_PATH + "/uploaddata";

    public static final String CAPTURE_SCREEN = ROOT_PATH + "/capturescreen";
    //导出质检数据路径
    public static final String QC_DATA_PATH = ROOT_PATH + "/qcdata";
<<<<<<< HEAD
    // 质检拷贝到本地的数据库路径
    public static final String QC_DATABASE_PATH = ROOT_PATH + "/qclocaldb";
=======
>>>>>>> 746d730cb42a41f26875c11a1735a2e36e6a7075

}
