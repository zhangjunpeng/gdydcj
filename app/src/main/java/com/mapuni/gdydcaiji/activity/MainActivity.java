package com.mapuni.gdydcaiji.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;

import com.mapuni.gdydcaiji.R;
import com.mapuni.gdydcaiji.utils.PermissionUtils;
import com.mapuni.gdydcaiji.utils.ToastUtils;

public class MainActivity extends BaseActivity {

    private long mExitTime;
    @Override
    protected String setTitleText() {
        return null;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        PermissionUtils.requestAllPermission(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {

    }

    /**
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    
    /**
     * 点击两次退出
     *
     * @return
     */
    public void exit() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            ToastUtils.showShort("再点一次退出");
            mExitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }
}
