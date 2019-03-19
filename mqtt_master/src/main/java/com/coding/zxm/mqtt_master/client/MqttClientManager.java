package com.coding.zxm.mqtt_master.client;

import android.Manifest;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.text.TextUtils;
import android.util.Log;

import com.coding.zxm.mqtt_master.client.listener.MqttActionListener;
import com.coding.zxm.mqtt_master.client.listener.MqttConnectionCallback;
import com.coding.zxm.mqtt_master.service.MqttAndroidClient;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Created by ZhangXinmin on 2019/3/18.
 * Copyright (c) 2018 . All rights reserved.
 */
public final class MqttClientManager {

    private static final String TAG = MqttClientManager.class.getSimpleName();

    private static MqttClientManager INSTANCE;

    private MqttAndroidClient mqttClient;
    private MqttConnectOptions mqttOptions;

    private MqttClientManager() {
    }

    public synchronized static MqttClientManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MqttClientManager();
        }
        return INSTANCE;
    }

    /**
     * Set
     *
     * @param config
     */
    public void resetConfig(@NonNull MqttConfig config) {
        if (config != null) {
            mqttClient = config.getMqttAndroidClient();
            mqttOptions = config.getConnectOptions();
        } else {
            Log.e(TAG, "mqtt config is null!");
        }
    }

    /**
     * Build a new connection {@link MqttAndroidClient}
     *
     * @param listener {@link MqttActionListener}
     */
    @RequiresPermission(Manifest.permission.INTERNET)
    public void connect(@NonNull MqttActionListener listener, MqttConnectionCallback callback) {

        if (mqttClient == null) {
            Log.e(TAG, "MqttAndroidClient is null,con't build connection!");
            return;
        }

        if (mqttOptions == null) {
            Log.e(TAG, "MqttConnectOptions is null,con't build connection!");
            return;
        }

        if (mqttClient.isConnected()) {
            Log.e(TAG, "This client has built connection to server!");
            return;
        }

        try {
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    if (callback != null) {
                        callback.connectionLost(cause);
                    }
                    Log.e(TAG, "The connection to the server is lost : " + cause != null ?
                            cause.toString() : "cause is empty!");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    final String msg = new String(message.getPayload());
                    final int qos = message.getQos();
                    if (callback != null) {
                        callback.messageArrived(topic, msg, qos);
                    }
                    Log.i(TAG, "Mqtt client get message from server[message : " + msg + ";'qos' : " + qos + "]");
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    if (callback != null) {
                        callback.deliveryComplete();
                    }
                    Log.i(TAG, "Mqtt client delivery message complete!");
                }
            });
            mqttClient.connect(mqttOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    if (listener != null) {
                        listener.onSuccess();
                    }
                    Log.i(TAG, "[connect()]-->Mqtt client build connection success!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    if (listener != null) {
                        listener.onFailure(exception);
                    }
                    Log.e(TAG, "[connect()]-->Mqtt client build connection failed!");
                }
            });
        } catch (MqttException e) {
            Log.e(TAG, "[connect()]-->Mqtt client build connection got exception : " + e.toString());
        }
    }

    /**
     * Subscribe to a topic, which may include wildcards.More information in
     * {@link MqttAndroidClient  #subscribe()}
     *
     * @param topic    the topic to subscribe to, which can include wildcards.
     * @param qos      the maximum quality of service at which to subscribe.
     * @param listener optional listener that will be notified when subscribe has
     *                 completed
     */
    public void subscribe(@NonNull String topic, @IntRange(from = 0, to = 2) int qos,
                          @NonNull MqttActionListener listener) {

        if (mqttClient == null) {
            Log.e(TAG, "MqttAndroidClient is null,con't build connection!");
            return;
        }

        if (mqttOptions == null) {
            Log.e(TAG, "MqttConnectOptions is null,con't build connection!");
            return;
        }

        if (!mqttClient.isConnected()) {
            Log.e(TAG, "This client hasn't built connection to server!");
            return;
        }

        Log.i(TAG, "[subscribe()]-->Mqtt client is going to subscribe the topic : [" + topic + "]"
                + mqttClient.hashCode());

        if (TextUtils.isEmpty(topic) || listener == null) {
            Log.e(TAG, "[subscribe()]-->Mqtt client subscribe params is illegal!");
            return;
        }
        if (mqttClient != null && mqttClient.isConnected()) {
            try {
                mqttClient.subscribe(topic, qos, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        if (listener != null) {
                            listener.onSuccess();
                        }
                        Log.i(TAG, "[subscribe()]-->Mqtt client subscribe the topic : [" + topic + "] success!");
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        if (listener != null) {
                            listener.onFailure(exception);
                        }
                        Log.e(TAG, "[subscribe()]-->Mqtt client subscribe the topic : [" + topic + "] falied!");
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
                Log.e(TAG, "[subscribe()]-->Mqtt client subscribe got exception : " + e.toString());
            }

        }
    }

    /**
     * Publishes a message to a topic on the server.
     *
     * @param topic    to deliver the message to, for example "finance/stock/ibm".
     * @param message  the message to publish
     * @param qos      the Quality of Service to deliver the message at. Valid values
     *                 are 0, 1 or 2.
     * @param retained whether or not this message should be retained by the server.
     * @param listener optional listener that will be notified when message delivery
     *                 has completed to the requested quality of service
     */
    public void publish(@NonNull String topic, @NonNull String message, @IntRange(from = 0, to = 2) int qos,
                        boolean retained, @NonNull MqttActionListener listener) {

        if (mqttClient == null) {
            Log.e(TAG, "MqttAndroidClient is null,con't build connection!");
            return;
        }

        if (mqttOptions == null) {
            Log.e(TAG, "MqttConnectOptions is null,con't build connection!");
            return;
        }

        if (!mqttClient.isConnected()) {
            Log.e(TAG, "This client hasn't built connection to server!");
            return;
        }

        Log.i(TAG, "[publish()]-->Mqtt client is going to publish the topic : [" + topic + "]-->message : ["
                + message + "]-->qos : [" + qos + "]-->retained : [" + retained + "]" + mqttClient.hashCode());

        if (TextUtils.isEmpty(topic) || TextUtils.isEmpty(message) || listener == null) {
            Log.e(TAG, "[publish()]-->Mqtt client publish params is illegal!");
            return;
        }
        try {
            mqttClient.publish(topic, message.getBytes(), qos, retained, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    if (listener != null) {
                        listener.onSuccess();
                    }
                    Log.i(TAG, "[publish()]-->Mqtt client publish the topic : [" + topic + "] success!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    if (listener != null) {
                        listener.onFailure(exception);
                    }
                    Log.e(TAG, "[publish()]-->Mqtt client publish the topic : [" + topic + "] falied!");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
            Log.e(TAG, "[publish()]-->Mqtt client publish got exception : " + e.toString());
        }
    }


    /**
     * Disconnects from the server.
     *
     * @param listener optional listener that will be notified when the disconnect
     *                 completes. Use null if not required.
     */
    public void disconnect(@NonNull MqttActionListener listener) {
        Log.i(TAG, "[disconnect()]-->Mqtt client is going to disconnect!" + mqttClient.hashCode());

        if (mqttClient != null) {
            if (!mqttClient.isConnected()) {
                Log.i(TAG, "[disconnect()]-->Mqtt client has not build the connection!");
                return;
            }

            try {
                mqttClient.disconnect(null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        if (listener != null) {
                            listener.onSuccess();
                        }
                        Log.i(TAG, "[disconnect()]-->Mqtt client disconnect success!");
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        if (listener != null) {
                            listener.onFailure(exception);
                        }
                        Log.e(TAG, "[disconnect()]-->Mqtt client disconnect falied!");
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
                Log.e(TAG, "[disconnect()]-->Mqtt client disconnect got exception : " + e.toString());
            }
        }
    }
}
