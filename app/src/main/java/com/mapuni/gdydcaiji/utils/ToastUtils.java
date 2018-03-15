package com.mapuni.gdydcaiji.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * 静态Toast
 * ToastUtils
 */
public class ToastUtils {
    private static Toast toast = null; //Toast的对象！

    public static void showToast(Context mContext, String text) {
        if (toast == null) {
            toast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
        } else {
            toast.setText(text);
        }
        toast.show();
    }

    /**
     * 在主线程弹出静态Toast
     *
     * @param mContext
     * @param text
     */
    public static void showToastOnMainThread(final Context mContext, final String text) {
        ThreadUtils.executeMainThread(new Runnable() {
            @Override
            public void run() {
                showToast(mContext, text);
            }
        });

    }

}
