package com.zxm.easymqtt.util;

import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Created by ZhangXinmin on 2018/11/20.
 * Copyright (c) 2018 . All rights reserved.
 */
public final class Debugger {
    public static boolean LOG_ENABLE = true;
    private static String LOG_TAG = "MqttClient";

    private Debugger() {

    }

    /**
     * 是否打印日志
     *
     * @param enable
     */
    public static void setLogEnable(boolean enable) {
        LOG_ENABLE = enable;
    }

    public static void init(@NonNull String tag) {
        LOG_TAG = tag;
    }

    /**
     * log.i
     *
     * @param msg
     */
    public static void i(@NonNull String msg) {
        i(LOG_TAG, msg);
    }

    /**
     * log.i
     *
     * @param tag
     * @param msg
     */
    public static void i(@NonNull String tag, @NonNull String msg) {
        if (LOG_ENABLE) {
            Log.i(tag, msg);
        }
    }

    /**
     * log.v
     *
     * @param msg
     */
    public static void v(@NonNull String msg) {
        v(LOG_TAG, msg);
    }

    /**
     * log.v
     *
     * @param tag
     * @param msg
     */
    public static void v(@NonNull String tag, @NonNull String msg) {
        if (LOG_ENABLE) {
            Log.v(tag, msg);
        }
    }

    /**
     * log.d
     *
     * @param msg
     */
    public static void d(@NonNull String msg) {
        d(LOG_TAG, msg);
    }

    /**
     * log.d
     *
     * @param tag
     * @param msg
     */
    public static void d(@NonNull String tag, @NonNull String msg) {
        if (LOG_ENABLE) {
            Log.d(tag, msg);
        }
    }

    /**
     * log.w
     *
     * @param msg
     */
    public static void w(@NonNull String msg) {
        w(LOG_TAG, msg);
    }

    /**
     * log.w
     *
     * @param tag
     * @param msg
     */
    public static void w(@NonNull String tag, @NonNull String msg) {
        if (LOG_ENABLE) {
            Log.w(tag, msg);
        }
    }

    /**
     * log.e
     *
     * @param msg
     */
    public static void e(@NonNull String msg) {
        e(LOG_TAG, msg);
    }

    /**
     * log.e
     *
     * @param tag
     * @param msg
     */
    public static void e(@NonNull String tag, @NonNull String msg) {
        if (LOG_ENABLE) {
            Log.e(tag, msg);
        }
    }
}
