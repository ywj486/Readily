package com.bc.ywjphone.readily.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.TextView;


import com.bc.ywjphone.readily.R;
import com.bc.ywjphone.readily.business.CategoryBusiness;
import com.bc.ywjphone.readily.entity.Categorys;

import java.util.List;

/**
 * Created by Administrator on 2016/12/20.
 */
public class CategoryAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List list;
    private CategoryBusiness categoryBusiness;


    public CategoryAdapter(Context context) {
        this.context = context;
        categoryBusiness = new CategoryBusiness(context);
        //获取全部根节点的未隐藏的类别
        list = categoryBusiness.getNotHideRootCategory();


    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getGroupCount() {
        return list.size();
    }
    /**
     * 获取某组的子成员个数
     */
    @Override
    public int getChildrenCount(int i) {

        Categorys parentCategory = (Categorys) getGroup(i);
        //根据父类下标获取未隐藏的子类数量
        int count = categoryBusiness.getNotHideCountByParentId(parentCategory.getCategoryId());
        return count;
    }

    /**
     * 获得组的对象
     *
     * @param i
     * @return
     */
    @Override
    public Object getGroup(int i) {
        return list.get(i);
    }

    /**
     * 获取子的对象
     *
     * @param i
     * @param i1
     * @return
     */
    @Override
    public Object getChild(int i, int i1) {
        Categorys parentCategory = (Categorys) getGroup(i);
        List<Categorys> childList = categoryBusiness.getNotHideCategoryListByParentId(
                parentCategory.getCategoryId());

        return childList.get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }


    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        GroupHolder holder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.category_group_list_item, null);
            holder = new GroupHolder();
            holder.category_group_name_tv = (TextView) view.findViewById(R.id.category_group_name_tv);
            holder.category_group_count_tv = (TextView) view.findViewById(R.id.category_group_count_tv);

            view.setTag(holder);
        } else {
            holder = (GroupHolder) view.getTag();
        }

        Categorys category = (Categorys) getGroup(i);
        holder.category_group_name_tv.setText(category.getCategoryName());
        int count = getChildrenCount(i);
        holder.category_group_count_tv.setText(context.getString(R.string.textview_text_childern_category
                , new Object[]{count}));

        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        ChildHolder childHolder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.category_children_list_item, null);
            childHolder = new ChildHolder();
            childHolder.category_children_name_tv = (TextView) view.findViewById(R.id.category_children_name_tv);
            view.setTag(childHolder);
        } else {
            childHolder = (ChildHolder) view.getTag();
        }
        Categorys category = (Categorys) getChild(i, i1);
        childHolder.category_children_name_tv.setText(category.getCategoryName());
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }


    class GroupHolder {
        TextView category_group_name_tv, category_group_count_tv;
    }

    class ChildHolder {
        TextView category_children_name_tv;
    }

    public void clear() {
        list.clear();
    }

    public void updateList() {
        list = categoryBusiness.getNotHideRootCategory();
        notifyDataSetChanged();
    }


}
