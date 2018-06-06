package com.mapuni.gdydcaiji.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONReader;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.mapuni.gdydcaiji.GdydApplication;
import com.mapuni.gdydcaiji.R;
import com.mapuni.gdydcaiji.adapter.FieidPersonListAdapter;
import com.mapuni.gdydcaiji.adapter.HomeAreaAdapter;
import com.mapuni.gdydcaiji.adapter.Level0Item;
import com.mapuni.gdydcaiji.adapter.Level1Item;
import com.mapuni.gdydcaiji.bean.FieidPerson;
import com.mapuni.gdydcaiji.bean.HomeArea;
import com.mapuni.gdydcaiji.bean.InLine;
import com.mapuni.gdydcaiji.bean.InPoint;
import com.mapuni.gdydcaiji.bean.InSurface;
import com.mapuni.gdydcaiji.database.greendao.DaoSession;
import com.mapuni.gdydcaiji.database.greendao.InLineDao;
import com.mapuni.gdydcaiji.database.greendao.InPointDao;
import com.mapuni.gdydcaiji.database.greendao.InSurfaceDao;
import com.mapuni.gdydcaiji.net.RetrofitFactory;
import com.mapuni.gdydcaiji.net.RetrofitService;
import com.mapuni.gdydcaiji.utils.DateUtil;
import com.mapuni.gdydcaiji.utils.LogUtils;
import com.mapuni.gdydcaiji.utils.PathConstant;
import com.mapuni.gdydcaiji.utils.SPUtils;
import com.mapuni.gdydcaiji.utils.ThreadUtils;
import com.mapuni.gdydcaiji.utils.ToastUtils;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by yf on 2018/4/11.
 * 下载内业数据
 */

public class DownloadInteriorDataActivity extends BaseActivity {
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.edit)
    TextView edit;
    @BindView(R.id.mRecycleview)
    RecyclerView mRecycleView;
