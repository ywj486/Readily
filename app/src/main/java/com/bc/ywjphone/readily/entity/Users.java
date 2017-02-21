package com.bc.ywjphone.readily.entity;

import java.util.Date;

/**
 * Created by Administrator on 2016/12/14 0014.
 */
public class Users {
    //主键
    private int userId;
    //用户名称
    private String userName;
    //添加日期
    private Date createData=new Date();
    //状态：0失效，1启用，默认启用
    private int state=1;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
