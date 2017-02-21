package com.bc.ywjphone.readily.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.bc.ywjphone.readily.R;
import com.bc.ywjphone.readily.View.DialogNumberKeyBoard;
import com.bc.ywjphone.readily.activity.base.FrameActivity;
import com.bc.ywjphone.readily.adapter.AccountBookSelectAdapter;
import com.bc.ywjphone.readily.adapter.CategoryAdapter;
import com.bc.ywjphone.readily.adapter.UserAdapter;
import com.bc.ywjphone.readily.business.AccountBookBusiness;
import com.bc.ywjphone.readily.business.CategoryBusiness;
import com.bc.ywjphone.readily.business.PayoutBusiness;
import com.bc.ywjphone.readily.business.UserBusiness;
import com.bc.ywjphone.readily.entity.AccountBooks;
import com.bc.ywjphone.readily.entity.Categorys;
import com.bc.ywjphone.readily.entity.Payout;
import com.bc.ywjphone.readily.entity.Users;
import com.bc.ywjphone.readily.utils.DataUtils;
import com.bc.ywjphone.readily.utils.DateUtil;
import com.bc.ywjphone.readily.utils.RegexTools;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class PayoutAddOrUpdateActivity extends FrameActivity
        implements View.OnClickListener {

    private Button payout_save_btn, payout_cancel_btn;
    private Button payout_select_user_btn, payout_select_type_btn;
    private Button payout_select_date_btn, payout_select_category_btn;
    private Button payout_enter_amount_btn, payout_select_account_book_btn;
    private EditText payout_select_account_book_et, payout_enter_amount_et;
    private EditText payout_select_date_et, payout_select_type_et;
    private EditText payout_select_user_et, payout_comment_et;
    private AutoCompleteTextView payout_select_category_actv;

    private Payout payout;
    private AccountBooks accountBook;

    private PayoutBusiness payoutBusiness;
    private AccountBookBusiness accountBookBusiness;
    private CategoryBusiness categoryBusiness;
    private UserBusiness userBusiness;

    private Integer accountBookId;
    private Integer categoryId;
    private String payoutUserId;// 保存消费人id以","分隔
    private String payoutTypeArray[]; // 计算方式数组
    private List<LinearLayout> itemColor; // 保存消费人
    private List<Users> userSelectedList; // 选择消费人
    List<Categorys> notHintCategory;
    List<String > listName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appendMainBody(R.layout.payout_add_or_update);
        //移除菜单
        removeBottomBox();
        initVariable();
        initView();
        initListeners();
        initData();
        setTitle();
    }

    private void setTitle() {
        String title;
        if (payout == null) {
            title = getString(R.string.title_payout_add_or_update,
                    new Object[]{getString(R.string.title_add)});
        } else {
            title = getString(R.string.title_payout_add_or_update,
                    new Object[]{getString(R.string.title_update)});
            bindData(payout);
            // 绑定数据
        }
        setTopBarTitle(title);
        // 标题传到TopBarTitle上面
    }

    private void initVariable() {
        payout = (Payout) getIntent().getSerializableExtra("payout");
        payoutBusiness = new PayoutBusiness(this);
        categoryBusiness = new CategoryBusiness(this);
        userBusiness = new UserBusiness(this);
        accountBookBusiness = new AccountBookBusiness(this);
        accountBook = accountBookBusiness.getDefaultAccountBook();// 获取默认账本

    }

    private void initView() {
        payout_cancel_btn = (Button) findViewById(R.id.payout_cancel_btn);
        payout_save_btn = (Button) findViewById(R.id.payout_save_btn);
        payout_select_account_book_btn = (Button) findViewById(R.id.payout_select_account_book_btn);
        payout_enter_amount_btn = (Button) findViewById(R.id.payout_enter_amount_btn);
        payout_select_category_btn = (Button) findViewById(R.id.payout_select_category_btn);
        payout_select_date_btn = (Button) findViewById(R.id.payout_select_date_btn);
        payout_select_type_btn = (Button) findViewById(R.id.payout_select_type_btn);
        payout_select_user_btn = (Button) findViewById(R.id.payout_select_user_btn);
        payout_select_account_book_et = (EditText) findViewById(R.id.payout_select_account_book_et);
        payout_enter_amount_et = (EditText) findViewById(R.id.payout_enter_amount_et);
        payout_select_date_et = (EditText) findViewById(R.id.payout_select_date_et);
        payout_select_type_et = (EditText) findViewById(R.id.payout_select_type_et);
        payout_select_user_et = (EditText) findViewById(R.id.payout_select_user_et);
        payout_comment_et = (EditText) findViewById(R.id.payout_comment_et);
        payout_select_category_actv = (AutoCompleteTextView) findViewById(R.id.payout_select_category_actv);
    }

    private void initListeners() {
        payout_cancel_btn.setOnClickListener(this);
        payout_save_btn.setOnClickListener(this);
        payout_select_account_book_btn.setOnClickListener(this);
        payout_enter_amount_btn.setOnClickListener(this);
        payout_select_category_btn.setOnClickListener(this);
        payout_select_date_btn.setOnClickListener(this);
        payout_select_type_btn.setOnClickListener(this);
        payout_select_user_btn.setOnClickListener(this);
        payout_select_category_actv
                .setOnItemClickListener(new OnAutoCompleteTextViewItemClickListener());
    }

    private void initData() {
        accountBookId = accountBook.getAccountBookId();
        payout_select_account_book_et.setText(accountBook.getAccountBookName());
        payout_select_category_actv.setAdapter(categoryBusiness.getAllCategoryArrayAdapter());
        payout_select_date_et.setText(DateUtil.getFormattedString(new Date(), "yyyy-MM-dd"));
        payoutTypeArray = getResources().getStringArray(R.array.payoutType);
        payout_select_type_et.setText(payoutTypeArray[0]);
    }

    //修改时绑定数据
    private void bindData(Payout payout) {
        payout_select_account_book_et.setText(payout.getAccountBookName());
        accountBookId = payout.getAccountBookId();
        payout_enter_amount_et.setText(payout.getAmount().toString());
        payout_select_category_actv.setText(payout.getCategoryName());
        categoryId = payout.getCategoryId();
        payout_select_date_et.setText(DateUtil.getFormattedString(payout.getPayoutDate(),
                "yyyy-MM-dd"));
        payout_select_type_et.setText(payout.getPayoutType());
        Log.e("TAG", "userId===="+payout.getPayoutUserId());
        String userName = userBusiness.getUserNameByUserId(payout
                .getPayoutUserId());
        payout_select_user_et.setText(userName);
        payoutUserId = payout.getPayoutUserId();
        payout_comment_et.setText(payout.getComment());
    }

    // 添加或者修改
    private void addOrEditPayout() {
        boolean checkResult = checkData();
        if (!checkResult) {
            return;
        }
        if (payout == null) {
            payout = new Payout();
        }
        payout.setAccountBookId(accountBookId);
        payout.setCategoryId(categoryId);
        payout.setAmount(new BigDecimal(payout_enter_amount_et.getText().toString().trim()));
        payout.setPayoutDate(DateUtil.getFormatDate(payout_select_date_et.getText().toString()
                .trim(), "yyyy-MM-dd"));
        payout.setPayoutType(payout_select_type_et.getText().toString().trim());
        payout.setPayoutUserId(payoutUserId);
        payout.setComment(payout_comment_et.getText().toString().trim());

        boolean result = false;
        if (payout.getPayoutId() == 0) {
            result = payoutBusiness.insertPayout(payout);
        } else {
            result = payoutBusiness.updatePayout(payout);
        }
        if (result) {
            showMsg(getString(R.string.tips_add_success));
            finish();
        } else {
            showMsg(getString(R.string.tips_add_fail));
        }

    }

    private boolean checkData() {
        //判断金额必须是数字，不能小数点后两位，可以是整数，一位或两位
        boolean checkResult = RegexTools.isMoney(payout_enter_amount_et.getText().toString().trim());
        if (!checkResult) {
            //获取焦点让用户重填
            payout_enter_amount_et.requestFocus();
            showMsg(getString(R.string.check_text_money));
            return false;
        }
        // 非空验证
        checkResult = RegexTools.isNull(categoryId);
        if (checkResult) {
            //是否能获得焦点
            payout_select_category_btn.setFocusable(true);
            //可以在TouchMode模式下还可以获取焦点
            payout_select_category_btn.setFocusableInTouchMode(true);
            payout_select_category_btn.requestFocus();
            showMsg(getString(R.string.check_text_category_is_null));
            return false;
        }
        // 日期验证,不许向未来穿越
        Log.e("TAG", "获取的时间是====="+payout_select_date_et.getText().toString());
        checkResult = DataUtils.isAfter(DataUtils.strToDateLong(payout_select_date_et.getText().toString()));
        if (checkResult) {
            //是否能获得焦点
            payout_select_date_btn.setFocusable(true);
            //可以在TouchMode模式下还可以获取焦点
            payout_select_date_btn.setFocusableInTouchMode(true);
            payout_select_date_btn.requestFocus();
            showMsg(getString(R.string.check_text_date_is_after));
            return false;
        }
        if (payoutUserId == null) {
            payout_select_user_btn.setFocusable(true);
            payout_select_user_btn.setFocusableInTouchMode(true);
            payout_select_user_btn.requestFocus();
            showMsg(getString(R.string.check_text_payout_user_is_null));
            return false;
        }
        String payoutType = payout_select_type_et.getText().toString();
        //均分或借贷
        if (payoutType.equals(payoutTypeArray[1])
                || payoutType.equals(payoutTypeArray[0])) {
            if (payoutUserId.split(",").length <= 1) {
                payout_select_type_btn.setFocusable(true);
                payout_select_type_btn.setFocusableInTouchMode(true);
                payout_select_type_btn.requestFocus();
                showMsg(getString(R.string.check_text_payout_user));
                return false;
            }
        } else {//个人
            if ("".equals(payoutUserId)) {
                payout_select_type_btn.setFocusable(true);
                payout_select_type_btn.setFocusableInTouchMode(true);
                payout_select_type_btn.requestFocus();
                showMsg(getString(R.string.check_text_payout_user2));
                return false;
            }
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.payout_save_btn:
                addOrEditPayout();
                break;
            case R.id.payout_cancel_btn:
                finish();
                break;
            case R.id.payout_select_account_book_btn:// 选择账本
                showAcountBookSelectDialog();
                break;
            case R.id.payout_enter_amount_btn:// 输入金额
                new DialogNumberKeyBoard(this,payout_enter_amount_et);
                break;
            case R.id.payout_select_category_btn:// 选择类别
                showCategorySelectDialog();
                break;
            case R.id.payout_select_date_btn:// 选择日期
                // 获取日历对象
                Calendar calendar = Calendar.getInstance();
                // 年 月和日
                showDateSelectDialog(calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
                break;
            case R.id.payout_select_type_btn:// 计算方式
                showPayoutTypeSelectDialog();
                break;
            case R.id.payout_select_user_btn:// 选择消费人
                showUserSelectDialog(payout_select_type_et.getText().toString().trim());
                break;

            default:
                break;
        }

    }

    //自动完成的监听
    private class OnAutoCompleteTextViewItemClickListener implements
            AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
           Categorys category = (Categorys) parent.getAdapter()
                    .getItem(position);
          categoryId = category.getCategoryId();
        }
    }

    //选择账本的对话框
    private void showAcountBookSelectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_list,
                null);
        ListView select_lv = (ListView) v
                .findViewById(R.id.select_lv);
        AccountBookSelectAdapter adapter = new AccountBookSelectAdapter(this);
        select_lv.setAdapter(adapter);
        builder.setTitle(R.string.button_text_select_account_book)
                .setNegativeButton(R.string.button_text_back, null)
                .setView(v);
        AlertDialog dialog = builder.create();
        dialog.show();
        select_lv.setOnItemClickListener(new OnAccountBookItemClickListener(dialog));
    }

    private class OnAccountBookItemClickListener implements AdapterView.OnItemClickListener {
        private AlertDialog alertDialog;

        public OnAccountBookItemClickListener(AlertDialog alertDialog) {
            this.alertDialog = alertDialog;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
            AccountBooks accountBook = (AccountBooks) parent.getAdapter()
                    .getItem(position);
            payout_select_account_book_et.setText(accountBook.getAccountBookName());
            accountBookId = accountBook.getAccountBookId();
            alertDialog.dismiss();
        }
    }

    //选择类别，自己写
    private void showCategorySelectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = LayoutInflater.from(this).inflate(R.layout.category, null);
        ExpandableListView categoryList = (ExpandableListView) v
                .findViewById(R.id.category_list_lv);
        CategoryAdapter adapter = new CategoryAdapter(this);
        categoryList.setAdapter(adapter);
        builder.setTitle(R.string.button_text_select_category)
                .setNegativeButton(R.string.button_text_back, null).setView(v);
        AlertDialog dialog = builder.create();
        dialog.show();
        categoryList.setOnGroupClickListener(new onCategorySelectedListener(
                dialog));
        categoryList.setOnChildClickListener(new onCategorySelectedListener(
                dialog));

    }

    // 种类选择监听事件
    private class onCategorySelectedListener implements ExpandableListView.OnChildClickListener,
            ExpandableListView.OnGroupClickListener {

        private AlertDialog dialog;

        public onCategorySelectedListener(AlertDialog dialog) {
            this.dialog = dialog;
        }

        // 子类监听事件
        @Override
        public boolean onChildClick(ExpandableListView parent, View v,
                                    int groupPosition, int childPosition, long id) {
            Categorys categoryGroup = (Categorys) parent.getAdapter().getItem(
                    groupPosition);
            List<Categorys> listGroup = categoryBusiness
                    .getNotHideCategoryListByParentId(categoryGroup
                            .getCategoryId());
            if (listGroup.size() > 0) {
                Categorys categoryChild = listGroup.get(childPosition);
                payout_select_category_actv.setText(categoryChild.getCategoryName());
                categoryId = categoryChild.getCategoryId();
                dialog.dismiss();
                return true;
            }

            return false;
        }

        @Override
        public boolean onGroupClick(ExpandableListView parent,
                                    View view, int groupPosition, long id) {
            Categorys categoryGroup = (Categorys) parent.getAdapter().getItem(
                    groupPosition);
            List<Categorys> listGroup = categoryBusiness
                    .getNotHideCategoryListByParentId(categoryGroup
                            .getCategoryId());
            if (listGroup.size() < 1) {
                payout_select_category_actv.setText(categoryGroup.getCategoryName());
                categoryId = categoryGroup.getCategoryId();
                dialog.dismiss();
                return true;
            }
            return false;
        }
    }

    //选择日期
    private void showDateSelectDialog(int year, int month, int day) {
        (new DatePickerDialog(this, new OnDateSelectedListener(), year, month,
                day)).show();
    }

    private class OnDateSelectedListener implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
           // Date date = new Date(year - 1900, monthOfYear, dayOfMonth);
            GregorianCalendar gc = new GregorianCalendar(year,monthOfYear,dayOfMonth);
            Date date = gc.getTime();
            payout_select_date_et.setText(DateUtil.getFormattedString(date, "yyyy-MM-dd"));
        }
    }

    // 计算方式,自己写
    private void showPayoutTypeSelectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = View.inflate(this, R.layout.payout_type, null);
        ListView lvType = (ListView) v.findViewById(R.id.lv_payout_type_list);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                R.layout.simple_spinner_item, payoutTypeArray);
        lvType.setAdapter(arrayAdapter);

        builder.setTitle(R.string.button_text_select_type)
                .setNegativeButton(R.string.button_text_back,
                        new OnSelectUserBack()).setView(v);
        AlertDialog dialog = builder.create();
        dialog.show();
        lvType.setOnItemClickListener(new OnTypeItemListener(dialog));

    }

    class OnTypeItemListener implements AdapterView.OnItemClickListener {

        private AlertDialog dialog;

        public OnTypeItemListener(AlertDialog dialog) {
            this.dialog = dialog;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            String type = (String) parent.getAdapter().getItem(position);
            if (type.equals(payoutTypeArray[2]) && payoutUserId != null
                    && payoutUserId.indexOf(",") <= payoutUserId.length() - 1) {
                payout_select_user_et.setText("");
                payoutUserId = "";
            }
            payout_select_type_et.setText(type);
            dialog.dismiss();
        }
    }


    // 选择消费人
    private void showUserSelectDialog(String payoutType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = LayoutInflater.from(this).inflate(R.layout.user, null);
        LinearLayout linearLayout = (LinearLayout) v
                .findViewById(R.id.user_list_ll);
        linearLayout.setBackgroundResource(R.color.blue);
        ListView select_lv = (ListView) v.findViewById(R.id.user_list_lv);
        UserAdapter adapter = new UserAdapter(this);
        select_lv.setAdapter(adapter);

        builder.setTitle(R.string.button_text_select_user)
                .setIcon(R.drawable.user_small_icon)
                .setNegativeButton(R.string.button_text_back,
                        new OnSelectUserBack())
                .setView(v);
        AlertDialog dialog = builder.create();
        dialog.show();
        select_lv.setOnItemClickListener(new OnUserItemClickListener(dialog,
                payoutType));
    }

    private class OnUserItemClickListener implements AdapterView.OnItemClickListener {
        private AlertDialog alertDialog;
        private String payoutType;

        public OnUserItemClickListener(AlertDialog alertDialog,
                                       String payoutType) {
            this.alertDialog = alertDialog;
            this.payoutType = payoutType;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            Users user = (Users) parent.getAdapter().getItem(position);
            //均分或者借贷
            if (payoutType.equals(payoutTypeArray[0])
                    || payoutType.equals(payoutTypeArray[1])) {
                LinearLayout linearLayout = (LinearLayout) view
                        .findViewById(R.id.user_item_ll);
                if (itemColor == null && userSelectedList == null) {
                    itemColor = new ArrayList<>();
                    userSelectedList = new ArrayList<>();
                }
                if (itemColor.contains(linearLayout)) {
                    linearLayout.setBackgroundResource(R.color.blue);
                    itemColor.remove(linearLayout);
                    userSelectedList.remove(user);
                } else {
                    linearLayout.setBackgroundResource(R.color.red);
                    itemColor.add(linearLayout);
                    userSelectedList.add(user);
                }
                return;
            }
            if (payoutType.equals(payoutTypeArray[2])) {// 个人消费
                userSelectedList = new ArrayList<>();// 初始化已选用户集合
                userSelectedList.add(user);// 将当前的用户的实体添加进集合
                payout_select_user_et.setText("");
                String name = "";
                payoutUserId = "";
                for (int i = 0; i < userSelectedList.size(); i++) {
                    name += userSelectedList.get(i).getUserName() + ",";
                    payoutUserId += userSelectedList.get(i).getUserId() + ",";

                }
                payout_select_user_et.setText(name);
                itemColor = null;
                userSelectedList = null;
                alertDialog.dismiss();
            }
        }
    }

    private class OnSelectUserBack implements
            android.content.DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            // 清空数据
            payout_select_user_et.setText("");
            String name = "";
            payoutUserId = "";
            if (userSelectedList != null) {
                for (int i = 0; i < userSelectedList.size(); i++) {
                    name += userSelectedList.get(i).getUserName() + ",";
                    payoutUserId += userSelectedList.get(i).getUserId() + ",";
                }
                // 设置显示的字符串
                payout_select_user_et.setText(name);
            }
            itemColor = null;
            userSelectedList = null;
        }
    }

}