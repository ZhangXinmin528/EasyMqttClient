package com.zxm.easymqttclient.util;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.zxm.easymqttclient.R;


/**
 * Created by ZhangXinmin on 2019/1/14.
 * Copyright (c) 2018 . All rights reserved.
 */
public final class DisplayUtil {
    private DisplayUtil() {
        throw new UnsupportedOperationException("U con't do this!");
    }

    /**
     * 非空
     *
     * @param value
     * @return
     */
    public static String isEmpty(String value) {
        return TextUtils.isEmpty(value) ? "--" : value;
    }

    /**
     * 问题咨询
     *
     * @param context
     */
    public static void sendConsultation(@NonNull Context context) {
        final String[] email = new String[]{"zhangxinmin528@sina.com"};
        final String subject = "[" + context.getString(R.string.app_name) + "]-问题咨询";
        final Intent feedback = new Intent(Intent.ACTION_SEND);
        feedback.setType("message/rfc822");
        feedback.putExtra(Intent.EXTRA_EMAIL, email);//接收人
        feedback.putExtra(Intent.EXTRA_CC, email);//抄送人
        feedback.putExtra(Intent.EXTRA_SUBJECT, subject);//主题
        context.startActivity(Intent.createChooser(feedback, "请选择邮箱"));
    }

    /**
     * 发送日志广播
     *
     * @param context
     * @param tag
     * @param msg
     */
    public static void sendLogEvent(@NonNull Context context,
                                    @NonNull String tag,
                                    @NonNull String msg) {

        if ( TextUtils.isEmpty(tag) || TextUtils.isEmpty(msg))
            return;

        final Intent intent = new Intent(Constant.ACTION_LOG_EVENT);
        intent.putExtra(Constant.EXTRA_TAG, tag);
        intent.putExtra(Constant.EXTRA_MSG, msg);
        sendBroadcastEvent(context, intent);
    }

    /**
     * 发送广播数据
     *
     * @param context
     * @param intent
     */
    public static void sendBroadcastEvent(@NonNull Context context,
                                          @NonNull Intent intent) {
        if (context == null || intent == null)
            return;

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

}
