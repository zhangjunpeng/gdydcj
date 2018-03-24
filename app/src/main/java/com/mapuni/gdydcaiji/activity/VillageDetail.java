package com.mapuni.gdydcaiji.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.mapuni.gdydcaiji.GdydApplication;
import com.mapuni.gdydcaiji.R;
import com.mapuni.gdydcaiji.bean.EventBJ;
import com.mapuni.gdydcaiji.bean.EventBean;
import com.mapuni.gdydcaiji.bean.TVillageInfo;
import com.mapuni.gdydcaiji.database.greendao.TVillageInfoDao;
import com.mapuni.gdydcaiji.view.ClearEditText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

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
    @BindView(R.id.ll_bjcaiji)
    LinearLayout llBjcaiji;
    @BindView(R.id.tv_collect_status)
    TextView tvCollectStatus;
    private TVillageInfoDao tVillageInfoDao;

    int requesCode = 1001;

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
    protected void initListener() {
        super.initListener();
        spFl.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        //自然村
                        llBjcaiji.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        //行政村
                        llBjcaiji.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        llBjcaiji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VillageDetail.this, VillageBorderActivity.class);
                if (resultBean != null) {
                    intent.putExtra("bj", resultBean.getZrcbj());
                }
                startActivity(intent);
            }
        });
    }

    @Subscribe
    public void update(EventBJ eventBJ) {
        Log.i("Eventbus", "update");
        String bj = eventBJ.beanStr;
        if (resultBean == null) {
            resultBean = new TVillageInfo();
            resultBean.setZrcbj(bj);
        }
        tvCollectStatus.setText("已采集");

    }

    @Override
    protected void showData() {
        etName.setText(resultBean.getName());
        etAddress.setText(resultBean.getDz());
        spFl.setSelection(Integer.parseInt(resultBean.getType()));
        if (TextUtils.isEmpty(resultBean.getZrcbj())) {
            tvCollectStatus.setText("未采集");
        } else {
            tvCollectStatus.setText("已采集");
        }

        if (!TextUtils.isEmpty(resultBean.getImg())) {
            imgUrl = Base64.decode(resultBean.getImg(), Base64.DEFAULT);
        }
        super.showData();
    }

    @Override
    protected void submit() {
        if (resultBean == null) {
            resultBean = new TVillageInfo();
        }
        resultBean.setLat(lat);
        resultBean.setLng(lng);
        resultBean.setName(getTextByView(etName));
        resultBean.setDz(getTextByView(etAddress));
        resultBean.setType(spFl.getSelectedItemPosition() + "");
        if (imgUrl != null && imgUrl.length > 0) {
            resultBean.setImg(Base64.encodeToString(imgUrl, Base64.DEFAULT));
        }
        if (spFl.getSelectedItemPosition() == 1) {
            resultBean.setZrcbj("");
        }
        resultBean.setOpttime(new Date(System.currentTimeMillis()));
        resultBean.setFlag(0);

        if (isInsert)
            tVillageInfoDao.insert(resultBean);
        else
            tVillageInfoDao.update(resultBean);
        Intent data = new Intent();
        data.putExtra("obj", resultBean);
        setResult(Activity.RESULT_OK, data);
        finish();

    }

}
