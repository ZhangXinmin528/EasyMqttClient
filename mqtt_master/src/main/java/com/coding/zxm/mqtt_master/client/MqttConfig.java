package com.coding.zxm.mqtt_master.client;

import android.content.Context;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.coding.zxm.mqtt_master.service.MqttAndroidClient;
import com.coding.zxm.mqtt_master.util.MqttDebuger;

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
    private static final String TAG = MqttConfig.class.getSimpleName();
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

    public String getConfigParams() {
        return builder.outputConfig();
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
         * @return {@link #Builder(Context)}
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
         * @return {@link #Builder(Context)}
         * @see MqttConnectOptions#setPassword(char[])
         */
        public Builder setPassWord(@NonNull String passWord) {
            P.mPassWord = passWord;
            return this;
        }

        /**
         * Set the specifies the name to identify to the server.
         *
         * @param clientId specifies the name by which this connection should be
         *                 identified to the server
         * @return {@link #Builder(Context)}
         */
        public Builder setClientId(@NonNull String clientId) {
            P.mClientId = clientId;
            return this;
        }

        /**
         * Set the host to build server url to create {@link MqttAndroidClient}.
         * <p> "tcp://" + P.mHost + ":" + P.mPort</p>
         *
         * @param host host value
         * @return {@link #Builder(Context)}
         */
        public Builder setHost(@NonNull String host) {
            P.mHost = host;
            return this;
        }

        /**
         * Set the port to build server url to create {@link MqttAndroidClient}.
         * *<p> "tcp://" + P.mHost + ":" + P.mPort</p>
         *
         * @param port port value
         * @return {@link #Builder(Context)}
         */
        public Builder setPort(@NonNull String port) {
            P.mPort = port;
            return this;
        }

        /**
         * Sets whether the client and server should remember state across restarts and reconnects.
         * More information in {@link MqttConnectOptions #setCleanSession(boolean)}.
         *
         * @param cleanSession Set to True to enable cleanSession
         * @return {@link #Builder(Context)}
         * @see MqttConnectOptions#setCleanSession(boolean)
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
         * @return {@link #Builder(Context)}
         * @see MqttConnectOptions#setAutomaticReconnect(boolean)
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
         * @return {@link #Builder(Context)}
         * @see MqttConnectOptions#setConnectionTimeout(int)
         */
        public Builder setConnectionTimeout(@IntRange(from = 0) int timeout) {
            P.mConnectionTimeout = timeout;
            return this;
        }

        /**
         * Sets the "keep alive" interval.More information in
         * {@link MqttConnectOptions #setKeepAliveInterval(int)}.
         *
         * @param keepalive the interval, measured in seconds, must be &gt;= 0.
         * @return {@link #Builder(Context)}
         * @see MqttConnectOptions#setKeepAliveInterval(int)
         */
        public Builder setKeepalive(@IntRange(from = 0) int keepalive) {
            P.mKeepalive = keepalive;
            return this;
        }

        /**
         * turn tracing on and off
         *
         * @param traceEnabled set <code>true</code> to enable trace, otherwise, set
         *                     <code>false</code> to disable trace
         * @return {@link #Builder(Context)}
         * @see com.coding.zxm.mqtt_master.service.MqttService#setTraceEnabled(boolean)
         */
        public Builder setTraceEnable(boolean traceEnabled) {
            P.mTraceEnable = traceEnabled;
            return this;
        }


        /**
         * init MqttConnectOptions
         */
        private void setUpOptions() {
            if (mConnectOptions != null) {
                mConnectOptions = null;
            }
            mConnectOptions = new MqttConnectOptions();

            mConnectOptions.setCleanSession(P.mCleanSession);
            mConnectOptions.setConnectionTimeout(P.mConnectionTimeout);
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
                MqttDebuger.d(TAG, "Uri : " + P.mUri,
                        "ClientId : " + P.mClientId,
                        "CleanSession : " + P.mCleanSession,
                        "Keepalive : " + P.mKeepalive,
                        "Reconnect : " + P.mReconnect,
                        "ConnectionTimeout : " + P.mConnectionTimeout);
            }

            if (mMqttAndroidClient != null) {
                mMqttAndroidClient.setTraceEnabled(P.mTraceEnable);
            }

            return new MqttConfig(this);
        }


        public String outputConfig() {
            return "MQTT connection configure :" + "\n" +
                    "Uri : " + "tcp://" + P.mHost + ":" + P.mPort + "\n" +
                    "ClientId : " + P.mClientId + "\n" +
                    "UserName : " + P.mUserName + "\n" +
                    "Password : " + P.mPassWord + "\n" +
                    "CleanSession : " + P.mCleanSession + "\n" +
                    "Keepalive : " + P.mKeepalive + "\n" +
                    "Reconnect : " + P.mReconnect + "\n" +
                    "ConnectionTimeout : " + P.mConnectionTimeout;
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
        private int mConnectionTimeout;
        //"keep alive" interval
        private int mKeepalive;
        private boolean mReconnect;

        //trace enable
        private boolean mTraceEnable;

        public ConfigParams(Context context) {
            this.mContext = context;
            mConnectionTimeout = defaultTimeOut;
            mKeepalive = defaultKeepAlive;
        }


        @Override
        public String toString() {
            return "ConfigParams{" +
                    "mClientId='" + mClientId + '\'' +
                    ", mHost='" + mHost + '\'' +
                    ", mPort='" + mPort + '\'' +
                    ", mUri='" + mUri + '\'' +
                    ", mUserName='" + mUserName + '\'' +
                    ", mPassWord='" + mPassWord + '\'' +
                    ", mCleanSession=" + mCleanSession +
                    ", mTimeout=" + mConnectionTimeout +
                    ", mKeepalive=" + mKeepalive +
                    ", mReconnect=" + mReconnect +
                    ", mTraceEnable=" + mTraceEnable +
                    '}';
        }
    }
}
