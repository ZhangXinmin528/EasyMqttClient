package com.zxm.easymqttclient.base;

import android.support.design.widget.TextInputEditText;
import android.view.View;
import android.widget.CheckBox;

import com.zxm.easymqtt.client.MqttClientManager;
import com.zxm.easymqtt.listener.MqttActionListener;
import com.zxm.easymqtt.util.Debugger;
import com.zxm.easymqttclient.R;

/**
 * Created by ZhangXinmin on 2018/11/19.
 * Copyright (c) 2018 . All rights reserved.
 * 建立连接
 */
public class NewConnectionActivity extends BaseActivity implements View.OnClickListener {

    private TextInputEditText mClientIdEt;
    private TextInputEditText mServerEt;
    private TextInputEditText mPortEt;
    private CheckBox mSessionCb;

    @Override
    protected Object setLayout() {
        return R.layout.activity_new_connection;
    }

    @Override
    protected void initParamsAndViews() {

    }

    @Override
    protected void initViews() {
        mClientIdEt = findViewById(R.id.et_client_id);
        mServerEt = findViewById(R.id.et_server);
        mPortEt = findViewById(R.id.et_port);

        mSessionCb = findViewById(R.id.cb_session);

        findViewById(R.id.btn_build_connect).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_build_connect:
                buildConnection();
                break;
        }
    }

    //创建连接
    private void buildConnection() {
        Debugger.setLogEnable(true);

        final String clientId = mClientIdEt.getEditableText().toString().trim();
        final String host = mServerEt.getEditableText().toString().trim();
        final String port = mPortEt.getEditableText().toString().trim();
        final boolean clearSession = mSessionCb.isChecked();

        MqttClientManager.Builder builder =
                new MqttClientManager.Builder(mContext)
                        .setHost(host)
                        .setPort(port)
                        .setClientId(clientId)
                        .setCleanSession(clearSession)
                        .buildClient();

        MqttClientManager.getInstance()
                .init(builder);

        MqttClientManager
                .getInstance()
                .buildConnection(new MqttActionListener() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFailure(Throwable exception) {

                    }
                });

    }
}
