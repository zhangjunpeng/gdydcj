package com.mapuni.gdydcaiji.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mapuni.gdydcaiji.GdydApplication;
import com.mapuni.gdydcaiji.R;
import com.mapuni.gdydcaiji.bean.EvevtUpdate;
import com.mapuni.gdydcaiji.bean.TBuildingInfo;
import com.mapuni.gdydcaiji.database.greendao.TBuildingInfoDao;
import com.mapuni.gdydcaiji.utils.SPUtils;
import com.mapuni.gdydcaiji.view.ClearEditText;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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
    @BindView(R.id.et_tele)
    ClearEditText etTele;
    @BindView(R.id.iv_calculator)
    ImageView ivCalculator;
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
    protected void initListener() {
        super.initListener();
        ivCalculator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openJS();
            }
        });
    }

    /**
     * 打开计算器
     */
    public void openJS() {
        PackageInfo pak = getAllApps(mContext, "Calculator", "calculator"); //大小写  
        if (pak != null) {
            Intent intent = new Intent();
            intent = this.getPackageManager().getLaunchIntentForPackage(pak.packageName);
            startActivity(intent);
        } else {
            Toast.makeText(this, "未找到计算器", Toast.LENGTH_SHORT).show();
        }
    }

    public PackageInfo getAllApps(Context context, String app_flag_1, String app_flag_2) {
        PackageManager pManager = context.getPackageManager();
        // 获取手机内所有应用  
        List<PackageInfo> packlist = pManager.getInstalledPackages(0);
        for (int i = 0; i < packlist.size(); i++) {
            PackageInfo pak = packlist.get(i);
            if (pak.packageName.contains(app_flag_1) || pak.packageName.contains(app_flag_2)) {
                return pak;
            }


        }
        return null;
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
        etTele.setText(resultBean.getTele());
        etLycs.setText(resultBean.getLycs());
        etLyzhs.setText(resultBean.getLyzhs());
        if (!TextUtils.isEmpty(resultBean.getImg())) {
            imgUrl = Base64.decode(resultBean.getImg(), Base64.DEFAULT);
        }
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
        resultBean.setTele(getTextByView(etTele));
        resultBean.setLycs(getTextByView(etLycs));
        resultBean.setLyzhs(getTextByView(etLyzhs));
        if (imgUrl != null && imgUrl.length > 0) {
            resultBean.setImg(Base64.encodeToString(imgUrl, Base64.DEFAULT));
        }
        resultBean.setOptuser(SPUtils.getInstance().getString("username", ""));
        resultBean.setOpttime(new Date(System.currentTimeMillis()));
        resultBean.setFlag(0);

        if (isInsert)
            tBuildingInfoDao.insert(resultBean);
        else
            tBuildingInfoDao.update(resultBean);

//        Intent data = new Intent();
//        data.putExtra("obj", resultBean);
//
//        setResult(Activity.RESULT_OK, data);
        EvevtUpdate evevtUpdate = new EvevtUpdate();
        EventBus.getDefault().post(evevtUpdate);
        finish();

    }

}
