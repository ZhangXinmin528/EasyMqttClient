package com.zxm.easymqttclient.app;

import android.app.Application;

import com.coding.zxm.mqtt_master.client.MqttClientManager;
import com.coding.zxm.mqtt_master.client.MqttConfig;
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

        initMqttClient();
    }

    private void initMqttClient() {

        final String clientId = "mqtt" + System.currentTimeMillis();

        final MqttConfig config = new MqttConfig.Builder(getApplicationContext())
                .setClientId(clientId)
                .setHost("111.231.71.179")
                .setPort("1883")
                .setUserName("mqttad")
                .setPassWord("t@ikang@2019")
                .setAutomaticReconnect(true)
                .setCleanSession(true)
                .setKeepalive(20)
                .create();

        MqttClientManager.getInstance().init(config);
    }

    private void initLogConfig() {
        final MLogger.LogConfig logConfig = new MLogger.LogConfig(getApplicationContext())
                .setLog2FileSwitch(true);
        MLogger.resetLogConfig(logConfig);
        MLogger.i(logConfig.toString());
    }
}
