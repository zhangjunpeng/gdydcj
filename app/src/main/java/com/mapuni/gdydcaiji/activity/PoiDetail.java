package com.mapuni.gdydcaiji.activity;

import android.app.Activity;
import android.content.Intent;
import android.util.Base64;
import android.widget.Spinner;
import android.widget.TextView;

import com.mapuni.gdydcaiji.GdydApplication;
import com.mapuni.gdydcaiji.R;
import com.mapuni.gdydcaiji.bean.TPoiInfo;
import com.mapuni.gdydcaiji.database.greendao.TPoiInfoDao;
import com.mapuni.gdydcaiji.view.ClearEditText;

import java.util.Date;

import butterknife.BindView;

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
    @BindView(R.id.et_fifAd)
    ClearEditText etFifAd;
    @BindView(R.id.et_sixAd)
    ClearEditText etSixAd;
    @BindView(R.id.et_sevenAd)
    ClearEditText etSevenAd;
    @BindView(R.id.sp_fl)
    Spinner spFl;
    @BindView(R.id.et_mjdj)
    ClearEditText etMjdj;
    @BindView(R.id.et_sslymc)
    ClearEditText etSslymc;
    private TPoiInfoDao tPoiInfoDao;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_poi_detail;
    }

    @Override
    protected void initView() {
        super.initView();
        title.setText("POI点采集");
        tPoiInfoDao = GdydApplication.getInstances().getDaoSession().getTPoiInfoDao();
        setSpinnerData(R.array.building_fl, spFl);
    }

    @Override
    protected void showData() {
        etPoiName.setText(resultBean.getName());
        etForthAd.setText(resultBean.getForthad());
        etFifAd.setText(resultBean.getFifad());
        etSixAd.setText(resultBean.getSixad());
        etSevenAd.setText(resultBean.getSevenad());
        spFl.setSelection(getSelectPosition(R.array.building_fl, resultBean.getFl()));
        etMjdj.setText(resultBean.getMjdj());
        etSslymc.setText(resultBean.getSslymc());
        imgUrl = Base64.decode(resultBean.getImg(), Base64.DEFAULT);
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
        resultBean.setFifad(getTextByView(etFifAd));
        resultBean.setSixad(getTextByView(etSixAd));
        resultBean.setSevenad(getTextByView(etSevenAd));
        resultBean.setFl(getResources().getStringArray(R.array.building_fl)[spFl.getSelectedItemPosition()]);
        resultBean.setMjdj(getTextByView(etMjdj));
        resultBean.setSslymc(getTextByView(etSslymc));
        if (imgUrl != null && imgUrl.length > 0) {
            resultBean.setImg(Base64.encodeToString(imgUrl, Base64.DEFAULT));
        }
        resultBean.setOpttime(new Date(System.currentTimeMillis()));
        resultBean.setFlag(0);

        if (isInsert)
            tPoiInfoDao.insert(resultBean);
        else
            tPoiInfoDao.update(resultBean);

        Intent data=new Intent();
        data.putExtra("obj",resultBean);
        setResult(Activity.RESULT_OK,data);
        finish();
    }

}
