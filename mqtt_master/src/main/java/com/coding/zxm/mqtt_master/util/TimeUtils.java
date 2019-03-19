package com.coding.zxm.mqtt_master.util;

import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ZhangXinmin on 2019/3/18.
 * Copyright (c) 2018 . All rights reserved.
 */
public final class TimeUtils {
    public static final DateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA);

    private TimeUtils() {
    }

    /**
     * Milliseconds to the formatted time string.
     *
     * @param millis The milliseconds.
     * @param format The format.
     * @return the formatted time string
     */
    public static String millis2String(final long millis, @NonNull final DateFormat format) {
        return format.format(new Date(millis));
    }

    /**
     * Return the current formatted time string.
     *
     * <p>
     * #yyyy-MM-dd HH:mm:ss.SSS;
     * </p>
     *
     * @return
     */
    public static String getNowString() {
        return getNowString(DATE_FORMAT);
    }

    /**
     * Return the current formatted time string.
     *
     * @param format The format.
     * @return the current formatted time string
     */
    public static String getNowString(@NonNull final DateFormat format) {
        return millis2String(System.currentTimeMillis(), format);
    }
}
