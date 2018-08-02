package com.mapuni.gdydcaiji.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mapuni.gdydcaiji.GdydApplication;
import com.mapuni.gdydcaiji.R;
import com.mapuni.gdydcaiji.bean.EventYD;
import com.mapuni.gdydcaiji.bean.EventYDInterior;
import com.mapuni.gdydcaiji.bean.EvevtUpdate;
import com.mapuni.gdydcaiji.bean.InPoint;
import com.mapuni.gdydcaiji.database.greendao.InPointDao;
import com.mapuni.gdydcaiji.utils.SPUtils;
import com.mapuni.gdydcaiji.utils.ShowDataUtils;
import com.mapuni.gdydcaiji.view.ClearEditText;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;
import java.util.List;

import butterknife.BindView;

/**
 * Created by yf on 2018/3/21.
 * 内业数据处理poi采集
 */

public class InteriorPoiDetail extends BaseDetailActivity<InPoint> implements View.OnClickListener {
    @BindView(R.id.tv_title)
    TextView title;
    @BindView(R.id.et_lyName)
    AutoCompleteTextView etLyName;
    @BindView(R.id.sp_lyfl)
    Spinner spLyfl;
    @BindView(R.id.et_address)
    AutoCompleteTextView etAddress;
    @BindView(R.id.et_dyh)
    ClearEditText etDyh;
    @BindView(R.id.et_tele)
    ClearEditText etTele;
    @BindView(R.id.sp_dj)
    Spinner spDj;
    @BindView(R.id.et_lycs)
    ClearEditText etLycs;
    @BindView(R.id.et_lyzhs)
    ClearEditText etLyzhs;
    @BindView(R.id.iv_calculator)
    ImageView ivCalculator;
    @BindView(R.id.ll_ssqy)
    LinearLayout llSsqy;
    @BindView(R.id.et_ssqy)
    AutoCompleteTextView etSsqy;
    @BindView(R.id.et_bz)
    ClearEditText etBz;
    @BindView(R.id.tv_type0)
    TextView tvType0;
    @BindView(R.id.tv_type1)
    TextView tvType1;
    @BindView(R.id.tv_type2)
    TextView tvType2;
    @BindView(R.id.tv_type3)
    TextView tvType3;
    @BindView(R.id.tv_xz0)
    TextView tvXz0;
    @BindView(R.id.tv_xz1)
    TextView tvXz1;
    @BindView(R.id.tv_xz2)
    TextView tvXz2;
    @BindView(R.id.edit)
    TextView edit;

