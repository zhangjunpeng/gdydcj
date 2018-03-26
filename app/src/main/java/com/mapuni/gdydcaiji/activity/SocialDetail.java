package com.mapuni.gdydcaiji.activity;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.Spinner;
import android.widget.TextView;

import com.mapuni.gdydcaiji.GdydApplication;
import com.mapuni.gdydcaiji.R;
import com.mapuni.gdydcaiji.bean.EvevtUpdate;
import com.mapuni.gdydcaiji.bean.TSocialInfo;
import com.mapuni.gdydcaiji.database.greendao.TSocialInfoDao;
import com.mapuni.gdydcaiji.view.ClearEditText;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;

import butterknife.BindView;

/**
 * Created by yf on 2018/3/21.
 * 小区、学校、医院采集
 */

public class SocialDetail extends BaseDetailActivity<TSocialInfo> {
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.et_name)
    ClearEditText etName;
    @BindView(R.id.et_forthAd)
    ClearEditText etForthAd;
    @BindView(R.id.et_fifAd)
    ClearEditText etFifAd;
    @BindView(R.id.et_address)
    ClearEditText etAddress;
    @BindView(R.id.sp_fl)
    Spinner spFl;
    @BindView(R.id.et_wyxx)
    ClearEditText etWyxx;
    @BindView(R.id.et_lxdh)
    ClearEditText etLxdh;
    private TSocialInfoDao tSocialInfoDao;

    private String bj;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_social_detail;
    }

    @Override
    protected void initView() {
        super.initView();
        title.setText("小区、学校、医院采集");
        tSocialInfoDao = GdydApplication.getInstances().getDaoSession().getTSocialInfoDao();
        setSpinnerData(R.array.social_type, spFl);

        bj = getIntent().getStringExtra("bj");
    }

    @Override
    protected void showData() {
        etName.setText(resultBean.getName());
        etForthAd.setText(resultBean.getForthad());
        etFifAd.setText(resultBean.getFifad());
        etAddress.setText(resultBean.getXqdz());
        spFl.setSelection(Integer.parseInt(resultBean.getType()));
        etWyxx.setText(resultBean.getWyxx());
        etLxdh.setText(resultBean.getLxdh());
        if (!TextUtils.isEmpty(resultBean.getImg())) {
            imgUrl = Base64.decode(resultBean.getImg(), Base64.DEFAULT);
        }
        super.showData();
    }

    @Override
    protected void submit() {
        if (resultBean == null) {
            resultBean = new TSocialInfo();
            resultBean.setLat(lat);
            resultBean.setLng(lng);
            resultBean.setBj(bj);
        }

        resultBean.setName(getTextByView(etName));
        resultBean.setForthad(getTextByView(etForthAd));
        resultBean.setFifad(getTextByView(etFifAd));
        resultBean.setXqdz(getTextByView(etAddress));
        resultBean.setType(spFl.getSelectedItemPosition() + "");
        resultBean.setWyxx(getTextByView(etWyxx));
        resultBean.setLxdh(getTextByView(etLxdh));
        if (imgUrl != null && imgUrl.length > 0) {
            resultBean.setImg(Base64.encodeToString(imgUrl, Base64.DEFAULT));
        }
        resultBean.setOpttime(new Date(System.currentTimeMillis()));
        resultBean.setFlag(0);
        

        if (isInsert)
            tSocialInfoDao.insert(resultBean);
        else
            tSocialInfoDao.update(resultBean);

//        Intent data=new Intent();
//        data.putExtra("obj",resultBean);
//        setResult(Activity.RESULT_OK,data);
        EvevtUpdate evevtUpdate=new EvevtUpdate();
        EventBus.getDefault().post(evevtUpdate);
        finish();
    }

}
