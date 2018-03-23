package com.mapuni.gdydcaiji.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.mapuni.gdydcaiji.bean.TBuildingInfo;
import com.mapuni.gdydcaiji.bean.TPoiInfo;
import com.mapuni.gdydcaiji.bean.TSocialInfo;
import com.mapuni.gdydcaiji.bean.TVillageInfo;
import com.mapuni.gdydcaiji.database.greendao.DaoSession;
import com.mapuni.gdydcaiji.database.greendao.TBuildingInfoDao;
import com.mapuni.gdydcaiji.database.greendao.TPoiInfoDao;
import com.mapuni.gdydcaiji.database.greendao.TSocialInfoDao;
import com.mapuni.gdydcaiji.database.greendao.TVillageInfoDao;
import com.mapuni.gdydcaiji.net.RetrofitFactory;
import com.mapuni.gdydcaiji.net.RetrofitService;
import com.mapuni.gdydcaiji.utils.DateUtil;
import com.mapuni.gdydcaiji.utils.FileUtils;
import com.mapuni.gdydcaiji.utils.PathConstant;
import com.mapuni.gdydcaiji.utils.ThreadUtils;
import com.mapuni.gdydcaiji.utils.ToastUtils;


import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by yf on 2018/3/22.
 */

public class UploadDataActivity extends BaseActivity {
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.et_start_time)
    TextView etStartTime;
    @BindView(R.id.et_stop_time)
    TextView etStopTime;

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

        DaoSession daoSession = GdydApplication.getInstances().getDaoSession();
        final TBuildingInfoDao tBuildingInfoDao = daoSession.getTBuildingInfoDao();
        final TPoiInfoDao tPoiInfoDao = daoSession.getTPoiInfoDao();
        final TSocialInfoDao tSocialInfoDao = daoSession.getTSocialInfoDao();
        final TVillageInfoDao tVillageInfoDao = daoSession.getTVillageInfoDao();

        ThreadUtils.executeSubThread(new Runnable() {
            @Override
            public void run() {
                //生成文件
                Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
                final List<TBuildingInfo> buildingInfos = tBuildingInfoDao.queryBuilder()
                        .where(TBuildingInfoDao.Properties.Flag.eq(0),  //未上传
                                TBuildingInfoDao.Properties.Opttime.between(DateUtil.getDateByFormat(startTime + " 00:00:00", DateUtil.YMDHMS), DateUtil.getDateByFormat(stopTime + " 24:00:00", DateUtil.YMDHMS)))
                        .build().list();
                String buildingJson = gson.toJson(buildingInfos);
                FileUtils.writeFile(PathConstant.UPLOAD_DATA + "/t_building_info.txt", buildingJson);

                List<TPoiInfo> poiInfos = tPoiInfoDao.queryBuilder()
                        .where(TPoiInfoDao.Properties.Flag.eq(0)
                                , TPoiInfoDao.Properties.Opttime.between(DateUtil.getDateByFormat(startTime + " 00:00:00", DateUtil.YMDHMS), DateUtil.getDateByFormat(stopTime + " 24:00:00", DateUtil.YMDHMS)))
                        .build().list();
                String poiJson = gson.toJson(poiInfos);
                FileUtils.writeFile(PathConstant.UPLOAD_DATA + "/t_poi_info.txt", poiJson);

                List<TSocialInfo> socialInfos = tSocialInfoDao.queryBuilder()
                        .where(TSocialInfoDao.Properties.Flag.eq(0)
                                , TSocialInfoDao.Properties.Opttime.between(DateUtil.getDateByFormat(startTime + " 00:00:00", DateUtil.YMDHMS), DateUtil.getDateByFormat(stopTime + " 24:00:00", DateUtil.YMDHMS)))
                        .build().list();
                String socialJson = gson.toJson(socialInfos);
                FileUtils.writeFile(PathConstant.UPLOAD_DATA + "/t_social_info.txt", socialJson);

                List<TVillageInfo> villageInfos = tVillageInfoDao.queryBuilder()
                        .where(TVillageInfoDao.Properties.Flag.eq(0)
                                , TVillageInfoDao.Properties.Opttime.between(DateUtil.getDateByFormat(startTime + " 00:00:00", DateUtil.YMDHMS), DateUtil.getDateByFormat(stopTime + " 24:00:00", DateUtil.YMDHMS)))
                        .build().list();
                String villageJson = gson.toJson(villageInfos);
                FileUtils.writeFile(PathConstant.UPLOAD_DATA + "/t_village_info.txt", villageJson);

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
        filePaths.add("/t_building_info.txt");
        filePaths.add("/t_poi_info.txt");
        filePaths.add("/t_social_info.txt");
        filePaths.add("/t_village_info.txt");

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

        final Call<RequestBody> call = RetrofitFactory.create(RetrofitService.class).upload(map);
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

        call.enqueue(new Callback<RequestBody>() {
            @Override
            public void onResponse(@NonNull Call<RequestBody> call, @NonNull Response<RequestBody> response) {
                // LogUtils.d("onResponse" + response.body());
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                ToastUtils.showShort("上传成功");
            }

            @Override
            public void onFailure(Call<RequestBody> call, Throwable t) {
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
                .setThemeColor(getResources().getColor(R.color.timepicker_dialog_bg))
                .setType(Type.YEAR_MONTH_DAY)
                .setWheelItemTextNormalColor(getResources().getColor(R.color.timetimepicker_default_text_color))
                .setWheelItemTextSelectorColor(getResources().getColor(R.color.timepicker_toolbar_bg))
                .setWheelItemTextSize(16)
                .build();
        mDialogYearMonthDay.show(getSupportFragmentManager(), getClass().getSimpleName());

    }

}
