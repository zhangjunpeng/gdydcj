package com.mapuni.gdydcaiji.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.esri.android.map.event.OnPanListener;
import com.esri.android.map.event.OnPinchListener;
import com.mapuni.gdydcaiji.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("Registered")
public abstract class BaseActivity extends AppCompatActivity {

    public Context mContext;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
        ButterKnife.bind(this);
        mContext = this;
        initView();
        initData();
        initListener();
    }

    protected abstract int getLayoutResId();

    protected abstract void initView();

    protected abstract void initData();

    protected abstract void initListener();

    /**
     * 开启界面
     */
    protected void openActivity(Context context, Class<? extends Activity> clazz) {
        Intent activityIntent = new Intent(context, clazz);
        startActivity(activityIntent);
    }

    class MyPanListener implements OnPanListener {

        @Override
        public void prePointerMove(float v, float v1, float v2, float v3) {

        }

        @Override
        public void postPointerMove(float v, float v1, float v2, float v3) {

        }

        @Override
        public void prePointerUp(float v, float v1, float v2, float v3) {

        }

        @Override
        public void postPointerUp(float v, float v1, float v2, float v3) {

        }
    }

    class MyPanclListener implements OnPinchListener {

        @Override
        public void prePointersMove(float v, float v1, float v2, float v3, double v4) {

        }

        @Override
        public void postPointersMove(float v, float v1, float v2, float v3, double v4) {
        }

        @Override
        public void prePointersDown(float v, float v1, float v2, float v3, double v4) {

        }

        @Override
        public void postPointersDown(float v, float v1, float v2, float v3, double v4) {

        }

        @Override
        public void prePointersUp(float v, float v1, float v2, float v3, double v4) {

        }

        @Override
        public void postPointersUp(float v, float v1, float v2, float v3, double v4) {

        }
    }

    public Bitmap Drawable2Bitmap(Drawable drawable){
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight(),drawable.getOpacity() != PixelFormat.OPAQUE ?Bitmap.Config.ARGB_8888: Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public Drawable Bitmap2Drawable(Context context, Bitmap bitmap){
        return new BitmapDrawable(context.getResources(), bitmap);
    }

    public Drawable setNewScaleDrable(Drawable old,int displayWidth,int displayHeight){
        Bitmap oldBmp=Drawable2Bitmap(old);
        Bitmap newBmp = Bitmap.createScaledBitmap(oldBmp, displayWidth, displayHeight, true);
        return Bitmap2Drawable(this,newBmp);
    }

//    @OnClick(R.id.back)
//    public void onViewClicked(View view){
//        switch (view.getId()){
//            case R.id.back:
//                finish();
//                break;
//        }
//    }
}
