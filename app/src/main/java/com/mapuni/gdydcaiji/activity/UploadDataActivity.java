package com.mapuni.gdydcaiji.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.mapuni.gdydcaiji.GdydApplication;
import com.mapuni.gdydcaiji.R;
import com.mapuni.gdydcaiji.bean.TbLine;
import com.mapuni.gdydcaiji.bean.TbPoint;
import com.mapuni.gdydcaiji.bean.TbSurface;
import com.mapuni.gdydcaiji.bean.UploadBean;
import com.mapuni.gdydcaiji.database.greendao.DaoSession;

import com.mapuni.gdydcaiji.database.greendao.TbLineDao;
import com.mapuni.gdydcaiji.database.greendao.TbPointDao;
import com.mapuni.gdydcaiji.database.greendao.TbSurfaceDao;
import com.mapuni.gdydcaiji.net.RetrofitFactory;
import com.mapuni.gdydcaiji.net.RetrofitService;
import com.mapuni.gdydcaiji.utils.DateUtil;
import com.mapuni.gdydcaiji.utils.FileUtils;
import com.mapuni.gdydcaiji.utils.LogUtils;
import com.mapuni.gdydcaiji.utils.PathConstant;
import com.mapuni.gdydcaiji.utils.ThreadUtils;
import com.mapuni.gdydcaiji.utils.ToastUtils;


import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by yf on 2018/3/22.
 */

