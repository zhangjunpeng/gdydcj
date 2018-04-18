package com.mapuni.gdydcaiji.activity

import android.app.ProgressDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.Gravity
import com.mapuni.gdydcaiji.R
import com.mapuni.gdydcaiji.bean.EventChangeMap
import com.mapuni.gdydcaiji.bean.EvevtUpdate
import com.mapuni.gdydcaiji.net.RetrofitFactory
import com.mapuni.gdydcaiji.net.RetrofitService
import com.mapuni.gdydcaiji.presenter.CheckPresenter
import com.mapuni.gdydcaiji.presenter.WaiYePresenter
import com.mapuni.gdydcaiji.utils.PathConstant
import com.mapuni.gdydcaiji.utils.SPUtils
import com.mapuni.gdydcaiji.utils.ThreadUtils
import com.mapuni.gdydcaiji.utils.ToastUtils
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_check.*
import okhttp3.ResponseBody
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.*

class CheckActivity : AppCompatActivity() {

    lateinit var checkPresenter: CheckPresenter

    private val filePath = PathConstant.DOWNLOAD_DATA_PATH + File.separator + "downloadData.txt"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_check)

        EventBus.getDefault().register(this)

        checkPresenter= CheckPresenter(this,mapview_check,recyler_check)
        initMapView()
        initData()
        initListener()
    }

    private fun initListener() {
        btn_menu.setOnClickListener {
            if (drawer.isDrawerOpen(Gravity.RIGHT)){
                drawer.closeDrawers()
            }else{
                drawer.openDrawer(Gravity.RIGHT)
            }
        }
        recyler_check.layoutManager = LinearLayoutManager(this)


        download_data.setOnClickListener{
            getDataFromServer()
        }
        update_data.setOnClickListener {
            checkPresenter.createFile()
        }
    }
    fun initData() {
        checkPresenter.updataGraphic()
    }
    private fun initMapView() {
        checkPresenter.initMap()
    }

    fun getDataFromServer(){

        // Dialog
        val pd = ProgressDialog(this)
        pd.setMessage("下载中...")
        // 点击对话框以外的地方无法取消
        pd.setCanceledOnTouchOutside(false)
        // 点击返回按钮无法取消
        pd.setCancelable(false)
        pd.show()

        RetrofitFactory.create(RetrofitService::class.java)
                .downloadData("'" + SPUtils.getInstance().getString("username") + "'", "", "", 1.toString() + "")
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .map { responseBody -> responseBody.byteStream() }
                .observeOn(Schedulers.io()) // 用于计算任务
                .doOnNext { inputStream -> writeFile(inputStream, filePath) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<InputStream> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(inputStream: InputStream) {
                        pd.dismiss()
                        ToastUtils.showShort("下载成功")
                        ThreadUtils.executeSubThread {
                            checkPresenter.insertData2DB(filePath)
                        }

                    }

                    override fun onError(e: Throwable) {

                        pd.dismiss()
                        ToastUtils.showShort("下载失败")
                    }

                    override fun onComplete() {
//                        checkPresenter.updataGraphic()

                    }
                })

    }

    /**
     * 将输入流写入文件
     *
     * @param inputString
     * @param filePath
     */
    private fun writeFile(inputString: InputStream, filePath: String) {

        val file = File(filePath)
        if (!File(PathConstant.DOWNLOAD_DATA_PATH).exists()) {
            File(PathConstant.DOWNLOAD_DATA_PATH).mkdirs()
        }
        if (file.exists()) {
            file.delete()
        }

        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(file)

            val b = ByteArray(1024)

            var len: Int
            do {
                len = inputString.read(b)
                if (len==-1){
                    break
                }else{
                    fos.write(b, 0, len)
                }
            }while (true)

            inputString.close()
            fos.close()

        } catch (e: FileNotFoundException) {
            ToastUtils.showShort("文件不存在")
        } catch (e: IOException) {
            ToastUtils.showShort("下载错误")
        }

    }

    @Subscribe
    fun onChange(evevtUpdate: EvevtUpdate){

        checkPresenter.updataGraphic()
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()

    }


}
