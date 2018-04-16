package com.mapuni.gdydcaiji.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mapuni.gdydcaiji.GdydApplication;
import com.mapuni.gdydcaiji.R;
import com.mapuni.gdydcaiji.bean.EvevtUpdate;
import com.mapuni.gdydcaiji.bean.TbPoint;
import com.mapuni.gdydcaiji.database.greendao.TbPointDao;
import com.mapuni.gdydcaiji.utils.SPUtils;
import com.mapuni.gdydcaiji.utils.ShowDataUtils;
import com.mapuni.gdydcaiji.view.ClearEditText;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yf on 2018/3/21.
 * poi采集
 */

public class PoiDetail extends BaseDetailActivity<TbPoint> {
    @BindView(R.id.tv_title)
    TextView title;
    @BindView(R.id.sp_lyType)
    Spinner spLyType;
    @BindView(R.id.sp_lyxz)
    Spinner spLyxz;
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
    @BindView(R.id.et_bz)
    ClearEditText etBz;

    private TbPointDao tPoiInfoDao;
//    private List<String> mAddArray = new ArrayList<>();
//    private String address;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_poi_detail;
    }

    @Override
    protected void initView() {
        super.initView();

        title.setText("POI点采集");
        tPoiInfoDao = GdydApplication.getInstances().getDaoSession().getTbPointDao();
        setSpinnerData(R.array.building_types, spLyType);
        setSpinnerData(R.array.building_xz, spLyxz);
        setSpinnerData(R.array.building_fl, spLyfl);
        setSpinnerData(R.array.poi_mjdj, spDj);
        spLyfl.setSelection(getResources().getStringArray(R.array.building_fl).length - 1);
        initListPopupWindow();

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
        ivCalculator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openJS();
            }
        });
    }

    @Override
    protected void showData() {
        etLyName.setText(resultBean.getName());
        etAddress.setText(resultBean.getDz());
        spLyType.setSelection(TextUtils.isEmpty(resultBean.getLytype()) ? 0 : getSelectPosition(R.array.building_types, resultBean.getLytype()));
        spLyxz.setSelection(TextUtils.isEmpty(resultBean.getLyxz()) ? 0 : getSelectPosition(R.array.building_xz, resultBean.getLyxz()));
        spLyfl.setSelection(getSelectPosition(R.array.building_fl, resultBean.getFl()));
        spDj.setSelection(TextUtils.isEmpty(resultBean.getDj()) ? 0 : getSelectPosition(R.array.poi_mjdj, resultBean.getDj()));
        etDyh.setText(resultBean.getDy());
        etTele.setText(resultBean.getLxdh());
        etLycs.setText(resultBean.getLycs());
        etLyzhs.setText(resultBean.getLyzhs());
        etBz.setText(resultBean.getNote());
        if (!TextUtils.isEmpty(resultBean.getImg())) {
            photoImg = resultBean.getImg();
        }
        if (roleid.equals("6")) {
            //外业
            if (resultBean.getId() != null && !TextUtils.isEmpty(resultBean.getAuthcontent())) {
                tvZjjgzs.setVisibility(View.VISIBLE);
                tvZjjgzs.setText(resultBean.getAuthcontent());
            }

        } else if (roleid.equals("2")) {
            //质检
            if (resultBean.getId() != null) {
                llZj.setVisibility(View.VISIBLE);
                etZjjg.setText(resultBean.getAuthcontent());
                cover.setVisibility(View.VISIBLE);
            }
        }
        super.showData();
    }

    @Override
    protected void submit() {
        if (resultBean == null) {
            resultBean = new TbPoint();
            resultBean.setLat(lat);
            resultBean.setLng(lng);
            resultBean.setOprator(SPUtils.getInstance().getString("username"));
        }

        resultBean.setName(getTextByView(etLyName));
        resultBean.setLytype(spLyType.getSelectedItemPosition() == 0 ? "" : getResources().getStringArray(R.array.building_types)[spLyType.getSelectedItemPosition()]);
        resultBean.setLyxz(spLyxz.getSelectedItemPosition() == 0 ? "" : getResources().getStringArray(R.array.building_xz)[spLyxz.getSelectedItemPosition()]);
        resultBean.setFl(getResources().getStringArray(R.array.building_fl)[spLyfl.getSelectedItemPosition()]);
        resultBean.setDz(getTextByView(etAddress));
        resultBean.setDy(getTextByView(etDyh));
        resultBean.setLxdh(getTextByView(etTele));
        resultBean.setDj(spDj.getSelectedItemPosition() == 0 ? "" : getResources().getStringArray(R.array.poi_mjdj)[spDj.getSelectedItemPosition()]);
        resultBean.setLycs(getTextByView(etLycs));
        resultBean.setLyzhs(getTextByView(etLyzhs));
        resultBean.setNote(getTextByView(etBz));
        resultBean.setImg(getPhotoImg());

        resultBean.setOpttime(new Date(System.currentTimeMillis()));
        resultBean.setFlag(0);

        if (isInsert)
            tPoiInfoDao.insert(resultBean);
        else {
            if (roleid.equals("6")) {
                //外业
                if (resultBean.getId() != null) {
                    resultBean.setAuthflag("0");
                }

            } else if (roleid.equals("2")) {
                //质检
                if (resultBean.getId() != null && !TextUtils.isEmpty(etZjjg.getText())) {
                    resultBean.setAuthcontent(getTextByView(etZjjg));
                    resultBean.setAuthflag("1");
                }
            }

            tPoiInfoDao.update(resultBean);
        }

        saveAddressAndName();

//        Intent data=new Intent();
//        data.putExtra("obj",resultBean);
//        setResult(Activity.RESULT_OK,data);
        EvevtUpdate evevtUpdate = new EvevtUpdate();
        EventBus.getDefault().post(evevtUpdate);
        finish();
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

}
