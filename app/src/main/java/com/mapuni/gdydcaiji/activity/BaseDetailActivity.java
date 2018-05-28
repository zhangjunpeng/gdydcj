package com.mapuni.gdydcaiji.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.mapuni.gdydcaiji.R;
import com.mapuni.gdydcaiji.adapter.PhotoAdapter;
import com.mapuni.gdydcaiji.bean.EventBean;
import com.mapuni.gdydcaiji.utils.CustomUtils;
import com.mapuni.gdydcaiji.utils.DialogUtils;
import com.mapuni.gdydcaiji.utils.FileIOUtils;
import com.mapuni.gdydcaiji.utils.LogUtils;
import com.mapuni.gdydcaiji.utils.PathConstant;
import com.mapuni.gdydcaiji.utils.SPUtils;
import com.mapuni.gdydcaiji.utils.ScreenUtils;
import com.mapuni.gdydcaiji.view.ClearEditText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
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
    @BindView(R.id.gv_photo)
    GridView gvPhoto;
    @BindView(R.id.tv_zjjgzs)
    TextView tvZjjgzs;
    @BindView(R.id.et_zjjg)
    ClearEditText etZjjg;
    @BindView(R.id.ll_zj)
    LinearLayout llZj;
    @BindView(R.id.ll_container)
    LinearLayoutCompat llContainer;
    @BindView(R.id.cover)
    View cover;

    //    protected boolean isEdit;
    protected T resultBean;
    //是否是新增
    protected boolean isInsert;
    protected List<String> imgUrls = new ArrayList<>();
    protected String photoImg;
    
    protected String roleid;
    private PhotoAdapter adapter;
    private final String filePath = PathConstant.IMAGE_PATH_CACHE + "/result.txt";

    @Override
    protected void initView() {

        EventBus.getDefault().register(this);

        llContainer
                .getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        ViewGroup.LayoutParams layoutParams = cover.getLayoutParams();
                        layoutParams.height = llContainer.getHeight();
                        cover.setLayoutParams(layoutParams);
                    }
                });
        
        /*设置dialog的宽*/
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = (int) (ScreenUtils.getScreenW(this) * 0.75);
        getWindow().setAttributes(lp);

        adapter = new PhotoAdapter(mContext.getApplicationContext(), imgUrls);
        gvPhoto.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        roleid = SPUtils.getInstance().getString("roleid");
//        resultBean = (T) getIntent().getSerializableExtra("resultBean");
        
        if (resultBean != null) {
            //查看
            isInsert = false;

            showData();
        } else {
            //新增
            isInsert = true;
        }
    }

    @Override
    protected void initListener() {

        gvPhoto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < imgUrls.size()) {
                    viewPluImg(position);
                } else {
                    //没照片
                    // 添加照片
                    DialogUtils.showChooseDialog(BaseDetailActivity.this, PathConstant.IMAGE_PATH_CACHE + "/chche.jpg");
                }
            }
        });
    }

    /**
     * 回显数据
     */
    protected void showData() {
        if (!TextUtils.isEmpty(photoImg)) {
            List<String> photos = Arrays.asList(photoImg.split(";"));
            ArrayList<String> templist = new ArrayList<>(photos);
            imgUrls.addAll(templist);
        }
        adapter.notifyDataSetChanged();
    }

    protected String getPhotoImg() {
        String photoImgStr = "";
        if (imgUrls != null && imgUrls.size() > 0) {
            for (String s :
                    imgUrls) {
                photoImgStr += s + ";";
            }
        }
        return TextUtils.isEmpty(photoImgStr) ? "" : photoImgStr.substring(0, photoImgStr.length() - 1);
    }

    /**
     * 查看大图
     *
     * @param position
     */

    private void viewPluImg(int position) {
        Intent intent = new Intent(this, ImageActivity.class);
        intent.putExtra("path", imgUrls.get(position));
        intent.putExtra("position", position);
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
                            String imgUrl = Base64.encodeToString(FileIOUtils.readFile2BytesByChannel(file), Base64.DEFAULT);
                            imgUrls.add(imgUrl);
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onError(Throwable e) {
                            LogUtils.d(e.getMessage());
                        }
                    }).launch();

        }
    }

    @OnClick({R.id.btn_save, R.id.back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();

                break;
            case R.id.btn_save:

                //保存
                submit();
                break;
        }
    }

    protected void submit(){
        cacheView();
    }

    @Subscribe
    public void deletePhoto(EventBean event) {
        if ("deleteImg".equals(event.beanStr)) {
            imgUrls.remove(event.position);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, R.anim.activity_dialog_out);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            cacheView();
        }
        return super.onKeyDown(keyCode, event);
    }

    //释放内存
    private void cacheView() {
        gvPhoto.removeAllViewsInLayout();
        gvPhoto = null;
        imgUrls.clear();
        photoImg = null;
        adapter = null;
        resultBean = null;
        setContentView(R.layout.activity_empty);
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
