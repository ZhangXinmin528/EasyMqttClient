package com.zxm.easymqttclient.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.coding.zxm.mqtt_master.client.MqttClientManager;
import com.coding.zxm.mqtt_master.client.MqttConfig;
import com.coding.zxm.mqtt_master.client.listener.MqttActionListener;
import com.coding.zxm.mqtt_master.client.listener.SimpleConnectionMqttCallback;
import com.zxm.easymqttclient.R;
import com.zxm.easymqttclient.base.BaseActivity;

/**
 * Created by ZhangXinmin on 2018/11/19.
 * Copyright (c) 2018 . All rights reserved.
 * 建立连接
 */
public class NewConnectionActivity extends BaseActivity implements View.OnClickListener {

    private static final int REQUEST_EXTERNAL = 1001;
    private TextInputEditText mClientIdEt;
    private TextInputEditText mServerEt;
    private TextInputEditText mPortEt;
    private CheckBox mSessionCb;
    private String[] PERMISSION_EXTERNAL = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected Object setLayout() {
        return R.layout.activity_new_connection;
    }

    @Override
    protected void initParamsAndViews() {

        checkStrogePermission();
    }

    /**
     * check permission
     */
    private void checkStrogePermission() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSION_EXTERNAL, REQUEST_EXTERNAL);
        }
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

        final String clientId = mClientIdEt.getEditableText().toString().trim();
        final String host = mServerEt.getEditableText().toString().trim();
        final String port = mPortEt.getEditableText().toString().trim();
        final boolean clearSession = mSessionCb.isChecked();

        if (!TextUtils.isEmpty(clientId) && !TextUtils.isEmpty(host) && !TextUtils.isEmpty(port)) {
            final MqttConfig config = new MqttConfig.Builder(getApplicationContext())
                    .setClientId(clientId)
                    .setHost(host)
                    .setPort(port)
                    .setAutomaticReconnect(true)
                    .setCleanSession(clearSession)
                    .setKeepalive(20)
                    .setUserName("")
                    .setPassWord("")
                    .create();

            MqttClientManager.getInstance().init(config);
        }


        MqttClientManager
                .getInstance()
                .connect(new MqttActionListener() {
                             @Override
                             public void onSuccess() {
                                 Toast.makeText(mContext, "Mqtt成功建立连接！", Toast.LENGTH_SHORT).show();
                                 Log.i(TAG, "Mqtt build connection success！");
                                 Intent detial = new Intent(mContext, ConnectionDetialActivity.class);
                                 startActivity(detial);
                             }

                             @Override
                             public void onFailure(Throwable exception) {
                                 Toast.makeText(mContext, "Mqtt建立连接失败！", Toast.LENGTH_SHORT).show();
                                 Log.e(TAG, "Mqtt build connection failed！");
                             }
                         },
                        new SimpleConnectionMqttCallback() {
                            @Override
                            public void messageArrived(String topic, String message, int qos) throws Exception {
                                super.messageArrived(topic, message, qos);
                            }

                            @Override
                            public void connectionLost(Throwable cause) {
                                super.connectionLost(cause);
                            }
                        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
