package com.mapuni.gdydcaiji;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.mapuni.gdydcaiji.database.greendao.DaoMaster;
import com.mapuni.gdydcaiji.database.greendao.DaoSession;
import com.mapuni.gdydcaiji.database.greendao.GreenDaoContext;
import com.mapuni.gdydcaiji.database.greendao.MyOpenHelper;
import com.mapuni.gdydcaiji.utils.PathConstant;
import com.mapuni.gdydcaiji.utils.SPUtils;
import com.mapuni.gdydcaiji.utils.ThreadUtils;
import com.mapuni.gdydcaiji.utils.Utils;
import com.tencent.bugly.crashreport.CrashReport;

import net.sqlcipher.database.SQLiteDatabase;

import org.greenrobot.greendao.database.Database;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by yf on 2018/3/15.
 */

public class GdydApplication extends Application {
    private MyOpenHelper mHelper;
    private Database db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    public static final String DB_KEY = "147258";
    public static final String ENCRYPTED_DB_NAME = "encrypted.db";
    public static final String OLD_DB_NAME = "sport.db";

    //静态单例  
    public static GdydApplication instances;

    @Override
    public void onCreate() {
        super.onCreate();
        instances = this;
        //发布前删除
//        CrashReport.setIsDevelopmentDevice(this, true);
        CrashReport.initCrashReport(getApplicationContext(), "3637144ca2", false);
        Utils.init(this);

//        boolean dbisen = SPUtils.getInstance().getBoolean("dbisen");
//        if (getDatabasePath(OLD_DB_NAME).getAbsoluteFile().exists() && !dbisen) {
//            ThreadUtils.executeSubThread(new Runnable() {
//                @Override
//                public void run() {
//                    encrypt(ENCRYPTED_DB_NAME, OLD_DB_NAME, DB_KEY);
//                    SPUtils.getInstance().put("dbisen", true);
//                    ThreadUtils.executeMainThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            setDatabase();
//                        }
//                    });
//                }
//            });
//        } else {
//            setDatabase();
//        }

    }


    public static GdydApplication getInstances() {
        return instances;
    }

    /**
     * 设置greenDao
     */
    public void setDatabase() {
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。  
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。  
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。  
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。  
//        mHelper = new MyOpenHelper(this, ENCRYPTED_DB_NAME, null);
        mHelper = new MyOpenHelper(new GreenDaoContext(), OLD_DB_NAME, null);
        try {
            db = mHelper.getEncryptedWritableDb(DB_KEY);
        } catch (Exception e) {
            db = mHelper.getWritableDb();
        }
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。  
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

//    public SQLiteDatabase getDb() {
//        return db;
//    }

    public String getDbPath() {
//        return getDatabasePath(ENCRYPTED_DB_NAME).getAbsolutePath();
        return PathConstant.QC_DATABASE_PATH + File.separator + OLD_DB_NAME;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /**
     * 加密数据库
     *
     * @param encryptedName 加密后的数据库名称
     * @param decryptedName 要加密的数据库名称
     * @param key           密码
     */
    private void encrypt(String encryptedName, String decryptedName, String key) {
        SQLiteDatabase.loadLibs(this);
        File databaseFile = getDatabasePath(decryptedName);
        SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(databaseFile, "", null);//打开要加密的数据库
         /*String passwordString = "1234"; //只能对已加密的数据库修改密码，且无法直接修改为“”或null的密码 
            database.changePassword(passwordString.toCharArray());*/

        File encrypteddatabaseFile = getDatabasePath(encryptedName);//新建加密后的数据库文件  
        //deleteDatabase(SDcardPath + encryptedName);  
        try {

            //连接到加密后的数据库，并设置密码  
            database.rawExecSQL(String.format("ATTACH DATABASE '%s' as " + encryptedName.split("\\.")[0] + " KEY '" + key + "';", encrypteddatabaseFile.getAbsolutePath()));
            //输出要加密的数据库表和数据到加密后的数据库文件中  
            database.rawExecSQL("SELECT sqlcipher_export('" + encryptedName.split("\\.")[0] + "');");
            //断开同加密后的数据库的连接  
            database.rawExecSQL("DETACH DATABASE " + encryptedName.split("\\.")[0] + ";");

            //打开加密后的数据库，测试数据库是否加密成功  
            SQLiteDatabase encrypteddatabase = SQLiteDatabase.openOrCreateDatabase(encrypteddatabaseFile, key, null);
            //encrypteddatabase.setVersion(database.getVersion());  
            encrypteddatabase.close();//关闭数据库  

            database.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.close();
        }
    }

    /**
     * 将assets文件夹下文件拷贝到/databases/下
     *
     * @param context
     * @param db_name
     */
    public static void copyDbFile(Context context, String db_name) {
        InputStream in = null;
        FileOutputStream out = null;
        String path = "/data/data/" + context.getPackageName() + "/databases/";
        File file = new File(path + db_name);

        //创建文件夹
        File filePath = new File(path);
        if (!filePath.exists())
            filePath.mkdirs();

        if (file.exists())
            return;

        try {
            in = context.getAssets().open(db_name); // 从assets目录下复制
            out = new FileOutputStream(file);
            int length = -1;
            byte[] buf = new byte[1024];
            while ((length = in.read(buf)) != -1) {
                out.write(buf, 0, length);
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}  