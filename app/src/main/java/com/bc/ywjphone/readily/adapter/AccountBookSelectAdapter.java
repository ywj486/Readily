package com.bc.ywjphone.readily.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bc.ywjphone.readily.R;
import com.bc.ywjphone.readily.adapter.base.SimpleBaseAdapter;
import com.bc.ywjphone.readily.business.AccountBookBusiness;
import com.bc.ywjphone.readily.business.PayoutBusiness;
import com.bc.ywjphone.readily.entity.AccountBooks;

import java.util.List;

public class AccountBookSelectAdapter extends SimpleBaseAdapter {
    private AccountBookBusiness accountBookBusiness;
    private PayoutBusiness payoutBusiness;
    private List<AccountBooks> list;

    public AccountBookSelectAdapter(Context context) {
        super(context, null);
        payoutBusiness = new PayoutBusiness(context);
        accountBookBusiness = new AccountBookBusiness(context);
        setListFromBusiess();
    }

    public void updateList() {
        setListFromBusiess();
        updateDisplay();
    }

    private void setListFromBusiess() {
        list = accountBookBusiness.getNotHideAccountBook();
        setList(list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.account_book_select_list_item,
                    null);
            holder = new Holder();
            holder.account_book_item_icon_iv = (ImageView) convertView
                    .findViewById(R.id.account_book_item_icon_iv);
            holder.account_book_item_name_tv = (TextView) convertView.findViewById(R.id.account_book_item_name_tv);
            convertView.setTag(holder);

        } else {
            holder = (Holder) convertView.getTag();
        }
        AccountBooks accountBook = (AccountBooks) datas.get(position);
        if (accountBook.getIsDefault() == 1) {
            holder.account_book_item_icon_iv.setImageResource(R.drawable.account_book_default);
        } else {
            holder.account_book_item_icon_iv.setImageResource(R.drawable.account_book_icon);
        }
        holder.account_book_item_name_tv.setText(accountBook.getAccountBookName());
        return convertView;
    }

    class Holder {
        ImageView account_book_item_icon_iv;
        TextView account_book_item_name_tv;
    }
}
