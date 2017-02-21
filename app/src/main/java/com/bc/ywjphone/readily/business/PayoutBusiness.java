package com.bc.ywjphone.readily.business;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.bc.ywjphone.readily.business.base.BaseBusiness;
import com.bc.ywjphone.readily.database.dao.PayoutDao;
import com.bc.ywjphone.readily.entity.AccountBooks;
import com.bc.ywjphone.readily.entity.Payout;

import java.util.List;

/**
 * Created by Administrator on 2016/12/22 0022.
 */
public class PayoutBusiness extends BaseBusiness {
    private PayoutDao payoutDao;
    AccountBookBusiness accountBookBusiness;

    public PayoutBusiness(Context context) {
        super(context);
        payoutDao = new PayoutDao(context);
        accountBookBusiness = new AccountBookBusiness(context);
    }

    public boolean insertPayout(Payout payout) {

        return payoutDao.insertCategory(payout);
    }

    public boolean updatePayout(Payout payout) {
        return payoutDao.updateCategory("payoutId=" + payout.getPayoutId(), payout);
    }

    //根据账本Id查询消费记录，注意排序
    public List<Payout> getPayoutListByAccountBookId(int accountBookId) {
        String condition = " and accountBookId=" + accountBookId +
                " and state = 1  order by payoutDate desc";
        //Log.e("TAG", "&&&&&&&&&&&-===="+payoutDao.getPayout(condition).get(0).getPayoutDate());
        return payoutDao.getPayout(condition);
    }

    //要求用一条语句，查出两个结果，使用sql内置函数，不许list.size(),sum,count
    public List getPayoutTotalMessage(String payoutDate, int accountBookId) {
        return payoutDao.getCountAndSum(payoutDate, accountBookId);
    }
    public List getPayoutTotalMessage(int accountBookId) {
        return payoutDao.getCountAndSum(accountBookId);
    }
    //根据消费记录Id进行逻辑删除
    public boolean deletePayoutByPayoutId(int payoutId) {
        String condition = " payoutId=" + payoutId;
        ContentValues contentValues = new ContentValues();
        contentValues.put("state", 0);
        return payoutDao.updatePayout(condition, contentValues);
    }
    //根据消费记录Id进行逻辑删除
    public boolean deletePayoutByPayoutId(Payout payoutId) {
        String condition = " payoutId=" + payoutId.getPayoutId();
        ContentValues contentValues = new ContentValues();
        contentValues.put("state", 0);
        Log.e("TAG", "condition=============="+condition);
        return payoutDao.updatePayout(condition, contentValues);
    }

    //获取所有的账本信息
    public List<AccountBooks> getNotHintBookCount() {
        String position = "and state = 1";
        List<AccountBooks> accountBooks = accountBookBusiness.getAccountBooks(position);
        return accountBooks;
    }
    //按照付款人的Id排序取出消费记录
    public List<Payout> getPayoutOrderByPayoutUserId(String condition) {
        condition +=" order by payoutUserId";
        List<Payout> list = payoutDao.getPayout(condition);
        if(list!=null&&list.size()>0) {
            return list;
        }
        return null;
    }
}
