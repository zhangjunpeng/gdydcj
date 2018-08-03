package com.mapuni.gdydcaiji.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.mapuni.gdydcaiji.GdydApplication;
import com.mapuni.gdydcaiji.R;
import com.mapuni.gdydcaiji.bean.EvevtUpdate;
import com.mapuni.gdydcaiji.bean.TbLine;
import com.mapuni.gdydcaiji.bean.TbPoint;
import com.mapuni.gdydcaiji.database.greendao.TbLineDao;
import com.mapuni.gdydcaiji.database.greendao.TbPointDao;
import com.mapuni.gdydcaiji.utils.SPUtils;
import com.mapuni.gdydcaiji.utils.ShowDataUtils;
import com.mapuni.gdydcaiji.view.ClearEditText;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;
import java.util.List;

import butterknife.BindView;

/**
 * Created by yf on 2018/4/2.
 */

public class LineDetail extends BaseDetailActivity<TbLine> {
    @BindView(R.id.tv_title)
    TextView title;
    @BindView(R.id.et_name)
    AutoCompleteTextView etName;
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
        initListPopupWindow();
    }

    protected void initData() {
        long bm = getIntent().getLongExtra("resultBm", -1);
        List<TbLine> list = tbLineDao.queryBuilder().where(TbLineDao.Properties.Bm.eq(bm)).list();
        if (!list.isEmpty()) {
            resultBean = list.get(0);
        }
        super.initData();
    }

    private void initListPopupWindow() {

        List<String> mNameArray = ShowDataUtils.getAddressOrNameArray("lyname");
        ArrayAdapter<String> lpwAdapter = new ArrayAdapter<>(this, R.layout.item_listpopupwindow, mNameArray);
        lpwAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        etName.setAdapter(lpwAdapter);
    }

    @Override
    protected void initListener() {
        super.initListener();
        etName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                AutoCompleteTextView view = (AutoCompleteTextView) v;
                view.showDropDown();
            }
        });
    }

    @Override
    protected void showData() {
        etName.setText(resultBean.getName());
        etQd.setText(resultBean.getSfz());
        etZd.setText(resultBean.getZdz());
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

        } else if (roleid.equals("2") || roleid.equals("8")) {
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
            resultBean = new TbLine();
            resultBean.setPolyarrays(bj);
            if (TextUtils.isEmpty(resultBean.getOprator())) {
                resultBean.setOprator(SPUtils.getInstance().getString("username"));
            }

        }

        resultBean.setName(getTextByView(etName));
        resultBean.setSfz(getTextByView(etQd));
        resultBean.setZdz(getTextByView(etZd));
        resultBean.setNote(getTextByView(etBz));
        resultBean.setImg(getPhotoImg());

        resultBean.setOpttime(new Date(System.currentTimeMillis()));

        if (isInsert) {
            resultBean.setFlag(0);
            tbLineDao.insert(resultBean);
        } else {
            if (resultBean.getFlag() == 0 && resultBean.getId() == null) {
                //修改本地未上传的新增数据，实际上还是未上传的新增数据
                resultBean.setFlag(0);
            } else
                resultBean.setFlag(2);
            if (roleid.equals("6")) {
                //外业
                if (resultBean.getId() != null) {
                    resultBean.setAuthflag("0");
                }

            } else if (roleid.equals("2") || roleid.equals("8")) {
                //质检
                if (resultBean.getId() != null && !TextUtils.isEmpty(etZjjg.getText())) {
                    resultBean.setAuthcontent(getTextByView(etZjjg));
                    resultBean.setAuthflag("1");
                }
            }
            tbLineDao.update(resultBean);
        }

        //保存名称
        ShowDataUtils.saveAddressOrName("lyname", getTextByView(etName));
        EvevtUpdate evevtUpdate = new EvevtUpdate();
        EventBus.getDefault().post(evevtUpdate);
        super.submit();
        finish();

    }

}
