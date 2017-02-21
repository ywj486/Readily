package com.bc.ywjphone.readily.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Administrator on 2016/12/22 0022.
 */
public class Payout implements Serializable{
    //主键
    private int payoutId;
    //账本Id外键
    private int accountBookId;
    //账本名称
    private String accountBookName;
    //类别Id的外键,保存
    private int categoryId;
    //类别的名称，显示
    private String categoryName;
    //类别路径
    private String path;
    //消费金额,用于超过16位的数字进行精确的计算和处理，商业计算
    private BigDecimal amount;
    //消费日期
    private Date payoutDate;
    //计算方式
    private String payoutType;
    //消费人Id主键
    private String payoutUserId;
    //备注
    private String comment;
    //添加日期
    private Date createDate=new Date();
    //状态：0失败，1启用，默认启用
    private int state=1;

    public int getPayoutId() {
        return payoutId;
    }

    public void setPayoutId(int payoutId) {
        this.payoutId = payoutId;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getPayoutDate() {
        return payoutDate;
    }

    public void setPayoutDate(Date payoutDate) {
        this.payoutDate = payoutDate;
    }

    public String getPayoutType() {
        return payoutType;
    }

    public void setPayoutType(String payoutType) {
        this.payoutType = payoutType;
    }

    public String getPayoutUserId() {
        return payoutUserId;
    }

    public void setPayoutUserId(String payoutUserId) {
        this.payoutUserId = payoutUserId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }


    @Override
    public String toString() {
        return "Payout{" +
                "payoutId=" + payoutId +
                ", accountBookId=" + accountBookId +
                ", accountBookName='" + accountBookName + '\'' +
                ", categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", path='" + path + '\'' +
                ", amount=" + amount +
                ", payoutDate=" + payoutDate +
                ", payoutType='" + payoutType + '\'' +
                ", payoutUserId='" + payoutUserId + '\'' +
                ", comment='" + comment + '\'' +
                ", createDate=" + createDate +
                ", state=" + state +
                '}';
    }
}
