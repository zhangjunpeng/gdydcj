package com.mapuni.gdydcaiji.activity;

import android.text.TextUtils;
import android.util.Base64;
import android.widget.Spinner;
import android.widget.TextView;

import com.mapuni.gdydcaiji.GdydApplication;
import com.mapuni.gdydcaiji.R;
import com.mapuni.gdydcaiji.bean.EventPloyonChange;
import com.mapuni.gdydcaiji.bean.TbSurface;
import com.mapuni.gdydcaiji.database.greendao.TbSurfaceDao;
import com.mapuni.gdydcaiji.utils.SPUtils;
import com.mapuni.gdydcaiji.view.ClearEditText;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;

import butterknife.BindView;

/**
 * Created by yf on 2018/3/21.
 * 小区、学校、医院采集
 */

public class SocialDetail extends BaseDetailActivity<TbSurface> {
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.et_name)
    ClearEditText etName;
    @BindView(R.id.et_address)
    ClearEditText etAddress;
    @BindView(R.id.sp_fl)
    Spinner spFl;
    @BindView(R.id.et_wyxx)
    ClearEditText etWyxx;
    @BindView(R.id.et_lxdh)
    ClearEditText etLxdh;
    @BindView(R.id.et_lds)
    ClearEditText etLds;
    @BindView(R.id.et_bz)
    ClearEditText etBz;

    private TbSurfaceDao tbSurfaceDao;

    private String bj;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_social_detail;
    }

    @Override
    protected void initView() {
        super.initView();
        title.setText("小区、学校、医院采集");
        tbSurfaceDao = GdydApplication.getInstances().getDaoSession().getTbSurfaceDao();
        setSpinnerData(R.array.social_type, spFl);

        bj = getIntent().getStringExtra("bj");
    }

    @Override
    protected void showData() {
        etName.setText(resultBean.getName());
        etAddress.setText(resultBean.getXqdz());
        spFl.setSelection(Integer.parseInt(resultBean.getFl()));
        etWyxx.setText(resultBean.getWyxx());
        etLxdh.setText(resultBean.getLxdh());
        etLds.setText(resultBean.getLds());
        etBz.setText(resultBean.getNote());
        if (!TextUtils.isEmpty(resultBean.getImg())) {
            imgUrl = Base64.decode(resultBean.getImg(), Base64.DEFAULT);
        }
        super.showData();
    }

    @Override
    protected void submit() {
        if (resultBean == null) {
            resultBean = new TbSurface();
//            resultBean.setLat(lat);
//            resultBean.setLng(lng);
            resultBean.setPolyarrays(bj);
        }

        resultBean.setName(getTextByView(etName));
        resultBean.setXqdz(getTextByView(etAddress));
        resultBean.setFl(spFl.getSelectedItemPosition() + "");
        resultBean.setWyxx(getTextByView(etWyxx));
        resultBean.setLxdh(getTextByView(etLxdh));
        resultBean.setLds(getTextByView(etLds));
        resultBean.setNote(getTextByView(etBz));
        if (imgUrl != null && imgUrl.length > 0) {
            resultBean.setImg(Base64.encodeToString(imgUrl, Base64.DEFAULT));
        }
        resultBean.setOprator(SPUtils.getInstance().getString("username", ""));
        resultBean.setOpttime(new Date(System.currentTimeMillis()));
        resultBean.setFlag(0);


        if (isInsert)
            tbSurfaceDao.insert(resultBean);
        else
            tbSurfaceDao.update(resultBean);

//        Intent data=new Intent();
//        data.putExtra("obj",resultBean);
//        setResult(Activity.RESULT_OK,data);
        EventPloyonChange eventPloyonChange = new EventPloyonChange();
        EventBus.getDefault().post(eventPloyonChange);
        finish();
    }

}