    private InPointDao tPoiInfoDao;
    private String lyType = "", lyXz = "";
    protected double lat;
    protected double lng;
//    private String address;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_poi_detail;
    }

    @Override
    protected void initView() {
        super.initView();

        llSsqy.setVisibility(View.VISIBLE);
        title.setText("POI点采集");
        tPoiInfoDao = GdydApplication.getInstances().getDaoSession().getInPointDao();
//        setSpinnerData(R.array.building_types, spLyType);
//        setSpinnerData(R.array.building_xz, spLyxz);
        setSpinnerData(R.array.building_fl, spLyfl);
        setSpinnerData(R.array.poi_mjdj, spDj);
        spLyfl.setSelection(getResources().getStringArray(R.array.building_fl).length - 1);
        initListPopupWindow();

        tvType0.setText(getResources().getStringArray(R.array.building_types)[0]);
        tvType1.setText(getResources().getStringArray(R.array.building_types)[1]);
        tvType2.setText(getResources().getStringArray(R.array.building_types)[2]);
        tvType3.setText(getResources().getStringArray(R.array.building_types)[3]);

        tvXz0.setText(getResources().getStringArray(R.array.building_xz)[0]);
        tvXz1.setText(getResources().getStringArray(R.array.building_xz)[1]);
        tvXz2.setText(getResources().getStringArray(R.array.building_xz)[2]);
    }

    @Override
    protected void initData() {
        long bm = getIntent().getLongExtra("resultBm", -1);
        lat = getIntent().getDoubleExtra("lat", 0);
        lng = getIntent().getDoubleExtra("lng", 0);
        List<InPoint> list = tPoiInfoDao.queryBuilder().where(InPointDao.Properties.Bm.eq(bm)).list();
        if (!list.isEmpty()) {
            resultBean = list.get(0);
        }
        super.initData();
    }

    private void initListPopupWindow() {
        List<String> mAddArray = ShowDataUtils.getAddressOrNameArray("address");
        ArrayAdapter<String> lpwAdapter = new ArrayAdapter<>(this, R.layout.item_listpopupwindow, mAddArray);
        lpwAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        etAddress.setAdapter(lpwAdapter);

        List<String> mNameArray = ShowDataUtils.getAddressOrNameArray("lyname");
        ArrayAdapter<String> lpwAdapter2 = new ArrayAdapter<>(this, R.layout.item_listpopupwindow, mNameArray);
        lpwAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        etLyName.setAdapter(lpwAdapter2);

        List<String> mAreaArray = ShowDataUtils.getAddressOrNameArray("homearea");
        ArrayAdapter<String> lpwAdapter3 = new ArrayAdapter<>(this, R.layout.item_listpopupwindow, mAreaArray);
        lpwAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        etSsqy.setAdapter(lpwAdapter3);
    }

    @Override
    protected void initListener() {
        super.initListener();
        etAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                AutoCompleteTextView view = (AutoCompleteTextView) v;
                view.showDropDown();
            }
        });

        etLyName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                AutoCompleteTextView view = (AutoCompleteTextView) v;
                view.showDropDown();
            }
        });
        
        etSsqy.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                AutoCompleteTextView view = (AutoCompleteTextView) v;
                view.showDropDown();
            }
        });
        
        ivCalculator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openJS();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new EventYDInterior(resultBean));
                finish();
            }
        });

        tvType0.setOnClickListener(this);
        tvType1.setOnClickListener(this);
        tvType2.setOnClickListener(this);
        tvType3.setOnClickListener(this);

        tvXz0.setOnClickListener(this);
        tvXz1.setOnClickListener(this);
        tvXz2.setOnClickListener(this);
    }

    @Override
    protected void showData() {
        edit.setVisibility(View.VISIBLE);
        edit.setText("移点");
        etLyName.setText(resultBean.getName());
        etAddress.setText(resultBean.getDz());
//        spLyType.setSelection(TextUtils.isEmpty(resultBean.getLytype()) ? 0 : getSelectPosition(R.array.building_types, resultBean.getLytype()));
        lyType = resultBean.getLytype();
        setSelecteTypeTv(lyType);
        lyXz = resultBean.getLyxz();
        setSelecteLyXzTv(lyXz);
//        spLyxz.setSelection(TextUtils.isEmpty(resultBean.getLyxz()) ? 0 : getSelectPosition(R.array.building_xz, ));
        String fl = resultBean.getFl();
        if (fl.equals("楼栋")) {
            fl = "商业楼宇";
        } else if (fl.equals("购物广场")) {
            fl = "大型商贸场所";
        }
        spLyfl.setSelection(getSelectPosition(R.array.building_fl, fl));
        spDj.setSelection(TextUtils.isEmpty(resultBean.getDj()) ? 0 : getSelectPosition(R.array.poi_mjdj, resultBean.getDj()));
        etDyh.setText(resultBean.getDy());
        etTele.setText(resultBean.getLxdh());
        etLycs.setText(resultBean.getLycs());
        etLyzhs.setText(resultBean.getLyzhs());
        etBz.setText(resultBean.getNote());
        etSsqy.setText(resultBean.getHomearea());
        if (!TextUtils.isEmpty(resultBean.getImg())) {
            photoImg = resultBean.getImg();
        }
//        if (roleid.equals("6")) {
//            //外业
//            if (resultBean.getId() != null && !TextUtils.isEmpty(resultBean.getAuthcontent())) {
//                tvZjjgzs.setVisibility(View.VISIBLE);
//                tvZjjgzs.setText(resultBean.getAuthcontent());
//            }
//
//        } else if (roleid.equals("2") || roleid.equals("8")) {
//            //质检
//            if (resultBean.getId() != null) {
//                llZj.setVisibility(View.VISIBLE);
//                etZjjg.setText(resultBean.getAuthcontent());
//                cover.setVisibility(View.VISIBLE);
//            }
//        }
        super.showData();
    }


    @Override
    protected void submit() {
        if (resultBean == null) {

            resultBean = new InPoint();
//            resultBean.setLat(lat);
//            resultBean.setLng(lng);
<<<<<<< HEAD
            if (TextUtils.isEmpty(resultBean.getOprator())) {
                resultBean.setOprator(SPUtils.getInstance().getString("username"));
            }
=======
            resultBean.setOprator(SPUtils.getInstance().getString("username"));
>>>>>>> 746d730cb42a41f26875c11a1735a2e36e6a7075
        }

        if (lat != 0) {
            resultBean.setLat(lat);
        }
        if (lng != 0) {
            resultBean.setLng(lng);
        }
        resultBean.setName(getTextByView(etLyName));
//        resultBean.setLytype(spLyType.getSelectedItemPosition() == 0 ? "" : getResources().getStringArray(R.array.building_types)[spLyType.getSelectedItemPosition()]);
        resultBean.setLytype(lyType);
//        resultBean.setLyxz(spLyxz.getSelectedItemPosition() == 0 ? "" : getResources().getStringArray(R.array.building_xz)[spLyxz.getSelectedItemPosition()]);
        resultBean.setLyxz(lyXz);
        resultBean.setFl(getResources().getStringArray(R.array.building_fl)[spLyfl.getSelectedItemPosition()]);
        resultBean.setDz(getTextByView(etAddress));
        resultBean.setDy(getTextByView(etDyh));
        resultBean.setLxdh(getTextByView(etTele));
        resultBean.setDj(spDj.getSelectedItemPosition() == 0 ? "" : getResources().getStringArray(R.array.poi_mjdj)[spDj.getSelectedItemPosition()]);
        resultBean.setLycs(getTextByView(etLycs));
        resultBean.setLyzhs(getTextByView(etLyzhs));
        resultBean.setHomearea(getTextByView(etSsqy));
        resultBean.setNote(getTextByView(etBz));
        resultBean.setImg(getPhotoImg());
        resultBean.setOpttime(new Date(System.currentTimeMillis()));

        if (isInsert) {
            resultBean.setFlag(0);
            tPoiInfoDao.insert(resultBean);
        } else {
            if (resultBean.getFlag() == 0 && resultBean.getId() == null) {
                //修改本地未上传的新增数据，实际上还是未上传的新增数据
                resultBean.setFlag(0);
            } else
                resultBean.setFlag(2);
//            if (roleid.equals("6")) {
//                //外业
//                if (resultBean.getId() != null) {
//                    resultBean.setAuthflag("0");
//                }
//
//            } else if (roleid.equals("2") || roleid.equals("8")) {
//                //质检
//                if (resultBean.getId() != null && !TextUtils.isEmpty(etZjjg.getText())) {
//                    resultBean.setAuthcontent(getTextByView(etZjjg));
//                    resultBean.setAuthflag("1");
//                }
//            }

            tPoiInfoDao.update(resultBean);
        }

        saveAddressAndName();

//        Intent data=new Intent();
//        data.putExtra("obj",resultBean);
//        setResult(Activity.RESULT_OK,data);

        super.submit();
        finish();
    }

    @Override
    protected void onPause() {
        EvevtUpdate evevtUpdate = new EvevtUpdate();
        EventBus.getDefault().post(evevtUpdate);
        super.onPause();
    }

    private void saveAddressAndName() {
        ShowDataUtils.saveAddressOrName("address", getTextByView(etAddress));
        ShowDataUtils.saveAddressOrName("lyname", getTextByView(etLyName));
    }

    /**
     * 打开计算器
     */
    public void openJS() {
        PackageInfo pak = getAllApps(mContext, "Calculator", "calculator"); //大小写  
        if (pak != null) {
            Intent intent = new Intent();
            intent = this.getPackageManager().getLaunchIntentForPackage(pak.packageName);
            startActivity(intent);
        } else {
            Toast.makeText(this, "未找到计算器", Toast.LENGTH_SHORT).show();
        }
    }

    public PackageInfo getAllApps(Context context, String app_flag_1, String app_flag_2) {
        PackageManager pManager = context.getPackageManager();
        // 获取手机内所有应用  
        List<PackageInfo> packlist = pManager.getInstalledPackages(0);
        for (int i = 0; i < packlist.size(); i++) {
            PackageInfo pak = packlist.get(i);
            if (pak.packageName.contains(app_flag_1) || pak.packageName.contains(app_flag_2)) {
                return pak;
            }


        }
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_type0:
                if (lyType.equals(getResources().getStringArray(R.array.building_types)[0])) {
                    setNormalColor(tvType0);
                    lyType = "";
                } else {
                    setselecteFirstTv(tvType0, tvType1, tvType2, tvType3);
                    lyType = getResources().getStringArray(R.array.building_types)[0];
                }
                break;
            case R.id.tv_type1:
                if (lyType.equals(getResources().getStringArray(R.array.building_types)[1])) {
                    setNormalColor(tvType1);
                    lyType = "";
                } else {
                    setselecteFirstTv(tvType1, tvType0, tvType2, tvType3);
                    lyType = getResources().getStringArray(R.array.building_types)[1];
                }
                break;
            case R.id.tv_type2:
                if (lyType.equals(getResources().getStringArray(R.array.building_types)[2])) {
                    setNormalColor(tvType2);
                    lyType = "";
                } else {
                    setselecteFirstTv(tvType2, tvType0, tvType1, tvType3);
                    lyType = getResources().getStringArray(R.array.building_types)[2];
                }
                break;
            case R.id.tv_type3:
                if (lyType.equals(getResources().getStringArray(R.array.building_types)[3])) {
                    setNormalColor(tvType3);
                    lyType = "";
                } else {
                    setselecteFirstTv(tvType3, tvType0, tvType1, tvType2);
                    lyType = getResources().getStringArray(R.array.building_types)[3];
                }
                break;
            case R.id.tv_xz0:
                if (lyXz.equals(getResources().getStringArray(R.array.building_xz)[0])) {
                    setNormalColor(tvXz0);
                    lyXz = "";
                } else {
                    setselecteFirstTv(tvXz0, tvXz1, tvXz2);
                    lyXz = getResources().getStringArray(R.array.building_xz)[0];
                }
                break;
            case R.id.tv_xz1:
                if (lyXz.equals(getResources().getStringArray(R.array.building_xz)[1])) {
                    setNormalColor(tvXz1);
                    lyXz = "";
                } else {
                    setselecteFirstTv(tvXz1, tvXz0, tvXz2);
                    lyXz = getResources().getStringArray(R.array.building_xz)[1];
                }
                break;
            case R.id.tv_xz2:
                if (lyXz.equals(getResources().getStringArray(R.array.building_xz)[2])) {
                    setNormalColor(tvXz2);
                    lyXz = "";
                } else {
                    setselecteFirstTv(tvXz2, tvXz0, tvXz1);
                    lyXz = getResources().getStringArray(R.array.building_xz)[2];
                }
                break;
        }
    }

    private void setNormalColor(TextView tv) {
        tv.setTextColor(getResources().getColor(R.color.black_text));
        tv.setBackgroundResource(R.drawable.poi_tv_bg_normal);
    }

    private void setselecteColor(TextView tv) {
        tv.setTextColor(getResources().getColor(R.color.white));
        tv.setBackgroundResource(R.drawable.poi_tv_bg_selector);
    }

    private void setselecteFirstTv(TextView... tv) {
        if (tv.length == 0) {
            return;
        }
        setselecteColor(tv[0]);
        for (int i = 1; i < tv.length; i++) {
            setNormalColor(tv[i]);
        }
    }

    private void setSelecteTypeTv(String s) {
        if (TextUtils.isEmpty(s)) {
            lyType = "";
            return;
        }

        int selectPosition = getSelectPosition(R.array.building_types, s);
        switch (selectPosition) {
            case 0:
                setselecteFirstTv(tvType0, tvType1, tvType2, tvType3);
                break;
            case 1:
                setselecteFirstTv(tvType1, tvType0, tvType2, tvType3);
                break;
            case 2:
                setselecteFirstTv(tvType2, tvType0, tvType1, tvType3);
                break;
            case 3:
                setselecteFirstTv(tvType3, tvType0, tvType1, tvType2);
                break;

        }

    }

    private void setSelecteLyXzTv(String s) {
        if (TextUtils.isEmpty(s)) {
            lyXz = "";
            return;
        }

        int selectPosition = getSelectPosition(R.array.building_xz, s);
        switch (selectPosition) {
            case 0:
                setselecteFirstTv(tvXz0, tvXz1, tvXz2);
                break;
            case 1:
                setselecteFirstTv(tvXz1, tvXz0, tvXz2);
                break;
            case 2:
                setselecteFirstTv(tvXz2, tvXz0, tvXz1);
                break;
        }
    }

}
