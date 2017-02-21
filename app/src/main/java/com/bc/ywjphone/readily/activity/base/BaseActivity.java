package com.bc.ywjphone.readily.activity.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.bc.ywjphone.readily.R;

import java.lang.reflect.Field;

/**
 * Created by Administrator on 2016/12/12 0012.
 * 跟业务无关的
 */
public class BaseActivity extends Activity {
    protected ProgressDialog progressDialog;

    protected void showMsg(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_LONG).show();
    }

    protected void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    protected void openActivity(Class<?> cls) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        startActivity(intent);
    }

    protected LayoutInflater getInflater() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        return layoutInflater;
    }

    //通过反射控制对话框不关闭
    protected void setAlertDialogIsClose(DialogInterface dialog, boolean isClose) {
        try {
            //dialog.getClass()获取对话框的类，getSuperclass获取超类，getDeclaredField获取字段
            Field filed = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
            //可以控制私有变量
            filed.setAccessible(true);
            //动态控制对话框的关闭
            filed.set(dialog, isClose);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected AlertDialog showAlertDialog(int titleResId, String msg,
                                          DialogInterface.OnClickListener clickListener) {
        String title = getResources().getString(titleResId);
        return showAlertDialog(title, msg, clickListener);
    }

    protected AlertDialog showAlertDialog(String title, String msg,
                                          DialogInterface.OnClickListener clickListener) {
        return new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(R.string.button_yes, clickListener)
                .setNegativeButton(R.string.button_no, null)
                .show();
    }

    protected void showProgreddDialog(int titleResId, int mesResId) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(titleResId);
        progressDialog.setMessage(getString(mesResId));
        progressDialog.show();
    }

    protected void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
