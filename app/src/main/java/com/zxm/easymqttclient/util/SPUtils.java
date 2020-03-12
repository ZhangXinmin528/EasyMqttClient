package com.zxm.easymqttclient.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.zxm.utils.core.sp.SharedPreferencesUtil;

/**
 * Created by ZhangXinmin on 2020/3/12.
 * Copyright (c) 2020 . All rights reserved.
 */
public final class SPUtils {

    //默认配置
    /**
     * whether the client and server should remember state across restarts and reconnects
     */
    public static final String SP_CLEAR_SESSION = "sp_clear_session";

    /**
     * 断开是否重连
     */
    public static final String SP_AUTO_RECONNECT = "sp_auto_reconnect";

    /**
     * connection timeout value
     */
    public static final String SP_CONNECTION_TIMEOUT = "sp_connection_timeout";

    /**
     * "keep alive" interval
     */
    public static final String SP_KEEPALIVE = "sp_keepalive";

    //用户配置
    /**
     * 是否保存Mqtt连接配置信息
     */
    public static final String SP_CACHE_CONFIGURE = "sp_cache_configure";

    /**
     * 自动加载缓存
     */
    public static final String SP_AUTOLOADING_CACHE = "sp_autoloading_cache";

    /**
     * Mqtt Client id
     */
    public static final String SP_MQTT_CLIENT_ID = "sp_mqtt_client_id";

    /**
     * Mqtt host
     */
    public static final String SP_MQTT_HOST = "sp_mqtt_host";

    /**
     * Mqtt port
     */
    public static final String SP_MQTT_PORT = "sp_mqtt_port";

    /**
     * User name
     */
    public static final String SP_MQTT_USER_NAME = "sp_mqtt_user_name";

    /**
     * Passworld
     */
    public static final String SP_MQTT_PWD = "sp_mqtt_pwd";

    /**
     * Subscribe topic
     */
    public static final String SP_SUBSCRIBE_TOPIC = "sp_subscribe_topic";

    /**
     * Publish topic
     */
    public static final String SP_PUBLISH_TOPIC = "sp_publish_topic";

    /**
     * 设置ClearSession状态
     *
     * @param context
     * @param state
     */
    public static void setClearSession(@NonNull Context context, boolean state) {
        SharedPreferencesUtil.put(context, SP_CLEAR_SESSION, state);
    }

    /**
     * 获取ClearSession状态
     *
     * @param context
     * @return
     */
    public static boolean getClearSession(@NonNull Context context) {
        return (boolean) SharedPreferencesUtil.get(context, SP_CLEAR_SESSION, false);
    }

    /**
     * 设置断线重连状态
     *
     * @param context
     * @param state
     */
    public static void setAutoReconnect(@NonNull Context context, boolean state) {
        SharedPreferencesUtil.put(context, SP_AUTO_RECONNECT, state);
    }

    /**
     * 获取断线重连状态
     *
     * @param context
     * @return
     */
    public static boolean getAutoReconnect(@NonNull Context context) {
        return (boolean) SharedPreferencesUtil.get(context, SP_AUTO_RECONNECT, true);
    }

    /**
     * 设置连接超时时间，单位：秒
     *
     * @param context
     * @param timeout
     */
    public static void setConnectionTimeout(@NonNull Context context, int timeout) {
        SharedPreferencesUtil.put(context, SP_CONNECTION_TIMEOUT, timeout);
    }

    /**
     * 获取连接超时时间，默认：30s
     *
     * @param context
     * @return
     */
    public static int getConnectionTimeout(@NonNull Context context) {
        return (int) SharedPreferencesUtil.get(context, SP_CONNECTION_TIMEOUT, 30);
    }

    /**
     * 设置keepalive时间间隔
     *
     * @param context
     * @param interval
     */
    public static void setKeepaliveInterval(@NonNull Context context, int interval) {
        SharedPreferencesUtil.put(context, SP_KEEPALIVE, interval);
    }

    /**
     * 获取keepalive时间间隔
     *
     * @param context
     * @return
     */
    public static int getKeepaliveInterval(@NonNull Context context) {
        return (int) SharedPreferencesUtil.get(context, SP_KEEPALIVE, 60);
    }

    //用户信息配置
    /**
     * 保存Mqtt信息缓存状态
     *
     * @param context
     * @param state
     */
    public static void setCacheState(@NonNull Context context, boolean state) {
        SharedPreferencesUtil.put(context, SP_CACHE_CONFIGURE, state);
    }

    /**
     * 获取Mqtt信息保存状态
     *
     * @param context
     * @return
     */
    public static boolean getCacheState(@NonNull Context context) {
        return (boolean) SharedPreferencesUtil.get(context, SP_CACHE_CONFIGURE, false);
    }

