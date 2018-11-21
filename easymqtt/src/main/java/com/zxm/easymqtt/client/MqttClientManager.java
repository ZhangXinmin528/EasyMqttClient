package com.zxm.easymqtt.client;

import android.content.Context;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.zxm.easymqtt.handler.SimpleMqttTraceHandler;
import com.zxm.easymqtt.listener.MqttActionListener;
import com.zxm.easymqtt.util.Debugger;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Created by ZhangXinmin on 2018/11/20.
 * Copyright (c) 2018 . All rights reserved.
 * The manager for {@link org.eclipse.paho.android.service.MqttAndroidClient}.
 */
public final class MqttClientManager {

    private static MqttClientManager INSTANCE;

    private Builder mBuilder;
    private MqttAndroidClient mMqttClient;

    private MqttClientManager() {

    }

    public static synchronized MqttClientManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MqttClientManager();
        }
        return INSTANCE;
    }

    /**
     * set builder for manager
     *
     * @param builder
     */
    public void init(@NonNull Builder builder) {
        mBuilder = builder;
        if (mMqttClient == null) {
            mMqttClient = mBuilder.mMqttAndroidClient;
        }

        if (mMqttClient != null && builder != null) {
            mMqttClient.setTraceEnabled(true);
            mMqttClient.setTraceCallback(new SimpleMqttTraceHandler());
        }
    }

    /**
     * Build a new connection {@link MqttAndroidClient}
     *
     * @param listener {@link MqttActionListener}
     */
    public void connect(@NonNull MqttActionListener listener) {
        if (mMqttClient == null) {
            mMqttClient = mBuilder.mMqttAndroidClient;
        }

        if (mMqttClient != null && !mMqttClient.isConnected()) {
            try {
                Debugger.i("[connect()]-->Mqtt client is connecting!" + mMqttClient.hashCode());
                mMqttClient.setCallback(new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable cause) {
                        Debugger.e("Mqtt client build connection to the server is lost!");
                    }

                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        final String msg = new String(message.getPayload());
                        final int qos = message.getQos();
                        Debugger.i("Mqtt client get message from server{'message' : " + msg + ";'qos' : " + qos + "}");
                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {
                        Debugger.i("Mqtt client delivery message complete!");
                    }
                });
                mMqttClient.connect(mBuilder.mConnectOptions, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        if (listener != null) {
                            listener.onSuccess();
                        }
                        Debugger.i("[connect()]-->Mqtt client build connection success!");
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        if (listener != null) {
                            listener.onFailure(exception);
                        }
                        Debugger.e("[connect()]-->Mqtt client build connection failed!");
                    }
                });
            } catch (MqttException e) {
                Debugger.e("[connect()]-->Mqtt client build connection got exception : " + e.toString());
            }
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

        Debugger.i("[subscribe()]-->Mqtt client is going to subscribe the topic : [" + topic + "]" + mMqttClient.hashCode());
        if (TextUtils.isEmpty(topic) || listener == null) {
            Debugger.e("[subscribe()]-->Mqtt client subscribe params is illegal!");
            return;
        }
        if (mMqttClient != null && mMqttClient.isConnected()) {
            try {
                mMqttClient.subscribe(topic, qos, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        if (listener != null) {
                            listener.onSuccess();
                        }
                        Debugger.i("[subscribe()]-->Mqtt client subscribe the topic : [" + topic + "] success!");
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        if (listener != null) {
                            listener.onFailure(exception);
                        }
                        Debugger.e("[subscribe()]-->Mqtt client subscribe the topic : [" + topic + "] falied!");
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
                Debugger.e("[subscribe()]-->Mqtt client subscribe got exception : " + e.toString());
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

        Debugger.i("[publish()]-->Mqtt client is going to publish the topic : [" + topic + "]-->message : ["
                + message + "]-->qos : [" + qos + "]-->retained : [" + retained + "]" + mMqttClient.hashCode());

        if (TextUtils.isEmpty(topic) || TextUtils.isEmpty(message) || listener == null) {
            Debugger.e("[publish()]-->Mqtt client publish params is illegal!");
            return;
        }
        if (mMqttClient != null && mMqttClient.isConnected()) {
            try {
                mMqttClient.publish(topic, message.getBytes(), qos, retained, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        if (listener != null) {
                            listener.onSuccess();
                        }
                        Debugger.i("[publish()]-->Mqtt client publish the topic : [" + topic + "] success!");
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        if (listener != null) {
                            listener.onFailure(exception);
                        }
                        Debugger.e("[publish()]-->Mqtt client publish the topic : [" + topic + "] falied!");
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
                Debugger.e("[publish()]-->Mqtt client publish got exception : " + e.toString());
            }
        }
    }

    /**
     * Disconnects from the server.
     *
     * @param listener optional listener that will be notified when the disconnect
     *                 completes. Use null if not required.
     */
    public void disconnect(@NonNull MqttActionListener listener) {
        Debugger.i("[disconnect()]-->Mqtt client is going to disconnect!" + mMqttClient.hashCode());

        if (mMqttClient != null) {
            if (!mMqttClient.isConnected()) {
                Debugger.i("[disconnect()]-->Mqtt client has not build the connection!");
                return;
            }

            try {
                mMqttClient.disconnect(null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        if (listener != null) {
                            listener.onSuccess();
                        }
                        Debugger.i("[disconnect()]-->Mqtt client disconnect success!");
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        if (listener != null) {
                            listener.onFailure(exception);
                        }
                        Debugger.e("[disconnect()]-->Mqtt client disconnect falied!");
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
                Debugger.e("[disconnect()]-->Mqtt client disconnect got exception : " + e.toString());
            }
        }
    }

    /**
     * MqttConnectOptions builder.
     */
    public static final class Builder {
        private final ClientControler P;
        private MqttConnectOptions mConnectOptions;
        private MqttAndroidClient mMqttAndroidClient;

        public Builder(Context context) {
            P = new ClientControler(context);
        }

        /**
         * Sets the user name to use for the connection.More information in
         * {@link MqttConnectOptions #setUserName(String)}.
         *
         * @param userName The Username as a String
         * @return Builder
         */
        public Builder setUserName(@NonNull String userName) {
            P.mUserName = userName;
            return this;
        }

        /**
         * Sets the password to use for the connection.More information in
         * {@link MqttConnectOptions #setPassword(String)}.
         *
         * @param passWord the passWord
         * @return Builder
         */
        public Builder setPassWord(@NonNull String passWord) {
            P.mPassWord = passWord;
            return this;
        }

        public Builder setClientId(@NonNull String clientId) {
            P.mClientId = clientId;
            return this;
        }

        public Builder setHost(@NonNull String host) {
            P.mHost = host;
            return this;
        }

        public Builder setPort(@NonNull String port) {
            P.mPort = port;
            return this;
        }

        /**
         * Sets whether the client and server should remember state across restarts and reconnects.
         * More information in {@link MqttConnectOptions #setCleanSession(boolean)}.
         *
         * @param cleanSession Set to True to enable cleanSession
         * @return Builder
         */
        public Builder setCleanSession(boolean cleanSession) {
            P.mCleanSession = cleanSession;
            return this;
        }

        /**
         * Sets whether the client will automatically attempt to reconnect to the
         * server if the connection is lost.More information in
         * {@link MqttConnectOptions #setAutomaticReconnect(boolean)}.
         *
         * @param reconnect Set to True to enable reconnect
         * @return Builder
         */
        public Builder setAutomaticReconnect(boolean reconnect) {
            P.mReconnect = reconnect;
            return this;
        }

        /**
         * Sets the connection timeout value.More information in
         * {@link MqttConnectOptions #setConnectionTimeout(int)}.
         *
         * @param timeout the timeout value, measured in seconds. It must be &gt;0;
         * @return Builder
         */
        public Builder setTimeout(@IntRange(from = 0) int timeout) {
            P.mTimeout = timeout;
            return this;
        }

        /**
         * Sets the "keep alive" interval.More information in
         * {@link MqttConnectOptions #setKeepAliveInterval(int)}.
         *
         * @param keepalive the interval, measured in seconds, must be &gt;= 0.
         * @return Builder
         */
        public Builder setKeepalive(@IntRange(from = 0) int keepalive) {
            P.mKeepalive = keepalive;
            return this;
        }

        /**
         * init MqttConnectOptions
         */
        private void setUpOptions() {
            if (mConnectOptions == null)
                mConnectOptions = new MqttConnectOptions();
            mConnectOptions.setCleanSession(P.mCleanSession);
            mConnectOptions.setConnectionTimeout(P.mTimeout);
            //Sets the "keep alive" interval.
            mConnectOptions.setKeepAliveInterval(P.mKeepalive);
            mConnectOptions.setAutomaticReconnect(P.mReconnect);
            if (!TextUtils.isEmpty(P.mUserName)) {
                mConnectOptions.setUserName(P.mUserName);
            }
            if (!TextUtils.isEmpty(P.mPassWord)) {
                mConnectOptions.setPassword(P.mPassWord.toCharArray());
            }

            if (!TextUtils.isEmpty(P.mHost) && !TextUtils.isEmpty(P.mPort)) {
                P.mUri = "tcp://" + P.mHost + ":" + P.mPort;
            }
        }

        /**
         * build {@link MqttAndroidClient}
         *
         * @return
         */
        public Builder buildClient() {
            setUpOptions();
            if (mMqttAndroidClient == null && !TextUtils.isEmpty(P.mUri) &&
                    !TextUtils.isEmpty(P.mClientId)) {
//                mMqttAndroidClient = new MqttAndroidClient(P.mContext, "tcp://broker.hivemq.com:1883", P.mClientId);
                mMqttAndroidClient = new MqttAndroidClient(P.mContext, P.mUri, P.mClientId);
                Debugger.i("url:" + P.mUri);
            }

            return this;
        }

    }

    /**
     * Controler class.
     */
    private static final class ClientControler {
        private Context mContext;
        private String mClientId;
        //User info
        //The Username as a String
        private String mUserName;
        private String mPassWord;
        //Url
        private String mUri;
        private String mHost;
        private String mPort;

        //whether the client and server should remember state across restarts and reconnects
        private boolean mCleanSession;
        //connection timeout value
        private int mTimeout;
        //"keep alive" interval
        private int mKeepalive;
        private boolean mReconnect;

        public ClientControler(Context context) {
            mContext = context;
            mTimeout = ActivityConstants.defaultTimeOut;
            mKeepalive = ActivityConstants.defaultKeepAlive;
//            mUserName = "admin";
//            mPassWord = "admin";
        }

    }
}

