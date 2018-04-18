package com.mapuni.gdydcaiji;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.multidex.MultiDex;

import com.mapuni.gdydcaiji.database.greendao.DaoMaster;
import com.mapuni.gdydcaiji.database.greendao.DaoSession;
import com.mapuni.gdydcaiji.database.greendao.MyOpenHelper;
import com.mapuni.gdydcaiji.utils.Utils;

/**
 * Created by yf on 2018/3/15.
 */

public class GdydApplication extends Application {
    private MyOpenHelper mHelper;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    //静态单例  
    public static GdydApplication instances;

    @Override
    public void onCreate() {
        super.onCreate();
        instances = this;
        Utils.init(this);
        setDatabase();
    }

    public static GdydApplication getInstances() {
        return instances;
    }

    /**
     * 设置greenDao
     */
    private void setDatabase() {
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。  
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。  
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。  
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。  
        mHelper = new MyOpenHelper(this, "sport.db", null);
        db = mHelper.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。  
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}  