package com.bc.ywjphone.readily.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.bc.ywjphone.readily.R;
import com.bc.ywjphone.readily.View.SlideMenuView;
import com.bc.ywjphone.readily.activity.base.FrameActivity;
import com.bc.ywjphone.readily.business.CategoryBusiness;
import com.bc.ywjphone.readily.entity.Categorys;
import com.bc.ywjphone.readily.utils.RegexTools;

public class CategoryAddOrUpdateActivity extends FrameActivity implements View.OnClickListener {

    private Button category_save_btn, category_cancel_btn;
    private EditText category_name_et;
    private Spinner category_parentId_sp;

    private CategoryBusiness categoryBusiness;
    private Categorys categorys;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appendMainBody(R.layout.category_add_or_update);
        // SlideMenuView slideMenuView = new SlideMenuView(this);
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
        if (categorys == null) {
            title = getString(R.string.title_category_add_or_update,
                    new Object[]{getString(R.string.title_add)});
        } else {
            title = getString(R.string.title_category_add_or_update,
                    new Object[]{getString(R.string.title_update)});
            bindData(categorys);
        }
        setTopBarTitle(title);
    }

    private void bindData(Categorys category) {
        category_name_et.setText(category.getCategoryName());
        ArrayAdapter<Categorys> arrayAdapter = (ArrayAdapter) category_parentId_sp.getAdapter();
        if (category.getParentId() != 0) {
            int position = 0;
            for (int i = 0; i < arrayAdapter.getCount(); i++) {
                Categorys categoryItem = (Categorys) arrayAdapter.getItem(i);
                if (categoryItem.getCategoryId() == category.getParentId()) {
                    position = arrayAdapter.getPosition(categoryItem);
                    break;
                }
            }
            category_parentId_sp.setSelection(position);
        } else {
            int count = categoryBusiness.getNotHideCountByParentId(
                    category.getCategoryId());
            if (count != 0) {
                category_parentId_sp.setEnabled(false);
            }
        }
    }

    private void initVariable() {
        categoryBusiness = new CategoryBusiness(this);
        categorys = (Categorys) getIntent().getSerializableExtra("category");
    }

    private void initView() {
        category_save_btn = (Button) findViewById(R.id.category_save_btn);
        category_cancel_btn = (Button) findViewById(R.id.category_cancel_btn);
        category_name_et = (EditText) findViewById(R.id.category_name_et);
        category_parentId_sp = (Spinner) findViewById(R.id.category_parentId_sp);
    }

    private void initListeners() {
        category_save_btn.setOnClickListener(this);
        category_cancel_btn.setOnClickListener(this);
    }

    private void initData() {
        ArrayAdapter<Categorys> arrayAdapter = categoryBusiness
                .getRootCategoryArrayAdapter();
        category_parentId_sp.setAdapter(arrayAdapter);
    }

    private void addOrUpdateCategory() {
        //拿到类别的名称
        String categoryName = category_name_et.getText().toString().trim();
        //判断是中英文或者数字
        boolean checkResult = RegexTools.RegexName(categoryName);
        if (!checkResult) {
            showMsg(getString(R.string.check_text_chinese_english_num,
                    new Object[]{getString(R.string.tx_category_name)}));
            return;//返回，后边代码不执行
        }
        if (categorys == null) {
            categorys = new Categorys();
            categorys.setPath("");
        }
        categorys.setCategoryName(categoryName);
        // 如果不是请选择的话，说明有父类
        if (!getString(R.string.spinner_choose)
                .equals(category_parentId_sp.getSelectedItem().toString())) {
            Categorys parentCategory = (Categorys) category_parentId_sp.getSelectedItem();
            //if (parentCategory != null) {
            categorys.setParentId(parentCategory.getCategoryId());
            // }
        } else {
            // 是请选择说明没有选择父类
            categorys.setParentId(0);
        }
        boolean result = false;
        if (categorys.getCategoryId() == 0) {
            result = categoryBusiness.insertCategory(categorys);
        } else {
            result = categoryBusiness.editCategory(categorys);
        }
        if (result) {
            showMsg(getString(R.string.tips_add_success));
            finish();
        } else {
            showMsg(getString(R.string.tips_add_fail));
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.category_save_btn:
                addOrUpdateCategory();
                break;
            case R.id.category_cancel_btn:
                finish();
                break;
        }
    }
}

