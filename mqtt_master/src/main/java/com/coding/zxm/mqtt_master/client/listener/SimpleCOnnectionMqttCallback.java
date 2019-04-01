package com.coding.zxm.mqtt_master.client.listener;

/**
 * Created by ZhangXinmin on 2018/11/27.
 * Copyright (c) 2018 . All rights reserved.
 */
public class SimpleCOnnectionMqttCallback implements MqttConnectionCallback {
    @Override
    public void connectionLost(Throwable cause) {

    }

    @Override
    public void messageArrived(String topic, String message, int qos) throws Exception {

    }

    @Override
    public void deliveryComplete() {

    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {

    }
}
