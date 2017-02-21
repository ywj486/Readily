package com.bc.ywjphone.readily.entity;

import java.util.Date;

/**
 * Created by Administrator on 2016/12/14 0014.
 */
public class AccountBooks {
    //主键
    private int accountBookId;
    //账本名称
    private String accountBookName;
    //添加日期
    private Date createData=new Date();
    //状态：0失效，1启用，默认启用
    private int state=1;
    //是否为默认账本
    private int isDefault;

    public int getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;
    }

    public int getAccountBookId() {
        return accountBookId;
    }

    public void setAccountBookId(int accountBookId) {
        this.accountBookId = accountBookId;
    }

    public String getAccountBookName() {
        return accountBookName;
    }

    public void setAccountBookName(String accountBookName) {
        this.accountBookName = accountBookName;
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
}
