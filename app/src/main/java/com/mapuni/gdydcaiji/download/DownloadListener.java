package com.mapuni.gdydcaiji.download;

public interface DownloadListener {

    void onStartDownload();

    void onProgress(int progress);

    void onFinishDownload();

    void onFail(String errorInfo);

}