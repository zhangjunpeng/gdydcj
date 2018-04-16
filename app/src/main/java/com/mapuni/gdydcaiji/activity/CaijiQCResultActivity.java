package com.mapuni.gdydcaiji.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mapuni.gdydcaiji.GdydApplication;
import com.mapuni.gdydcaiji.R;
import com.mapuni.gdydcaiji.bean.DownloadBean;
import com.mapuni.gdydcaiji.bean.TbLine;
import com.mapuni.gdydcaiji.bean.TbPoint;
import com.mapuni.gdydcaiji.bean.TbSurface;
import com.mapuni.gdydcaiji.database.greendao.DaoSession;
import com.mapuni.gdydcaiji.database.greendao.TbLineDao;
import com.mapuni.gdydcaiji.database.greendao.TbPointDao;
import com.mapuni.gdydcaiji.database.greendao.TbSurfaceDao;
import com.mapuni.gdydcaiji.net.RetrofitFactory;
import com.mapuni.gdydcaiji.net.RetrofitService;
import com.mapuni.gdydcaiji.utils.DateUtil;
import com.mapuni.gdydcaiji.utils.PathConstant;
import com.mapuni.gdydcaiji.utils.SPUtils;
import com.mapuni.gdydcaiji.utils.ThreadUtils;
import com.mapuni.gdydcaiji.utils.ToastUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by yf on 2018/4/12.
 * 采集人员质检结果
 */

public class CaijiQCResultActivity extends BaseActivity {
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.edit)
    TextView edit;
    @BindView(R.id.tv_result)
    TextView tvResult;

    private TbPointDao tbPointDao;
    private TbLineDao tbLineDao;
    private TbSurfaceDao tbSurfaceDao;
    private final String filePath = PathConstant.DOWNLOAD_DATA_PATH + File.separator + "downloadData.txt";

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_qcresult;
    }

    @Override
    protected void initView() {

        tvTitle.setText("质检结果");

        DaoSession daoSession = GdydApplication.getInstances().getDaoSession();
        tbPointDao = daoSession.getTbPointDao();
        tbLineDao = daoSession.getTbLineDao();
        tbSurfaceDao = daoSession.getTbSurfaceDao();

    }

    @Override
    protected void initData() {

        // Dialog
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("下载中...");
        // 点击对话框以外的地方无法取消
        pd.setCanceledOnTouchOutside(false);
        // 点击返回按钮无法取消
        pd.setCancelable(false);
        pd.show();

        RetrofitFactory.create(RetrofitService.class)
                .downloadData("'" + SPUtils.getInstance().getString("username") + "'", "", "", 1 + "")
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .map(new Function<ResponseBody, InputStream>() {

                    @Override
                    public InputStream apply(ResponseBody responseBody) throws Exception {
                        return responseBody.byteStream();
                    }

                })
                .observeOn(Schedulers.io()) // 用于计算任务
                .doOnNext(new Consumer<InputStream>() {
                    @Override
                    public void accept(InputStream inputStream) throws Exception {
                        writeFile(inputStream, filePath);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<InputStream>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(InputStream inputStream) {
                        pd.dismiss();
                        ToastUtils.showShort("下载成功");
                        ThreadUtils.executeSubThread(new Runnable() {
                            @Override
                            public void run() {
                                insertData2DB();
                            }
                        });

                    }

                    @Override
                    public void onError(Throwable e) {

                        pd.dismiss();
                        ToastUtils.showShort("下载失败");
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    @Override
    protected void initListener() {

    }

    private void insertData2DB() {
        FileInputStream fileTemp = null;
        try {

            fileTemp = new FileInputStream(new File(filePath));
            int length = fileTemp.available();
            byte[] buffer = new byte[length];
            fileTemp.read(buffer);
            String json = new String(buffer);
            Gson gson = new GsonBuilder().setDateFormat(DateUtil.YMDHMS).create();
            DownloadBean downloadBean = gson.fromJson(json, DownloadBean.class);
            List<TbPoint> tb_points = downloadBean.getTb_point();
            List<TbLine> tb_lines = downloadBean.getTb_line();
            List<TbSurface> tb_surfaces = downloadBean.getTb_surface();

            int updateSize = 0;

            if (tb_points != null && tb_points.size() > 0) {
                tbPointDao.insertOrReplaceInTx(tb_points);
                updateSize += tb_points.size();
            }
            if (tb_lines != null && tb_lines.size() > 0) {
                tbLineDao.insertOrReplaceInTx(tb_lines);
                updateSize += tb_lines.size();
            }
            if (tb_surfaces != null && tb_surfaces.size() > 0) {
                tbSurfaceDao.insertOrReplaceInTx(tb_surfaces);
                updateSize += tb_surfaces.size();
            }

            int size = tbPointDao.queryBuilder()
                    .where(TbPointDao.Properties.Flag.eq(0),  //未上传
                            TbPointDao.Properties.Authflag.eq("1"))//错误
                    .list().size();
            int size1 = tbLineDao.queryBuilder()
                    .where(TbLineDao.Properties.Flag.eq(0),  //未上传
                            TbLineDao.Properties.Authflag.eq("1"))//错误
                    .list().size();
            int size2 = tbSurfaceDao.queryBuilder()
                    .where(TbSurfaceDao.Properties.Flag.eq(0),  //未上传
                            TbSurfaceDao.Properties.Authflag.eq("1"))//错误
                    .list().size();

            final int totalSize = size + size1 + size2;

            final int finalUpdateSize = updateSize;
            ThreadUtils.executeMainThread(new Runnable() {
                @Override
                public void run() {
                    tvResult.setText("需要修改数据总数：" + totalSize + "\n新增数据数:" + finalUpdateSize);
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将输入流写入文件
     *
     * @param inputString
     * @param filePath
     */
    private void writeFile(InputStream inputString, String filePath) {

        File file = new File(filePath);
        if (!new File(PathConstant.DOWNLOAD_DATA_PATH).exists()) {
            new File(PathConstant.DOWNLOAD_DATA_PATH).mkdirs();
        }
        if (file.exists()) {
            file.delete();
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);

            byte[] b = new byte[1024];

            int len;
            while ((len = inputString.read(b)) != -1) {
                fos.write(b, 0, len);
            }
            inputString.close();
            fos.close();

        } catch (FileNotFoundException e) {
            ToastUtils.showShort("文件不存在");
        } catch (IOException e) {
            ToastUtils.showShort("下载错误");
        }

    }
}
