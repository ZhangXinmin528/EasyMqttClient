package com.coding.zxm.mqtt_master.client;

import android.content.Context;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.coding.zxm.mqtt_master.service.MqttAndroidClient;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

/**
 * Created by ZhangXinmin on 2019/3/18.
 * Copyright (c) 2018 . All rights reserved.
 */
public final class MqttConfig {
    /**
     * Default timeout
     */
    public static final int defaultTimeOut = 1000;
    /**
     * Default keep alive value
     */
    public static final int defaultKeepAlive = 10;

    private Builder builder;

    public MqttConfig(@NonNull Builder builder) {
        this.builder = builder;
    }

    /**
     * Get mqtt connection options.
     *
     * @return
     */
    public MqttConnectOptions getConnectOptions() {
        if (builder != null) {
            return builder.getConnectOptions();
        }
        return null;
    }

    /**
     * Get mqtt android client to build connection.
     *
     * @return {@link MqttAndroidClient}
     */
    public MqttAndroidClient getMqttAndroidClient() {
        if (builder != null) {
            return builder.getMqttAndroidClient();
        }
        return null;
    }


    public static class Builder {
        private final ConfigParams P;

        private MqttConnectOptions mConnectOptions;

        private MqttAndroidClient mMqttAndroidClient;

        /**
         * You'd better use application context.
         *
         * @param context
         */
        public Builder(Context context) {
            P = new ConfigParams(context);
        }

        /**
         * Sets the user name to use for the connection.More information in
         * {@link MqttConnectOptions #setUserName(String)}.
         *
         * @param userName The Username as a String
         * @return MqttConfig
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
         * @return MqttConfig
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
         * @return MqttConfig
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
         * @return MqttConfig
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
         * @return MqttConfig
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
         * @return MqttConfig
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
         * Get mqtt connection options.
         *
         * @return MqttConnectOptions
         */
        public MqttConnectOptions getConnectOptions() {
            return mConnectOptions;
        }

        /**
         * Get mqtt android client to build connection.
         *
         * @return MqttAndroidClient
         */
        public MqttAndroidClient getMqttAndroidClient() {
            return mMqttAndroidClient;
        }

        /**
         * build {@link MqttAndroidClient}
         *
         * @return
         */
        public MqttConfig create() {
            setUpOptions();
            if (mMqttAndroidClient == null && !TextUtils.isEmpty(P.mUri) &&
                    !TextUtils.isEmpty(P.mClientId)) {
                mMqttAndroidClient = new MqttAndroidClient(P.mContext, P.mUri, P.mClientId);
            }

            return new MqttConfig(this);
        }
    }


    /**
     * mqtt config params
     */
    private static final class ConfigParams {
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

        public ConfigParams(Context context) {
            this.mContext = context;
            mTimeout = defaultTimeOut;
            mKeepalive = defaultKeepAlive;
        }
    }
}
