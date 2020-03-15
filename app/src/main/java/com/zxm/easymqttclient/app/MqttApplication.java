package com.zxm.easymqttclient.app;

import android.app.Application;

import com.coding.zxm.mqtt_master.util.MqttDebuger;
import com.tencent.bugly.Bugly;
import com.zxm.easymqttclient.BuildConfig;


/**
 * Created by ZhangXinmin on 2019/3/20.
 * Copyright (c) 2018 . All rights reserved.
 */
public class MqttApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        initBugly();

        initLogConfig();
    }

    private void initLogConfig() {
        MqttDebuger.setDebugerEnable(getApplicationContext(), BuildConfig.DEBUG);
    }

    private void initBugly(){
        Bugly.init(this,"60b2e0b6ed",BuildConfig.DEBUG);
    }
}
