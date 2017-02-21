package com.bc.ywjphone.readily.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.bc.ywjphone.readily.R;
import com.bc.ywjphone.readily.View.SlideMenuItem;
import com.bc.ywjphone.readily.View.SlideMenuView;
import com.bc.ywjphone.readily.adapter.AppGridAdapter;
import com.bc.ywjphone.readily.activity.base.FrameActivity;
import com.bc.ywjphone.readily.business.DataBackupBusiness;
import com.bc.ywjphone.readily.service.ServiceDatabaseBackup;

import java.util.Date;

public class MainActivity extends FrameActivity implements SlideMenuView.OnSlideMenuListener {

    private GridView main_body_gv;
    private AppGridAdapter gridAdapter;
    private DataBackupBusiness dataBackupBusiness;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideTitleBackBtn();
        appendMainBody(R.layout.main_body);
        initVariable();
        initView();
        initListeners();
        initData();
        createSlideMenu(R.array.SlideMenuActivity);
        startMyService();
    }

    //开启服务
    private void startMyService() {
        Intent intent = new Intent(this, ServiceDatabaseBackup.class);
        startService(intent);
    }

    private void initVariable() {
        gridAdapter = new AppGridAdapter(this);
        dataBackupBusiness = new DataBackupBusiness(this);
    }

    private void initView() {
        main_body_gv = (GridView) findViewById(R.id.main_body_gv);
    }

    private void initListeners() {
        main_body_gv.setOnItemClickListener(new OnGridItemClickListener());
    }

    private void initData() {
        main_body_gv.setAdapter(gridAdapter);
    }

    @Override
    public void onSlideMenuItemClick(SlideMenuItem item) {
        slideMenuToggle();
        if (item.getItemId() == 0) {
            databaseBackup();//数据备份
        }
        if (item.getItemId() == 1) {
            databaseRestore();//数据还原
        }
    }

    //数据还原
    private void databaseRestore() {
        if (dataBackupBusiness.databaseRestore()) {
            showMsg(R.string.dialog_restore_success);
        } else {
            showMsg(R.string.dialog_restore_fail);
        }
    }

    //数据备份
    private void databaseBackup() {
        if (dataBackupBusiness.databaseBackup(new Date())) {
            showMsg(R.string.dialog_backup_success);
        } else {
            showMsg(R.string.dialog_backup_fail);
        }
    }

    private class OnGridItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String menuName = (String) adapterView.getAdapter().getItem(i);
            if (menuName.equals(getString(R.string.grid_user_manage))) {
                openActivity(UserActivity.class);
                return;
            }
            if (menuName.equals(getString(R.string.grid_account_manage))) {
                openActivity(AccountBookActivity.class);
                return;
            }
            if (menuName.equals(getString(R.string.grid_category_manage))) {
                openActivity(CategoryActivity.class);
                return;
            }
            if (menuName.equals(getString(R.string.grid_payout_add))) {
                openActivity(PayoutAddOrUpdateActivity.class);
                return;
            }
            if (menuName.equals(getString(R.string.grid_payout_manage))) {
                openActivity(PayoutActivity.class);
                return;
            }
            if (menuName.equals(getString(R.string.grid_statistics_manage))) {
                openActivity(StatisticsActivity.class);
                return;
            }
        }
    }
}

