package com.mapuni.gdydcaiji.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mapuni.gdydcaiji.R;
import com.mapuni.gdydcaiji.bean.MapBean;
import com.mapuni.gdydcaiji.download.DownloadObserver;
import com.mapuni.gdydcaiji.net.RetrofitFactory;
import com.mapuni.gdydcaiji.net.RetrofitService;
import com.mapuni.gdydcaiji.utils.FileSizeUtils;
import com.mapuni.gdydcaiji.utils.FileUtils;
import com.mapuni.gdydcaiji.utils.PathConstant;
import com.mapuni.gdydcaiji.utils.ProgressDialogFactory;
import com.mapuni.gdydcaiji.utils.SPUtils;
import com.mapuni.gdydcaiji.utils.ThreadUtils;
import com.mapuni.gdydcaiji.utils.ToastUtils;
import com.mapuni.gdydcaiji.utils.ZipUtils;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by yf on 2018/3/20.
 */

public class DownloadMapActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.mRecycleview)
    RecyclerView mRecycleView;

    private List<MapBean.SlicesBean> mapBeanList = new ArrayList<>();
    private DownloadMapAdapter adapter;
    private ProgressDialog progressDialog;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_download;
    }

    @Override
    protected void initView() {

        title.setText("地图下载");
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mRecycleView.setHasFixedSize(true);
        mRecycleView.setNestedScrollingEnabled(false);
        mRecycleView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .size(1)
                .colorResId(R.color.gray_line)
                .build());//添加分隔线

        adapter = new DownloadMapAdapter(R.layout.item_download_map, mapBeanList);
        mRecycleView.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("正在搜索地图...");
        // 点击对话框以外的地方无法取消
        progressDialog.setCanceledOnTouchOutside(false);
        // 点击返回按钮无法取消
        progressDialog.setCancelable(false);
        progressDialog.show();

        RetrofitFactory.create(RetrofitService.class).getMapList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MapBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MapBean mapBean) {

                        List<MapBean.SlicesBean> slices = mapBean.getSlices();
                        mapBeanList.addAll(slices);
                        adapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        ToastUtils.showShort("搜索地图失败，请检查网络！");
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onComplete() {

                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                });
    }

    @Override
    protected void initListener() {

        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.iv_download:
                        // 显示进度对话框
//                        showDownloadDialog(mapBeanList.get(position).getFileSize());
                        // 下载文件
                        int fileId = mapBeanList.get(position).getId();
                        String fileName = mapBeanList.get(position).getDetail();
                        int fileSize = mapBeanList.get(position).getFileSize();
                        downloadMap(fileId, fileName, fileSize);
                        break;
                }
            }
        });
        
        adapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                showDeleteMapDialog(position);
                return false;
            }
        });

    }

    private void downloadMap(final int fileId, String fileName, int fileSize) {
        final ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setMax(100);
        dialog.show();
        RetrofitFactory.create(RetrofitService.class).downloadMap(String.valueOf(fileId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DownloadObserver(fileName + ".zip", fileSize) {

                    @Override
                    protected void onError(String errorMsg) {
                    }

                    @Override
                    protected void onSuccess(long bytesRead, long contentLength, float progress,
                                             boolean done, String filePath) {

                        dialog.setProgress((int) progress);
                        if (done) {
                            dialog.dismiss();
                            String fileIds = SPUtils.getInstance().getString("downloaded", "") + "#" + fileId;
                            SPUtils.getInstance().put("downloaded", fileIds);
                            adapter.notifyDataSetChanged();

                            autoUndoZipFile(new File(filePath));
                        }
                        
                    }
                });
    }

    /**
     * 自动解压zip文件
     *
     * @param file
     */
    private void autoUndoZipFile(final File file) {
        final ProgressDialog undoZipDialog = new ProgressDialog(mContext);
        undoZipDialog.setMessage("正在解压...");
        undoZipDialog.show();
        final String mapPath = PathConstant.UNDO_ZIP_PATH;
        ThreadUtils.executeSubThread(new Runnable() {
            @Override
            public void run() {
                try {
                    ZipUtils.UnZipFolder(file.getAbsolutePath(), mapPath);

                    ThreadUtils.executeMainThread(new Runnable() {
                        @Override
                        public void run() {
                            undoZipDialog.dismiss();
                            EventBus.getDefault().post(new SuccessEvent(1));
                            ToastUtils.showShort("解压完成");
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    ThreadUtils.executeMainThread(new Runnable() {
                        @Override
                        public void run() {
                            undoZipDialog.dismiss();
                            ToastUtils.showShort("解压出错！");
                        }
                    });
                }
            }
        });


    }

    /**
     * 弹出是否删除地图的dialog
     *
     * @param position
     */
    private void showDeleteMapDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("是否删除地图");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ProgressDialogFactory.getProgressDialog(mContext, ProgressDialog.STYLE_SPINNER,
                        null, -1, "正在删除...", true, true,null);
                ThreadUtils.executeSubThread(new Runnable() {
                    @Override
                    public void run() {
                        SystemClock.sleep(2000);
                        FileUtils.deleteFile(PathConstant.ZIP_PATH + "/" +
                                mapBeanList.get(position).getDetail() + ".zip");
                        String fileIds = SPUtils.getInstance().getString("downloaded", "") + "";
                        String newFileIds = fileIds.replace(mapBeanList.get(position).getId() + "", "");
                        SPUtils.getInstance().put("downloaded", newFileIds);
                        ThreadUtils.executeMainThread(new Runnable() {
                            @Override
                            public void run() {
                                ProgressDialogFactory.dismiss();
                                ToastUtils.showShort("地图已删除");
                                adapter.notifyDataSetChanged();
                            }
                        });

                    }
                });


            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();

    }

    class DownloadMapAdapter extends BaseQuickAdapter<MapBean.SlicesBean, BaseViewHolder> {

        public DownloadMapAdapter(int layoutResId, @Nullable List<MapBean.SlicesBean> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, MapBean.SlicesBean item) {

            String fileId = String.valueOf(item.getId());
            boolean isDownloaded = SPUtils.getInstance().getString("downloaded", "").toString().contains(fileId);
            helper
                    .setText(R.id.tv_map_name, item.getDetail())
                    .setText(R.id.tv_zip_size, FileSizeUtils.byte2MB(mContext, item.getFileSize()))
                    .setImageResource(R.id.iv_download, isDownloaded ? R.drawable.xzwc_icon : R.drawable.xiazai_icon);
            if (!isDownloaded) {
                helper.addOnClickListener(R.id.iv_download);
            }
        }
    }

    @OnClick(R.id.back)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    public class SuccessEvent {
        // 1 下载成功  -1 下载失败
        public int downloadStatus;

        public SuccessEvent(int downloadStatus) {
            this.downloadStatus = downloadStatus;
        }
    }
}
