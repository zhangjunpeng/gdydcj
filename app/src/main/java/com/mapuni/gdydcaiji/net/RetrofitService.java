package com.mapuni.gdydcaiji.net;

import com.mapuni.gdydcaiji.bean.FieidPerson;
import com.mapuni.gdydcaiji.bean.HomeArea;
import com.mapuni.gdydcaiji.bean.LoginBean;
import com.mapuni.gdydcaiji.bean.MapBean;
import com.mapuni.gdydcaiji.bean.UploadBean;

import java.util.List;
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
     * 获取外业人员列表
     *
     * @return
     */
    @GET("building/treePerson")
    Observable<List<FieidPerson>> getFieidPersonList(@Query("id") String id);

    /**
     * 下载数据
     *
     * @return
     */
    @Streaming
    @GET("building/downloadData")
    Observable<ResponseBody> downloadData(@Query("optUser") String optUser,
                                          @Query("startTime") String startTime,
                                          @Query("endTime") String endTime,
                                          @Query("status") String status//0->未审核状态；1->错误的状态
    );

    /**
     * 获取区域列表
     *
     * @return
     */
    @GET("building/getHomeArea")
    Observable<HomeArea> getHomeAreaList();

    @GET("building/selectByHomeArea")
    Observable<ResponseBody> downloadAreaData(@Query("homearea") String homearea);

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
    Call<UploadBean> upload(@PartMap Map<String, RequestBody> files);

    // 上传内业处理数据
    @Multipart
    @POST("building/importMobileData")
    Call<UploadBean> uploadMobileData(@PartMap Map<String, RequestBody> files);
}



