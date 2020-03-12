package com.zxm.easymqttclient.ui.activity;

import android.annotation.SuppressLint;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.zxm.easymqttclient.R;
import com.zxm.easymqttclient.base.BaseActivity;
import com.zxm.easymqttclient.util.SPUtils;

/**
 * Created by ZhangXinmin on 2020/3/12.
 * Copyright (c) 2020 . All rights reserved.
 * MQTT默认信息配置
 */
public class MqttDefaultActivity extends BaseActivity implements
        View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private boolean mIsClearSession;
    private boolean mIsAutoReconnect;

    private TextInputEditText mTimeoutEt;
    private TextInputEditText mKeepAliveEt;

    @Override
    protected Object setLayout() {
        return R.layout.activity_mqtt_default;
    }

    @Override
    protected void initParamsAndViews() {
        mIsClearSession = false;
        mIsAutoReconnect = true;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initViews() {
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Mqtt默认配置");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //clear session
        final CheckBox sessionCb = findViewById(R.id.cb_session);
        sessionCb.setOnCheckedChangeListener(this);
        sessionCb.setChecked(SPUtils.getClearSession(mContext));
        final CheckBox autoReconnectCb = findViewById(R.id.cb_auto_reconnect);
        autoReconnectCb.setOnCheckedChangeListener(this);
        autoReconnectCb.setChecked(SPUtils.getAutoReconnect(mContext));

        mTimeoutEt = findViewById(R.id.et_connection_timeout);
        final int timeout = SPUtils.getConnectionTimeout(mContext);
        mTimeoutEt.setText(timeout + "");
        mKeepAliveEt = findViewById(R.id.et_keepalive);
        final int keepalive = SPUtils.getKeepaliveInterval(mContext);
        mKeepAliveEt.setText(keepalive + "");

        findViewById(R.id.tv_save).setOnClickListener(this);
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
        SPUtils.setClearSession(mContext, mIsClearSession);
        SPUtils.setAutoReconnect(mContext, mIsAutoReconnect);

        final String timeOut = mTimeoutEt.getEditableText().toString().trim();
        if (!TextUtils.isEmpty(timeOut)) {
            final int time = Integer.parseInt(timeOut);
            if (time <= 0) {
                Toast.makeText(mContext, "输入的连接超时时间无效", Toast.LENGTH_SHORT).show();
                return;
            }
            SPUtils.setConnectionTimeout(mContext, time);
        }

        final String keepalive = mKeepAliveEt.getEditableText().toString().trim();
        if (!TextUtils.isEmpty(keepalive)) {
            final int interval = Integer.parseInt(keepalive);
            if (interval <= 0) {
                Toast.makeText(mContext, "输入的时间间隔无效", Toast.LENGTH_SHORT).show();
                return;
            }
            SPUtils.setKeepaliveInterval(mContext, interval);
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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_session:
                mIsClearSession = isChecked;
                break;
            case R.id.cb_auto_reconnect:
                mIsAutoReconnect = isChecked;
                break;
        }
    }
}
