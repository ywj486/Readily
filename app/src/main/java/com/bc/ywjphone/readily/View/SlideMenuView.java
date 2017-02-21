package com.bc.ywjphone.readily.View;

import android.app.Activity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.bc.ywjphone.readily.R;
import com.bc.ywjphone.readily.adapter.SlideMenuAdapter;
import com.bc.ywjphone.readily.utils.DisplayUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/13 0013.
 */
public class SlideMenuView {
    private Activity activity;
    private List<SlideMenuItem> menuList;
    private boolean isClosed;
    private RelativeLayout bottomBoxLayout;
    private OnSlideMenuListener onSlideMenuListener;

    public SlideMenuView(Activity activity) {
        this.activity = activity;
        initView();
        if(activity instanceof OnSlideMenuListener) {
            this.onSlideMenuListener = (OnSlideMenuListener) activity;
            initVariable();
            initListeners();
        }
    }

    //初始化变量
    private void initVariable() {
        menuList = new ArrayList<>();
        isClosed = true;
    }

    //初始化控件
    private void initView() {
        bottomBoxLayout = (RelativeLayout) activity.findViewById(R.id.inclued_buttom);
    }

    //初始化监听
    private void initListeners() {
        bottomBoxLayout.setOnClickListener(new OnSlideMenuClick());
        //能够获取焦点
        bottomBoxLayout.setFocusableInTouchMode(true);
        bottomBoxLayout.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if(keyCode==KeyEvent.KEYCODE_MENU&& keyEvent.getAction()==KeyEvent.ACTION_DOWN){
                    toggle();
                }
                return false;
            }
        });
    }

    //监听菜单的点击事件
        private class OnSlideMenuClick implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            //调用控制开关的方法
            toggle();
        }
    }

    //打开菜单
    private void open() {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );
        layoutParams.addRule(RelativeLayout.BELOW, R.id.inclued_title);
        bottomBoxLayout.setLayoutParams(layoutParams);
        isClosed = false;
    }

    //关闭菜单
    private void close() {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                DisplayUtil.dip2px(activity, 68)
        );
        //和父控件对齐
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        bottomBoxLayout.setLayoutParams(layoutParams);
        isClosed = true;
    }

    //开关方法控制打开/关闭
    public void toggle() {
        if (isClosed) {
            open();
        } else {
            close();
        }
    }

    //添加菜单项
    public void add(SlideMenuItem slideMenuItem) {
        menuList.add(slideMenuItem);
    }

    //绑定数据源
    public void bindList() {
        SlideMenuAdapter adapter = new SlideMenuAdapter(activity, menuList);
        ListView listview = (ListView) activity.findViewById(R.id.bottom_list);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new OnSlideMenuItemClick());
    }

    private class OnSlideMenuItemClick implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            SlideMenuItem item = (SlideMenuItem) adapterView.getItemAtPosition(position);
            onSlideMenuListener.onSlideMenuItemClick(item);
        }
    }

    public interface OnSlideMenuListener {
        void onSlideMenuItemClick(SlideMenuItem item);
    }

    public void removeBottomBox(){
        RelativeLayout main_rl= (RelativeLayout) activity.findViewById(R.id.main_rl);
        main_rl.removeView(bottomBoxLayout);
        bottomBoxLayout=null;
    }
}
