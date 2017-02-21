package com.bc.ywjphone.readily.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.bc.ywjphone.readily.database.base.SQLiteDAOBase;
import com.bc.ywjphone.readily.entity.Payout;
import com.bc.ywjphone.readily.utils.DataUtils;
import com.bc.ywjphone.readily.utils.DateUtil;
import com.google.android.gms.playlog.internal.LogEvent;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/12/14 0014.
 */
public class PayoutDao extends SQLiteDAOBase {

    public PayoutDao(Context context) {
        super(context);
    }

    @Override
    protected String[] getTableNumAndPk() {
        return new String[]{"payout", "payoutId"};
    }

    @Override
    protected Object findModel(Cursor cursor) {
        Payout payout = new Payout();
        payout.setPayoutId(cursor.getInt(cursor.getColumnIndex("payoutId")));
        payout.setAccountBookId(cursor.getInt(cursor.getColumnIndex("accountBookId")));
        payout.setAccountBookName(cursor.getString(cursor.getColumnIndex("accountBookName")));
        payout.setCategoryId(cursor.getInt(cursor.getColumnIndex("categoryId")));
        payout.setCategoryName(cursor.getString(cursor.getColumnIndex("categoryName")));
        payout.setPath(cursor.getString(cursor.getColumnIndex("path")));
        payout.setAmount(new BigDecimal(cursor.getString(cursor.getColumnIndex("amount"))));
        Date payoutDate = DateUtil.getFormatDate(cursor.getString(
                cursor.getColumnIndex("payoutDate")), "yyyy-MM-dd HH:mm:ss");
        payout.setPayoutDate(payoutDate);

        payout.setPayoutType(cursor.getString(cursor.getColumnIndex("payoutType")));
        payout.setPayoutUserId(cursor.getString(cursor.getColumnIndex("payoutUserId")));
        payout.setComment(cursor.getString(cursor.getColumnIndex("comment")));
        Date createDate = DateUtil.getFormatDate(cursor.getString(
                cursor.getColumnIndex("createDate")), "yyyy-MM-dd HH:mm:ss");
        payout.setState(cursor.getInt(cursor.getColumnIndex("state")));

        Log.e("TAG", "我的时间到底是多少===="+payout.getPayoutDate());

        return payout;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        StringBuilder sql = new StringBuilder();
        sql.append("Create  TABLE [payout](");
        sql.append("         [payoutId] integer PRIMARY KEY AUTOINCREMENT NOT NULL");
        sql.append(" ,[accountBookId] int NOT NULL");
        sql.append(" ,[categoryId] int NOT NULL");
        sql.append(" ,[amount] decimal NOT NULL");
        sql.append(" ,[payoutDate] datatime NOT NULL");
        sql.append(",[payoutType] varchar(20) NOT NULL");
        sql.append(" ,[payoutUserId] text NOT NULL");
        sql.append(",[comment] varchar(20) NOT NULL");
        sql.append(" ,[createDate] datatime NOT NULL");
        sql.append(" ,[state] int NOT NULL");
        sql.append(" )");
        //执行sql语句
        database.execSQL(sql.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase database) {

    }

    public boolean insertCategory(Payout payout) {
        ContentValues contentValues = createParms(payout);
        //插入成功返回 新的id值，失败返回-1
        long newid = getDatabase().insert(getTableNumAndPk()[0],
                null, contentValues);
        payout.setPayoutId((int) newid);
        return newid > 0;
    }

    public boolean deletePayout(String condition) {
        return delete(getTableNumAndPk()[0], condition);
    }

    public boolean updateCategory(String condition, Payout payout) {
        ContentValues contentValues = createParms(payout);
        //调用下面重载的方法
        return updateCategory(condition, contentValues);
    }

    public boolean updateCategory(String condition, ContentValues contentValues) {
        return getDatabase().update(getTableNumAndPk()[0],
                contentValues, condition, null) > 0;
    }

    //这个也是修改,修改单个
    public boolean updatePayout(String condition, ContentValues contentValues) {
        // 2、要修改的数据 ， 3、条件    4、向里面填的值
        Log.e("TAG", "condition-----"+condition);
        Log.e("TAG", "contentVaules------"+contentValues);
        return getDatabase().update(getTableNumAndPk()[0],
                contentValues, condition, null) > 0;
    }

    public List<Payout> getPayout(String condition) {
        String sql = "select * from v_payout where 1=1 " + condition;
        return getList(sql);
    }

    //获取数量
    public int getChildCount(String condition) {
        List<Payout> payout = getPayout(condition);
        Log.e("TAG", "payout数量===="+payout.get(0).getPayoutDate());
        return payout.size();
    }

    public ContentValues createParms(Payout info) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("accountBookId", info.getAccountBookId());
        contentValues.put("categoryId", info.getCategoryId());
        contentValues.put("amount", info.getAmount().toString());
        contentValues.put("payoutDate", DateUtil.getFormattedString(info.getPayoutDate(),
                "yyyy-MM-dd HH:mm:ss"));
        contentValues.put("payoutType", info.getPayoutType());
        contentValues.put("payoutUserId", info.getPayoutUserId());
        contentValues.put("comment", info.getComment());
        contentValues.put("createDate", DateUtil.getFormattedString(info.getCreateDate(),
                "yyyy-MM-dd HH:mm:ss"));
        contentValues.put("state", info.getState());
        return contentValues;
    }

    //模糊查询  数量和总和
    public List getCountAndSum(String payoutDate, int accountBookId) {
        String date= DataUtils.getDateTomorrow(payoutDate)+" 00:00:00";
        String sql = "select sum(amount),count(payoutId) from payout " +
                "where state = 1 and accountBookId = " + accountBookId + " and payoutDate=" +"'"+ date+"'";
        Log.e("TAG", "sql==="+sql);
        Cursor cursor = getDatabase().rawQuery(sql, null);
        List list = new ArrayList();
        while (cursor.moveToNext()) {
            list.add(cursor.getString(0));
            list.add(cursor.getString(1));
        }

        Log.e("TAG", "集合#########"+list);
        return list;
    }
    //模糊查询  数量和总和
    public  List getCountAndSum( int accountBookId) {
        String sql = "select sum(amount),count(payoutId) from payout where " +
                "state = 1 and accountBookId = " + accountBookId;
        Cursor cursor = getDatabase().rawQuery(sql, null);
        List list = new ArrayList();
        while (cursor.moveToNext()) {
            list.add(cursor.getString(0));
            list.add(cursor.getString(1));
        }
        return list;
    }
}
