package com.mapuni.gdydcaiji.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mapuni.gdydcaiji.R;

import java.util.List;

/**
 * Created by yf on 2018/4/17.
 */

public class PpwAdapter extends BaseQuickAdapter<String, BaseViewHolder> {


    public PpwAdapter(int layoutResId, @Nullable List<String> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tv_item, item);
    }
}
