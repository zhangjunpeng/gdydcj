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

}
