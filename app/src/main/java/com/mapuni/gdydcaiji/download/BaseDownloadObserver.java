package com.mapuni.gdydcaiji.download;

import com.mapuni.gdydcaiji.utils.ToastUtils;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import okhttp3.ResponseBody;

/**
 * Created by allen on 2017/6/13.
 *
 * @author Allen
 */

public abstract class BaseDownloadObserver implements Observer<ResponseBody> {

    /**
     * 失败回调
     *
     * @param errorMsg 错误信息
     */
    protected abstract void doOnError(String errorMsg);


    @Override
    public void onError(@NonNull Throwable e) {
//        String error = ApiException.handleException(e).getMessage();
        setError(e.getMessage());
    }

    private void setError(String errorMsg) {
        ToastUtils.showShort(errorMsg);
        doOnError(errorMsg);
    }

}
