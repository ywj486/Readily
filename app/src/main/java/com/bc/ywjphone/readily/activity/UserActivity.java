package com.bc.ywjphone.readily.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Switch;

import com.bc.ywjphone.readily.R;
import com.bc.ywjphone.readily.View.SlideMenuItem;
import com.bc.ywjphone.readily.View.SlideMenuView;
import com.bc.ywjphone.readily.activity.base.FrameActivity;
import com.bc.ywjphone.readily.adapter.UserAdapter;
import com.bc.ywjphone.readily.business.UserBusiness;
import com.bc.ywjphone.readily.entity.Users;
import com.bc.ywjphone.readily.utils.RegexTools;

public class UserActivity extends FrameActivity implements SlideMenuView.OnSlideMenuListener {

    private ListView user_list_lv;
    private UserAdapter userAdapter;
    private UserBusiness userBusiness;
    private Users user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appendMainBody(R.layout.user);

        initVariable();
        initView();
        initListeners();
        initData();
        createSlideMenu(R.array.SlideMenuUser);
    }

    private void initVariable() {
        userBusiness = new UserBusiness(this);
    }

    private void initView() {
        user_list_lv = (ListView) findViewById(R.id.user_list_lv);
    }

    private void initListeners() {
        //注册上下文菜单
        registerForContextMenu(user_list_lv);
    }

    private void initData() {
        if (userAdapter == null) {
            userAdapter = new UserAdapter(this);
            user_list_lv.setAdapter(userAdapter);
        } else {
            userAdapter.clear();
            userAdapter.updateList();
        }
        setTitle();
    }

    private void setTitle() {
        setTopBarTitle(getString(R.string.title_user,
                new Object[]{userAdapter.getCount()}));
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo)
                menuInfo;
        ListAdapter listAdapter = user_list_lv.getAdapter();
        //获取点击后的用户值
        user = (Users) listAdapter.getItem(acmi.position);
        menu.setHeaderIcon(R.drawable.user_small_icon);
        menu.setHeaderTitle(user.getUserName());
        createContextMenu(menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1://修改
                showUserAddOrUpdate(user);
                break;
            case 2://删除
                delete();
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void delete() {
        String msg = getString(R.string.dialog_message_user_delete,
                new Object[]{user.getUserName()});
        showAlertDialog(R.string.dialog_title_delete, msg, new OnDeleteClickListener());
    }

    private class OnDeleteClickListener implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            boolean result = userBusiness.hideUserByUserId(user.getUserId());
            if (result) {
                //成功就初始化数据，刷新
                initData();
            } else {//失败就提示用户
                showMsg(getString(R.string.tips_delete_fail));
            }
        }
    }

    @Override
    public void onSlideMenuItemClick(SlideMenuItem item) {
        //关闭菜单显示对话框
        slideMenuToggle();
        if (item.getItemId() == 0) {
            showUserAddOrUpdate(null);
        }
    }

    private void showUserAddOrUpdate(Users user) {
        View view = getInflater().inflate(R.layout.user_add_or_update, null);
        EditText user_name_et = (EditText) view.findViewById(R.id.user_name_et);
        String title;
        if (user == null) {
            title = getString(R.string.dialog_title_user,
                    new Object[]{getString(R.string.title_add)});
        } else {
            //修改的时候需要把名字传进去
            user_name_et.setText(user.getUserName());
            title = getString(R.string.dialog_title_user,
                    new Object[]{getString(R.string.title_update)});
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setView(view)
                .setIcon(R.drawable.grid_user)
                .setNeutralButton(getString(R.string.button_save),
                        new OnAddOrUpdateListener(user, user_name_et, true))
                .setNegativeButton(getString(R.string.button_cancel),
                        new OnAddOrUpdateListener(null, null, false)).show();
    }

    private class OnAddOrUpdateListener implements DialogInterface.OnClickListener {
        private Users user;
        private EditText userNameET;
        private boolean isSaveButton;

        public OnAddOrUpdateListener(Users user, EditText userNameET, boolean isSaveButton) {
            this.user = user;
            this.userNameET = userNameET;
            this.isSaveButton = isSaveButton;
        }

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            if (!isSaveButton) {
                setAlertDialogIsClose(dialogInterface, true);
                return;
            }
            if (user == null) {
                user = new Users();
            }
            String username = userNameET.getText().toString().trim();
            //使用正则表达式
            boolean checkResult = RegexTools.RegexName(username);
            if (!checkResult) {
                showMsg(getString(R.string.check_text_chinese_english_num,
                        new Object[]{userNameET.getHint()}));
                setAlertDialogIsClose(dialogInterface, false);
                return;
            } else {
                setAlertDialogIsClose(dialogInterface, true);
            }
            checkResult = userBusiness.isExitUserByUserName(username, user.getUserId());
            if (checkResult) {
                //这里只能判断用户是已经被删除的状态
                boolean del = userBusiness.isStateDel(username);
                if (del) {
                    showMsg(getString(R.string.check_text_user_exist));
                    setAlertDialogIsClose(dialogInterface, false);
                    return;
                } else {
                    Users users = userBusiness.getUser(" and userName='" + username + "'");
                    this.user = users;
                    setAlertDialogIsClose(dialogInterface, true);
                }
            } else {
                setAlertDialogIsClose(dialogInterface, true);
            }
            user.setUserName(username);
            boolean result = false;
            if (user.getUserId() == 0) {
                //新建用户
                result = userBusiness.insertUser(user);
            } else {
                //修改用户信息
                result = userBusiness.updateUser(user);
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
