package com.zxm.easymqttclient.util;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
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

}
