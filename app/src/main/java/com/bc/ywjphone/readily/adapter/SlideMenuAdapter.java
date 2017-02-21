package com.bc.ywjphone.readily.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bc.ywjphone.readily.R;
import com.bc.ywjphone.readily.View.SlideMenuItem;
import com.bc.ywjphone.readily.adapter.base.SimpleBaseAdapter;

import java.util.List;

/**
 * Created by Administrator on 2016/12/12 0012.
 */
public class SlideMenuAdapter extends SimpleBaseAdapter {

    public SlideMenuAdapter(Context context, List list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        Holder holder;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.slide_menu_item, null);
            holder = new Holder();
            holder.item_iv = (TextView) view.findViewById(R.id.slide_listview_item);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        SlideMenuItem item=(SlideMenuItem) datas.get(position);
        holder.item_iv.setText(item.getTitle());
        return view;
    }

    private class Holder {
        TextView item_iv;
    }
}
