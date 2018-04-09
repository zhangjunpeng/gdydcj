package com.mapuni.gdydcaiji.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;

import com.mapuni.gdydcaiji.GdydApplication;
import com.mapuni.gdydcaiji.R;
import com.mapuni.gdydcaiji.bean.EvevtUpdate;
import com.mapuni.gdydcaiji.bean.TbSurface;
import com.mapuni.gdydcaiji.database.greendao.TbSurfaceDao;
import com.mapuni.gdydcaiji.utils.SPUtils;
import com.mapuni.gdydcaiji.utils.ShowDataUtils;
import com.mapuni.gdydcaiji.view.ClearEditText;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;
import java.util.List;

import butterknife.BindView;

/**
 * Created by yf on 2018/3/21.
 * 小区、学校、医院采集
 */

public class SocialDetail extends BaseDetailActivity<TbSurface> {
    @BindView(R.id.tv_title)
    TextView title;
    @BindView(R.id.et_name)
    AutoCompleteTextView etName;
    @BindView(R.id.et_address)
    AutoCompleteTextView etAddress;
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
        title.setText("面采集");
        tbSurfaceDao = GdydApplication.getInstances().getDaoSession().getTbSurfaceDao();
        setSpinnerData(R.array.social_type, spFl);

        bj = getIntent().getStringExtra("bj");
        initListPopupWindow();
    }

    private void initListPopupWindow() {
        List<String> mAddArray = ShowDataUtils.getAddressOrNameArray("address");
        ArrayAdapter<String> lpwAdapter = new ArrayAdapter<>(this, R.layout.item_listpopupwindow, mAddArray);
        lpwAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        etAddress.setAdapter(lpwAdapter);

        List<String> mNameArray = ShowDataUtils.getAddressOrNameArray("lyname");
        ArrayAdapter<String> lpwAdapter2 = new ArrayAdapter<>(this, R.layout.item_listpopupwindow, mNameArray);
        lpwAdapter2.setDropDownViewResource(R.layout.item_spinner_dropdown);
        etName.setAdapter(lpwAdapter2);
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
        etAddress.setText(resultBean.getXqdz());
        spFl.setSelection(Integer.parseInt(resultBean.getFl()));
        etWyxx.setText(resultBean.getWyxx());
        etLxdh.setText(resultBean.getLxdh());
        etLds.setText(resultBean.getLds());
        etBz.setText(resultBean.getNote());
        if (!TextUtils.isEmpty(resultBean.getImg())) {
            imgUrl = resultBean.getImg();
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
        if (!TextUtils.isEmpty(imgUrl)) {
            resultBean.setImg(imgUrl);
        }
        resultBean.setOprator(SPUtils.getInstance().getInt("userId", -1) + "");
        resultBean.setOpttime(new Date(System.currentTimeMillis()));
        resultBean.setFlag(0);


        if (isInsert)
            tbSurfaceDao.insert(resultBean);
        else
            tbSurfaceDao.update(resultBean);

        saveAddressAndName();
        EvevtUpdate evevtUpdate = new EvevtUpdate();
        EventBus.getDefault().post(evevtUpdate);
        finish();
    }

    private void saveAddressAndName() {
        ShowDataUtils.saveAddressOrName("address", getTextByView(etAddress));
        ShowDataUtils.saveAddressOrName("lyname", getTextByView(etName));
    }
}