//    @BindView(R.id.et_start_time)
//    TextView etStartTime;
//    @BindView(R.id.et_stop_time)
//    TextView etStopTime;

    //    private List<FieidPerson> fieidPersonList = new ArrayList<>();
    private ArrayList<String> res = new ArrayList<>();
    private HomeAreaAdapter adapter;


    private InPointDao tbPointDao;
    private InLineDao tbLineDao;
    private InSurfaceDao tbSurfaceDao;
    private final String filePath = PathConstant.DOWNLOAD_DATA_PATH + File.separator + "downloadInteriorData.txt";
    private ProgressDialog pd;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_download;
    }

    @Override
    protected void initView() {
        tvTitle.setText("数据下载");
        edit.setVisibility(View.VISIBLE);
        edit.setText("下载");
        back.setVisibility(View.VISIBLE);

        DaoSession daoSession = GdydApplication.getInstances().getDaoSession();
        tbPointDao = daoSession.getInPointDao();
        tbLineDao = daoSession.getInLineDao();
        tbSurfaceDao = daoSession.getInSurfaceDao();


        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mRecycleView.setHasFixedSize(true);
        mRecycleView.setNestedScrollingEnabled(false);
        mRecycleView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .size(1)
                .colorResId(R.color.gray_line)
                .build());//添加分隔线
        adapter = new HomeAreaAdapter();
        mRecycleView.setAdapter(adapter);
    }

    @Override
    protected void initData() {

//        int userId = SPUtils.getInstance().getInt("userId");
        RetrofitFactory.create(RetrofitService.class).getHomeAreaList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HomeArea>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(final HomeArea homeArea) {

//                        fieidPersonList.addAll(fieidPeople);
                        if (homeArea.getData() == null || homeArea.getData().size() == 0) {
                            showEmptyPage();
                            return;
                        }

                        adapter.setNewData(homeArea.getData());
//                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {

                        showEmptyPage();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    protected void initListener() {

    }

    private void downloadData() {

        List<String> areaNames = adapter.getAreaNames();
        if (areaNames.size() == 0) {
            ToastUtils.showShort("请先选择要下载的区域");
            return;
        }
        String fieidStr = "";
        for (int i = 0; i < areaNames.size(); i++) {
            fieidStr += areaNames.get(i) + ",";
        }

        fieidStr = fieidStr.substring(0, fieidStr.length() - 1);

        // Dialog
        pd = new ProgressDialog(this);
        pd.setMessage("下载中...");
        // 点击对话框以外的地方无法取消
        pd.setCanceledOnTouchOutside(false);
        // 点击返回按钮无法取消
        pd.setCancelable(false);
        pd.show();

        RetrofitFactory.create(RetrofitService.class)
                .downloadAreaData(fieidStr)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .map(new Function<ResponseBody, InputStream>() {

                    @Override
                    public InputStream apply(ResponseBody responseBody) throws Exception {
                        return responseBody.byteStream();
                    }

                })
                .observeOn(Schedulers.io()) // 用于计算任务
                .doOnNext(new Consumer<InputStream>() {
                    @Override
                    public void accept(InputStream inputStream) throws Exception {
                        writeFile(inputStream, filePath);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<InputStream>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(InputStream inputStream) {
//                        pd.dismiss();
                        pd.setMessage("处理数据中...");
                        ToastUtils.showShort("下载成功");
//                        ThreadUtils.executeSubThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                insertData2DB();
//                            }
//                        });
                        new InserDataTask().execute("");

                    }

                    @Override
                    public void onError(Throwable e) {

                        if (pd.isShowing())
                            pd.dismiss();
                        ToastUtils.showShort("下载失败");
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

//    private void insertData2DB() {
//        FileInputStream fileTemp = null;
//        try {
//
////            fileTemp = new FileInputStream(new File(filePath));
////            int length = fileTemp.available();
////            byte[] buffer = new byte[length];
////            fileTemp.read(buffer);
////            String json = new String(buffer);
//            FileReader fileReader = new FileReader(filePath);
//            JsonReader jsonReader = new JsonReader(fileReader);
//            jsonReader.setLenient(true);
//            Gson gson = new GsonBuilder().setDateFormat(DateUtil.YMDHMS).create();
//            DownloadBean downloadBean = gson.fromJson(jsonReader, DownloadBean.class);
//            List<TbPoint> tb_points = downloadBean.getTb_point();
//            List<TbLine> tb_lines = downloadBean.getTb_line();
//            List<TbSurface> tb_surfaces = downloadBean.getTb_surface();
//
//            if (tb_points != null && tb_points.size() > 0) {
////                tbPointDao.insertInTx(tb_points);
//                tbPointDao.insertOrReplaceInTx(tb_points);
//            }
//            if (tb_lines != null && tb_lines.size() > 0) {
//                tbLineDao.insertOrReplaceInTx(tb_lines);
//            }
//            if (tb_surfaces != null && tb_surfaces.size() > 0) {
//                tbSurfaceDao.insertOrReplaceInTx(tb_surfaces);
//            }
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private void insertData2DB2() {
        FileInputStream fileTemp = null;

        FileReader fileReader = null;
        
        try {
            fileReader = new FileReader(filePath);

            JSONReader jsonReader = new JSONReader(fileReader);
            jsonReader.startObject();
            while (jsonReader.hasNext()) {
                String elem = jsonReader.readString();
                if ("data".equals(elem)) {
                    jsonReader.startArray();
                    while (jsonReader.hasNext()) {
                        jsonReader.startObject();
                        InPoint tbPoint = new InPoint();
                        while (jsonReader.hasNext()) {
                            String itemName = jsonReader.readString();
                            Object itemValue = jsonReader.readObject();
                            readTbPoint(tbPoint, itemName, itemValue);
                        }
                        jsonReader.endObject();
                        tbPointDao.insertOrReplace(tbPoint);
                    }
                    jsonReader.endArray();
                } else if ("tb_line".equals(elem)) {
                    jsonReader.startArray();
                    while (jsonReader.hasNext()) {
                        jsonReader.startObject();
                        InLine tbLine = new InLine();
                        while (jsonReader.hasNext()) {
                            String itemName = jsonReader.readString();
                            Object itemValue = jsonReader.readObject();
                            readTbLine(tbLine, itemName, itemValue);
                        }
                        jsonReader.endObject();
                        tbLineDao.insertOrReplace(tbLine);
                    }
                    jsonReader.endArray();
                } else if ("tb_surface".equals(elem)) {
                    jsonReader.startArray();
                    while (jsonReader.hasNext()) {
                        jsonReader.startObject();
                        InSurface tbSurface = new InSurface();
                        while (jsonReader.hasNext()) {
                            String itemName = jsonReader.readString();
                            Object itemValue = jsonReader.readObject();
                            readTbSurface(tbSurface, itemName, itemValue);
                        }
                        jsonReader.endObject();
                        tbSurfaceDao.insertOrReplace(tbSurface);
                    }
                    jsonReader.endArray();
                } else {
                    jsonReader.readObject();
                }
            }

            jsonReader.endObject();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void readTbSurface(InSurface tbSurface, String itemName, Object itemValue) {
        if ("id".equals(itemName)) {
            tbSurface.setId(Long.valueOf(itemValue.toString()));
        }
//        else if ("bm".equals(itemName)) {
//            tbSurface.setBm(Long.valueOf(itemValue.toString()));
//        } 
        else if ("name".equals(itemName)) {
            tbSurface.setName((String) itemValue);
        } else if ("xqdz".equals(itemName)) {
            tbSurface.setXqdz((String) itemValue);
        } else if ("fl".equals(itemName)) {
            tbSurface.setFl((String) itemValue);
        } else if ("wyxx".equals(itemName)) {
            tbSurface.setWyxx((String) itemValue);
        } else if ("lxdh".equals(itemName)) {
            tbSurface.setLxdh((String) itemValue);
        } else if ("lds".equals(itemName)) {
            tbSurface.setLds((String) itemValue);
        } else if ("polyarrays".equals(itemName)) {
            tbSurface.setPolyarrays((String) itemValue);
        } else if ("oprator".equals(itemName)) {
            tbSurface.setOprator((String) itemValue);
        } else if ("opttime".equals(itemName)) {
            tbSurface.setOpttime(DateUtil.getDateByFormat((String) itemValue, DateUtil.YMDHMS));
        } else if ("deleteflag".equals(itemName)) {
            tbSurface.setDeleteflag((String) itemValue);
        } else if ("createtime".equals(itemName)) {
            tbSurface.setCreatetime(DateUtil.getDateByFormat((String) itemValue, DateUtil.YMDHMS));
        } else if ("note".equals(itemName)) {
            tbSurface.setNote((String) itemValue);
        } else if ("img".equals(itemName)) {
            tbSurface.setImg((String) itemValue);
        } else if ("authflag".equals(itemName)) {
            tbSurface.setAuthflag((String) itemValue);
        } else if ("authcontent".equals(itemName)) {
            tbSurface.setAuthcontent((String) itemValue);
        } else if ("homearea".equals(itemName)) {
            tbSurface.setHomearea((String) itemValue);
        }
    }

    private void readTbLine(InLine tbLine, String itemName, Object itemValue) {
        if ("id".equals(itemName)) {
            tbLine.setId(Long.valueOf(itemValue.toString()));
        }
//        else if ("bm".equals(itemName)) {
//            tbLine.setBm(Long.valueOf(itemValue.toString()));
//        } 
        else if ("name".equals(itemName)) {
            tbLine.setName((String) itemValue);
        } else if ("sfz".equals(itemName)) {
            tbLine.setSfz((String) itemValue);
        } else if ("zdz".equals(itemName)) {
            tbLine.setZdz((String) itemValue);
        } else if ("polyarrays".equals(itemName)) {
            tbLine.setPolyarrays((String) itemValue);
        } else if ("oprator".equals(itemName)) {
            tbLine.setOprator((String) itemValue);
        } else if ("opttime".equals(itemName)) {
            tbLine.setOpttime(DateUtil.getDateByFormat((String) itemValue, DateUtil.YMDHMS));
        } else if ("deleteflag".equals(itemName)) {
            tbLine.setDeleteflag((String) itemValue);
        } else if ("createtime".equals(itemName)) {
            tbLine.setCreatetime(DateUtil.getDateByFormat((String) itemValue, DateUtil.YMDHMS));
        } else if ("note".equals(itemName)) {
            tbLine.setNote((String) itemValue);
        } else if ("img".equals(itemName)) {
            tbLine.setImg((String) itemValue);
        } else if ("authflag".equals(itemName)) {
            tbLine.setAuthflag((String) itemValue);
        } else if ("authcontent".equals(itemName)) {
            tbLine.setAuthcontent((String) itemValue);
        } else if ("homearea".equals(itemName)) {
            tbLine.setHomearea((String) itemValue);
        }
    }

    private void readTbPoint(InPoint tbPoint, String itemName, Object itemValue) {
        if ("id".equals(itemName)) {
            tbPoint.setId(Long.valueOf(itemValue.toString()));
        }
//        else if ("bm".equals(itemName)) {
//            tbPoint.setBm(Long.valueOf(itemValue.toString()));
//        } 
        else if ("lytype".equals(itemName)) {
            tbPoint.setLytype((String) itemValue);
        } else if ("lyxz".equals(itemName)) {
            tbPoint.setLyxz((String) itemValue);
        } else if ("name".equals(itemName)) {
            tbPoint.setName((String) itemValue);
        } else if ("fl".equals(itemName)) {
            tbPoint.setFl((String) itemValue);
        } else if ("dz".equals(itemName)) {
            tbPoint.setDz((String) itemValue);
        } else if ("dy".equals(itemName)) {
            tbPoint.setDy((String) itemValue);
        } else if ("lxdh".equals(itemName)) {
            tbPoint.setLxdh((String) itemValue);
        } else if ("dj".equals(itemName)) {
            tbPoint.setDj((String) itemValue);
        } else if ("lycs".equals(itemName)) {
            tbPoint.setLycs((String) itemValue);
        } else if ("lyzhs".equals(itemName)) {
            tbPoint.setLyzhs((String) itemValue);
        } else if ("lng".equals(itemName)) {
            tbPoint.setLng(Double.parseDouble(itemValue.toString()));
        } else if ("lat".equals(itemName)) {
            tbPoint.setLat(Double.parseDouble(itemValue.toString()));
        } else if ("oprator".equals(itemName)) {
            tbPoint.setOprator((String) itemValue);
        } else if ("opttime".equals(itemName)) {
            tbPoint.setOpttime(DateUtil.getDateByFormat((String) itemValue, DateUtil.YMDHMS));
        } else if ("deleteflag".equals(itemName)) {
            tbPoint.setDeleteflag((String) itemValue);
        } else if ("createtime".equals(itemName)) {
            tbPoint.setCreatetime(DateUtil.getDateByFormat((String) itemValue, DateUtil.YMDHMS));
        } else if ("note".equals(itemName)) {
            tbPoint.setNote((String) itemValue);
        } else if ("img".equals(itemName)) {
            tbPoint.setImg((String) itemValue);
        } else if ("authflag".equals(itemName)) {
            tbPoint.setAuthflag((String) itemValue);
        } else if ("authcontent".equals(itemName)) {
            tbPoint.setAuthcontent((String) itemValue);
        } else if ("homearea".equals(itemName)) {
            tbPoint.setHomearea((String) itemValue);
        }
    }

//    private class FieidPersonListAdapter extends BaseQuickAdapter<FieidPerson, BaseViewHolder> {
//
//        public FieidPersonListAdapter(int layoutResId, @Nullable List<FieidPerson> data) {
//            super(layoutResId, data);
//        }
//
//        @Override
//        protected void convert(BaseViewHolder helper, final FieidPerson item) {
//            helper.setText(R.id.tv_fieid_name, item.getName());
//            CheckBox checkBox = helper.getView(R.id.cb_fieid);
//            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    if (isChecked) {
//                        if (!fieidIds.contains(item.getName()))
//                            fieidIds.add(item.getName());
//                    } else {
//                        if (fieidIds.contains(item.getName()))
//                            fieidIds.remove(item.getName());
//                    }
//                }
//            });
//        }
//    }


    protected void showEmptyPage() {
        View common_no_data = View.inflate(this, R.layout.common_no_data, null);
        LinearLayout common_no_dataViewById = (LinearLayout) common_no_data.findViewById(R.id.common_no_data);
        common_no_dataViewById.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                initData();
            }
        });
        adapter.setEmptyView(common_no_data);
    }


    @OnClick({R.id.back, R.id.edit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.edit:
                downloadData();
                break;
//            case R.id.et_start_time:
//                showDatePickerDialog(etStartTime);
//                break;
//            case R.id.et_stop_time:
//                showDatePickerDialog(etStopTime);
//                break;
        }
    }

    /**
     * 将输入流写入文件
     *
     * @param inputString
     * @param filePath
     */
    private void writeFile(InputStream inputString, String filePath) {

        File file = new File(filePath);
        if (!new File(PathConstant.DOWNLOAD_DATA_PATH).exists()) {
            new File(PathConstant.DOWNLOAD_DATA_PATH).mkdirs();
        }
        if (file.exists()) {
            file.delete();
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);

            byte[] b = new byte[1024];

            int len;
            while ((len = inputString.read(b)) != -1) {
                fos.write(b, 0, len);
            }
            inputString.close();
            fos.close();

        } catch (FileNotFoundException e) {
            deleteDownloadFile();
            ToastUtils.showShort("文件不存在");
        } catch (IOException e) {
            deleteDownloadFile();
            ToastUtils.showShort("下载错误");
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

    class InserDataTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            insertData2DB2();
            return "done";
        }

        @Override
        protected void onPostExecute(String s) {
            if (pd.isShowing())
                pd.dismiss();
            if ("done".equals(s)) {
                deleteDownloadFile();
//                Intent intent = new Intent(DownloadInteriorDataActivity.this, EditInteriorActivity.class);
//                startActivity(intent);
                finish();
            }
            super.onPostExecute(s);
        }
    }

    //删除下载文件
    private void deleteDownloadFile() {
        if (new File(filePath).exists())
            new File(filePath).delete();
    }
}
