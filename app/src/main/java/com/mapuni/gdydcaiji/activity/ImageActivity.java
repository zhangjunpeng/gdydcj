package com.mapuni.gdydcaiji.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.mapuni.gdydcaiji.R;
import com.mapuni.gdydcaiji.bean.EventBean;
import com.mapuni.gdydcaiji.utils.DialogUtils;
import com.mapuni.gdydcaiji.view.PinchImageView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ImageActivity extends AppCompatActivity {

    @BindView(R.id.image_view)
    PinchImageView imageView;
    @BindView(R.id.iv_delete)
    ImageView ivDelete;
    @BindView(R.id.back)
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ButterKnife.bind(this);
        String path = getIntent().getStringExtra("path");
        Glide.with(this)
                .load(Base64.decode(path, Base64.DEFAULT))
                .apply(new RequestOptions()
                        .error(R.drawable.not_have_image)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .timeout(1000))
                .into(imageView);
    }

    @OnClick({R.id.iv_delete, R.id.back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_delete:
                DialogUtils.showWarningDialog(this, "确定要删除照片吗", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EventBus.getDefault().post(new EventBean("deleteImg"));
                        finish();
                    }
                });
                break;
            case R.id.back:
                finish();
                break;
        }
    }
}
