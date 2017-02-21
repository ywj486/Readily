package com.bc.ywjphone.readily.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.bc.ywjphone.readily.R;
import com.bc.ywjphone.readily.View.SlideMenuItem;
import com.bc.ywjphone.readily.View.SlideMenuView;
import com.bc.ywjphone.readily.activity.base.FrameActivity;
import com.bc.ywjphone.readily.adapter.AccountBookAdapter;
import com.bc.ywjphone.readily.business.AccountBookBusiness;
import com.bc.ywjphone.readily.business.PayoutBusiness;
import com.bc.ywjphone.readily.entity.AccountBooks;
import com.bc.ywjphone.readily.entity.Payout;
import com.bc.ywjphone.readily.utils.RegexTools;
import com.google.android.gms.playlog.internal.LogEvent;

import java.util.List;

public class AccountBookActivity extends FrameActivity implements SlideMenuView.OnSlideMenuListener {

    private ListView account_book_list_lv;
    private AccountBookAdapter accountBookAdapter;
    private AccountBookBusiness accountBookBusiness;
    private PayoutBusiness payoutBusiness;
    private AccountBooks accountBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appendMainBody(R.layout.account_book_list);

        initVariable();
        initView();
        initListeners();
        initData();
        createSlideMenu(R.array.SlideMenuAccountBook);
    }

    private void initVariable() {
        accountBookBusiness = new AccountBookBusiness(this);
        payoutBusiness=new PayoutBusiness(this);
    }

    private void initView() {
        account_book_list_lv = (ListView) findViewById(R.id.account_book_list_lv);
    }

    private void initListeners() {
        //注册上下文菜单
        registerForContextMenu(account_book_list_lv);
    }

    private void initData() {
        if (accountBookAdapter == null) {
            accountBookAdapter = new AccountBookAdapter(this);
            account_book_list_lv.setAdapter(accountBookAdapter);
        } else {
            accountBookAdapter.clear();
            accountBookAdapter.updateList();
        }
        setTitle();
    }

    private void setTitle() {
        setTopBarTitle(getString(R.string.title_accountBook,
                new Object[]{accountBookAdapter.getCount()}));
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo)
                menuInfo;
        ListAdapter listAdapter = account_book_list_lv.getAdapter();
        //获取点击后的用户值
        accountBook = (AccountBooks) listAdapter.getItem(acmi.position);
        menu.setHeaderIcon(R.drawable.account_book_small_icon);
        menu.setHeaderTitle(accountBook.getAccountBookName());
        createContextMenu(menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1://修改
                showAccountBookAddOrUpdate(accountBook);
                break;
            case 2://删除
                delete();
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void delete() {
        String msg = getString(R.string.dialog_message_accountBook_delete,
                new Object[]{accountBook.getAccountBookName()});
        showAlertDialog(R.string.dialog_title_delete, msg, new OnDeleteClickListener());
    }

//    private class OnDeleteClickListener implements DialogInterface.OnClickListener {
//
//        @Override
//        public void onClick(DialogInterface dialogInterface, int i) {
//            if (accountBook.getIsDefault() != 1) {
//                boolean result = accountBookBusiness.deleteAccountBookByAccountId(accountBook.getAccountBookId());
//                if (result) {
//                    //成功就初始化数据，刷新
//                    initData();
//                } else {//失败就提示用户
//                    showMsg(getString(R.string.tips_delete_fail));
//                }
//            } else {
//                showMsg(getString(R.string.tips_delete_fail_account));
//            }

    /**
     * 删除账本弹出的对话框监听
     */
    private class OnDeleteClickListener implements DialogInterface.OnClickListener{

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {

            //判断该账本是否是默认账本
            if(accountBook.getIsDefault()!=1){
                //判断点击的账本下是否有消费记录
                List<Payout> payoutlist = payoutBusiness.getPayoutListByAccountBookId(accountBook.getAccountBookId());

                boolean result=false;
                //判断该账本下是否有消费记录
                if(payoutlist.size()!=0){
                    //删除消费记录
                    for(int j = 0; j < payoutlist.size(); j++) {
                        result = payoutBusiness.deletePayoutByPayoutId(payoutlist.get(j));
                    }
                }else{
                    result=true;
                }

                boolean result1=false;
                if(result) {
                    //根据账本Id删除 账本
                    result1 = accountBookBusiness.deleteAccountBookByAccountId(accountBook);
                }

                //判断如果消费记录删除成功并且账本删除成功就刷新列表
                if(result1&&result){
                    initData();
                    showMsg(getString(R.string.tips_delete_success));
                } else {
                    showMsg(getString(R.string.tips_delete_fail));
                }
            }else{
                showMsg("默认账本无法删除");
            }
        }
    }

    @Override
    public void onSlideMenuItemClick(SlideMenuItem item) {
        //关闭菜单显示对话框
        slideMenuToggle();
        if (item.getItemId() == 0) {
            showAccountBookAddOrUpdate(null);
        }
    }

    private void showAccountBookAddOrUpdate(AccountBooks accountBook) {
        View view = getInflater().inflate(R.layout.account_book_add_or_update, null);
        EditText accountBook_name_et = (EditText) view.findViewById(R.id.account_book_name_et);
        CheckBox account_book_check_default_cb = (CheckBox) view.findViewById(R.id.account_book_check_default_cb);
        String title;
        if (accountBook == null) {
            title = getString(R.string.dialog_title_account_book,
                    new Object[]{getString(R.string.title_add)});
        } else {
            //修改的时候需要把名字传进去
            accountBook_name_et.setText(accountBook.getAccountBookName());
            title = getString(R.string.dialog_title_account_book,
                    new Object[]{getString(R.string.title_update)});
            if (accountBook.getIsDefault() == 1) {
                account_book_check_default_cb.setChecked(true);
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setView(view)
                .setIcon(R.drawable.grid_account_book)
                .setNeutralButton(getString(R.string.button_save),
                        new OnAddOrUpdateListener(accountBook, accountBook_name_et,
                                account_book_check_default_cb, true))
                .setNegativeButton(getString(R.string.button_cancel),
                        new OnAddOrUpdateListener(null, null, null, false)).show();
    }

    private class OnAddOrUpdateListener implements DialogInterface.OnClickListener {
        private AccountBooks accountBook;
        private EditText accountBookNameET;
        private CheckBox accountBookDefaultCB;
        private boolean isSaveButton;

        public OnAddOrUpdateListener(AccountBooks accountBook, EditText account_book_name_et,
                                     CheckBox account_book_default_cb, boolean isSaveButton) {
            this.accountBook = accountBook;
            this.accountBookNameET = account_book_name_et;
            this.accountBookDefaultCB = account_book_default_cb;
            this.isSaveButton = isSaveButton;
        }

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            if (!isSaveButton) {
                setAlertDialogIsClose(dialogInterface, true);
                return;
            }
            if (accountBook == null) {
                accountBook = new AccountBooks();
            }
            String accountBookName = accountBookNameET.getText().toString().trim();
            //使用正则表达式
            boolean checkResult = RegexTools.RegexName(accountBookName);
            if (!checkResult) {
                showMsg(getString(R.string.check_text_chinese_english_num,
                        new Object[]{accountBookNameET.getHint()}));
                setAlertDialogIsClose(dialogInterface, false);
                return;
            } else {
                setAlertDialogIsClose(dialogInterface, true);
            }
            //验证用户是否存在
            checkResult = accountBookBusiness.isExitAccountBookByAccountBookName(accountBookName, accountBook.getAccountBookId());
            if (checkResult) {
                //这里只能判断用户是已经被删除的状态
                boolean del = accountBookBusiness.isStateDel(accountBookName);
                if (del) {
                    showMsg(getString(R.string.check_text_account_book_exist));
                    setAlertDialogIsClose(dialogInterface, false);
                    return;
                } else {
                    AccountBooks accountBook = accountBookBusiness.getAccountBook(" and accountBookName='" + accountBookName + "'");
                    this.accountBook = accountBook;
                    setAlertDialogIsClose(dialogInterface, true);
                }
            } else {
                setAlertDialogIsClose(dialogInterface, true);
            }
            accountBook.setAccountBookName(accountBookName);
            if (accountBookDefaultCB.isChecked()) {
                accountBook.setIsDefault(1);
            } else {
                if (accountBook.getIsDefault() == 1) {
                    showMsg(getString(R.string.account_dialog_show_massage_false));
                    setAlertDialogIsClose(dialogInterface, false);
                    return;
                } else {
                    accountBook.setIsDefault(0);
                }
            }
            boolean result = false;
            if (accountBook.getAccountBookId() == 0) {
                //新建用户
                result = accountBookBusiness.insertAccountBook(accountBook);
            } else {
                //修改用户信息
                result = accountBookBusiness.updateAccountBook(accountBook);
            }
            if (result) {
                //刷新列表
                initData();
            } else {
                //提示保存失败
                showMsg(getString(R.string.tips_add_fail));
            }
        }
    }
}
