package com.bc.ywjphone.readily.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.bc.ywjphone.readily.R;
import com.bc.ywjphone.readily.View.SlideMenuItem;
import com.bc.ywjphone.readily.View.SlideMenuView;
import com.bc.ywjphone.readily.activity.base.FrameActivity;
import com.bc.ywjphone.readily.adapter.AccountBookAdapter;
import com.bc.ywjphone.readily.adapter.PayoutAdapter;
import com.bc.ywjphone.readily.business.AccountBookBusiness;
import com.bc.ywjphone.readily.business.PayoutBusiness;
import com.bc.ywjphone.readily.entity.AccountBooks;
import com.bc.ywjphone.readily.entity.Payout;

import java.util.List;

public class PayoutActivity extends FrameActivity implements SlideMenuView.OnSlideMenuListener {

    private ListView payout_list_lv;
    private PayoutAdapter payoutAdapter;
    private PayoutBusiness payoutBusiness;
    private AccountBookBusiness accountBookBusiness;
    Payout payout;
    AccountBooks defaultAccountBook;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appendMainBody(R.layout.payout_list);
        context = this;
        initVariable();
        initView();
        initListeners();
        initData();
        createSlideMenu(R.array.SlideMenuPayout);
    }


    private void initVariable() {
        accountBookBusiness = new AccountBookBusiness(this);
        payoutBusiness = new PayoutBusiness(this);
        payout = new Payout();
        //获取默认账本
        defaultAccountBook = accountBookBusiness.getDefaultAccountBook();
    }


    private void initListeners() {
        //注册上下文菜单
        registerForContextMenu(payout_list_lv);
    }

    private void initData() {
        //根据账本的id 来获得 账本 payout的信息
        //绑定Adapter，设置标题
//        payoutAdapter = new PayoutAdapter(this, defaultAccountBook.getAccountBookId());
//        payout_list_lv.setAdapter(payoutAdapter);
//        setTitle(defaultAccountBook);
        if (payoutAdapter == null) {
            payoutAdapter = new PayoutAdapter(this, defaultAccountBook.getAccountBookId());
        } else {
            payoutAdapter.clear();
            payoutAdapter.updateList();
        }
        payout_list_lv.setAdapter(payoutAdapter);
        setTitle(defaultAccountBook);
    }

    private void setTitle(AccountBooks accountBook) {
        //替换多条  第二个参数是数量
        String stringFormat = "查询消费-%s(%s)";
        setTopBarTitle(String.format(stringFormat, accountBook.getAccountBookName(), payoutAdapter.getCount()));
    }

    private void initView() {
        payout_list_lv = (ListView) findViewById(R.id.payout_list_lv);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        //得到菜单信息  ,取出消费记录 设置菜单图标和标题 创建菜单
        AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo)
                menuInfo;
        ListAdapter listAdapter = payout_list_lv.getAdapter();
        //获取点击后的用户值
        payout = (Payout) listAdapter.getItem(acmi.position);
        //通过id找到账本
        String condition = " and accountBookId = " + payout.getAccountBookId();
        payout.setAccountBookName(accountBookBusiness.getAccountBook(condition)
                .getAccountBookName());
        //设置图片
        menu.setHeaderIcon(R.drawable.payout_small_icon);
        //设置标题
        menu.setHeaderTitle(payout.getCategoryName());
        //父类的方法 给menu项设置 id
        createContextMenu(menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1://修改
                showPayoutAddOrUpdate(payout);
                break;
            case 2://删除
                delete(payout);
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void showPayoutAddOrUpdate(Payout payout) {
        if (payout != null) {
            //说明是修改
            Intent intent = new Intent(this, PayoutAddOrUpdateActivity.class);
            intent.putExtra("payout", payout);
            startActivityForResult(intent,1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1) {
            initData();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void delete(Payout payout) {
        //提示信息：你确定要删除某消费记录么？
        //显示对话框 并监听
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_title_delete)
                .setPositiveButton("确定", new OnDeleteClickListener())
                .setNegativeButton("取消", null)
                .show();
    }


    private class OnDeleteClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            //根据消费记录Id进行逻辑删除
            boolean result = payoutBusiness.deletePayoutByPayoutId(payout.getPayoutId());
            if (result) {
                //提示删除失败刷新适配器
                showMsg(getString(R.string.tips_add_success));
                initData();
            } else {
                showMsg(getString(R.string.tips_delete_fail));
            }
        }
    }

    //菜单项的点击
    @Override
    public void onSlideMenuItemClick(SlideMenuItem item) {

        //关闭滑动菜单，如果点击 第0个菜单，弹出选择账本对话框
        //点击关闭
        AlertDialog show1 = null;
        View view = LayoutInflater.from(this).inflate(R.layout.account_book_list, null);
        ListView account_book_list_lv = (ListView) view.findViewById(R.id.account_book_list_lv);
        //获得所有账本信息
        List<AccountBooks> notHintBookCount = payoutBusiness.getNotHintBookCount();
        AccountBookAdapter accountBookAdapter = new AccountBookAdapter(context);
        account_book_list_lv.setAdapter(accountBookAdapter);
        slideMenuToggle();
        if (item.getItemId() == 0) {
            show1 = new AlertDialog.Builder(this)
                    .setTitle(R.string.button_text_select_account_book)
                    .setView(view)
                    .setNegativeButton(R.string.button_text_back, null)
                    .show();
        }
        account_book_list_lv.setOnItemClickListener(new setViewOnClickListenerItem
                (show1, account_book_list_lv, notHintBookCount));
    }

    private class setViewOnClickListenerItem implements ListView.OnItemClickListener {
        AlertDialog show1;
        ListView account_book_list_lv;
        List<AccountBooks> notHintBookCount;

        public setViewOnClickListenerItem(AlertDialog show1, ListView account_book_list_lv,
                                          List<AccountBooks> notHintBookCount) {
            this.show1 = show1;
            this.account_book_list_lv = account_book_list_lv;
            this.notHintBookCount = notHintBookCount;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            payoutAdapter = new PayoutAdapter(context, notHintBookCount.get(position).getAccountBookId());
            payout_list_lv.setAdapter(payoutAdapter);
            //设置头标题
            setTitle(notHintBookCount.get(position));
            show1.dismiss();
        }
    }
}