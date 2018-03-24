package com.mapuni.gdydcaiji.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISLocalTiledLayer;
import com.esri.android.runtime.ArcGISRuntime;
import com.mapuni.gdydcaiji.R;
import com.mapuni.gdydcaiji.utils.PathConstant;
import com.mapuni.gdydcaiji.utils.PermissionUtils;
import com.mapuni.gdydcaiji.utils.SPUtils;
import com.mapuni.gdydcaiji.utils.ThreadUtils;
import com.mapuni.gdydcaiji.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @BindView(R.id.mapview)
    MapView mapview;
    @BindView(R.id.btn_menu)
    Button btnMenu;
    private long mExitTime;
    private GraphicsLayer graphicsLayer;
    private String mapFileName;
    private String mapFilePath;
    private String[] allDirNames;
    private File[] allDirFiles;
    public int statue_code;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        PermissionUtils.requestAllPermission(this);

    }

    @Override
    protected void initData() {
        ArcGISRuntime.setClientId("aGuEaYO5Vrvl8hIs");//加入arcgis研发验证码
        mapFileName = SPUtils.getInstance().getString("checkedMap", "");
        mapFilePath = SPUtils.getInstance().getString("checkedMapPath", "");
        if (!TextUtils.isEmpty(mapFileName) && !TextUtils.isEmpty(mapFilePath)
                && new File(mapFilePath).exists()) {
            ArcGISLocalTiledLayer layer = new ArcGISLocalTiledLayer("file://" + mapFilePath + "/layers");
            System.out.println("file://" + mapFilePath + "/layers");
            mapview.addLayer(layer);
            graphicsLayer = new GraphicsLayer();
            mapview.addLayer(graphicsLayer, 1);
        } else {
            // 获取所有地图文件
            getAllFiles();
        }

    }

    @Override
    protected void initListener() {
        mapview.setOnPanListener(new MyPanListener());
        mapview.setOnPinchListener(new MyPanclListener());
        
    }

    private void getAllFiles() {
        ThreadUtils.executeSubThread(new Runnable() {
            @Override
            public void run() {
                String path = PathConstant.UNDO_ZIP_PATH;
                File fileDir = new File(path);
                if (!fileDir.exists()) {
                    fileDir.mkdirs();
                }
                // 获得文件夹下所有文件夹和文件的名字
                allDirNames = fileDir.list();
                // 获得文件夹下所有文件夹和文件
                allDirFiles = fileDir.listFiles();
                // 等待切换fragment动画完成
                //SystemClock.sleep(1000);
                ThreadUtils.executeMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (allDirFiles != null && allDirFiles.length != 0) {
                            mapview.setVisibility(View.VISIBLE);
                            // 默认显示数组中的第一个文件      按字母顺序排列
                            mapFilePath = allDirFiles[0].getAbsolutePath();
                            // 将选中的地图名字存入sp中
                            SPUtils.getInstance().put("checkedMap", allDirFiles[0].getName());
                            SPUtils.getInstance().put("checkedMapPath", allDirFiles[0].getAbsolutePath());
                            ArcGISLocalTiledLayer layer = new ArcGISLocalTiledLayer("file://" + mapFilePath + "/layers");
                            mapview.addLayer(layer);
                            graphicsLayer = new GraphicsLayer();
                            mapview.addLayer(graphicsLayer, 1);

                        } else {
                            mapview.setVisibility(View.GONE);
                            showNotHaveMapDialog();
                        }
                    }
                });

            }
        });

    }

    /**
     * 弹出没有地图Dialog
     */
    private void showNotHaveMapDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("无地图文件，请先下载地图");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openActivity(mContext, DownloadMapActivity.class);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    @OnClick({R.id.btn_periphery, R.id.btn_create_poi, R.id.btn_create_line, R.id.btn_create_gon, R.id.btn_upload_data, R.id.btn_marker_setting, R.id.btn_menu})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.btn_periphery:
                Intent intent1 = new Intent(mContext, BuildingDetail.class);
                startActivity(intent1);
                break;
            case R.id.btn_create_poi:
                break;
            case R.id.btn_create_line:
                break;
            case R.id.btn_create_gon:
                break;
            case R.id.btn_upload_data:
                Intent intent = new Intent(mContext, UploadDataActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_marker_setting:
                //备份
                copyDatabase();
                break;
            case R.id.btn_menu:

                View inflate = LayoutInflater.from(mContext).inflate(R.layout.ppw_menu, null, false);
                TextView tvChooseMap = inflate.findViewById(R.id.tv_choosemap);
                TextView tvCopyDb = inflate.findViewById(R.id.tv_copy_bd);

                tvChooseMap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openActivity(mContext, ChooseMapActivity.class);
                    }
                });

                PopupWindow ppw = new PopupWindow(inflate, view.getWidth(), 320, true);
                ppw.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));
                ppw.setOutsideTouchable(true);
                ppw.setTouchable(true);

                //获取点击View的坐标
                int[] location = new int[2];
                view.getLocationOnScreen(location);
                int y = location[1] - ppw.getHeight();
                ppw.showAtLocation(view, Gravity.NO_GRAVITY, location[0], y);

                break;
        }
    }

    private void copyDatabase() {
        ThreadUtils.executeSubThread(new Runnable() {
            @Override
            public void run() {
//                FileUtils.copyFile2(GdydApplication.getInstances().getDb().getPath(), PathConstant.DATABASE_PATH + "/sport.db");

                ThreadUtils.executeMainThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showShort("备份成功");
                    }
                });
            }

        });

    }

//    @Subscribe
//    public void onEventMainThread(EventBean eventBean) {
//        if ("download".equals(eventBean))
//            // 下载成功
//            getAllFiles();
//    }

    /**
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 点击两次退出
     *
     * @return
     */
    public void exit() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            ToastUtils.showShort("再点一次退出");
            mExitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
