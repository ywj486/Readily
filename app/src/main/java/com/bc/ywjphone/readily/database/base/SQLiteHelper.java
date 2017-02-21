package com.bc.ywjphone.readily.database.base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bc.ywjphone.readily.utils.Reflection;

import java.util.List;

/**
 * Created by Administrator on 2016/12/13 0013.
 */
public class SQLiteHelper extends SQLiteOpenHelper {
    private static SQLiteDataBaseConfig CONFIG;
    private static SQLiteHelper INSTANCE;
    private Reflection reflection;
    private Context context;

    public interface SQLiteDataTable {
        public void onCreate(SQLiteDatabase database);

        public void onUpgrade(SQLiteDatabase database);
    }

    public synchronized static SQLiteHelper getInstance(Context context) {
        if (INSTANCE == null) {
            //进行初始化CONFIG，否则就是null
            CONFIG = SQLiteDataBaseConfig.getInstance(context);
            INSTANCE = new SQLiteHelper(context);
        }
        return INSTANCE;
    }

    private SQLiteHelper(Context context) {
        super(context, CONFIG.getDatabaseName(), null, CONFIG.getVersion());
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        List<String> list = CONFIG.getTables();
        reflection = new Reflection();
        for (int i = 0; i < list.size(); i++) {
            try {
                //参数1：获取的全类名、参数2：构造函数的参数 参数3：参数类型
                SQLiteDataTable sqLiteDataTable = (SQLiteDataTable) reflection.newInstance(list.get(i),
                        new Object[]{context}, new Class[]{Context.class});
                sqLiteDataTable.onCreate(sqLiteDatabase);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
