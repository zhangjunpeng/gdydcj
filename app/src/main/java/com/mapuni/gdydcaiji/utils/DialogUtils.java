package com.mapuni.gdydcaiji.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.mapuni.gdydcaiji.R;

import java.io.File;
import java.io.IOException;

/**
 * Created by oldJin on 2017/12/15.
 */

public class DialogUtils {

    public static void showChooseDialog(final Activity activity, final String pathString) {

        final Dialog dialog = new Dialog(activity, R.style.ChooseDialogStyle);
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_choose, null);
        // 拍照
        view.findViewById(R.id.tv_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(pathString);
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // 指定存储照片的路径
                Uri imageUri = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    // >=24
                    imageUri = FileProvider.getUriForFile(activity, "com.gdydcaiji.fileprovider", file);
                } else {
                    // <=23
                    imageUri = Uri.fromFile(file);
                }
                // LogUtils.d(imageUri);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                activity.startActivityForResult(intent, 5001);
                dialog.cancel();
            }
        });
        // 相册
        view.findViewById(R.id.tv_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent albumIntent = new Intent(Intent.ACTION_PICK);
                albumIntent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activity.startActivityForResult(albumIntent, 5002);
                dialog.cancel();
            }
        });
        // 取消
        view.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.setContentView(view);
        // 一定要在setContentView后设置
        WindowManager.LayoutParams attributes = dialog.getWindow().getAttributes();
        attributes.gravity = Gravity.BOTTOM;
        attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
        attributes.height = WindowManager.LayoutParams.WRAP_CONTENT;
        attributes.windowAnimations = R.style.DialogAnimation;
        dialog.show();
    }

    public static void showWarningDialog(Context context,
                                         String message,
                                         DialogInterface.OnClickListener positiveListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("提示");
        builder.setMessage(message);
        builder.setPositiveButton("确定", positiveListener);
        builder.setNegativeButton("取消", null);
        builder.show();
    }


}
