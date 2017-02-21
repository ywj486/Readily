package com.bc.ywjphone.readily.adapter.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by Administrator on 2016/12/13 0013.
 */
public abstract class SimpleBaseAdapter extends BaseAdapter {
    protected Context context = null;
    protected List datas = null;
    protected LayoutInflater layoutInflater;

    public SimpleBaseAdapter(Context context, List datas) {
        this.context = context;
        this.datas = datas;
        layoutInflater = LayoutInflater.from(context);
    }

    public void setList(List list) {
        datas = list;
    }

    public void clear(){
        datas.clear();
    }

    public  void updateDisplay(){
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return datas != null ? datas.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return datas != null ? datas.get(i) : null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public abstract View getView(int i, View view, ViewGroup viewGroup);
}
