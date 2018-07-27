package com.mapuni.gdydcaiji.database.greendao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.mapuni.gdydcaiji.GdydApplication;

import org.greenrobot.greendao.database.Database;

public class MyOpenHelper extends DaoMaster.OpenHelper {

    public MyOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onCreate(Database db) {
//        if (GdydApplication.getInstances().getDatabasePath(GdydApplication.OLD_DB_NAME).exists()) {
//            DaoMaster.createAllTables(db, true);
//        } else {
            super.onCreate(db);
//        }
    }

    /**
     * 数据库升级
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        //操作数据库的更新 有几个表升级都可以传入到下面
        MigrationHelper.getInstance().migrate(db, TbPointDao.class, TbLineDao.class, TbSurfaceDao.class);
    }

}