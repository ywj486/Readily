package com.bc.ywjphone.readily.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bc.ywjphone.readily.R;

/**
 * Created by Administrator on 2016/12/12 0012.
 */
public class AppGridAdapter extends BaseAdapter {
    private Context context;
    private Integer[] imgInteger = {R.drawable.b, R.drawable.bb,
            R.drawable.bbb, R.drawable.bbbb, R.drawable.bbbbb,
            R.drawable.bbbbbb};
    private String[] imgString = new String[6];

    public AppGridAdapter(Context context) {
        this.context = context;
        imgString[0] = context.getString(R.string.grid_payout_add);
        imgString[1] = context.getString(R.string.grid_payout_manage);
        imgString[2] = context.getString(R.string.grid_account_manage);
        imgString[3] = context.getString(R.string.grid_statistics_manage);
        imgString[4] = context.getString(R.string.grid_category_manage);
        imgString[5] = context.getString(R.string.grid_user_manage);
    }

    @Override
    public int getCount() {
        return imgString.length;
    }

    @Override
    public Object getItem(int position) {
        return imgString[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        Holder holder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.main_body_item, null);
            holder = new Holder();
            holder.icon_iv = (ImageView) view.findViewById(R.id.main_body_item_icon);
            holder.name_iv = (TextView) view.findViewById(R.id.main_body_item_name);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        holder.icon_iv.setImageResource(imgInteger[position]);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(80, 80);
        holder.icon_iv.setLayoutParams(layoutParams);
        holder.icon_iv.setScaleType(ImageView.ScaleType.FIT_XY);
        holder.name_iv.setText(imgString[position]);

        return view;
    }

    private class Holder {
        ImageView icon_iv;
        TextView name_iv;
    }
}
