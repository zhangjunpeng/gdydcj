package com.mapuni.gdydcaiji.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mapuni.gdydcaiji.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("Registered")
public abstract class BaseActivity extends AppCompatActivity {

    @BindView(R.id.title)
    TextView tvTitle;
    public Context mContext;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
        ButterKnife.bind(this);
        mContext = this;
        tvTitle.setText(setTitleText());
        initView();
        initData();
        initListener();
    }

    protected abstract String setTitleText();

    protected abstract int getLayoutResId();

    protected abstract void initView();

    protected abstract void initData();

    protected abstract void initListener();

    @OnClick(R.id.back)
    public void onViewClicked(View view){
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
        }
    }
}
