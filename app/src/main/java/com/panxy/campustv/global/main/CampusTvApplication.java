package com.panxy.campustv.global.main;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.panxy.campustv.global.utils.LogUtil;

import org.xutils.DbManager;
import org.xutils.db.table.TableEntity;
import org.xutils.x;

public class CampusTvApplication extends MultiDexApplication {

    private volatile static CampusTvApplication mInstance;
    private static final String TAG = "CampusTvApplication.class";
    private static Context context;
    private static int mainThreadId;
    private static DbManager db;

    private static int deviceBrand = 0;

    public static int getStatusBarHeight() {
        return statusBarHeight;
    }

    private static int statusBarHeight = 0;

    public static int getDeviceBrand() {
        return deviceBrand;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        mInstance = this;

        context = getApplicationContext();
        mainThreadId = android.os.Process.myTid();

        statusBarHeight = measureStatusBarHeight();
        //initDB();

    }



    public static CampusTvApplication getInstance() {
        if (mInstance == null) {
            synchronized (CampusTvApplication.class) {
                if (mInstance == null) {
                    mInstance = new CampusTvApplication();
                }
            }
        }
        return mInstance;
    }

    public static Context getContext() {
        return context;
    }

    public static int getMainThreadId() {
        return mainThreadId;
    }

    public static DbManager getDb() {
        return db;
    }

    private void initDB() {
        x.Ext.init(this);
        // 是否输出debug日志, 开启debug会影响性能.
        x.Ext.setDebug(false);

        /**
         * 初始化DaoConfig配置
         */
        DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
                //设置数据库名，默认xutils.db
                .setDbName("lawyer.db")
                //设置数据库路径，默认存储在app的私有目录
                //.setDbDir(new File("/mnt/sdcard/"))
                //设置数据库的版本号
                .setDbVersion(16)
                //设置数据库打开的监听
                .setDbOpenListener(new DbManager.DbOpenListener() {
                    @Override
                    public void onDbOpened(DbManager db) {
                        //开启数据库支持多线程操作，提升性能，对写入加速提升巨大
                        db.getDatabase().enableWriteAheadLogging();
                    }
                })
                //设置表创建的监听
                .setTableCreateListener(new DbManager.TableCreateListener() {
                    @Override
                    public void onTableCreated(DbManager db, TableEntity<?> table){
                        LogUtil.d(TAG, "onTableCreated：" + table.getName());
                    }

                });

        db = x.getDb(daoConfig);
        //数据库路径：data/data/com.zhihuizhengan.lawyer/databases/lawyer.db
    }

    private int measureStatusBarHeight(){
        int statusBarHeight = -1;
        int statusBarHeight1 = -1;
        //获取status_bar_height资源的ID
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight1 = getResources().getDimensionPixelSize(resourceId);
        }
        Log.e("webaaaaa", "状态栏-方法1:" + statusBarHeight1);
        if(statusBarHeight1 == -1){
            int statusBarHeight2 = -1;
            try {
                Class<?> clazz = Class.forName("com.android.internal.R$dimen");
                Object object = clazz.newInstance();
                int height = Integer.parseInt(clazz.getField("status_bar_height")
                        .get(object).toString());
                statusBarHeight2 = getResources().getDimensionPixelSize(height);
                statusBarHeight = statusBarHeight2;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            statusBarHeight = statusBarHeight1;
        }
        return statusBarHeight;
    }
}
