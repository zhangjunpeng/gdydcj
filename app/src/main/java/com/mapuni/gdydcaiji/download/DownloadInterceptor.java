package com.mapuni.gdydcaiji.download;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class DownloadInterceptor implements Interceptor {

    private DownloadListener downloadListener;
    private int fileSize;

    public DownloadInterceptor(int fileSize, DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
        this.fileSize = fileSize;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        return response.newBuilder().body(
                new ResponseBody(response.body(), fileSize, downloadListener)).build();
    }
}