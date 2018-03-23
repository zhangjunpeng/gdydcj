package com.mapuni.gdydcaiji.activity;

import android.util.Base64;
import android.widget.Spinner;
import android.widget.TextView;

import com.mapuni.gdydcaiji.GdydApplication;
import com.mapuni.gdydcaiji.R;
import com.mapuni.gdydcaiji.bean.TVillageInfo;
import com.mapuni.gdydcaiji.database.greendao.TVillageInfoDao;
import com.mapuni.gdydcaiji.view.ClearEditText;

import java.util.Date;

import butterknife.BindView;

/**
 * Created by yf on 2018/3/21.
 * 行政村、自然村采集
 */

public class VillageDetail extends BaseDetailActivity<TVillageInfo> {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.et_Name)
    ClearEditText etName;
    @BindView(R.id.et_address)
    ClearEditText etAddress;
    @BindView(R.id.sp_fl)
    Spinner spFl;
    private TVillageInfoDao tVillageInfoDao;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_village_detail;
    }

    @Override
    protected void initView() {
        super.initView();
        title.setText("行政村、自然村采集");
        setSpinnerData(R.array.village_type, spFl);
        tVillageInfoDao = GdydApplication.getInstances().getDaoSession().getTVillageInfoDao();
    }

    @Override
    protected void showData() {
        etName.setText(resultBean.getName());
        etAddress.setText(resultBean.getDz());
        spFl.setSelection(getSelectPosition(R.array.village_type, resultBean.getType()));
        imgUrl = Base64.decode(resultBean.getImg(), Base64.DEFAULT);
        super.showData();
    }

    @Override
    protected void submit() {
        if (resultBean == null) {
            resultBean = new TVillageInfo();
            resultBean.setLat(lat);
            resultBean.setLng(lng);
        }

        resultBean.setName(getTextByView(etName));
        resultBean.setDz(getTextByView(etAddress));
        if (imgUrl != null && imgUrl.length > 0) {
            resultBean.setImg(Base64.encodeToString(imgUrl, Base64.DEFAULT));
        }
        resultBean.setOpttime(new Date(System.currentTimeMillis()));
        resultBean.setFlag(0);

        if (isInsert)
            tVillageInfoDao.insert(resultBean);
        else
            tVillageInfoDao.update(resultBean);


    }

}
