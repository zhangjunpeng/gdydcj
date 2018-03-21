package com.mapuni.gdydcaiji.utils;

import android.content.Context;
import android.text.format.Formatter;

/**
 * Created by Summer on 2017/3/31.
 * 转换文件大小单位工具类
 */

public class FileSizeUtils {

    public static String byte2MB(Context context, long bytes) {
        String formatFileSize = Formatter.formatFileSize(context, bytes);
        return formatFileSize;
    }

}
