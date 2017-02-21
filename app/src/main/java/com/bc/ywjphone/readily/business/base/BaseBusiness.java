package com.bc.ywjphone.readily.business.base;

import android.content.Context;

/**
 * Created by Administrator on 2016/12/14 0014.
 */
public class BaseBusiness {
    public Context context;

    public BaseBusiness(Context context) {
        this.context = context;
    }

    public String getAllNotHideCategory() {
        String condition = " and state = 1";
        return condition;
    }


}
