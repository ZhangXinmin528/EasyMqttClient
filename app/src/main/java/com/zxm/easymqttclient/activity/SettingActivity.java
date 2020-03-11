package com.zxm.easymqttclient.activity;

import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.zxm.easymqttclient.R;
import com.zxm.easymqttclient.base.BaseActivity;
import com.zxm.easymqttclient.util.DisplayUtil;
import com.zxm.utils.core.app.AppUtil;
import com.zxm.utils.core.text.ClickableMovementMethod;

import static android.text.Spanned.SPAN_INCLUSIVE_EXCLUSIVE;

/**
 * Created by ZhangXinmin on 2019/9/12.
 * Copyright (c) 2019 . All rights reserved.
 * 关于
 */
public class SettingActivity extends BaseActivity {

    @Override
    protected Object setLayout() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initParamsAndViews() {

    }

    @Override
    protected void initViews() {
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("关于");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        final TextView version = findViewById(R.id.tv_app_version);
        version.setText(getString(R.string.all_app_version,
                AppUtil.getAppVersionName(mContext)));

        final TextView blog = findViewById(R.id.tv_personal_blog);
        final String blogContent = "https://blog.csdn.net/zxm528";
        final SpannableStringBuilder blogBuilder = new SpannableStringBuilder(blogContent);
        blogBuilder.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Toast.makeText(mContext, "正在开发中~", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(mContext.getResources().getColor(R.color.colorPrimary));
                ds.setUnderlineText(true);
            }
        }, 0, blogContent.length(), SPAN_INCLUSIVE_EXCLUSIVE);

        blog.setText(blogBuilder);
        blog.setMovementMethod(ClickableMovementMethod.getInstance());

        final TextView email = findViewById(R.id.tv_personal_email);
        final String emailContent = "zhangxinmin528@sina.com";
        final SpannableStringBuilder emailBuilder = new SpannableStringBuilder(emailContent);
        emailBuilder.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                DisplayUtil.sendConsultation(mContext);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(mContext.getResources().getColor(R.color.colorPrimary));
                ds.setUnderlineText(true);
            }
        }, 0, emailContent.length(), SPAN_INCLUSIVE_EXCLUSIVE);

        email.setText(emailBuilder);
        email.setMovementMethod(ClickableMovementMethod.getInstance());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
