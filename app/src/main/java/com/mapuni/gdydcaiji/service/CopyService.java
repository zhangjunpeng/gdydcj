package com.mapuni.gdydcaiji.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.mapuni.gdydcaiji.GdydApplication;
import com.mapuni.gdydcaiji.utils.FileUtils;
import com.mapuni.gdydcaiji.utils.PathConstant;
import com.mapuni.gdydcaiji.utils.ThreadUtils;
import com.mapuni.gdydcaiji.utils.ToastUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by yf on 2018/3/26.
 */

public class CopyService extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                ThreadUtils.executeSubThread(new Runnable() {
                    @Override
                    public void run() {

                        FileUtils.copyFile(GdydApplication.getInstances().getDb().getPath(), PathConstant.DATABASE_PATH + "/sport.db", new FileUtils.OnReplaceListener() {
                            @Override
                            public boolean onReplace() {
                                return true;
                            }
                        });
                    }
                });
            }
        }, 0, 60 * 60 * 1000);
        return super.onStartCommand(intent, flags, startId);
    }
    
}
