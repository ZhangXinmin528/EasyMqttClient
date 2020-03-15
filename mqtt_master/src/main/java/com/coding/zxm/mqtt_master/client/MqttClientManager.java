package com.coding.zxm.mqtt_master.client;

import android.Manifest;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.text.TextUtils;

import com.coding.zxm.mqtt_master.client.listener.MqttActionListener;
import com.coding.zxm.mqtt_master.client.listener.MqttConnectionCallback;
import com.coding.zxm.mqtt_master.client.listener.SimpleConnectionMqttCallback;
import com.coding.zxm.mqtt_master.service.MqttAndroidClient;
import com.coding.zxm.mqtt_master.util.MqttDebuger;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
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

    private MqttConfig mqttConfig;

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
     * Get mqtt config.
     *
     * @return
     */
    public MqttConfig getMqttConfig() {
        return mqttConfig;
    }

    /**
     * Use this method after configuring add the mqtt configs.
     *
     * @return
     */
    public String outMqttConfigure() {
        if (mqttConfig != null) {
            return mqttConfig.getConfigParams();
        } else return "";
    }


    /**
     * init MqttClient
     * <p>
     * You must init mqtt config before connected.
     * </p>
     *
     * @param config {@link MqttConfig}
     */
    public void init(@NonNull MqttConfig config) {
        if (config != null) {
            mqttConfig = config;
        }
        if (mqttClient != null) {
            mqttClient = null;
        }
        if (mqttOptions != null) {
            mqttOptions = null;
        }
        if (mqttConfig != null) {
            mqttClient = mqttConfig.getMqttAndroidClient();
            mqttOptions = mqttConfig.getConnectOptions();
        }
    }

    /**
     * Build a new connection {@link MqttAndroidClient}
     *
     * @param listener {@link MqttActionListener}
     * @param callback Use {@link MqttConnectionCallback} or{@link SimpleConnectionMqttCallback}
     */
    @RequiresPermission(Manifest.permission.INTERNET)
    public void connect(@NonNull MqttActionListener listener, MqttConnectionCallback callback) {

        if (mqttClient == null) {
            MqttDebuger.e(TAG, "MqttAndroidClient is null,con't build connection!");
            return;
        }

        if (mqttOptions == null) {
            MqttDebuger.e(TAG, "MqttConnectOptions is null,con't build connection!");
            return;
        }

        if (mqttClient.isConnected()) {
            MqttDebuger.e(TAG, "This client has built connection to server!");
            return;
        }

        try {
            mqttClient.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectComplete(boolean reconnect, String serverURI) {
                    if (callback != null) {
                        callback.connectComplete(reconnect, serverURI);
                    }
                    MqttDebuger.file(MqttDebuger.I, TAG, "The connection to the server is completed " +
                            "successfully : [reconnect = " + reconnect + "]");

                }

                @Override
                public void connectionLost(Throwable cause) {
                    if (callback != null) {
                        callback.connectionLost(cause);
                    }
                    if (cause != null) {
                        if (cause instanceof MqttException) {
                            final MqttException exception = (MqttException) cause;
                            MqttDebuger.file(MqttDebuger.E, TAG, "The connection to the server is lost : " + exception.toString());
                        }
                    } else {
                        MqttDebuger.file(MqttDebuger.E, TAG, "The connection to the server is lost : cause is null!");
                    }

                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    final String msg = new String(message.getPayload());
                    final int qos = message.getQos();
                    if (callback != null) {
                        callback.messageArrived(topic, msg, qos);
                    }
                    MqttDebuger.file(TAG, "Mqtt client message arrived!");
                    MqttDebuger.json(msg);
                }


                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    if (callback != null) {
                        callback.deliveryComplete();
                    }
                    MqttDebuger.file(TAG, "Mqtt client delivery message complete!");
                }
            });
            mqttClient.connect(mqttOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    if (listener != null) {
                        listener.onSuccess();
                    }
                    MqttDebuger.file(TAG, "[connect()]-->Mqtt client build connection success!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    if (listener != null) {
                        listener.onFailure(exception);
                    }
                    MqttDebuger.file(MqttDebuger.E, TAG, "[connect()]-->Mqtt client build connection failed!");
                }
            });
        } catch (MqttException e) {
            MqttDebuger.e(MqttDebuger.E, TAG, "[connect()]-->Mqtt client build connection got exception : " + e.toString());
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
            MqttDebuger.e(TAG, "MqttAndroidClient is null,con't build connection!");
            return;
        }

        if (mqttOptions == null) {
            MqttDebuger.e(TAG, "MqttConnectOptions is null,con't build connection!");
            return;
        }

        if (!mqttClient.isConnected()) {
            MqttDebuger.e(TAG, "This client hasn't built connection to server!");
            return;
        }

        MqttDebuger.i(TAG, "[subscribe()]-->Mqtt client is going to subscribe the topic : [" + topic + "]"
                + mqttClient.hashCode());

        if (TextUtils.isEmpty(topic) || listener == null) {
            MqttDebuger.e(TAG, "[subscribe()]-->Mqtt client subscribe params is illegal!");
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
                        MqttDebuger.file(TAG, "[subscribe()]-->Mqtt client subscribe the topic : [" + topic + "] success!");
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        if (listener != null) {
                            listener.onFailure(exception);
                        }
                        MqttDebuger.file(MqttDebuger.E, "[subscribe()]-->Mqtt client subscribe the topic : [" + topic + "] falied!");
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
                MqttDebuger.e(TAG, "[subscribe()]-->Mqtt client subscribe got exception : " + e.toString());
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
            MqttDebuger.e(TAG, "MqttAndroidClient is null,con't build connection!");
            return;
        }

        if (mqttOptions == null) {
            MqttDebuger.e(TAG, "MqttConnectOptions is null,con't build connection!");
            return;
        }

        if (!mqttClient.isConnected()) {
            MqttDebuger.e(TAG, "This client hasn't built connection to server!");
            return;
        }

        MqttDebuger.i(TAG, "[publish()]-->Mqtt client is going to publish the topic : [" + topic + "]-->message : ["
                + message + "]-->qos : [" + qos + "]-->retained : [" + retained + "]" + mqttClient.hashCode());

        if (TextUtils.isEmpty(topic) || TextUtils.isEmpty(message) || listener == null) {
            MqttDebuger.e(TAG, "[publish()]-->Mqtt client publish params is illegal!");
            return;
        }
        try {
            mqttClient.publish(topic, message.getBytes(), qos, retained, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    if (listener != null) {
                        listener.onSuccess();
                    }
                    MqttDebuger.file(TAG, "[publish()]-->Mqtt client publish the topic : [" + topic + "] success!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    if (listener != null) {
                        listener.onFailure(exception);
                    }
                    MqttDebuger.file(MqttDebuger.E, TAG, "[publish()]-->Mqtt client publish the topic : [" + topic + "] falied!");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
            MqttDebuger.e(TAG, "[publish()]-->Mqtt client publish got exception : " + e.toString());
        }
    }

    /**
     * Disconnects from the server.
     *
     * @param listener optional listener that will be notified when the disconnect
     *                 completes. Use null if not required.
     */
    public void disconnect(@NonNull MqttActionListener listener) {

        if (mqttClient == null) {
            MqttDebuger.e(TAG, "MqttAndroidClient is null,con't dissconnect with the server!");
            return;
        }

        MqttDebuger.i(TAG, "[disconnect()]-->Mqtt client is going to disconnect!" + mqttClient.hashCode());

        if (!mqttClient.isConnected()) {
            MqttDebuger.i(TAG, "[disconnect()]-->Mqtt client has not build the connection!");
            return;
        }

        try {
            mqttClient.disconnect(null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    if (listener != null) {
                        listener.onSuccess();
                    }
                    MqttDebuger.file(TAG, "[disconnect()]-->Mqtt client disconnect success!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    if (listener != null) {
                        listener.onFailure(exception);
                    }
                    MqttDebuger.file(MqttDebuger.E, TAG, "[disconnect()]-->Mqtt client disconnect falied!");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
            MqttDebuger.e(TAG, "[disconnect()]-->Mqtt client disconnect got exception : " + e.toString());
        }
    }

    /**
     * Determines if this client is currently connected to the server.
     *
     * @return <code>true</code> if connected, <code>false</code> otherwise.
     */
    public boolean isConnected() {
        if (mqttClient != null) {
            return mqttClient.isConnected();
        }
        return false;
    }

}
