package com.bc.ywjphone.readily.database.base;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.bc.ywjphone.readily.entity.Payout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/14 0014.
 */
public abstract class SQLiteDAOBase implements SQLiteHelper.SQLiteDataTable{
    private Context context;
    private SQLiteDatabase database;

    public SQLiteDAOBase(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public SQLiteDatabase getDatabase() {
        if (database == null) {
            database = SQLiteHelper.getInstance(context).getWritableDatabase();
        }
        return database;
    }

    //开启事务
    public void beginTransaction() {
        database.beginTransaction();
    }

    //设置事务已经成功
    public void setTransactionSuccessful() {
        database.setTransactionSuccessful();
    }

    //结束事务
    public void endTransaction() {
        database.endTransaction();
    }

    //获取表名和主键
    protected abstract String[] getTableNumAndPk();

    //执行sql语句可以返回一个游标
    public Cursor execSql(String sql) {
        return getDatabase().rawQuery(sql, null);
    }

    //获取总数(注意sql语句的空格)
    public int getCount(String condition) {
        String[] str = getTableNumAndPk();
        return getCount(str[1], str[0], condition);
    }

    //获取总数(注意sql语句的空格)
    public int getCount(String pk, String tableName, String condition) {
        //select * from users where 1=1 and id=1 and name="abc"
        Cursor cursor = execSql("select" + pk + " from " + tableName + " where 1=1 " + condition);
        int count = cursor.getCount();
        //cursor关闭，不可以垃圾回收,否则会造成内存泄漏
        cursor.close();
        return count;
    }

    //删除
    protected boolean delete(String tableName, String condition) {
        //如果影响的条数大于等于0就成功了
        return getDatabase().delete(tableName, " 1=1 " + condition, null) >= 0;
    }

    //游标转集合
    protected List cursorToList(Cursor cursor) {
        List list = new ArrayList();
        while (cursor.moveToNext()) {
            Object object = findModel(cursor);
            list.add(object);
        }
        cursor.close();
        //返回list不会为null，因为new了一个ArrayList，可能为0
        return list;
    }

    //获取集合
    protected List getList(String sql) {
        Cursor curosr = execSql(sql);
        return cursorToList(curosr);
    }

    //谁继承谁实现此方法
    protected abstract Object findModel(Cursor cursor);

}
