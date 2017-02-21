package com.bc.ywjphone.readily.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.bc.ywjphone.readily.business.DataBackupBusiness;
import com.bc.ywjphone.readily.receiver.DatabaseBackupReceiver;

import java.util.Date;

/**
 * Created by Administrator on 2016/12/30 0030.
 */
public class ServiceDatabaseBackup extends Service {
    //定义自动备份的间隔时间
    private static final long SPACINGIN_TERVAL = 1000000000;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        DataBackupBusiness dataBackupBusiness = new DataBackupBusiness(this);
        //读取上一次备份的日期
        long backupMillise = dataBackupBusiness.loadDatabaseBackupDate();
        Date backupDate = new Date();
        if (backupMillise == 0) {
            dataBackupBusiness.databaseBackup(backupDate);
            backupMillise = dataBackupBusiness.loadDatabaseBackupDate();
        } else {
            //如果当前日期-上次备份的日期>=时间间隔，说明又该备份了
            if (backupDate.getTime() - backupMillise >= SPACINGIN_TERVAL) {
                //再执行一次备份
                dataBackupBusiness.databaseBackup(backupDate);
                //又得到最新的备份时间
                backupMillise = dataBackupBusiness.loadDatabaseBackupDate();
            }
        }
        Intent i = new Intent(this, DatabaseBackupReceiver.class);
        i.putExtra("date", backupMillise);// 备份日期
        //延迟意图
        // 参数1 上下文 2请求码 3 想要打开那个类 延时的意图 打开数据库广播接收器 4 覆盖方式
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_ONE_SHOT);
        //获取定时器对象
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        // 获取alarmManger报警管理对象 要设置懂事 任务就可以使用
        // 参数1唤醒类型，RTC_WAKEUP在系统休眠状态下照样备份
        // 参数二触发的时间 SPACINGIN_TERVAL 间隔多长时间再次进行备份
        // 上次备份的时间 十秒钟在备份 backupMillise 3表示闹钟响应动作
        am.set(AlarmManager.RTC_WAKEUP, backupMillise + SPACINGIN_TERVAL, pi);
        return super.onStartCommand(intent, flags, startId);
    }
}
