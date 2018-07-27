package com.mapuni.gdydcaiji.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by Summer on 2017/4/1.
 */

public class ProgressDialogFactory {

    public static ProgressDialog mProgressDialog;


    /**
     * 进度条会显示出来
     *
     * @param context
     * @param progressStyle 进度条的样式 ProgressDialog.STYLE_HORIZONTAL:横向 ProgressDialog.STYLE_SPINNER 圆圈
     * @param NumberFormat  "%1d KB/%2d KB" 定义显示进度的单位
     * @param max           最大值
     * @param title         标题
     * @param outsideCancel 点击外部是否可取消对话框
     * @param backCancel    点击返回按键是否可取消对话框
     * @param listener      是否显示按钮
     * @return ProgressDialog
     */
    public static ProgressDialog getProgressDialog(Context context, int progressStyle
            , String NumberFormat, int max, String title, boolean outsideCancel
            , boolean backCancel, DialogInterface.OnClickListener listener) {
        if (context == null) {
            return null;
        }
        mProgressDialog = new ProgressDialog(context);
        // 默认圆圈样式
        if (progressStyle == ProgressDialog.STYLE_HORIZONTAL || progressStyle == ProgressDialog.STYLE_SPINNER) {
            mProgressDialog.setProgressStyle(progressStyle);
        }
        if (NumberFormat != null) {
            mProgressDialog.setProgressNumberFormat(NumberFormat);
        }
        if (max != -1) {
            mProgressDialog.setMax(max);
        }
        if (title != null) {
            mProgressDialog.setTitle(title);
        }
        // 点击对话框以外的地方无法取消
        mProgressDialog.setCanceledOnTouchOutside(outsideCancel);
        // 点击返回按钮无法取消
        mProgressDialog.setCancelable(backCancel);
        // 显示确定/取消按钮
        if (listener != null) {
            mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", listener);
        }

        mProgressDialog.show();

        return mProgressDialog;
    }

    /**
     * 点击外部和返回按钮不可取消，默认圆圈样式，
     *
     * @param context
     * @param title
     * @return
     */
    public static ProgressDialog getProgressDialog(Context context, String title) {
        return getProgressDialog(context, ProgressDialog.STYLE_SPINNER, null, -1,
                title, true, true,null);
    }

    /**
     * 点击外部和返回按钮不可取消，默认圆圈样式，
     *
     * @param context
     * @param title
     * @return
     */
    public static ProgressDialog getProgressDialog(Context context, String title, DialogInterface.OnClickListener listener) {
        return getProgressDialog(context, ProgressDialog.STYLE_SPINNER, null, -1,
                title, false, false, listener);
    }

    /**
     * 设置进度
     *
     * @param progress
     */
    public static void setProgress(int progress) {
        mProgressDialog.setProgress(progress);
    }

    /**
     * 设置总进度
     * @param max
     */
    public static void setMax(int max) {
        mProgressDialog.setMax(max);
    }
    
    public static void dismiss() {
        mProgressDialog.dismiss();
    }

    /**
     * 设置标题
     *
     * @param title
     */
    public static void setTitle(String title) {
        mProgressDialog.setTitle(title);
    }

    /**
     * 是否正在显示
     *
     * @return
     */
    public static boolean isShowing() {
        return mProgressDialog.isShowing();
    }
}
