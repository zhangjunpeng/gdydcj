package com.mapuni.gdydcaiji.net;

import com.mapuni.gdydcaiji.bean.LoginBean;
import com.mapuni.gdydcaiji.bean.MapBean;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

/**
 * Created by oldJin on 2017/11/9.
 */

public interface RetrofitService {


    /**
     * 登录接口
     *
     * @return
     */
    @GET("login/mobileLogin")
    Call<LoginBean> login(
            @Query("userName") String userName,
            @Query("password") String password

    );

    /**
     * 获取地图列表
     *
     * @return
     */
    @GET("slicesFile/syncSlicesFile")
    Observable<MapBean> getMapList(@Query("username") String userName);

    /**
     * 大文件官方建议用 @Streaming 来进行注解，不然会出现IO异常，小文件可以忽略不注入
     * 获取地图列表
     *
     * @return
     */
    @Streaming
    @GET("slicesFile/mobileDownLoadSlices")
    Observable<ResponseBody> downloadMap(@Query("fileid") String fileid);

    // 上传数据
    @Multipart
    @POST("building/importData")
    Call<RequestBody> upload(@PartMap Map<String, RequestBody> files);
}



