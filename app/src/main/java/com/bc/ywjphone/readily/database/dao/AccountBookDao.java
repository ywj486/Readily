package com.bc.ywjphone.readily.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bc.ywjphone.readily.R;
import com.bc.ywjphone.readily.database.base.SQLiteDAOBase;
import com.bc.ywjphone.readily.entity.AccountBooks;
import com.bc.ywjphone.readily.utils.DateUtil;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/12/14 0014.
 */
public class AccountBookDao extends SQLiteDAOBase {

    public AccountBookDao(Context context) {
        super(context);
    }

    @Override
    protected String[] getTableNumAndPk() {
        return new String[]{"accountBook", "accountBookId"};
    }

    @Override
    protected Object findModel(Cursor cursor) {
        AccountBooks accountBook = new AccountBooks();
        accountBook.setAccountBookId(cursor.getInt(cursor.getColumnIndex("accountBookId")));
        accountBook.setAccountBookName(cursor.getString(cursor.getColumnIndex("accountBookName")));
        Date createDate = DateUtil.getFormatDate(cursor.getString(
                cursor.getColumnIndex("createData")), "yyyy-MM-dd HH:mm:ss");
        accountBook.setCreateData(createDate);
        accountBook.setState(cursor.getInt(cursor.getColumnIndex("state")));
        accountBook.setIsDefault(cursor.getInt(cursor.getColumnIndex("isDefault")));
        return accountBook;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        StringBuilder sql = new StringBuilder();
        sql.append("Create  TABLE [accountBook](");
        sql.append("         [accountBookId] integer PRIMARY KEY AUTOINCREMENT NOT NULL");
        sql.append(",[accountBookName] varchar(20) NOT NULL");
        sql.append(" ,[createData] datetime NOT NULL");
        sql.append(" ,[state] int NOT NULL");
        sql.append(" ,[isDefault] int NOT NULL");
        sql.append(" )");
        //执行sql语句
        database.execSQL(sql.toString());
        //建表之后默认添加了三条数据
        initDefaultData(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database) {

    }

    public boolean insertAccountBook(AccountBooks accountBook) {
        ContentValues contentValues = createParms(accountBook);
        //插入成功返回 新的id值，失败返回-1
        long newId = getDatabase().insert(getTableNumAndPk()[0], null, contentValues);
        return newId > 0;
    }

    public boolean deleteAccountBook(String condition) {
        return delete(getTableNumAndPk()[0], condition);
    }

    public boolean updateAccountBook(String condition, AccountBooks accountBook) {
        ContentValues contentValues = createParms(accountBook);
        //调用下面重载的方法
        return updateAccountBook(condition, contentValues);
    }
 
    public boolean updateAccountBook(String condition, ContentValues contentValues) {
        return getDatabase().update(getTableNumAndPk()[0],
                contentValues, condition, null) > 0;
    }

    public List<AccountBooks> getAccountBooks(String condition) {
        String sql = "select * from accountBook where 1=1 " + condition;
        return getList(sql);
    }

    public ContentValues createParms(AccountBooks info) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("accountBookName", info.getAccountBookName());
        contentValues.put("createData", DateUtil.getFormattedString(info.getCreateData(),
                "yyyy-MM-dd HH:mm:ss"));
        contentValues.put("state", info.getState());
        contentValues.put("isDefault", info.getIsDefault());
        return contentValues;
    }

    private void initDefaultData(SQLiteDatabase database) {
        AccountBooks accountBook = new AccountBooks();
        String[] accountBookNames = getContext().getResources().
                        getStringArray(R.array.InitDefaultAccountBookName);
        accountBook.setAccountBookName(accountBookNames[0]);
        //插入默认账本为1
        accountBook.setIsDefault(1);
        ContentValues contentValues = createParms(accountBook);
        database.insert(getTableNumAndPk()[0], null, contentValues);
    }
}
