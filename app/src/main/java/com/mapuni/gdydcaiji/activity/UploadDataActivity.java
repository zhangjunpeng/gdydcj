package com.mapuni.gdydcaiji.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
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
import com.mapuni.gdydcaiji.utils.SPUtils;
import com.mapuni.gdydcaiji.utils.ThreadUtils;
import com.mapuni.gdydcaiji.utils.ToastUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
import butterknife.ButterKnife;
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
    @BindView(R.id.tv_title)
    TextView title;
    @BindView(R.id.et_start_time)
    TextView etStartTime;
    @BindView(R.id.et_stop_time)
    TextView etStopTime;
    @BindView(R.id.back)
    ImageView back;
    private List<TbPoint> tbPointList1, tbPointList2;
    private List<TbLine> tbLineList1, tbLineList2;
    private List<TbSurface> tbSurfaceList1, tbSurfaceList2;


    private int updataNum = 0;
    private Date upStartTime, upStopTime;
    private AlertDialog dialog;
    private TbPointDao tbPointDao;
    private TbLineDao tbLineDao;
    private TbSurfaceDao tbSurfaceDao;
    private final String filePath = PathConstant.UPLOAD_DATA + "/upload.txt";
    private ProgressDialog pd;
    private Call<UploadBean> call;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_uploaddata;
    }

    @Override
    protected void initView() {

        title.setText("数据上传");
        back.setVisibility(View.VISIBLE);
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
        showProgressDialog();

        ThreadUtils.executeSubThread(new Runnable() {
            @Override
            public void run() {
                //生成文件
                Gson gson = new GsonBuilder().setDateFormat(DateUtil.YMDHMS).registerTypeAdapterFactory(new NullStringToEmptyAdapterFactory()).excludeFieldsWithoutExposeAnnotation().create();
                Map<String, Object> map = new HashMap<>();
                //未上传,新增
                tbPointList1 = tbPointDao.queryBuilder()
                        .where(TbPointDao.Properties.Flag.eq(0),  //新增未上传
                                TbPointDao.Properties.Id.isNull(),
                                TbPointDao.Properties.Lat.notEq(0.0),
                                TbPointDao.Properties.Opttime.between(DateUtil.getDateByFormat(startTime + " 00:00:00", DateUtil.YMDHMS), DateUtil.getDateByFormat(stopTime + " 24:00:00", DateUtil.YMDHMS)))
                        .orderAsc(TbPointDao.Properties.Opttime).list();
//                String buildingJson1 = gson.toJson(tbPointList1);
                map.put("tb_point", tbPointList1);
//                FileUtils.writeFile(PathConstant.UPLOAD_DATA + "/tb_point.txt", buildingJson);
                if (tbPointList1 != null && tbPointList1.size() > 0) {
                    updataNum += tbPointList1.size();
                    upStartTime = tbPointList1.get(0).getOpttime();
                    upStopTime = tbPointList1.get(tbPointList1.size() - 1).getOpttime();
                }

//                //未上传,修改（id不为空，flag=0）
                tbPointList2 = tbPointDao.queryBuilder()
                        .where(TbPointDao.Properties.Flag.eq(2),  //修改未上传
//                                TbPointDao.Properties.Id.isNotNull(),
//                                TbPointDao.Properties.Authflag.eq(getFlagByUser()),
                                TbPointDao.Properties.Lat.notEq(0.0),
                                TbPointDao.Properties.Opttime.between(DateUtil.getDateByFormat(startTime + " 00:00:00", DateUtil.YMDHMS), DateUtil.getDateByFormat(stopTime + " 24:00:00", DateUtil.YMDHMS)))
                        .orderAsc(TbPointDao.Properties.Opttime).list();
//                String buildingJson2 = gson.toJson(tbPointList2);
                map.put("tb_point_modify", tbPointList2);
//                FileUtils.writeFile(PathConstant.UPLOAD_DATA + "/tb_point.txt", buildingJson);
                if (tbPointList2 != null && tbPointList2.size() > 0) {
                    updataNum += tbPointList2.size();
                    upStartTime = upStartTime == null ? tbPointList2.get(0).getOpttime() : new Date(Math.min(upStartTime.getTime(), tbPointList2.get(0).getOpttime().getTime()));
                    upStopTime = upStopTime == null ? tbPointList2.get(tbPointList2.size() - 1).getOpttime() : new Date(Math.max(upStopTime.getTime(), tbPointList2.get(tbPointList2.size() - 1).getOpttime().getTime()));
                }

                //未上传,新增
                tbLineList1 = tbLineDao.queryBuilder()
                        .where(TbLineDao.Properties.Flag.eq(0),
                                TbLineDao.Properties.Id.isNull(),
                                TbLineDao.Properties.Opttime.between(DateUtil.getDateByFormat(startTime + " 00:00:00", DateUtil.YMDHMS), DateUtil.getDateByFormat(stopTime + " 24:00:00", DateUtil.YMDHMS)))
                        .orderAsc(TbLineDao.Properties.Opttime).list();
//                String poiJson1 = gson.toJson(tbLineList1);
//                FileUtils.writeFile(PathConstant.UPLOAD_DATA + "/tb_line.txt", poiJson);

                map.put("tb_line", tbLineList1);
                if (tbLineList1 != null && tbLineList1.size() > 0) {
                    updataNum += tbLineList1.size();
                    upStartTime = upStartTime == null ? tbLineList1.get(0).getOpttime() : new Date(Math.min(upStartTime.getTime(), tbLineList1.get(0).getOpttime().getTime()));
                    upStopTime = upStopTime == null ? tbLineList1.get(tbLineList1.size() - 1).getOpttime() : new Date(Math.max(upStopTime.getTime(), tbLineList1.get(tbLineList1.size() - 1).getOpttime().getTime()));
                }

                //未上传,修改（id不为空，flag=0）
                tbLineList2 = tbLineDao.queryBuilder()
                        .where(TbLineDao.Properties.Flag.eq(2),
//                                TbLineDao.Properties.Id.isNotNull(),
//                                TbLineDao.Properties.Authflag.eq(getFlagByUser()),
                                TbLineDao.Properties.Opttime.between(DateUtil.getDateByFormat(startTime + " 00:00:00", DateUtil.YMDHMS), DateUtil.getDateByFormat(stopTime + " 24:00:00", DateUtil.YMDHMS)))
                        .orderAsc(TbLineDao.Properties.Opttime).list();
//                String poiJson2 = gson.toJson(tbLineList2);
//                FileUtils.writeFile(PathConstant.UPLOAD_DATA + "/tb_line.txt", poiJson);

                map.put("tb_line_modify", tbLineList2);
                if (tbLineList2 != null && tbLineList2.size() > 0) {
                    updataNum += tbLineList2.size();
                    upStartTime = upStartTime == null ? tbLineList2.get(0).getOpttime() : new Date(Math.min(upStartTime.getTime(), tbLineList2.get(0).getOpttime().getTime()));
                    upStopTime = upStopTime == null ? tbLineList2.get(tbLineList2.size() - 1).getOpttime() : new Date(Math.max(upStopTime.getTime(), tbLineList2.get(tbLineList2.size() - 1).getOpttime().getTime()));
                }

                //未上传,新增
                tbSurfaceList1 = tbSurfaceDao.queryBuilder()
                        .where(TbSurfaceDao.Properties.Flag.eq(0),
                                TbSurfaceDao.Properties.Id.isNull(),
                                TbSurfaceDao.Properties.Opttime.between(DateUtil.getDateByFormat(startTime + " 00:00:00", DateUtil.YMDHMS), DateUtil.getDateByFormat(stopTime + " 24:00:00", DateUtil.YMDHMS)))
                        .orderAsc(TbSurfaceDao.Properties.Opttime).list();
//                String socialJson1 = gson.toJson(tbSurfaceList1);
//                FileUtils.writeFile(PathConstant.UPLOAD_DATA + "/tb_surface.txt", socialJson);
                map.put("tb_surface", tbSurfaceList1);

                if (tbSurfaceList1 != null && tbSurfaceList1.size() > 0) {
                    updataNum += tbSurfaceList1.size();
                    upStartTime = upStartTime == null ? tbSurfaceList1.get(0).getOpttime() : new Date(Math.min(upStartTime.getTime(), tbSurfaceList1.get(0).getOpttime().getTime()));
                    upStopTime = upStopTime == null ? tbSurfaceList1.get(tbSurfaceList1.size() - 1).getOpttime() : new Date(Math.max(upStopTime.getTime(), tbSurfaceList1.get(tbSurfaceList1.size() - 1).getOpttime().getTime()));
                }

                //未上传,修改（id不为空，flag=0）
                tbSurfaceList2 = tbSurfaceDao.queryBuilder()
                        .where(TbSurfaceDao.Properties.Flag.eq(2),
//                                TbSurfaceDao.Properties.Id.isNotNull(),
//                                TbSurfaceDao.Properties.Authflag.eq(getFlagByUser()),
                                TbSurfaceDao.Properties.Opttime.between(DateUtil.getDateByFormat(startTime + " 00:00:00", DateUtil.YMDHMS), DateUtil.getDateByFormat(stopTime + " 24:00:00", DateUtil.YMDHMS)))
                        .orderAsc(TbSurfaceDao.Properties.Opttime).list();
//                String socialJson2 = gson.toJson(tbSurfaceList2);
//                FileUtils.writeFile(PathConstant.UPLOAD_DATA + "/tb_surface.txt", socialJson);
                map.put("tb_surface_modify", tbSurfaceList2);

                String json = gson.toJson(map);
                FileUtils.writeFile(filePath, json);
                if (tbSurfaceList2 != null && tbSurfaceList2.size() > 0) {
                    updataNum += tbSurfaceList2.size();
                    upStartTime = upStartTime == null ? tbSurfaceList2.get(0).getOpttime() : new Date(Math.min(upStartTime.getTime(), tbSurfaceList2.get(0).getOpttime().getTime()));
                    upStopTime = upStopTime == null ? tbSurfaceList2.get(tbSurfaceList2.size() - 1).getOpttime() : new Date(Math.max(upStopTime.getTime(), tbSurfaceList2.get(tbSurfaceList2.size() - 1).getOpttime().getTime()));
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
        filePaths.add("/upload.txt");
//        filePaths.add("/tb_line.txt");
//        filePaths.add("/tb_surface.txt");

        File file;
        for (int i = 0; i < filePaths.size(); i++) {
            file = new File(PathConstant.UPLOAD_DATA + filePaths.get(i));
            if (file.exists() && file.length() > 10) {
                RequestBody build = RequestBody.create(MediaType.parse("text/plain;charset=utf-8"), file);
                map.put("file\"; filename=\"" + file.getName(), build);
            }
        }

        if (updataNum == 0) {
            ToastUtils.showShort("没有新数据");
            if (pd.isShowing()) {
                pd.dismiss();
            }
            return;
        }

        call = RetrofitFactory.create(RetrofitService.class).upload(map);

        call.enqueue(new Callback<UploadBean>() {
            @Override
            public void onResponse(@NonNull Call<UploadBean> call, @NonNull Response<UploadBean> response) {
                LogUtils.d("onResponse" + response.body());

                UploadBean body = response.body();
                if (body == null) {
                    if (pd.isShowing()) {
                        pd.dismiss();
                    }
                    showResponseDialog("上传失败");
                    updataNum = 0;
                    upStartTime = null;
                    upStopTime = null;
                    deleteUpdateFile();
                    return;
                }
                processData(body);
            }

            @Override
            public void onFailure(Call<UploadBean> call, Throwable t) {
                t.printStackTrace();
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                if (!call.isCanceled()) {
                    // 非点击取消
                    //LogUtils.d(t.getMessage());
                    ToastUtils.showShort("网络错误");
                } else {
                    ToastUtils.showShort("上传取消");
                }
                updataNum = 0;
                upStartTime = null;
                upStopTime = null;
                deleteUpdateFile();

            }
        });

    }

    @NonNull
    private void showProgressDialog() {
        // Dialog
        pd = new ProgressDialog(mContext);
        pd.setMessage("正在上传...");
        // 点击对话框以外的地方无法取消
        pd.setCanceledOnTouchOutside(false);
        // 点击返回按钮无法取消
        pd.setCancelable(false);
        pd.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (call != null)
                    call.cancel();
            }
        });
        pd.show();
    }

    /**
     * 处理数据
     *
     * @param body
     */
    private void processData(UploadBean body) {
        if (body.isStatus()) {
            ThreadUtils.executeSubThread(new Runnable() {
                @Override
                public void run() {
                    updateData();
                    ThreadUtils.executeMainThread(new Runnable() {
                        @Override
                        public void run() {
                            if (pd.isShowing()) {
                                pd.dismiss();
                            }
                            showResponseDialog("上传成功\n" + "总数：" + updataNum + "\n" + DateUtil.getStringByFormat(upStartTime, DateUtil.YMDHMS) + "\n" + DateUtil.getStringByFormat(upStopTime, DateUtil.YMDHMS));
                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    captureScreen();
                                }
                            }, 100);
                            updataNum = 0;
                            upStartTime = null;
                            upStopTime = null;
                            deleteUpdateFile();
                        }
                    });
                }
            });

        } else {

            if (pd.isShowing()) {
                pd.dismiss();
            }
            showResponseDialog("上传失败");
            updataNum = 0;
            upStartTime = null;
            upStopTime = null;
            deleteUpdateFile();
        }

    }

    //删除上传文件
    private void deleteUpdateFile() {
        if (new File(filePath).exists())
            new File(filePath).delete();
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
        updatePoi();
        updateLine();
        updateSurface();

    }

    private void updateSurface() {
        if (tbSurfaceList1 != null && tbSurfaceList1.size() > 0) {
            for (int i = 0; i < tbSurfaceList1.size(); i++) {
                tbSurfaceList1.get(i).setFlag(1);
            }
            tbSurfaceDao.updateInTx(tbSurfaceList1);

        }
        if (tbSurfaceList2 != null && tbSurfaceList2.size() > 0) {
            for (int i = 0; i < tbSurfaceList2.size(); i++) {
                tbSurfaceList2.get(i).setFlag(1);
            }
            tbSurfaceDao.updateInTx(tbSurfaceList2);

        }
    }

    private void updateLine() {
        if (tbLineList1 != null && tbLineList1.size() > 0) {
            for (int i = 0; i < tbLineList1.size(); i++) {
                tbLineList1.get(i).setFlag(1);
            }
            tbLineDao.updateInTx(tbLineList1);
        }

        if (tbLineList2 != null && tbLineList2.size() > 0) {
            for (int i = 0; i < tbLineList2.size(); i++) {
                tbLineList2.get(i).setFlag(1);
            }
            tbLineDao.updateInTx(tbLineList2);
        }
    }

    private void updatePoi() {
        if (tbPointList1 != null && tbPointList1.size() > 0) {
            for (int i = 0; i < tbPointList1.size(); i++) {
                tbPointList1.get(i).setFlag(1);
            }
            tbPointDao.updateInTx(tbPointList1);
        }

        if (tbPointList2 != null && tbPointList2.size() > 0) {
            for (int i = 0; i < tbPointList2.size(); i++) {
                tbPointList2.get(i).setFlag(1);
            }
            tbPointDao.updateInTx(tbPointList2);
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
                updataNum = 0;
                upStartTime = null;
                upStopTime = null;
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

    public String getFlagByUser() {
        String flag = "0";
        String roleid = SPUtils.getInstance().getString("roleid");
        if (roleid.equals("6")) {
            //外业
            flag = "0";
        } else if (roleid.equals("2") || roleid.equals("8")) {
            //质检
            flag = "1";
        }
        return flag;
    }

    public class NullStringToEmptyAdapterFactory<T> implements TypeAdapterFactory {
        @SuppressWarnings("unchecked")
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            Class<T> rawType = (Class<T>) type.getRawType();
            if (rawType != String.class) {
                return null;
            }
            return (TypeAdapter<T>) new StringNullAdapter();
        }
    }

    public class StringNullAdapter extends TypeAdapter<String> {
        @Override
        public String read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return "";
            }
            return reader.nextString();
        }

        @Override
        public void write(JsonWriter writer, String value) throws IOException {
            if (value == null) {
                writer.nullValue();
                return;
            }
            writer.value(value);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        deleteUpdateFile();
    }
}
