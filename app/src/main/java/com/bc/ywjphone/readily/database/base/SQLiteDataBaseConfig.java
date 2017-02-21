package com.bc.ywjphone.readily.database.base;

import android.content.Context;

import com.bc.ywjphone.readily.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/12/13 0013.
 */
public class SQLiteDataBaseConfig {
    public static final String DATABASE_NAME = "readily.db";//数据库名
    private static final int VERSION = 1;//数据库版本

    private static SQLiteDataBaseConfig INSTANCE;
    private static Context CONTEXT;

    private SQLiteDataBaseConfig() {

    }

    public synchronized static SQLiteDataBaseConfig getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new SQLiteDataBaseConfig();
            CONTEXT = context;
        }
        return INSTANCE;
    }

    public String getDatabaseName() {
        return DATABASE_NAME;
    }

    public int getVersion() {
        return VERSION;
    }

    /**
     * 获取到所有DAO全类名的数组（android不知道反射哪个类，用全类名）
     *
     * com.bc.ywjphone.readily.database.dao.userdao
     *
     * @return
     */
    public ArrayList<String> getTables() {
        ArrayList<String> list = new ArrayList<String>();
        String[] sqliteDaoClassName = CONTEXT.getResources().getStringArray(
                R.array.SQLiteDAOClassName);
        String packagePath = CONTEXT.getPackageName() + ".database.dao.";
        for (int i = 0; i < sqliteDaoClassName.length; i++) {
            list.add(packagePath + sqliteDaoClassName[i]);
        }
        return list;
    }
}
