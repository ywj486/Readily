package com.bc.ywjphone.readily.receiver;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.bc.ywjphone.readily.R;
import com.bc.ywjphone.readily.activity.MainActivity;
import com.bc.ywjphone.readily.service.ServiceDatabaseBackup;
import com.google.android.gms.playlog.internal.LogEvent;

/**
 * Created by Administrator on 2016/12/30 0030.
 */
public class DatabaseBackupReceiver extends BroadcastReceiver {
    //使用通知来做提示
    NotificationManager notificationManager;
    Notification notification;
    Intent i;
    PendingIntent pendingIntent;

    @SuppressLint("NewApi")
    @Override
    public void onReceive(Context context, Intent intent) {
        //获取系统服务,强转为通知管理器
        notificationManager = (NotificationManager) context.getSystemService
                (Context.NOTIFICATION_SERVICE);
        Log.e("TAG", "广播---" + intent.getLongExtra("date", 0));
        //点击通知时显示的内容
        String contentTitle = "长颜草帮帮记通知您";
        String contentText = "长颜草帮帮记已执行数据备份";
        //点击通知时打开MainActivity
        i = new Intent(context, MainActivity.class);
        //设置启动模式
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //上下文，请求码，意图，覆盖方式(用户一点击就可以打开主页面)
        pendingIntent = PendingIntent.getActivity(context, 100, i, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT < 11) {
            notification = new Notification();
            //设置通知在状态栏显示的图标
            notification.icon = R.mipmap.ic_launcher;
            //声音
            notification.defaults = Notification.DEFAULT_SOUND;
            //设置通知显示的参数
            notification.tickerText = contentTitle;
            notification.contentIntent = pendingIntent;
        } else {
            Notification.Builder builder = new Notification.Builder(context);
            //设置通知在状态显示的图标
            builder.setSmallIcon(R.mipmap.ic_launcher);
            //设置通知显示的标题
            builder.setContentTitle(contentTitle);
            //设置通知显示的内容
            builder.setContentText(contentText);
            //设置通知时发出的声音
            builder.setDefaults(Notification.DEFAULT_SOUND);
            builder.setContentIntent(pendingIntent);
            notification = builder.build();
        }
        //可以理解为开始执行这个通知
        notificationManager.notify(1, notification);
        //再次启动服务
        Intent serviceIntent = new Intent(context, ServiceDatabaseBackup.class);
        context.startService(serviceIntent);
    }
}
