package com.mapuni.gdydcaiji.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISLocalTiledLayer;
import com.esri.android.map.event.OnLongPressListener;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.runtime.ArcGISRuntime;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.mapuni.gdydcaiji.R;
import com.mapuni.gdydcaiji.utils.LogUtils;
import com.mapuni.gdydcaiji.utils.PathConstant;
import com.mapuni.gdydcaiji.utils.PermissionUtils;
import com.mapuni.gdydcaiji.utils.SPUtils;
import com.mapuni.gdydcaiji.utils.ScreenUtils;
import com.mapuni.gdydcaiji.utils.ThreadUtils;
import com.mapuni.gdydcaiji.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements OnLongPressListener, OnSingleTapListener {

    @BindView(R.id.mapview)
    MapView mapview;
    private long mExitTime;
    private GraphicsLayer graphicsLayer;
    private String mapFileName;
    private String mapFilePath;
    private String[] allDirNames;
    private File[] allDirFiles;
    public int statue_code;
    public static final int COLLECT_POINT_STATE = 100;
    public static final int COLLECT_LINE_STATE = 101;
    public static final int COLLECT_GON_STATE = 102;
    //线
    List<Point> linePoint;
    public int graphicLineUid;

    //面
    List<Point> gonPoint;
    public int grahicGonUid;

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
        mapview.setOnLongPressListener(this);
        mapview.setOnSingleTapListener(this);
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

    @OnClick({R.id.btn_periphery, R.id.btn_create_poi, R.id.btn_create_line, R.id.btn_create_gon, R.id.btn_upload_data, R.id.btn_marker_setting})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.btn_periphery:
                break;
            case R.id.btn_create_poi:
                break;
            case R.id.btn_create_line:
                break;
            case R.id.btn_create_gon:
                break;
            case R.id.btn_upload_data:
                break;
            case R.id.btn_marker_setting:
                break;
        }
    }

    @Override
    public boolean onLongPress(float v, float v1) {
        return false;
    }

    public void onSingleTap(float v, float v1) {
//        if (callout != null && callout.isShowing()) {
//            callout.hide();
//        }
        if (statue_code == COLLECT_POINT_STATE) {
            //选点，线，面状态
            int[] uids = getGraphicsLayer().getGraphicIDs(v, v1, 10, 1);
            if (uids.length > 0) {
                Graphic graphic = getGraphicsLayer().getGraphic(uids[0]);
                Geometry geometry = graphic.getGeometry();

//                if (Geometry.isPoint(geometry.getType().value())) {
//                    //如果是点
//                    final PointInfo pointInfo = pointInfoMap.get(uids[0]);
//                    Point point = (Point) geometry;
//                    LinearLayout linearLayout = new LinearLayout(this);
//                    if (pointInfo == null) {
//                        return;
//                    }
//                    if (TextUtils.isEmpty(pointInfo.getName())) {
//                        //如果没有设置详情
//                        TextView textView = new TextView(this);
//                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
//                        String date_str = simpleDateFormat.format(new Date(Long.parseLong(pointInfo.getUpdata())));
//                        textView.setText(date_str);
//                        //textView.setBackgroundColor(Color.BLACK);
//                        //textView.setTextColor(Color.WHITE);
//                        textView.setGravity(Gravity.CENTER);
//                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                        linearLayout.addView(textView, params);
//                        linearLayout.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                Intent intent = new Intent(MainActivity.this, SaveInfoActivity.class);
//                                Bundle bundle = new Bundle();
//                                bundle.putSerializable("pointinfo", pointInfo);
//                                bundle.putInt("type", model);
//                                intent.putExtra("object", bundle);
//                                startActivityForResult(intent, SAVEINFO_CODE);
//                                mapview_collection.getCallout().hide();
//
//                            }
//                        });
//
//                    } else if (pointInfo.getType().equals("起始符")
//                            || pointInfo.getType().equals("终止符")) {
//                        TextView textView = new TextView(this);
//                        textView.setText(pointInfo.getName() + pointInfo.getType());
//                        textView.setBackgroundColor(Color.BLACK);
//                        textView.setTextColor(Color.WHITE);
//                        textView.setGravity(Gravity.CENTER);
//                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                        linearLayout.addView(textView, params);
//                    } else {
//                        //如果设置详情
//                        TextView textView = new TextView(this);
//                        textView.setText(pointInfo.getName());
//                        // textView.setBackgroundColor(Color.BLACK);
//                        // textView.setTextColor(Color.WHITE);
//                        textView.setGravity(Gravity.CENTER);
//                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                        linearLayout.addView(textView, params);
//                        linearLayout.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                Intent intent = new Intent(MainActivity.this, SaveInfoActivity.class);
//                                Bundle bundle = new Bundle();
//                                bundle.putSerializable("pointinfo", pointInfo);
//                                bundle.putInt("type", model);
//                                intent.putExtra("object", bundle);
//                                startActivityForResult(intent, SAVEINFO_CODE);
//                                mapview_collection.getCallout().hide();
//                            }
//                        });
//
//                    }
//
//                    callout = mapview_collection.getCallout();
//                    callout.setContent(linearLayout);
//                    callout.setOffset(0, 75);
//                    callout.show(point);
//
//                } else if (geometry.getType().value() == Geometry.Type.POLYLINE.value()) {
//                    LineInfo lineInfo = lineInfoMap.get(uids[0]);
//                    Intent intent = new Intent(MainActivity.this, SaveLineInfoActivity.class);
//                    intent.putExtra("lineinfo", lineInfo);
//                    startActivityForResult(intent, SAVEINFO_CODE);
//                } else if (geometry.getType().value() == Geometry.Type.POLYGON.value()) {
//                    GonInfo gonInfo = gonInfoMap.get(uids[0]);
////                    Intent intent = new Intent(CollectionActivity.this, SaveGonInfoActivity.class);
////                    intent.putExtra("goninfo", gonInfo);
////                    startActivityForResult(intent, SAVEGON_CODE);
//                    Intent intent = new Intent(this, TestSaveGonActivity.class);
//                    intent.putExtra("goninfo", gonInfo);
//                    startActivityForResult(intent, SAVEGON_CODE);
//                }
            }
        } else if (statue_code == COLLECT_LINE_STATE) {
            //画线状态
            Point point = new Point(v, v1);

            linePoint.add(mapview.toMapPoint(point));
            try {
                getGraphicsLayer().removeGraphic(graphicLineUid);
            } catch (Exception e) {
                LogUtils.i(e.toString());
            }
            drawLine(linePoint);

        } else if (statue_code == COLLECT_GON_STATE) {
            //画面状态

            Point point = new Point(v, v1);
            gonPoint.add(mapview.toMapPoint(point));
            try {
                getGraphicsLayer().removeGraphic(grahicGonUid);
            } catch (Exception e) {
                LogUtils.i(e.toString());
            }
            drawGon(gonPoint);
        }
    }

    public void drawLine(List<Point> linePoint) {

        if (linePoint.size() == 1) {
            graphicLineUid = addPointInMap(linePoint.get(0));
        } else if (linePoint.size() > 1) {
            Polyline polyline = new Polyline();
            polyline.startPath(linePoint.get(0));
            for (int i = 1; i < linePoint.size(); i++) {
                polyline.lineTo(linePoint.get(i));
            }
            getGraphicsLayer().removeGraphic(graphicLineUid);
            SimpleLineSymbol simpleLineSymbol = new SimpleLineSymbol(Color.RED, 2);
            graphicLineUid = getGraphicsLayer().addGraphic(new Graphic(polyline, simpleLineSymbol));
        }


    }

    public void drawGon(List<Point> pointList) {
        if (gonPoint.size() == 1) {
            grahicGonUid = addPointInMap(gonPoint.get(0));
        } else if (gonPoint.size() >= 2) {
            SimpleFillSymbol fillSymbol = new SimpleFillSymbol(Color.argb(100, 255, 0, 0));
            Polygon polygon = new Polygon();
            polygon.startPath(pointList.get(0));
            for (int i = 1; i < pointList.size(); i++) {
                polygon.lineTo(pointList.get(i));
            }
            getGraphicsLayer().removeGraphic(grahicGonUid);

            grahicGonUid = getGraphicsLayer().addGraphic(new Graphic(polygon, fillSymbol));
        }
    }


    public int addPointInMap(Point point) {
        BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(R.mipmap.ic_launcher);
        BitmapDrawable drawable_new = (BitmapDrawable) setNewScaleDrable(drawable, ScreenUtils.dp2px(mContext, 75), ScreenUtils.dp2px(mContext, 75));

        PictureMarkerSymbol pictureMarkerSymbol = new PictureMarkerSymbol(this, drawable_new);
        Graphic graphic = new Graphic(point, pictureMarkerSymbol);
        return getGraphicsLayer().addGraphic(graphic);
    }

    public GraphicsLayer getGraphicsLayer() {
        return (GraphicsLayer) mapview.getLayer(1);
    }

    @Subscribe
    public void onEventMainThread(DownloadMapActivity.SuccessEvent successEvent) {
        // 下载成功
        getAllFiles();
    }

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
