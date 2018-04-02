package com.mapuni.gdydcaiji.activity;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.mapuni.gdydcaiji.R;
import com.mapuni.gdydcaiji.bean.EventBean;
import com.mapuni.gdydcaiji.service.CopyService;
import com.mapuni.gdydcaiji.utils.CustomUtils;
import com.mapuni.gdydcaiji.utils.DialogUtils;
import com.mapuni.gdydcaiji.utils.FileIOUtils;
import com.mapuni.gdydcaiji.utils.LogUtils;
import com.mapuni.gdydcaiji.utils.PathConstant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * Created by yf on 2017/12/21.
 */

public abstract class BaseDetailActivity<T> extends BaseActivity {
    @BindView(R.id.btn_save)
    Button btnSave;
    //    @BindView(R.id.edit)
//    TextView edit;
//    @BindView(R.id.cover)
//    View cover;
//    @BindView(R.id.ll_container)
//    LinearLayoutCompat llContainer;
    @BindView(R.id.iv_image)
    ImageView ivImg;

    //    protected boolean isEdit;
    protected T resultBean;
    //是否是新增
    protected boolean isInsert;
    protected byte[] imgUrl;
    protected double lat;
    protected double lng;

    @Override
    protected void initView() {

        EventBus.getDefault().register(this);
//        // 设置覆盖物的高度
//        llContainer
//                .getViewTreeObserver()
//                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                    @Override
//                    public void onGlobalLayout() {
//                        ViewGroup.LayoutParams layoutParams = cover.getLayoutParams();
//                        layoutParams.height = llContainer.getHeight();
//                        cover.setLayoutParams(layoutParams);
//                    }
//                });

    }

    @Override
    protected void initData() {
        resultBean = (T) getIntent().getSerializableExtra("resultBean");
        if (resultBean != null) {
            //查看
//            edit.setVisibility(View.VISIBLE);
//            cover.setVisibility(View.VISIBLE);
//            btnSave.setVisibility(View.GONE);
//            isEdit = false;
            isInsert = false;
            showData();
        } else {
            //新增
            lat = getIntent().getDoubleExtra("lat", 0);
            lng = getIntent().getDoubleExtra("lng", 0);
//            edit.setVisibility(View.GONE);
//            cover.setVisibility(View.GONE);
//            btnSave.setVisibility(View.VISIBLE);
//            isEdit = true;
            isInsert = true;
        }
    }

    @Override
    protected void initListener() {

    }

    /**
     * 回显数据
     */
    protected void showData() {
        if (imgUrl != null && imgUrl.length > 0) {
            Glide
                    .with(mContext)
                    .load(Base64.decode(imgUrl, Base64.DEFAULT))
                    .apply(new RequestOptions()
                            .error(R.drawable.not_have_image)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .timeout(1000))
                    .into(ivImg);
        }
    }

    /**
     * 查看大图
     */
    private void viewPluImg() {
        Intent intent = new Intent(this, ImageActivity.class);
        intent.putExtra("path", imgUrl);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String pathStr = null;
            switch (requestCode) {
                case 5001:
                    pathStr = PathConstant.IMAGE_PATH_CACHE + "/chche.jpg";
                    break;
                case 5002:
                    if (data != null) {
                        pathStr = CustomUtils.parseFilePath(data.getData(), this);
                    }
                    break;
            }
//            LogUtils.d(pathStr);
            Luban.with(this)
                    .load(pathStr)  // 传人要压缩的图片列表
                    .ignoreBy(100) // 忽略不压缩图片的大小  KB
                    .setTargetDir(PathConstant.IMAGE_PATH) // 设置压缩后文件存储位置
                    .setCompressListener(new OnCompressListener() { //设置回调
                        @Override
                        public void onStart() {
                        }

                        @Override
                        public void onSuccess(File file) {
                            imgUrl = Base64.encode(FileIOUtils.readFile2BytesByChannel(file), Base64.DEFAULT);
                            Glide
                                    .with(mContext)
                                    .load(Base64.decode(imgUrl, Base64.DEFAULT))
                                    .apply(new RequestOptions()
                                            .error(R.drawable.not_have_image)
                                            .diskCacheStrategy(DiskCacheStrategy.NONE).timeout(1000))
                                    .into(ivImg);
                        }

                        @Override
                        public void onError(Throwable e) {
                            LogUtils.d(e.getMessage());
                        }
                    }).launch();

        }
    }

    @OnClick({R.id.btn_save, R.id.back, R.id.iv_image})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
//                if (isEdit) {
                DialogUtils.showWarningDialog(mContext, "当前处于编辑状态，确定要退出吗？", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
//                } else {
//                    finish();
//                }

                break;
            case R.id.btn_save:

                //保存
                submit();
                break;
//            case R.id.edit:
//                btnSave.setVisibility(View.VISIBLE);
//                cover.setVisibility(View.GONE);
//                edit.setVisibility(View.GONE);
//                isEdit = true;
//                break;
            case R.id.iv_image:
                if (imgUrl == null || imgUrl.length == 0) {
                    //没照片
                    // 添加照片
                    DialogUtils.showChooseDialog(BaseDetailActivity.this, PathConstant.IMAGE_PATH_CACHE + "/chche.jpg");
                } else {
                    viewPluImg();
                }
                break;
        }
    }

    protected abstract void submit();

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//            if (isEdit) {
            DialogUtils.showWarningDialog(mContext, "当前处于编辑状态，确定要退出吗？", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            return true;
//            } else {
//                return super.onKeyDown(keyCode, event);
//            }

        }
        return super.onKeyDown(keyCode, event);
    }

    @Subscribe
    public void deletePhoto(EventBean event) {
        if ("deleteImg".equals(event.beanStr)) {
            imgUrl = null;
            ivImg.setImageResource(R.drawable.selector_camera);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    protected void setSpinnerData(int id, Spinner spinner) {
        List<String> mItems = Arrays.asList(getResources().getStringArray(id));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_spinner, R.id.tv_type, mItems);
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinner.setAdapter(adapter);
    }

    protected int getSelectPosition(int id, String s) {
        if (TextUtils.isEmpty(s)) {
            return 0;
        }
        String[] stringArray = getResources().getStringArray(id);
        for (int i = 0; i < stringArray.length; i++) {
            if (s.equals(stringArray[i])) {
                return i;
            }
        }
        return 0;
    }

    @NonNull
    protected String getTextByView(TextView view) {
        return TextUtils.isEmpty(view.getText()) ? "" : view.getText().toString().trim();
    }

}
