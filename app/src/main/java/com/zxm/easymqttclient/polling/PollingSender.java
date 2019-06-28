package com.zxm.easymqttclient.polling;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.NonNull;

import com.coding.zxm.mqtt_master.util.MLogger;
import com.zxm.easymqttclient.util.Constant;
import com.zxm.easymqttclient.util.TimeUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by ZhangXinmin on 2019/5/21.
 * Copyright (c) 2018 . All rights reserved.
 */
public final class PollingSender {
    /**
     * Available recurrence interval.
     */
    public static final long INTERVAL_FIVE_MINUTES = 5 * 60 * 1000;

    public static final DateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA);

    private static final String TAG = "PollingSender";

    private Context context;
    private BroadcastReceiver receiver;
    private String action;
    private PendingIntent pendingIntent;
    private long keepAlive;
    private boolean isStarted;

    /**
     * Constructor
     *
     * @param context
     * @param interval
     */
    public PollingSender(@NonNull Context context, long interval,
                         @NonNull BroadcastReceiver receiver) {

        if (context != null && receiver != null) {
            this.context = context;
            this.receiver = receiver;
            this.keepAlive = interval;

            init();
        }

    }

    private void init() {
        action = Constant.ACTION_HEART_POLLING + "." + TAG;
        isStarted = false;
    }

    public void start() {
        if (receiver != null) {
            MLogger.i(TAG, "start()~");
            context.registerReceiver(receiver, new IntentFilter(action));

            pendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(action),
                    PendingIntent.FLAG_UPDATE_CURRENT);

            schedule(10000);
            isStarted = true;
        }
        isStarted = false;
    }

    public void stop() {
        MLogger.i(TAG, "stop()~");

        if (isStarted) {
            if (pendingIntent != null) {
                // Cancel Alarm.
                final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Service.ALARM_SERVICE);
                if (alarmManager != null) {
                    alarmManager.cancel(pendingIntent);
                }
            }

            isStarted = false;

            if (receiver != null) {
                context.unregisterReceiver(receiver);
            }
        }
    }

    public void schedule() {
        schedule(keepAlive);
    }

    public void schedule(long delayMillseconds) {
        final long nextAlarmMillseconds = System.currentTimeMillis() + delayMillseconds;

        MLogger.i(TAG, "schedule next alerm at : " + TimeUtil.getNowTimeStamp(DATE_FORMAT));

        AlarmManager alarmManager = (AlarmManager) context
                .getSystemService(Service.ALARM_SERVICE);

        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= 23) {
                // In SDK 23 and above, dosing will prevent setExact, setExactAndAllowWhileIdle will force
                // the device to run this task whilst dosing.
                MLogger.i(TAG, "Alarm scheule using setExactAndAllowWhileIdle, next : " + delayMillseconds);
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextAlarmMillseconds,
                        pendingIntent);
            } else if (Build.VERSION.SDK_INT >= 19) {
                MLogger.i(TAG, "Alarm scheule using setExact, next : "
                        + nextAlarmMillseconds);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, delayMillseconds,
                        pendingIntent);
            } else {
                MLogger.i(TAG, "Alarm scheule using set, next : "
                        + nextAlarmMillseconds);
                alarmManager.set(AlarmManager.RTC_WAKEUP, delayMillseconds,
                        pendingIntent);
            }
        }
    }

    /**
     * Current polling state
     *
     * @return
     */
    public boolean isPollingStarted() {
        return isStarted;
    }

}
