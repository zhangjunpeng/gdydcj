package com.mapuni.gdydcaiji.activity;

import android.text.TextUtils;
import android.util.Base64;
import android.widget.TextView;

import com.mapuni.gdydcaiji.GdydApplication;
import com.mapuni.gdydcaiji.R;
import com.mapuni.gdydcaiji.bean.EvevtUpdate;
import com.mapuni.gdydcaiji.bean.TbLine;
import com.mapuni.gdydcaiji.database.greendao.TbLineDao;
import com.mapuni.gdydcaiji.utils.SPUtils;
import com.mapuni.gdydcaiji.view.ClearEditText;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;

import butterknife.BindView;

/**
 * Created by yf on 2018/4/2.
 */

public class LineDetail extends BaseDetailActivity<TbLine> {
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.et_name)
    ClearEditText etName;
    @BindView(R.id.et_qd)
    ClearEditText etQd;
    @BindView(R.id.et_zd)
    ClearEditText etZd;
    @BindView(R.id.et_bz)
    ClearEditText etBz;
    private TbLineDao tbLineDao;
    private String bj;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_line_detail;
    }

    @Override
    protected void initView() {
        super.initView();
        title.setText("线采集");
        tbLineDao = GdydApplication.getInstances().getDaoSession().getTbLineDao();
        bj = getIntent().getStringExtra("bj");
    }

    @Override
    protected void showData() {
        etName.setText(resultBean.getName());
        etQd.setText(resultBean.getSfz());
        etZd.setText(resultBean.getZdz());
        etBz.setText(resultBean.getNote());
        if (!TextUtils.isEmpty(resultBean.getImg())) {
            imgUrl = Base64.decode(resultBean.getImg(), Base64.DEFAULT);
        }
        super.showData();
    }

    @Override
    protected void submit() {
        if (resultBean == null) {
            resultBean = new TbLine();
            resultBean.setPolyarrays(bj);
        }

        resultBean.setName(getTextByView(etName));
        resultBean.setSfz(getTextByView(etQd));
        resultBean.setZdz(getTextByView(etZd));
        resultBean.setNote(getTextByView(etBz));
        if (imgUrl != null && imgUrl.length > 0) {
            resultBean.setImg(Base64.encodeToString(imgUrl, Base64.DEFAULT));
        }
        resultBean.setOprator(SPUtils.getInstance().getString("username", ""));
        resultBean.setOpttime(new Date(System.currentTimeMillis()));
        resultBean.setFlag(0);

        if (isInsert)
            tbLineDao.insert(resultBean);
        else
            tbLineDao.update(resultBean);

        EvevtUpdate evevtUpdate = new EvevtUpdate();
        EventBus.getDefault().post(evevtUpdate);
        finish();

    }

}