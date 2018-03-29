package com.mapuni.gdydcaiji.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.ListPopupWindow;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.mapuni.gdydcaiji.GdydApplication;
import com.mapuni.gdydcaiji.R;
import com.mapuni.gdydcaiji.bean.EvevtUpdate;
import com.mapuni.gdydcaiji.bean.TPoiInfo;
import com.mapuni.gdydcaiji.database.greendao.TPoiInfoDao;
import com.mapuni.gdydcaiji.utils.SPUtils;
import com.mapuni.gdydcaiji.view.ClearEditText;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yf on 2018/3/21.
 * poi采集
 */

public class PoiDetail extends BaseDetailActivity<TPoiInfo> {
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.et_poiName)
    ClearEditText etPoiName;
    @BindView(R.id.et_forthAd)
    ClearEditText etForthAd;
    //    @BindView(R.id.et_fifAd)
//    ClearEditText etFifAd;
//    @BindView(R.id.et_sixAd)
//    ClearEditText etSixAd;
//    @BindView(R.id.et_sevenAd)
//    ClearEditText etSevenAd;
    @BindView(R.id.sp_fl)
    Spinner spFl;
    @BindView(R.id.sp_mjdj)
    Spinner spMjdj;
    @BindView(R.id.et_sslymc)
    ClearEditText etSslymc;
    @BindView(R.id.sp_isly)
    Spinner spIsly;
    @BindView(R.id.ll_poi)
    LinearLayoutCompat llPoi;
    @BindView(R.id.sp_lyType)
    Spinner spLyType;
    @BindView(R.id.sp_lyxz)
    Spinner spLyxz;
    @BindView(R.id.sp_lyfl)
    Spinner spLyfl;
    @BindView(R.id.et_tele)
    ClearEditText etTele;
    @BindView(R.id.et_lycs)
    ClearEditText etLycs;
    @BindView(R.id.et_lyzhs)
    ClearEditText etLyzhs;
    @BindView(R.id.ll_ly)
    LinearLayoutCompat llLy;
    private TPoiInfoDao tPoiInfoDao;
    private ListPopupWindow mPopup;
    private List<String> mAddArray = new ArrayList<>();
    private String address;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_poi_detail;
    }

    @Override
    protected void initView() {
        super.initView();
        title.setText("POI点采集");
        etPoiName.requestFocus();
        tPoiInfoDao = GdydApplication.getInstances().getDaoSession().getTPoiInfoDao();
        setSpinnerData(R.array.choose, spIsly);
        setSpinnerData(R.array.building_fl, spFl);
        setSpinnerData(R.array.building_types, spLyType);
        setSpinnerData(R.array.building_xz, spLyxz);
        setSpinnerData(R.array.poi_mjdj, spMjdj);
//        setSpinnerData(R.array.building_fl, spLyfl);
        List<String> mItems = Arrays.asList(getResources().getStringArray(R.array.building_fl));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_spinner, R.id.tv_type, mItems.subList(0, mItems.size() - 1));
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spLyfl.setAdapter(adapter);

        initListPopupWindow();

    }

    private void initListPopupWindow() {
        address = SPUtils.getInstance().getString("address");
        if (TextUtils.isEmpty(address)) {
            return;
        }
        mAddArray = new ArrayList<>(Arrays.asList(address.split(";")));
        mPopup = new ListPopupWindow(this);
        ArrayAdapter<String> lpwAdapter = new ArrayAdapter<>(this, R.layout.item_listpopupwindow, mAddArray);
        mPopup.setAdapter(lpwAdapter);
        mPopup.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopup.setModal(true);
        mPopup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                etForthAd.setText(mAddArray.get(position));
                etForthAd.setSelection(etForthAd.getText().length());
                mPopup.dismiss();
            }
        });
    }

    @Override
    protected void initListener() {
        super.initListener();
        spIsly.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        //不是楼宇
                        llLy.setVisibility(View.GONE);
                        llPoi.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        llLy.setVisibility(View.VISIBLE);
                        llPoi.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        etForthAd.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!TextUtils.isEmpty(address)) {
                    mPopup.setDropDownGravity(Gravity.START);
                    mPopup.setAnchorView(etForthAd);
                    mPopup.show();
                }
                return false;
            }

        });
    }

    @Override
    protected void showData() {
        etPoiName.setText(resultBean.getName());
//        spIsly.setSelection(resultBean.get);
        etForthAd.setText(resultBean.getForthad());
        spFl.setSelection(getSelectPosition(R.array.building_fl, resultBean.getFl()));
//        etMjdj.setText(resultBean.getMjdj());
        spMjdj.setSelection(getSelectPosition(R.array.poi_mjdj, resultBean.getMjdj()));
        etSslymc.setText(resultBean.getSslymc());
        if (!TextUtils.isEmpty(resultBean.getImg())) {
            imgUrl = Base64.decode(resultBean.getImg(), Base64.DEFAULT);
        }
        super.showData();
    }

    @Override
    protected void submit() {
        if (resultBean == null) {
            resultBean = new TPoiInfo();
            resultBean.setLat(lat);
            resultBean.setLng(lng);
        }

        resultBean.setName(getTextByView(etPoiName));
        resultBean.setForthad(getTextByView(etForthAd));
        resultBean.setFl(getResources().getStringArray(R.array.building_fl)[spFl.getSelectedItemPosition()]);
        resultBean.setMjdj(getResources().getStringArray(R.array.poi_mjdj)[spMjdj.getSelectedItemPosition()]);
        resultBean.setSslymc(getTextByView(etSslymc));
        if (imgUrl != null && imgUrl.length > 0) {
            resultBean.setImg(Base64.encodeToString(imgUrl, Base64.DEFAULT));
        }
        resultBean.setOptuser(SPUtils.getInstance().getString("username", ""));
        resultBean.setOpttime(new Date(System.currentTimeMillis()));
        resultBean.setFlag(0);

        if (isInsert)
            tPoiInfoDao.insert(resultBean);
        else
            tPoiInfoDao.update(resultBean);

        saveAddress();

//        Intent data=new Intent();
//        data.putExtra("obj",resultBean);
//        setResult(Activity.RESULT_OK,data);
        EvevtUpdate evevtUpdate = new EvevtUpdate();
        EventBus.getDefault().post(evevtUpdate);
        finish();
    }

    private void saveAddress() {

        String etAddressStr = getTextByView(etForthAd);
        if (TextUtils.isEmpty(etAddressStr)) {
            return;
        }

        if (address.contains(etAddressStr)) {
            mAddArray.remove(etAddressStr);
        } else if (mAddArray.size() >= 10) {
            mAddArray.remove(9);
        }
        mAddArray.add(0, etAddressStr);

        String spAddress = "";
        for (int i = 0; i < mAddArray.size(); i++) {
            spAddress += mAddArray.get(i) + ";";
        }

        SPUtils.getInstance().put("address", spAddress.substring(0, spAddress.length() - 1));
    }

}
