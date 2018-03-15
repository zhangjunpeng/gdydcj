package com.mapuni.gdydcaiji.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Summer on 2017/3/30.
 */

public class ThreadUtils {

    private static Executor executor = Executors.newSingleThreadExecutor();
    private static Handler handler = new Handler(Looper.getMainLooper());

    /**
     * 子线程
     *
     * @param runnable
     */
    public static void executeSubThread(Runnable runnable) {
        executor.execute(runnable);
    }

    /**
     * 主线程
     *
     * @param runnable
     */
    public static void executeMainThread(Runnable runnable) {
        handler.post(runnable);
    }


}
