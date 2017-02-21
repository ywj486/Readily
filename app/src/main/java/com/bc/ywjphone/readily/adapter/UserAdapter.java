package com.bc.ywjphone.readily.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bc.ywjphone.readily.R;
import com.bc.ywjphone.readily.adapter.base.SimpleBaseAdapter;
import com.bc.ywjphone.readily.business.UserBusiness;
import com.bc.ywjphone.readily.entity.Users;

import java.util.List;

/**
 * Created by Administrator on 2016/12/12 0012.
 */
public class UserAdapter extends SimpleBaseAdapter {
    private UserBusiness userBusiness;

    public UserAdapter(Context context) {
        super(context, null);
        userBusiness = new UserBusiness(context);
        setListFromBusiness();
    }

    private void setListFromBusiness() {
        List<Users> list = userBusiness.getNotHideUser();
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
            view = layoutInflater.inflate(R.layout.user_list_item, null);
            holder = new Holder();
            holder.user_item_icon_iv = (ImageView) view.findViewById(R.id.user_item_icon_iv);
            holder.user_item_name_tv = (TextView) view.findViewById(R.id.user_item_name_tv);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        Users user = (Users) datas.get(position);
        holder.user_item_icon_iv.setImageResource(R.drawable.grid_user);
        holder.user_item_name_tv.setText(user.getUserName());
        return view;
    }

    private class Holder {
        ImageView user_item_icon_iv;
        TextView user_item_name_tv;
    }
    public Object getList(int position){
        return  getItem(position);
    }
}
