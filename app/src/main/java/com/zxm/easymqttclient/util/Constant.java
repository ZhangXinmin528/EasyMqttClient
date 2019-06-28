package com.zxm.easymqttclient.util;


import com.zxm.easymqttclient.BuildConfig;

/**
 * Created by ZhangXinmin on 2019/6/26.
 * Copyright (c) 2018 . All rights reserved.
 */
public class Constant {

    /**
     * 心跳
     */
    public static final String ACTION_HEART_POLLING = "com.zxm.easymqttclient";

    /**
     * MQTT HOST
     */
    public static final String MQTT_HOST = BuildConfig.IS_DEBUG ?
            "111.231.71.179" : "119.253.83.48";

    /**
     * MQTT PORT
     */
    public static final String MQTT_PORT = BuildConfig.IS_DEBUG ?
            "1883" : "80";

    /**
     * Mqtt user name
     */
    public static final String MQTT_NAME = BuildConfig.IS_DEBUG ?
            "" : "mqttad";

    /**
     * Mqtt password
     */
    public static final String MQTT_PWD = BuildConfig.IS_DEBUG ?
            "" : "t@ikang@2019";

    public static final String MQTT_TOPIC = "infrared/793619615";
}
