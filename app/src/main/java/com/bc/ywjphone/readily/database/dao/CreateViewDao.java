package com.bc.ywjphone.readily.database.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.bc.ywjphone.readily.database.base.SQLiteHelper;


/**
 * Created by Administrator on 2016/12/14 0014.
 */
public class CreateViewDao implements SQLiteHelper.SQLiteDataTable{

    private Context context;
    public CreateViewDao(Context context) {
      this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        StringBuilder sql = new StringBuilder();
        sql.append("Create  VIEW [v_payout] as");
        sql.append(" select p.*, c.parentId, c.categoryName, c.path, a.accountBookName");
        sql.append(" from payout p left join category c on p.categoryId=c.categoryId ");
        sql.append(" left join accountBook a on p.accountBookId=a.accountBookId ");

        //执行sql语句
        database.execSQL(sql.toString());

    }

    @Override
    public void onUpgrade(SQLiteDatabase database) {

    }
}
