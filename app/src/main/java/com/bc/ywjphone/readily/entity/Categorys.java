package com.bc.ywjphone.readily.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2016/12/14 0014.
 */
public class Categorys implements Serializable {
    //主键
    private int categoryId;
    //用户名称
    private String categoryName;
    //添加日期
    private Date createData = new Date();
    //状态：0失效，1启用，默认启用
    private int state = 1;
    //父类Id
    private int parentId = 0;
    //路径
    private String path;

    public Categorys() {
    }

    public Categorys(int categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Date getCreateData() {
        return createData;
    }

    public void setCreateData(Date createData) {
        this.createData = createData;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return categoryName;
    }
}
