package com.bc.ywjphone.readily.business;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.bc.ywjphone.readily.business.base.BaseBusiness;
import com.bc.ywjphone.readily.database.dao.AccountBookDao;
import com.bc.ywjphone.readily.database.dao.PayoutDao;
import com.bc.ywjphone.readily.entity.AccountBooks;
import com.bc.ywjphone.readily.entity.Users;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/14 0014.
 */
public class AccountBookBusiness extends BaseBusiness {

    private AccountBookDao accountBookDao;
    private PayoutDao payoutDao;
    List<AccountBooks> list;

    public AccountBookBusiness(Context context) {
        super(context);
        accountBookDao = new AccountBookDao(context);
        payoutDao = new PayoutDao(context);
    }

    public boolean insertAccountBook(AccountBooks accountBook) {
        return insertOrUpdate(accountBook, true);
    }

    public boolean setIsDefault(int accountBookId) throws Exception {
        //默认账本的条件
        String condition = " isDefault=1";
        ContentValues contentValues = new ContentValues();
        //把默认账本改为非默认
        contentValues.put("isDefault", 0);
        boolean result = accountBookDao.updateAccountBook(condition, contentValues);
        contentValues.clear();
        condition = "accountBookId=" + accountBookId;
        contentValues.put("isDefault", 1);
        boolean result2 = accountBookDao.updateAccountBook(condition, contentValues);
        if (result && result2) {
            return true;
        } else {
            return false;
        }
    }

    public boolean updateAccountBook(AccountBooks accountBook) {
        return insertOrUpdate(accountBook, false);
    }


    public boolean insertOrUpdate(AccountBooks accountBook, boolean isInsert) {
        accountBookDao.beginTransaction();
        boolean result;
        try {
            if (isInsert) {
                result = accountBookDao.insertAccountBook(accountBook);
                //如果插入成功 则把这个实体拿出来获取他的id 进行下一步的操作，，通过姓名查实体
                String condition = " and accountBookName = '" + accountBook.getAccountBookName() + "'";
                accountBook = getAccountBook(condition);
            } else {
                String condition = " accountBookId=" + accountBook.getAccountBookId();
                result = accountBookDao.updateAccountBook(condition, accountBook);
            }
            boolean result2 = true;
            if (accountBook.getIsDefault() == 1 && result) {
                result2 = setIsDefault(accountBook.getAccountBookId());
            }
            if (result && result2) {
                accountBookDao.setTransactionSuccessful();
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            accountBookDao.endTransaction();
        }
    }

    //查找全部用户
    public List<AccountBooks> getAccountBooks(String condition) {
        return accountBookDao.getAccountBooks(condition);
    }

    //获取单个用户
    public AccountBooks getAccountBookByAccountBookId(int accountBookId) {
        //有1=1 所以需要写and语句
        List<AccountBooks> list = accountBookDao.getAccountBooks(" and accountBookId=" + accountBookId);
        if (list != null && list.size() == 1) {
            return list.get(0);
        } else {
            return null;
        }
    }

    //查找所有没有被隐藏的用户
    public List<AccountBooks> getNotHideAccountBook() {
        return accountBookDao.getAccountBooks(" and state=1 ");
    }

    //查找用户是否存在
    public boolean isExitAccountBookByAccountBookName(String accountBookName, Integer accountBookId) {
        String condition = " and state=1 and accountBookName='" + accountBookName + "'";
        if (accountBookId != null) {
            condition += " and accountBookId <> " + accountBookId;
        }
       list = accountBookDao.getAccountBooks(condition);
        if (list != null && list.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    //在这里封装一个方法，用来判断 已经存在的 ，状态为删除的 用户
    public boolean isStateDel(String accountBookName) {
        boolean boo = true;
        for (int i = 0; i < list.size(); i++) {
            Log.e("TAG", "list的长度====="+list.size());
            if (list.get(i).getState() == 0) {
                //在这里进行修改用户状态的语句
                ContentValues contentValues = new ContentValues();
                contentValues.put("state", 1);
                String condition1 = " accountBookName='" + accountBookName + "'";
                accountBookDao.updateAccountBook(condition1, contentValues);
                boo = false;
            }
        }
        return boo;
    }

    //查看单个的方法
    public AccountBooks getAccountBook(String condition) {
        List<AccountBooks> accountBooks = accountBookDao.getAccountBooks(condition);
        return accountBooks.get(0);
    }
    //获取默认账本
    public AccountBooks getDefaultAccountBook() {
        List<AccountBooks> list = accountBookDao.getAccountBooks(" and state=1 and isDefault=1");
        return list.get(0);
    }

    //根据账本Id删除账本
    public boolean deleteAccountBookByAccountId(AccountBooks accountBookId) {
        //逻辑删除
        String condition = " accountBookId=" + accountBookId.getAccountBookId();
        ContentValues contentValues = new ContentValues();
        contentValues.put("state", 0);
        return accountBookDao.updateAccountBook(condition, contentValues);
        //直接删除
        // String condition = " and accountBookId=" + accountBookId;
        // return accountBookDao.deleteAccountBook(condition);
    }
    //这是一个方法
    public String getAccountBookNameByAccountId(int accountBookId) {
    AccountBooks accountBook=getAccountBookByAccountBookId(accountBookId);
        return accountBook==null?null:accountBook.getAccountBookName();
    }
}
