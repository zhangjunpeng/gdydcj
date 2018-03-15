package com.mapuni.gdydcaiji.utils;

import android.Manifest;
import android.app.Activity;
import android.support.annotation.NonNull;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.util.List;

/**
 * Created by oldJin on 2017/12/12.
 */

public class PermissionUtils {

    /**
     * 获取应用使用到的所有权限
     *
     * @param activity
     */
    public static void requestAllPermission(final Activity activity) {
        AndPermission
                .with(activity)
                .requestCode(200)
                .permission(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                )
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                        AndPermission
                                .rationaleDialog(activity, rationale)
                                .show();
                    }
                })
                .callback(new PermissionListener() {
                    @Override
                    public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                    }

                    @Override
                    public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
                        for (String deniedPermission : deniedPermissions) {
                            LogUtils.d(deniedPermission);
                        }
                        activity.finish();
                    }
                })
                .start();
    }
}
