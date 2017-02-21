package com.bc.ywjphone.readily.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bc.ywjphone.readily.R;
import com.bc.ywjphone.readily.adapter.base.SimpleBaseAdapter;
import com.bc.ywjphone.readily.business.AccountBookBusiness;
import com.bc.ywjphone.readily.business.PayoutBusiness;
import com.bc.ywjphone.readily.entity.AccountBooks;
import com.bc.ywjphone.readily.utils.DataUtils;

import java.util.List;

/**
 * Created by Administrator on 2016/12/12 0012.
 */
public class AccountBookAdapter extends SimpleBaseAdapter {
    private AccountBookBusiness accountBookBusiness;
    private PayoutBusiness payoutBusiness;
    List<AccountBooks> list;
    int position;

    public AccountBookAdapter(Context context) {
        super(context, null);
        accountBookBusiness = new AccountBookBusiness(context);
        payoutBusiness = new PayoutBusiness(context);
        setListFromBusiness();
    }

    private void setListFromBusiness() {
        list = accountBookBusiness.getNotHideAccountBook();
        setList(list);
    }

    public void updateList() {
        setListFromBusiness();
        updateDisplay();
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        Holder holder;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.account_book_list_item, null);
            holder = new Holder();
            holder.account_book_item_icon_iv = (ImageView) view.findViewById(R.id.account_book_item_icon_iv);
            holder.account_book_item_name_tv = (TextView) view.findViewById(R.id.account_book_item_name_tv);
            holder.account_book_item_money_tv = (TextView) view.findViewById(R.id.account_book_item_money_tv);
            holder.account_book_item_total_tv = (TextView) view.findViewById(R.id.account_book_item_total_tv);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        AccountBooks accountBook = (AccountBooks) datas.get(position);
        if (accountBook.getIsDefault() == 1) {
            holder.account_book_item_icon_iv.setImageResource(R.drawable.account_book_default);
        } else {
            holder.account_book_item_icon_iv.setImageResource(R.drawable.account_book_icon);
        }
        holder.account_book_item_name_tv.setText(accountBook.getAccountBookName());
        List mm = payoutBusiness.getPayoutTotalMessage(list.get(position).getAccountBookId());
        //如果消费金额值为null时，则赋值为0，反之使用获取到的值
        if (mm.get(0) == null) {
            holder.account_book_item_money_tv.setText("共0笔");
            holder.account_book_item_total_tv.setText("合计消费" + 0 + "元");
        } else {
            holder.account_book_item_money_tv.setText("共" + mm.get(1) + "笔");
            holder.account_book_item_total_tv.setText("合计消费" + mm.get(0) + "元");
        }
        return view;
    }

    private class Holder {
        ImageView account_book_item_icon_iv;
        TextView account_book_item_name_tv;
        TextView account_book_item_money_tv;
        TextView account_book_item_total_tv;
    }
}
