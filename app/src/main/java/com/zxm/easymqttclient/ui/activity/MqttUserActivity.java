package com.zxm.easymqttclient.ui.activity;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;

import com.zxm.easymqttclient.R;
import com.zxm.easymqttclient.base.BaseActivity;
import com.zxm.easymqttclient.util.SPUtils;

/**
 * Created by ZhangXinmin on 2020/3/12.
 * Copyright (c) 2020 . All rights reserved.
 * 用户信息配置
 */
public class MqttUserActivity extends BaseActivity implements
        View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private TextInputEditText mClientIdEt;
    private TextInputEditText mHostEt;
    private TextInputEditText mPortEt;
    private TextInputEditText mSubscribeTopicEt;
    private TextInputEditText mPublishTopicEt;

    private boolean mIsCacheEnable;
    private boolean mIsAutoloadingEnable;

    @Override
    protected Object setLayout() {
        return R.layout.activity_mqtt_user;
    }

    @Override
    protected void initParamsAndViews() {
        mIsCacheEnable = false;
        mIsAutoloadingEnable = false;
    }

    @Override
    protected void initViews() {
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Mqtt用户配置");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mClientIdEt = findViewById(R.id.et_mqtt_client_id);
        final String id = SPUtils.getMqttClientId(mContext);
        if (!TextUtils.isEmpty(id)){
            mClientIdEt.setText(id);
        }
        mHostEt = findViewById(R.id.et_mqtt_host);
        final String host = SPUtils.getMqttHost(mContext);
        if (!TextUtils.isEmpty(host)){
            mHostEt.setText(host);
        }
        mPortEt = findViewById(R.id.et_mqtt_port);
        final String port = SPUtils.getMqttPort(mContext);
        if (!TextUtils.isEmpty(port)){
            mPortEt.setText(port);
        }
        mSubscribeTopicEt = findViewById(R.id.et_mqtt_subscribe_topic);
        final String subTopic = SPUtils.getMqttSubscribeTopic(mContext);
        if (!TextUtils.isEmpty(subTopic)){
            mSubscribeTopicEt.setText(subTopic);
        }
        mPublishTopicEt = findViewById(R.id.et_mqtt_publish_topic);
        final String publishTopic = SPUtils.getMqttPublishTopic(mContext);
        if (!TextUtils.isEmpty(publishTopic)){
            mPublishTopicEt.setText(publishTopic);
        }

        final SwitchCompat cache = findViewById(R.id.sw_cache);
        cache.setOnCheckedChangeListener(this);
        cache.setChecked(SPUtils.getCacheState(mContext));

        final SwitchCompat locadCache = findViewById(R.id.sw_load_cache);
        locadCache.setOnCheckedChangeListener(this);
        locadCache.setChecked(SPUtils.getAutoloadingCache(mContext));

        findViewById(R.id.tv_save).setOnClickListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.sw_cache:
                mIsCacheEnable = isChecked;
                break;
            case R.id.sw_load_cache:
                mIsAutoloadingEnable = false;
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_save:
                saveConfigure();
                break;
        }
    }

    private void saveConfigure() {
        SPUtils.setCacheState(mContext, mIsCacheEnable);
        SPUtils.setAutoloadingCache(mContext, mIsAutoloadingEnable);

        final String clientId = mClientIdEt.getEditableText().toString().trim();
        if (!TextUtils.isEmpty(clientId)) {
            SPUtils.setMqttClientId(mContext, clientId);
        }

        final String host = mHostEt.getEditableText().toString().trim();
        if (!TextUtils.isEmpty(host)) {
            SPUtils.setMqttHost(mContext, host);
        }

        final String port = mPortEt.getEditableText().toString().trim();
        if (!TextUtils.isEmpty(port)) {
            SPUtils.setMqttPort(mContext, port);
        }

        final String subTopic = mSubscribeTopicEt.getEditableText().toString().trim();
        if (!TextUtils.isEmpty(subTopic)) {
            SPUtils.setMqttSubscribeTopic(mContext, subTopic);
        }

        final String publishTopic = mPublishTopicEt.getEditableText().toString().trim();
        if (!TextUtils.isEmpty(publishTopic)) {
            SPUtils.setMqttPublishTopic(mContext, publishTopic);
        }
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
