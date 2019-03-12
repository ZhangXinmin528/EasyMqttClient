package com.zxm.easymqttclient.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.zxm.easymqtt.client.MqttClientManager;
import com.zxm.easymqtt.listener.MqttActionListener;
import com.zxm.easymqtt.listener.SimpleMqttCallback;
import com.zxm.easymqtt.util.DiskFormatStrategy;
import com.zxm.easymqttclient.R;
import com.zxm.easymqttclient.base.BaseActivity;

import java.util.Objects;

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

        FormatStrategy logStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
                .methodCount(0)         // (Optional) How many method line to show. Default 2
                .methodOffset(0)        // (Optional) Hides internal method calls up to offset. Default 5
                .tag("MqttTest")   // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .build();

        DiskFormatStrategy diskStrategy =
                DiskFormatStrategy.newBuilder()
                        .tag("MqttTest")
                        .logFolder(getLogFileFolder())
                        .build();

        MqttClientManager.setLogEnable(true, logStrategy);
        MqttClientManager.setSaveLogEnable(true, diskStrategy);

        MqttClientManager.Builder builder =
                new MqttClientManager.Builder(mContext)
                        .setHost(host)
                        .setPort(port)
                        .setClientId(clientId)
                        .setCleanSession(clearSession)
                        .setKeepalive(20)
                        .setAutomaticReconnect(true)
                        .buildClient();

        MqttClientManager.getInstance()
                .init(builder);

        MqttClientManager.getInstance()
                .connect(new MqttActionListener() {
                             @Override
                             public void onSuccess() {
                                 Toast.makeText(mContext, "Mqtt成功建立连接！", Toast.LENGTH_SHORT).show();
                                 Logger.i(TAG, "Mqtt build connection success！");
                                 Intent detial = new Intent(mContext, ConnectionDetialActivity.class);
                                 startActivity(detial);
                             }

                             @Override
                             public void onFailure(Throwable exception) {
                                 Toast.makeText(mContext, "Mqtt建立连接失败！", Toast.LENGTH_SHORT).show();
                                 Logger.e(TAG, "Mqtt build connection failed！");
                             }
                         },
                        new SimpleMqttCallback() {
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

    /**
     * 获取文件目录
     *
     * @return
     */
    public String getLogFileFolder() {
        if (isExternalStorageWritable()) {
            return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath();
        } else {
            return Objects.requireNonNull(getExternalFilesDir(null)).getPath();
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
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
