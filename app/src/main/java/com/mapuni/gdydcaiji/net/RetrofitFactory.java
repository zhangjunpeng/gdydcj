package com.mapuni.gdydcaiji.net;


import com.mapuni.gdydcaiji.utils.LogUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by oldJin on 2017/11/9.
 */

public class RetrofitFactory {

    //正式
//    public static final String BASE_URL = "http://123.160.246.203:8281/gdwycj/post/";
    //测试
//    public static final String BASE_URL = "http://123.160.246.203:8055/gdwycj1/post/";

    //测试机器
    public static final String BASE_URL = "http://192.168.120.248:8080/gdwycj/post/";

    /**
     * 请求超时时间
     */
    private static final int DEFAULT_TIMEOUT = 1800;


    public static RetrofitService create(Class<RetrofitService> service) {
        RetrofitService retrofitService = new Retrofit.Builder()
                .client(getOkHttpClient())
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(service);

        return retrofitService;
    }

    private static OkHttpClient getOkHttpClient() {

        //新建log拦截器
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                LogUtils.d("request:" + message);
            }
        });
        //日志显示级别
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient
                .Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request mRequest = chain.request().newBuilder()
                                .header("Accept-Encoding", "identity")
                                .build();
                        return chain.proceed(mRequest);
                    }
                })
                .readTimeout(DEFAULT_TIMEOUT,TimeUnit.SECONDS)//设置读取超时时间  
                .writeTimeout(DEFAULT_TIMEOUT,TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .build();
    }

}