public class UploadDataActivity extends BaseActivity {
    private static final int REQUEST_MEDIA_PROJECTION = 1;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.et_start_time)
    TextView etStartTime;
    @BindView(R.id.et_stop_time)
    TextView etStopTime;
    private List<TbPoint> tbPointList = new ArrayList<>();
    private List<TbLine> tbLineList;
    private List<TbSurface> tbSurfaceList;


    private int updataNum = 0;
    private Date upStartTime, upStopTime;
    private AlertDialog dialog;
    private TbPointDao tbPointDao;
    private TbLineDao tbLineDao;
    private TbSurfaceDao tbSurfaceDao;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_uploaddata;
    }

    @Override
    protected void initView() {

        title.setText("数据上传");
    }

    @Override
    protected void initData() {

        DaoSession daoSession = GdydApplication.getInstances().getDaoSession();
        tbPointDao = daoSession.getTbPointDao();
        tbLineDao = daoSession.getTbLineDao();
        tbSurfaceDao = daoSession.getTbSurfaceDao();
    }

    /**
     * 上传数据
     */
    private void createFiles() {
        final String startTime = etStartTime.getText().toString().trim();
        final String stopTime = etStopTime.getText().toString().trim();
        if (TextUtils.isEmpty(startTime)) {
            ToastUtils.showShort("请选择采集开始时间");
            showDatePickerDialog(etStartTime);
            return;
        }
        if (TextUtils.isEmpty(stopTime)) {
            ToastUtils.showShort("请选择采集结束时间");
            showDatePickerDialog(etStopTime);
            return;
        }

        ThreadUtils.executeSubThread(new Runnable() {
            @Override
            public void run() {
                //生成文件
                Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
                //未上传
                tbPointList = tbPointDao.queryBuilder()
                        .where(TbPointDao.Properties.Flag.eq(0),  //未上传
                                TbPointDao.Properties.Opttime.between(DateUtil.getDateByFormat(startTime + " 00:00:00", DateUtil.YMDHMS), DateUtil.getDateByFormat(stopTime + " 24:00:00", DateUtil.YMDHMS)))
                        .orderAsc(TbPointDao.Properties.Opttime).list();
                String buildingJson = gson.toJson(tbPointList);
                FileUtils.writeFile(PathConstant.UPLOAD_DATA + "/tb_point.txt", buildingJson);
                if (tbPointList != null && tbPointList.size() > 0) {
                    updataNum += tbPointList.size();
                    upStartTime = tbPointList.get(0).getOpttime();
                    upStopTime = tbPointList.get(tbPointList.size() - 1).getOpttime();
                }

                tbLineList = tbLineDao.queryBuilder()
                        .where(TbLineDao.Properties.Flag.eq(0)
                                , TbLineDao.Properties.Opttime.between(DateUtil.getDateByFormat(startTime + " 00:00:00", DateUtil.YMDHMS), DateUtil.getDateByFormat(stopTime + " 24:00:00", DateUtil.YMDHMS)))
                        .orderAsc(TbLineDao.Properties.Opttime).list();
                String poiJson = gson.toJson(tbLineList);
                FileUtils.writeFile(PathConstant.UPLOAD_DATA + "/tb_line.txt", poiJson);

                if (tbLineList != null && tbLineList.size() > 0) {
                    updataNum += tbLineList.size();
                    upStartTime = upStartTime == null ? tbLineList.get(0).getOpttime() : new Date(Math.min(upStartTime.getTime(), tbLineList.get(0).getOpttime().getTime()));
                    upStopTime = upStopTime == null ? tbLineList.get(tbLineList.size() - 1).getOpttime() : new Date(Math.max(upStopTime.getTime(), tbLineList.get(tbLineList.size() - 1).getOpttime().getTime()));
                }

                tbSurfaceList = tbSurfaceDao.queryBuilder()
                        .where(TbSurfaceDao.Properties.Flag.eq(0)
                                , TbSurfaceDao.Properties.Opttime.between(DateUtil.getDateByFormat(startTime + " 00:00:00", DateUtil.YMDHMS), DateUtil.getDateByFormat(stopTime + " 24:00:00", DateUtil.YMDHMS)))
                        .orderAsc(TbSurfaceDao.Properties.Opttime).list();
                String socialJson = gson.toJson(tbSurfaceList);
                FileUtils.writeFile(PathConstant.UPLOAD_DATA + "/tb_surface.txt", socialJson);

                if (tbSurfaceList != null && tbSurfaceList.size() > 0) {
                    updataNum += tbSurfaceList.size();
                    upStartTime = upStartTime == null ? tbSurfaceList.get(0).getOpttime() : new Date(Math.min(upStartTime.getTime(), tbSurfaceList.get(0).getOpttime().getTime()));
                    upStopTime = upStopTime == null ? tbSurfaceList.get(tbSurfaceList.size() - 1).getOpttime() : new Date(Math.max(upStopTime.getTime(), tbSurfaceList.get(tbSurfaceList.size() - 1).getOpttime().getTime()));
                }


                ThreadUtils.executeMainThread(new Runnable() {
                    @Override
                    public void run() {
                        //联网上传
                        uploadData();
                    }
                });
            }
        });


    }

    private void uploadData() {
        Map<String, RequestBody> map = new HashMap<>();

        List<String> filePaths = new ArrayList<>();
        filePaths.add("/tb_point.txt");
        filePaths.add("/tb_line.txt");
        filePaths.add("/tb_surface.txt");

        File file;
        for (int i = 0; i < filePaths.size(); i++) {
            file = new File(PathConstant.UPLOAD_DATA + filePaths.get(i));
            if (file.exists() && file.length() > 10) {
                RequestBody build = RequestBody.create(MediaType.parse("text/plain;charset=utf-8"), file);
                map.put("file\"; filename=\"" + file.getName(), build);
            }
        }

        if (map.size() == 0) {
            ToastUtils.showShort("没有新数据");
            return;
        }

        final Call<UploadBean> call = RetrofitFactory.create(RetrofitService.class).upload(map);
        // Dialog
        final ProgressDialog pd = new ProgressDialog(mContext);
        pd.setMessage("正在上传...");
        // 点击对话框以外的地方无法取消
        pd.setCanceledOnTouchOutside(false);
        // 点击返回按钮无法取消
        pd.setCancelable(false);
        pd.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                call.cancel();
            }
        });
        pd.show();

        call.enqueue(new Callback<UploadBean>() {
            @Override
            public void onResponse(@NonNull Call<UploadBean> call, @NonNull Response<UploadBean> response) {
                LogUtils.d("onResponse" + response.body());

                if (pd.isShowing()) {
                    pd.dismiss();
                }
                UploadBean body = response.body();
                if (body != null && body.isResult()) {
                    showResponseDialog("上传成功\n" + "总数：" + updataNum + "\n" + DateUtil.getStringByFormat(upStartTime, DateUtil.YMDHMS) + "\n" + DateUtil.getStringByFormat(upStopTime, DateUtil.YMDHMS));
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            captureScreen();
                        }
                    },100);
                    
                    ThreadUtils.executeSubThread(new Runnable() {
                        @Override
                        public void run() {
                            updateData();
                        }
                    });

                } else {
                    showResponseDialog("上传失败");
                }


            }

            @Override
            public void onFailure(Call<UploadBean> call, Throwable t) {
                t.printStackTrace();
                if (!call.isCanceled()) {
                    // 非点击取消
                    //LogUtils.d(t.getMessage());
                    if (pd.isShowing()) {
                        pd.dismiss();
                    }
                    ToastUtils.showShort("网络错误");
                } else {
                    ToastUtils.showShort("上传取消");
                }

            }
        });

    }

    private void showResponseDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("提示")
                .setCancelable(false)
                .setMessage(message)
                .setPositiveButton("确定", null);
        dialog = builder.create();
        dialog.show();
    }

    /**
     * 获取整个窗口的截图
     *
     * @return
     */
    @SuppressLint("NewApi")
    private void captureScreen() {
        View cv = getWindow().getDecorView();

        cv.setDrawingCacheEnabled(true);
        cv.buildDrawingCache();
        Bitmap bitmap = cv.getDrawingCache();

        bitmap.setHasAlpha(false);
        bitmap.prepareToDraw();

        View dialogView = dialog.getWindow().getDecorView();
        int location[] = new int[2];
        cv.getLocationOnScreen(location);
        int location2[] = new int[2];
        dialogView.getLocationOnScreen(location2);
        dialogView.setDrawingCacheEnabled(true);
        dialogView.buildDrawingCache();
        Bitmap bitmap2 = Bitmap.createBitmap(dialogView.getDrawingCache(), 0, 0, dialogView.getWidth(), dialogView.getHeight());
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(bitmap2, location2[0] - location[0], location2[1] - location[1], new Paint());

        try {
            if (!new File(PathConstant.CAPTURE_SCREEN).exists()) {
                new File(PathConstant.CAPTURE_SCREEN).mkdirs();
            }
            File file = new File(PathConstant.CAPTURE_SCREEN + "/" + System.currentTimeMillis() + ".jpg");
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        cv.destroyDrawingCache();
        dialogView.destroyDrawingCache();

    }


    /**
     * 将flag标记为1
     */
    private void updateData() {
        if (tbPointList != null && tbPointList.size() > 0) {
            for (int i = 0; i < tbPointList.size(); i++) {
                tbPointList.get(i).setFlag(1);
            }
            tbPointDao.updateInTx(tbPointList);
        }
        if (tbLineList != null && tbLineList.size() > 0) {
            for (int i = 0; i < tbLineList.size(); i++) {
                tbLineList.get(i).setFlag(1);
            }
            tbLineDao.updateInTx(tbLineList);
        }
        if (tbSurfaceList != null && tbSurfaceList.size() > 0) {
            for (int i = 0; i < tbSurfaceList.size(); i++) {
                tbSurfaceList.get(i).setFlag(1);
            }
            tbSurfaceDao.updateInTx(tbSurfaceList);

        }

    }

    @Override
    protected void initListener() {

    }

    @OnClick({R.id.back, R.id.btn_save, R.id.et_start_time, R.id.et_stop_time})
    public void onClickView(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.btn_save:
                createFiles();
                break;
            case R.id.et_start_time:
                showDatePickerDialog(etStartTime);
                break;
            case R.id.et_stop_time:
                showDatePickerDialog(etStopTime);
                break;
        }

    }

    /**
     * 显示时间选择框
     */
    protected void showDatePickerDialog(final TextView tv) {
        // 回显时间，展示选择框
        Calendar calendar = new GregorianCalendar();
        String text = tv.getText().toString().trim();
        if (!TextUtils.isEmpty(text)) {
            Date date = DateUtil.getDateByFormat(text, DateUtil.YMD);
            calendar.setTime(date == null ? new Date() : date);
        }

        long _100year = 100L * 365 * 1000 * 60 * 60 * 24L;//100年
        TimePickerDialog mDialogYearMonthDay = new TimePickerDialog.Builder()
                .setCallBack(new OnDateSetListener() {
                    @Override
                    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
                        tv.setText(DateUtil.getStringByFormat(millseconds, DateUtil.YMD));
                    }
                })
                .setCancelStringId("取消")
                .setSureStringId("确定")
                .setTitleStringId("选择日期")
                .setYearText("年")
                .setMonthText("月")
                .setDayText("日")
                .setCyclic(false)
                .setMinMillseconds(System.currentTimeMillis() - _100year)//设置最小时间
//                .setMinMillseconds(System.currentTimeMillis())//设置最小时间为当前时间
//                .setMaxMillseconds(System.currentTimeMillis() + _100year)//设置最大时间+100年
                .setMaxMillseconds(System.currentTimeMillis())//设置最大时间是当前时间
                .setCurrentMillseconds(calendar.getTimeInMillis())//设置当前时间
                .setThemeColor(getResources().getColor(R.color.color_deep_sky_blue))
                .setType(Type.YEAR_MONTH_DAY)
                .setWheelItemTextNormalColor(getResources().getColor(R.color.timetimepicker_default_text_color))
                .setWheelItemTextSelectorColor(getResources().getColor(R.color.timepicker_toolbar_bg))
                .setWheelItemTextSize(16)
                .build();
        mDialogYearMonthDay.show(getSupportFragmentManager(), getClass().getSimpleName());

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (dialog != null && dialog.isShowing())
            dialog.dismiss();

    }

}
