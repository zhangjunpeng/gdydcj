package com.mapuni.gdydcaiji.activity;

import android.app.Activity;
import android.content.Intent;
import android.util.Base64;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.mapuni.gdydcaiji.GdydApplication;
import com.mapuni.gdydcaiji.R;
import com.mapuni.gdydcaiji.bean.TBuildingInfo;
import com.mapuni.gdydcaiji.database.greendao.TBuildingInfoDao;
import com.mapuni.gdydcaiji.utils.ToastUtils;
import com.mapuni.gdydcaiji.view.ClearEditText;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.BindView;

/**
 * Created by yf on 2018/3/21.
 * 楼宇信息采集
 */

public class BuildingDetail extends BaseDetailActivity<TBuildingInfo> {
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.sp_lyType)
    Spinner spLyType;
    @BindView(R.id.sp_lyxz)
    Spinner spLyxz;
    @BindView(R.id.sp_lyfl)
    Spinner spLyfl;
    @BindView(R.id.et_lyName)
    ClearEditText etLyName;
    @BindView(R.id.et_forthAd)
    ClearEditText etForthAd;
    @BindView(R.id.et_fifAd)
    ClearEditText etFifAd;
    @BindView(R.id.et_sixAd)
    ClearEditText etSixAd;
    @BindView(R.id.et_lycs)
    ClearEditText etLycs;
    @BindView(R.id.et_lyzhs)
    ClearEditText etLyzhs;
    private TBuildingInfoDao tBuildingInfoDao;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_building_detail;
    }

    @Override
    protected void initView() {
        super.initView();
        title.setText("楼宇信息点采集");
        setSpinnerData(R.array.building_types, spLyType);
        setSpinnerData(R.array.building_xz, spLyxz);
//        setSpinnerData(R.array.building_fl, spLyfl);
        List<String> mItems = Arrays.asList(getResources().getStringArray(R.array.building_fl));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_spinner, R.id.tv_type, mItems.subList(0, mItems.size() - 1));
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spLyfl.setAdapter(adapter);
        tBuildingInfoDao = GdydApplication.getInstances().getDaoSession().getTBuildingInfoDao();
    }

    @Override
    protected void showData() {
        spLyType.setSelection(getSelectPosition(R.array.building_types, resultBean.getLytype()));
        spLyxz.setSelection(getSelectPosition(R.array.building_xz, resultBean.getLyxz()));
        spLyfl.setSelection(getSelectPosition(R.array.building_fl, resultBean.getLyfl()));
        etLyName.setText(resultBean.getName());
        etForthAd.setText(resultBean.getForthad());
        etFifAd.setText(resultBean.getFifad());
        etSixAd.setText(resultBean.getSixad());
        etLycs.setText(resultBean.getLycs());
        etLyzhs.setText(resultBean.getLyzhs());
        imgUrl = Base64.decode(resultBean.getImg(), Base64.DEFAULT);
        super.showData();
    }

    @Override
    protected void submit() {

        if (resultBean == null) {
            resultBean = new TBuildingInfo();
            resultBean.setLat(lat);
            resultBean.setLng(lng);
        }

        resultBean.setLytype(getResources().getStringArray(R.array.building_types)[spLyType.getSelectedItemPosition()]);
        resultBean.setLyxz(getResources().getStringArray(R.array.building_xz)[spLyxz.getSelectedItemPosition()]);
        resultBean.setLyfl(getResources().getStringArray(R.array.building_fl)[spLyfl.getSelectedItemPosition()]);
        resultBean.setName(getTextByView(etLyName));
        resultBean.setForthad(getTextByView(etForthAd));
        resultBean.setFifad(getTextByView(etFifAd));
        resultBean.setSixad(getTextByView(etSixAd));
        resultBean.setLycs(getTextByView(etLycs));
        resultBean.setLyzhs(getTextByView(etLyzhs));
        if (imgUrl != null && imgUrl.length > 0) {
            resultBean.setImg(Base64.encodeToString(imgUrl, Base64.DEFAULT));
        }
        resultBean.setOpttime(new Date(System.currentTimeMillis()));
        resultBean.setFlag(0);

        if (isInsert)
            tBuildingInfoDao.insert(resultBean);
        else
            tBuildingInfoDao.update(resultBean);

        Intent data=new Intent();
        data.putExtra("obj",resultBean);

        setResult(Activity.RESULT_OK,data);
        finish();

    }

}
