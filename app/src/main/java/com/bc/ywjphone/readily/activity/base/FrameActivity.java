package com.bc.ywjphone.readily.activity.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bc.ywjphone.readily.R;
import com.bc.ywjphone.readily.View.SlideMenuItem;
import com.bc.ywjphone.readily.View.SlideMenuView;

/**
 * Created by Administrator on 2016/12/12 0012.
 * 跟业务有关的
 */
public class FrameActivity extends BaseActivity {
    private SlideMenuView slideMenuView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        View view=findViewById(R.id.app_back);
        view.setOnClickListener(new OnBackListener());
    }
private class OnBackListener implements View.OnClickListener{

    @Override
    public void onClick(View view) {
        finish();
    }
}
    protected void hideTitleBackBtn(){
        findViewById(R.id.app_back).setVisibility(View.GONE);
    }
    protected void appendMainBody(View view) {
        LinearLayout mainBody = (LinearLayout) findViewById(R.id.main_body);
        //动态设置宽高
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
        //给body添加视图和宽高
        mainBody.addView(view, layoutParams);
    }

    protected void appendMainBody(int resId) {
        View view = LayoutInflater.from(this).inflate(resId, null);
        appendMainBody(view);
    }

    //创建滑动菜单
    protected void createSlideMenu(int resId) {
        slideMenuView = new SlideMenuView(this);
        String[] menuItemArray = getResources().getStringArray(resId);
        for (int i = 0; i < menuItemArray.length; i++) {
            SlideMenuItem item = new SlideMenuItem(i, menuItemArray[i]);
            slideMenuView.add(item);

        }
        slideMenuView.bindList();
    }

    //切换菜单开关
    protected void slideMenuToggle() {
        slideMenuView.toggle();
    }

    //创建上下文菜单
    protected void createContextMenu(Menu menu) {
        menu.add(0, 1, 0, R.string.menu_text_update);
        menu.add(0, 2, 0, R.string.menu_text_delete);
    }

    protected void setTopBarTitle(String title) {
        TextView top_title_tv = (TextView) findViewById(R.id.top_title);
        top_title_tv.setText(title);
    }

    protected void removeBottomBox() {
        slideMenuView = new SlideMenuView(this);
        slideMenuView.removeBottomBox();
    }
}

