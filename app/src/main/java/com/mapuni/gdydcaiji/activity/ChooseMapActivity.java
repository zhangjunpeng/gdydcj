package com.mapuni.gdydcaiji.activity;

import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mapuni.gdydcaiji.R;
import com.mapuni.gdydcaiji.utils.PathConstant;
import com.mapuni.gdydcaiji.utils.SPUtils;
import com.mapuni.gdydcaiji.utils.ThreadUtils;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by yf on 2018/3/22.
 */

public class ChooseMapActivity extends BaseActivity {
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.mRecycleview)
    RecyclerView mRecycleView;
    @BindView(R.id.edit)
    TextView edit;

    private List<String> fileDirNames = new ArrayList<>();
    private List<String> fileDirPaths = new ArrayList<>();
    private ChooseMapAdapter adapter;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_download;
    }

    @Override
    protected void initView() {

        title.setText("选择地图");
        edit.setVisibility(View.VISIBLE);
        edit.setText("增加");
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mRecycleView.setHasFixedSize(true);
        mRecycleView.setNestedScrollingEnabled(false);
        mRecycleView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .size(1)
                .colorResId(R.color.gray_line)
                .build());//添加分隔线

        adapter = new ChooseMapAdapter(R.layout.item_choose_map_listview, fileDirNames);
        mRecycleView.setAdapter(adapter);

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllFiles();
    }

    @Override
    protected void initListener() {

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                SPUtils.getInstance().put("checkedMap", fileDirNames.get(position));
                SPUtils.getInstance().put("checkedMapPath", fileDirPaths.get(position));
                adapter.notifyDataSetChanged();
                openActivity(mContext, CollectionActivity.class);
                finish();
            }
        });
    }

    private void getAllFiles() {
        ThreadUtils.executeSubThread(new Runnable() {
            @Override
            public void run() {
                String path = PathConstant.UNDO_ZIP_PATH;
                File fileDir = new File(path);
                if (!fileDir.exists()) {
                    fileDir.mkdirs();
                }
                // 获得文件夹下所有文件夹和文件的名字
                String[] fileDirs = fileDir.list();
                // 获得文件夹下所有文件夹和文件
                File files[] = fileDir.listFiles();
                //添加之前先清空集合
                fileDirNames.clear();
                fileDirPaths.clear();
                //抛出java.lang.UnsupportedOperationException
                List<String> tempList = Arrays.asList(fileDirs);
                fileDirNames.addAll(tempList);
                for (File file : files) {
                    fileDirPaths.add(file.getAbsolutePath());
                }

                ThreadUtils.executeMainThread(new Runnable() {
                    @Override
                    public void run() {

                        adapter.notifyDataSetChanged();
                    }
                });


            }
        });

    }


    class ChooseMapAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

        public ChooseMapAdapter(int layoutResId, @Nullable List<String> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, String item) {
            String checkedMapName = SPUtils.getInstance().getString("checkedMap", "");
            helper.setText(R.id.tv_file_name, item)
                    .setGone(R.id.iv_choose_tick, item.equals(checkedMapName));
        }
    }

    @OnClick({R.id.back, R.id.edit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.edit:
                openActivity(mContext, DownloadMapActivity.class);
                break;
        }
    }

}
