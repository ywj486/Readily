package com.bc.ywjphone.readily.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bc.ywjphone.readily.R;
import com.bc.ywjphone.readily.database.base.SQLiteDAOBase;
import com.bc.ywjphone.readily.entity.Categorys;
import com.bc.ywjphone.readily.utils.DateUtil;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/12/14 0014.
 */
public class CategoryDao extends SQLiteDAOBase {

    public CategoryDao(Context context) {
        super(context);
    }

    @Override
    protected String[] getTableNumAndPk() {
        return new String[]{"category", "categoryId"};
    }

    @Override
    protected Object findModel(Cursor cursor) {
        Categorys category = new Categorys();
        category.setCategoryId(cursor.getInt(cursor.getColumnIndex("categoryId")));
        category.setCategoryName(cursor.getString(cursor.getColumnIndex("categoryName")));
        category.setParentId(cursor.getInt(cursor.getColumnIndex("parentId")));
        category.setPath(cursor.getString(cursor.getColumnIndex("path")));
        Date createDate = DateUtil.getFormatDate(cursor.getString(
                cursor.getColumnIndex("createData")), "yyyy-MM-dd HH:mm:ss");
        category.setCreateData(createDate);
        category.setState(cursor.getInt(cursor.getColumnIndex("state")));
        return category;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        StringBuilder sql = new StringBuilder();
        sql.append("Create  TABLE [category](");
        sql.append("         [categoryId] integer PRIMARY KEY AUTOINCREMENT NOT NULL");
        sql.append(",[categoryName] varchar(20) NOT NULL");
        sql.append(" ,[createData] datatime NOT NULL");
        sql.append(" ,[state] int NOT NULL");
        sql.append(" ,[parentId] int NOT NULL");
        sql.append(",[path] text NOT NULL");
        sql.append(" )");
        //执行sql语句
        database.execSQL(sql.toString());
        //建表之后默认添加了三条数据
        initDefaultData(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database) {

    }

    public boolean insertCategory(Categorys category) {
        ContentValues contentValues = createParms(category);
        //插入成功返回 新的id值，失败返回-1
        long newid = getDatabase().insert(getTableNumAndPk()[0], null, contentValues);
        category.setCategoryId((int)newid);
        return newid > 0;
    }

    public boolean deleteCategory(String condition) {
        return delete(getTableNumAndPk()[0], condition);
    }

    public boolean updateCategory(String condition, Categorys category) {
        ContentValues contentValues = createParms(category);
        //调用下面重载的方法
        return updateCategory(condition, contentValues);
    }

    public boolean updateCategory(String condition, ContentValues contentValues) {
        return getDatabase().update(getTableNumAndPk()[0],
                contentValues, condition, null) > 0;
    }

    public List<Categorys> getCategorys(String condition) {
        String sql = "select * from category where 1=1 " + condition;
        return getList(sql);
    }
    //获取数量
    public int getChildCount(String condition){
        List<Categorys> category = getCategorys(condition);
        return category.size();
    }
    public ContentValues createParms(Categorys info) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("categoryName", info.getCategoryName());
        contentValues.put("createData", DateUtil.getFormattedString(info.getCreateData(),
                "yyyy-MM-dd HH:mm:ss"));
        contentValues.put("state", info.getState());
        contentValues.put("parentId", info.getParentId());
        contentValues.put("path", info.getPath());
        return contentValues;
    }

    private void initDefaultData(SQLiteDatabase database) {
        Categorys category = new Categorys();
        category.setPath("");
        category.setParentId(0);
        String[] categoryNames = getContext().getResources().
                getStringArray(R.array.InitDefaultCategoryName);
        for (int i = 0; i < categoryNames.length; i++) {
            category.setCategoryName(categoryNames[i]);
            ContentValues contentValues = createParms(category);
            long newid = database.insert(getTableNumAndPk()[0], null, contentValues);
            category.setPath(newid + ".");
            contentValues = createParms(category);
            database.update(getTableNumAndPk()[0], contentValues, "categoryId=?",
                    new String[]{newid + ""});
        }
    }
}
