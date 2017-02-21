package com.bc.ywjphone.readily.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.bc.ywjphone.readily.R;
import com.bc.ywjphone.readily.View.SlideMenuItem;
import com.bc.ywjphone.readily.View.SlideMenuView;
import com.bc.ywjphone.readily.activity.base.FrameActivity;
import com.bc.ywjphone.readily.adapter.AccountBookSelectAdapter;
import com.bc.ywjphone.readily.business.AccountBookBusiness;
import com.bc.ywjphone.readily.business.StatisticsBusiness;
import com.bc.ywjphone.readily.entity.AccountBooks;

public class StatisticsActivity extends FrameActivity implements SlideMenuView.OnSlideMenuListener {

    private TextView statistics_result_tv;
    private StatisticsBusiness statisticsBusiness;
    private AccountBookBusiness accountBookBusiness;
    private AccountBooks accountBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appendMainBody(R.layout.statistic);

        initVariable();
        initView();
        initListeners();
        initData();
        setTitle();
        createSlideMenu(R.array.SlideMenuStatistics);
    }

    private void initVariable() {
        statisticsBusiness = new StatisticsBusiness(this);
        accountBookBusiness = new AccountBookBusiness(this);
        accountBook = accountBookBusiness.getDefaultAccountBook();
    }

    private void initView() {
        statistics_result_tv = (TextView) findViewById(R.id.statistic_result_tv);
    }

    private void initListeners() {

    }

    private void initData() {
        showProgreddDialog(R.string.dialog_title_statistics,
                R.string.statistics_waiting_progress);
        new BindDataThread().start();
    }

    private class BindDataThread extends Thread {
        @Override
        public void run() {
            String result = statisticsBusiness.
                    getPayoutUserIdByAccountBookId(accountBook.getAccountBookId());
            Message message = handler.obtainMessage();
            message.obj = result;
            message.what = 1;
            handler.sendMessage(message);
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    String result = (String) msg.obj;
                    statistics_result_tv.setText(result);
                    dismissProgressDialog();
                    break;
            }
        }
    };

    private void setTitle() {
        setTopBarTitle(getString(R.string.title_statistics,
                new Object[]{accountBook.getAccountBookName()}));
    }

    @Override
    public void onSlideMenuItemClick(SlideMenuItem item) {
        //关闭菜单显示对话框
        slideMenuToggle();
        if (item.getItemId() == 0) {//切换账本
            showAccountBookSelectDialog();
        }
        if (item.getItemId() == 1) {//导出表格
            exportData();
        }
    }

    private void showAccountBookSelectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getInflater().inflate(R.layout.dialog_list, null);
        ListView select_lv = (ListView) view.findViewById(R.id.select_lv);
        AccountBookSelectAdapter accountBookSelectAdapter = new AccountBookSelectAdapter(this);
        select_lv.setAdapter(accountBookSelectAdapter);

        builder.setTitle(R.string.button_text_select_account_book)
                .setNegativeButton(R.string.button_text_back, null)
                .setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
        select_lv.setOnItemClickListener(new OnAccountBookItemClickListener(dialog));
    }

    private class OnAccountBookItemClickListener implements AdapterView.OnItemClickListener {
        private AlertDialog dialog;

        public OnAccountBookItemClickListener(AlertDialog dialog) {
            this.dialog = dialog;
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            accountBook = (AccountBooks) adapterView.getAdapter().getItem(i);
            initData();
            dialog.dismiss();
        }
    }

    private void exportData() {
        String result = "";
        try {
            result = statisticsBusiness.exportStatistics(accountBook.getAccountBookId());
        } catch (Exception e) {
            e.printStackTrace();
            //导出失败
            result = getString(R.string.export_data_fail);
        }
        //提示用户
        showMsg(result);
    }
}
