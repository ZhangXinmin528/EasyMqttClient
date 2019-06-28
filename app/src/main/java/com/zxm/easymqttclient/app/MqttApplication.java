package com.zxm.easymqttclient.app;

import android.app.Application;

import com.coding.zxm.mqtt_master.util.MLogger;

/**
 * Created by ZhangXinmin on 2019/3/20.
 * Copyright (c) 2018 . All rights reserved.
 */
public class MqttApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        initLogConfig();
    }

    private void initLogConfig() {
        final MLogger.LogConfig logConfig = new MLogger.LogConfig(getApplicationContext())
                .setLog2FileSwitch(true)
                .setLog2FileSwitch(true);

        MLogger.resetLogConfig(logConfig);
        MLogger.i(logConfig.toString());
    }
}
