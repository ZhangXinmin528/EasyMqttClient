package com.zxm.easymqttclient.app;

import android.app.Application;

import com.coding.zxm.mqtt_master.util.MqttDebuger;


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
        MqttDebuger.setDebugerEnable(getApplicationContext(), true);
    }
}
