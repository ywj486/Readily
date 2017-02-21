package com.bc.ywjphone.readily.business;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.widget.ArrayAdapter;

import com.bc.ywjphone.readily.R;
import com.bc.ywjphone.readily.business.base.BaseBusiness;
import com.bc.ywjphone.readily.database.dao.CategoryDao;
import com.bc.ywjphone.readily.entity.CategoryTotal;
import com.bc.ywjphone.readily.entity.Categorys;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/14.
 * 业务层的增删改查
 */
public class CategoryBusiness extends BaseBusiness {


    public CategoryDao categoryDao;

    public CategoryBusiness(Context context) {
        super(context);
        categoryDao = new CategoryDao(context);
    }

    //获取所有根节点的类别
    public List getNotHideRootCategory() {
        return categoryDao.getCategorys(" and parentId=0 and state=1");
    }

    //获取未隐藏的数量  （包括子类和主类的总数）
    public int getNotHideCount() {
        List<Categorys> list = categoryDao.getCategorys(" and state=1");
        return list.size();
    }

    //获得所有为隐藏的子类的数量
    public int getNotHideCountByParentId(int categoryId) {
        //int childCount = categoryDao.getChildCount(getNotHideCountByParentId1(categoryId));
        List<Categorys> list =getNotHideCategoryListByParentId(categoryId);
        return  list.size();
    }

    //通过id 找到  子类的集合
//    public List<Categorys> getNotHideCategoryListByParentId(int categoryId) {
//        List<Categorys> category = categoryDao.getCategorys(getNotHideCategoryListByParentId1(categoryId));
//        return category;
//    }
    public List<Categorys> getNotHideCategoryListByParentId(int categoryId) {
        return categoryDao.getCategorys(" and parentId="+categoryId+" and state=1");
    }


    public ArrayAdapter<Categorys> getRootCategoryArrayAdapter() {
        List<Categorys> list = getNotHideRootCategory();
        list.add(0, new Categorys(0, context.getString(R.string.spinner_choose)));
        ArrayAdapter arrayAdapter = new ArrayAdapter(context, R.layout.simple_spinner_item, list);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return arrayAdapter;
    }

    //根据子类获取父类
    private Categorys getCategoryByCategoryId(int categoryId) {
        List<Categorys> list = categoryDao.getCategorys(" and categoryId="+categoryId+" and state=1");
        return list.get(0);
    }

    //插入类别
    public boolean insertCategory(Categorys category) {
        categoryDao.beginTransaction();
        try {
            boolean result = categoryDao.insertCategory(category);
            boolean result2 = true;
            //根据类别的Id获取类别，父类别
            Categorys parentCategory = getCategoryByCategoryId(category.getCategoryId());
            String path;
            if (parentCategory != null) {
                path = parentCategory.getPath() + category.getCategoryId() + ".";
            } else {
                path = category.getCategoryId() + ".";
            }
            category.setPath(path);
            result2 = editCategory(category);
            if (result && result2) {
                categoryDao.setTransactionSuccessful();
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            categoryDao.endTransaction();
        }
    }

    public boolean editCategory(Categorys category) {
        String condition = " categoryId=" + category.getCategoryId();
        boolean result = categoryDao.updateCategory(condition, category);
        return result;
    }

    public boolean hideCatrgoryByPath(String path) {
        String condition = " path like '" + path + "%'";
        ContentValues contentValues = new ContentValues();
        contentValues.put("state", 0);
        boolean result = categoryDao.updateCategory(condition, contentValues);
        return result;
    }
    public ArrayAdapter<Categorys> getAllCategoryArrayAdapter() {
        //获取未隐藏的类别
        List<Categorys> list = getNotHideRootCategory();
        ArrayAdapter<Categorys> arrayAdapter = new ArrayAdapter<Categorys>(context, R.layout.comment_auto_complete,list);
        //arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return arrayAdapter;
    }
    //获得所有未隐藏的数据
    public List<Categorys> getNotHintCategory(){
        List<Categorys> categoryArrays = categoryDao.getCategorys(getAllNotHideCategory());
        return categoryArrays;
    }
public List<CategoryTotal> categoryTotalByRootCategory(){
    String condition=" and parentId=0 and state=1";
    return getCategoryTotal(condition);
}
public List<CategoryTotal> getCategoryTotalByParentId(int parentId){
    String condition=" and parentId="+parentId+" and state=1";
    return getCategoryTotal(condition);
}

    private List<CategoryTotal> getCategoryTotal(String condition) {
        String sql="select count(payoutId) as count,sum(amount) as sumAmount," +
                "categoryName from v_payout where 1=1 "+condition+" group by categoryId";

        Cursor cursor=categoryDao.execSql(sql);
        List<CategoryTotal> list=new ArrayList<>();
        while(cursor.moveToNext()) {
            CategoryTotal categoryTotal=new CategoryTotal();
            categoryTotal.count=cursor.getString(cursor.getColumnIndex("count"));
            categoryTotal.sumAmount=cursor.getString(cursor.getColumnIndex("sumAmount"));
            categoryTotal.categoryName=cursor.getString(cursor.getColumnIndex("categoryName"));
            list.add(categoryTotal);
        }
        return list;
    }
}
