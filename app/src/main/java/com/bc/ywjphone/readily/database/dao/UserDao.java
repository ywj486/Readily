package com.bc.ywjphone.readily.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bc.ywjphone.readily.R;
import com.bc.ywjphone.readily.database.base.SQLiteDAOBase;
import com.bc.ywjphone.readily.entity.Users;
import com.bc.ywjphone.readily.utils.DateUtil;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/12/14 0014.
 */
public class UserDao extends SQLiteDAOBase {

    public UserDao(Context context) {
        super(context);
    }

    @Override
    protected String[] getTableNumAndPk() {
        return new String[]{"users", "userId"};
    }

    @Override
    protected Object findModel(Cursor cursor) {
        Users user = new Users();
        user.setUserId(cursor.getInt(cursor.getColumnIndex("userId")));
        user.setUserName(cursor.getString(cursor.getColumnIndex("userName")));
        Date createDate = DateUtil.getFormatDate(cursor.getString(
                cursor.getColumnIndex("createData")), "yyyy-MM-dd HH:mm:ss");
        user.setCreateData(createDate);
        user.setState(cursor.getInt(cursor.getColumnIndex("state")));
        return user;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        StringBuilder sql = new StringBuilder();
        sql.append("Create  TABLE [users](");
        sql.append("         [userId] integer PRIMARY KEY AUTOINCREMENT NOT NULL");
        sql.append(",[userName] varchar(20) NOT NULL");
        sql.append(" ,[createData] datatime NOT NULL");
        sql.append(" ,[state] int NOT NULL");
        sql.append(" )");
        //执行sql语句
        database.execSQL(sql.toString());
        //建表之后默认添加了三条数据
        initDefaultData(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database) {

    }

    public boolean insertUser(Users user) {
        ContentValues contentValues = createParms(user);
        //插入成功返回 新的id值，失败返回-1
        long newid = getDatabase().insert(getTableNumAndPk()[0], null, contentValues);
        return newid > 0;
    }

    public boolean deleteUser(String condition) {
        return delete(getTableNumAndPk()[0], condition);
    }

    public boolean updateUser(String condition, Users user) {
        ContentValues contentValues = createParms(user);
        //调用下面重载的方法
        return updateUser(condition, contentValues);
    }

    public boolean updateUser(String condition, ContentValues contentValues) {
        return getDatabase().update(getTableNumAndPk()[0],
                contentValues, condition, null) > 0;
    }

    public List<Users> getUsers(String condition) {
        String sql = "select * from users where 1=1 " + condition;
        return getList(sql);
    }

    public ContentValues createParms(Users info) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("userName", info.getUserName());
        contentValues.put("createData", DateUtil.getFormattedString(info.getCreateData(),
                "yyyy-MM-dd HH:mm:ss"));
        contentValues.put("state", info.getState());
        return contentValues;
    }

    private void initDefaultData(SQLiteDatabase database) {
        Users user = new Users();
        String[] userNames = getContext().getResources().
                getStringArray(R.array.InitDefaultUserName);
        for (int i = 0; i < userNames.length; i++) {
            user.setUserName(userNames[i]);
            ContentValues contentValues = createParms(user);
            database.insert(getTableNumAndPk()[0], null, contentValues);
        }
    }
}
