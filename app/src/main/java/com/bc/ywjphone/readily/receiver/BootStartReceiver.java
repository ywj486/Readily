package com.bc.ywjphone.readily.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bc.ywjphone.readily.service.ServiceDatabaseBackup;

/**
 * Created by Administrator on 2016/12/30 0030.
 * 开机启动广播接收器
 */
public class BootStartReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //启动服务
        Intent i = new Intent(context, ServiceDatabaseBackup.class);
        context.startService(i);
    }
}
