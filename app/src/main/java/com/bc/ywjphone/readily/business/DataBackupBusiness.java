package com.bc.ywjphone.readily.business;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import com.bc.ywjphone.readily.business.base.BaseBusiness;
import com.bc.ywjphone.readily.database.base.SQLiteDataBaseConfig;
import com.bc.ywjphone.readily.utils.FileUtil;

import java.io.File;
import java.util.Date;

/**
 * Created by Administrator on 2016/12/30 0030.
 */
public class DataBackupBusiness extends BaseBusiness {
    private static final String SDCARD_PATH = Environment.getExternalStorageDirectory().getPath()
            + "/Readily/DataBaseBak/";
    private String DATA_PATH = Environment.getDataDirectory() + "/data/" + context.getPackageName()
            + "/databases/";

    public DataBackupBusiness(Context context) {
        super(context);
    }

    //读取上一次数据备份的日期
    public long loadDatabaseBackupDate() {
        long databaseBackupDate = 0;
        //获取指定key的sp对象
        SharedPreferences sp = context.getSharedPreferences("databaseBackupDate", Context.MODE_PRIVATE);
        //如果sp不为空
        if (sp != null) {
            databaseBackupDate = sp.getLong("databaseBackupDate", 0);
        }
        return databaseBackupDate;

    }

    //数据备份
    public boolean databaseBackup(Date backupDate) {
        boolean result = false;
        try {
            File sourceFile = new File(DATA_PATH + SQLiteDataBaseConfig.DATABASE_NAME);
            //如果数据库文件存在的话才进行备份操作
            if (sourceFile.exists()) {
                //把文件保存到sd卡的指定目录下
                File fileDir = new File(SDCARD_PATH);
                // 目录不存在就创建
                if (!fileDir.exists()) {
                    //创建一个文件夹
                    fileDir.mkdir();
                }
                //从这个路径下拷贝到另一个文件下，参数1 原路径+文件名
                //参数2 目录路径+文件名
                FileUtil.copyFile(DATA_PATH + SQLiteDataBaseConfig.DATABASE_NAME,
                        SDCARD_PATH + SQLiteDataBaseConfig.DATABASE_NAME);
                result = true;
            }
            // 保存日期共享偏好
            saveDatabaseBackupDate(backupDate.getTime());
        } catch (Exception e) {

            e.printStackTrace();
        }
        return result;
    }

    //保存日期，自己写
    private void saveDatabaseBackupDate(long time) {
        SharedPreferences sh = context.getSharedPreferences(
                "databaseBackupDate", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sh.edit();
        edit.putLong("databaseBackupDate", time);
        edit.commit();
    }

    //数据还原
    public boolean databaseRestore() {
        boolean result = false;
        File sourceFile = new File(SDCARD_PATH
                + SQLiteDataBaseConfig.DATABASE_NAME);
        if (sourceFile.exists()) {
            Log.e("TAG","说明文件存在，在数据还原的时候");
            File fileDir = new File(DATA_PATH);
            Log.e("TAG","文件存在么？"+fileDir.exists());
            if (!fileDir.exists()) {
                Log.e("TAG","开始创建数据库文件在刚开始运行的时候呀");
                //创建文件夹
                fileDir.mkdirs();
            }
            FileUtil.copyFolder(SDCARD_PATH + SQLiteDataBaseConfig.DATABASE_NAME,
                    DATA_PATH);
            result = true;
        }
        return result;

    }

}