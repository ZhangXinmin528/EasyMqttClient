package com.zxm.easymqttclient.util;

import com.zxm.easymqttclient.BuildConfig;

/**
 * Created by ZhangXinmin on 2019/6/26.
 * Copyright (c) 2018 . All rights reserved.
 */
public class Constant {

    /**
     * MQTT HOST
     */
    public static final String MQTT_HOST = BuildConfig.DEBUG ?
            "111.231.71.179" : "119.253.83.48";

    /**
     * MQTT PORT
     */
    public static final String MQTT_PORT = BuildConfig.DEBUG ?
            "1883" : "80";

    /**
     * Mqtt user name
     */
    public static final String MQTT_NAME = BuildConfig.DEBUG ?
            "" : "mqttad";

    public static final String MQTT_PWD = BuildConfig.DEBUG ?
            "" : "t@ikang@2019";
}
