package com.bc.ywjphone.readily.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import com.bc.ywjphone.readily.R;
import com.bc.ywjphone.readily.View.SlideMenuItem;
import com.bc.ywjphone.readily.View.SlideMenuView;
import com.bc.ywjphone.readily.activity.base.FrameActivity;
import com.bc.ywjphone.readily.adapter.CategoryAdapter;
import com.bc.ywjphone.readily.business.CategoryBusiness;
import com.bc.ywjphone.readily.entity.CategoryTotal;
import com.bc.ywjphone.readily.entity.Categorys;

import java.io.Serializable;
import java.util.List;

public class CategoryActivity extends FrameActivity implements SlideMenuView.OnSlideMenuListener {

    private ExpandableListView category_list_elv;
    private CategoryAdapter categoryAdapter;
    private CategoryBusiness categoryBusiness;
    private Categorys category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appendMainBody(R.layout.category);

        initVariable();
        initView();
        initListeners();
        initData();
        createSlideMenu(R.array.SlideMenuCategory);
    }

    private void initVariable() {
        categoryBusiness = new CategoryBusiness(this);
    }

    private void initView() {
        category_list_elv = (ExpandableListView) findViewById(R.id.category_list_lv);
    }

    private void initListeners() {
        //注册上下文菜单
        registerForContextMenu(category_list_elv);
    }

    private void initData() {
      if (categoryAdapter == null) {
            categoryAdapter = new CategoryAdapter(this);
            category_list_elv.setAdapter(categoryAdapter);
        } else {
            categoryAdapter.clear();
            categoryAdapter.updateList();
        }
        setTitle();
    }

    private void setTitle() {
        int count=categoryBusiness.getNotHideCount();//包括子类和主类的总数
        setTopBarTitle(getString(R.string.title_category,
                new Object[]{count}));
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        //获取菜单信息
        ExpandableListView.ExpandableListContextMenuInfo elcm =
                (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;
        //获取菜单的位置信息
        long position = elcm.packedPosition;
        //得到菜单类型，知道是子还是组
        int type = ExpandableListView.getPackedPositionType(position);
        //根据位置信息得到组位置
        int groupPosition = ExpandableListView.getPackedPositionGroup(position);
        switch (type) {
            case ExpandableListView.PACKED_POSITION_TYPE_GROUP://是组
                //根据组的位置取得实体
                category = (Categorys) categoryAdapter.getGroup(groupPosition);
                break;
            case ExpandableListView.PACKED_POSITION_TYPE_CHILD://是子
                //获取子位置
                int childPosition = ExpandableListView.getPackedPositionChild(position);
                category = (Categorys) categoryAdapter.getChild(groupPosition, childPosition);
                break;
        }


        menu.setHeaderIcon(R.drawable.category_small_icon);
        if (category != null) {
            menu.setHeaderTitle(category.getCategoryName());
        }
        createContextMenu(menu);
        menu.add(0, 3, 0, R.string.catetory_total);
        if (categoryAdapter.getChildrenCount(groupPosition) != 0 && category.getParentId() == 0) {
            menu.findItem(2).setEnabled(false);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case 1://修改
              intent=new Intent(this,CategoryAddOrUpdateActivity.class);
                intent.putExtra("category",category);//需要在实体类进行序列化
                startActivityForResult(intent,1);
                break;
            case 2://删除
                delete(category);
                break;
            case 3://统计类别
                List<CategoryTotal> list=categoryBusiness.getCategoryTotalByParentId(category.getParentId());
                intent=new Intent();
                //list进行强转 需要实体类实现序列化接口
                intent.putExtra("total",(Serializable) list);
                intent.setClass(this,CategoryCharActivity.class);
                startActivity(intent);
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       initData();//刷新列表
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void delete(Categorys category) {
        String msg = getString(R.string.dialog_message_category_delete,
                new Object[]{category.getCategoryName()});
        showAlertDialog(R.string.dialog_title_delete, msg, new OnDeleteClickListener());
    }

    private class OnDeleteClickListener implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            boolean result = categoryBusiness.hideCatrgoryByPath(category.getPath());
            if(result) {
                //成功就初始化数据，刷新
                initData();
            }else {//失败就提示用户
                showMsg(getString(R.string.tips_delete_fail));
            }
        }
    }

    @Override
    public void onSlideMenuItemClick(SlideMenuItem item) {
        //关闭菜单显示对话框
        slideMenuToggle();
        if (item.getItemId() == 0) {
            Intent intent=new Intent(this,CategoryAddOrUpdateActivity.class);
            startActivityForResult(intent,1);
            return;
        }
        if(item.getItemId()==1) {
            List<CategoryTotal> list=categoryBusiness.categoryTotalByRootCategory();
            Intent intent=new Intent(this,CategoryCharActivity.class);
            //list进行强转 需要实体类实现序列化接口
            intent.putExtra("total",(Serializable) list);
            startActivity(intent);
            return;
        }
    }
}