    /**
     * 保存是否自动加载缓存信息
     *
     * @param context
     * @param state
     */
    public static void setAutoloadingCache(@NonNull Context context, boolean state) {
        SharedPreferencesUtil.put(context, SP_AUTOLOADING_CACHE, state);
    }

    /**
     * 获取是否自动加载缓存信息
     *
     * @param context
     * @return
     */
    public static boolean getAutoloadingCache(@NonNull Context context) {
        return (boolean) SharedPreferencesUtil.get(context, SP_AUTOLOADING_CACHE, false);
    }

    /**
     * 设置ClientID
     *
     * @param context
     * @param id
     */
    public static void setMqttClientId(@NonNull Context context, @NonNull String id) {
        if (!TextUtils.isEmpty(id)) {
            SharedPreferencesUtil.put(context, SP_MQTT_CLIENT_ID, id);
        }
    }

    /**
     * 获取ClientId
     *
     * @param context
     * @return
     */
    public static String getMqttClientId(@NonNull Context context) {
        return (String) SharedPreferencesUtil.get(context, SP_MQTT_CLIENT_ID, "");
    }

    /**
     * 设置Mqtt host
     *
     * @param context
     * @param host
     */
    public static void setMqttHost(@NonNull Context context, @NonNull String host) {
        if (!TextUtils.isEmpty(host)) {
            SharedPreferencesUtil.put(context, SP_MQTT_HOST, host);
        }
    }

    /**
     * 获取Mqtt host
     *
     * @param context
     * @return
     */
    public static String getMqttHost(@NonNull Context context) {
        return (String) SharedPreferencesUtil.get(context, SP_MQTT_HOST, "");
    }

    /**
     * 设置Mqtt port
     *
     * @param context
     * @param port
     */
    public static void setMqttPort(@NonNull Context context, @NonNull String port) {
        if (!TextUtils.isEmpty(port)) {
            SharedPreferencesUtil.put(context, SP_MQTT_PORT, port);
        }
    }

    /**
     * 获取Mqtt port
     *
     * @param context
     * @return
     */
    public static String getMqttPort(@NonNull Context context) {
        return (String) SharedPreferencesUtil.get(context, SP_MQTT_PORT, "");
    }

    /**
     * 设置Mqtt user name
     *
     * @param context
     * @param username
     */
    public static void setMqttUserName(@NonNull Context context, @NonNull String username) {
        if (!TextUtils.isEmpty(username)) {
            SharedPreferencesUtil.put(context, SP_MQTT_USER_NAME, username);
        }
    }

    /**
     * 获取Mqtt user name
     *
     * @param context
     * @return
     */
    public static String getMqttUserName(@NonNull Context context) {
        return (String) SharedPreferencesUtil.get(context, SP_MQTT_USER_NAME, "");
    }

    /**
     * 设置Mqtt pwd
     *
     * @param context
     * @param pwd
     */
    public static void setMqttPwd(@NonNull Context context, @NonNull String pwd) {
        if (!TextUtils.isEmpty(pwd)) {
            SharedPreferencesUtil.put(context, SP_MQTT_PWD, pwd);
        }
    }

    /**
     * 获取Mqtt pwd
     *
     * @param context
     * @return
     */
    public static String getMqttPwd(@NonNull Context context) {
        return (String) SharedPreferencesUtil.get(context, SP_MQTT_PWD, "");
    }

    /**
     * 设置Mqtt订阅主题
     *
     * @param context
     * @param topic
     */
    public static void setMqttSubscribeTopic(@NonNull Context context, @NonNull String topic) {
        if (!TextUtils.isEmpty(topic)) {
            SharedPreferencesUtil.put(context, SP_SUBSCRIBE_TOPIC, topic);
        }
    }

    /**
     * 获取Mqtt订阅主题
     *
     * @param context
     * @return
     */
    public static String getMqttSubscribeTopic(@NonNull Context context) {
        return (String) SharedPreferencesUtil.get(context, SP_SUBSCRIBE_TOPIC, "");
    }

    /**
     * 设置Mqtt发布主题
     *
     * @param context
     * @param topic
     */
    public static void setMqttPublishTopic(@NonNull Context context, @NonNull String topic) {
        if (!TextUtils.isEmpty(topic)) {
            SharedPreferencesUtil.put(context, SP_PUBLISH_TOPIC, topic);
        }
    }

    /**
     * 获取Mqtt发布主题
     *
     * @param context
     * @return
     */
    public static String getMqttPublishTopic(@NonNull Context context) {
        return (String) SharedPreferencesUtil.get(context, SP_PUBLISH_TOPIC, "");
    }
}
