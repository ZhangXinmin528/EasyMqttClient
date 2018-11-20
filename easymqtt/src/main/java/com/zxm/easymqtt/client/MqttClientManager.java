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
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * Created by ZhangXinmin on 2018/11/20.
 * Copyright (c) 2018 . All rights reserved.
 * The manager for {@link org.eclipse.paho.android.service.MqttAndroidClient}.
 */
public final class MqttClientManager {

    private static MqttClientManager INSTANCE;

    private Builder mBuilder;
    private MqttAndroidClient mMqttClient;
    private Connection.ConnectionStatus mStatus = Connection.ConnectionStatus.NONE;

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

        if (builder != null) {
            mMqttClient.setTraceEnabled(true);
            mMqttClient.setTraceCallback(new SimpleMqttTraceHandler());
        }
    }

    /**
     * Build a new connection {@link MqttAndroidClient}
     *
     * @param listener {@link MqttActionListener}
     */
    public void buildConnection(@NonNull MqttActionListener listener) {
        if (mMqttClient == null) {
            mMqttClient = mBuilder.mMqttAndroidClient;
        }

        if (mStatus.equals(Connection.ConnectionStatus.CONNECTING)) {
            Debugger.e("Mqtt client is connecting, you'd better avoid do it.");
            return;
        }

        if (mStatus.equals(Connection.ConnectionStatus.CONNECTED)) {
            Debugger.e("Mqtt client has connected, you must not do it.");
            return;
        }

        if (listener != null) {
            try {
                mStatus = Connection.ConnectionStatus.CONNECTING;
                Debugger.i("Mqtt client is connecting!");
                mMqttClient.connect(mBuilder.mConnectOptions, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        listener.onSuccess();
                        mStatus = Connection.ConnectionStatus.CONNECTED;
                        Debugger.i("Mqtt client build connection success!");
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        listener.onFailure(exception);
                        mStatus = Connection.ConnectionStatus.ERROR;
                        Debugger.e("Mqtt client build connection failed : " + exception.getMessage());
                    }
                });
            } catch (MqttException e) {
                mStatus = Connection.ConnectionStatus.ERROR;
                Debugger.e("Mqtt client build connection got exception : " + e.toString());
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
                mMqttAndroidClient = new MqttAndroidClient(P.mContext, P.mUri, P.mClientId);
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

        public ClientControler(Context context) {
            mContext = context;
            mTimeout = ActivityConstants.defaultTimeOut;
            mKeepalive = ActivityConstants.defaultKeepAlive;
        }

    }
}

