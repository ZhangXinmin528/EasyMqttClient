package com.zxm.easymqttclient.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.coding.zxm.mqtt_master.client.MqttClientManager;
import com.coding.zxm.mqtt_master.client.MqttConfig;
import com.coding.zxm.mqtt_master.client.listener.MqttActionListener;
import com.coding.zxm.mqtt_master.client.listener.SimpleConnectionMqttCallback;
import com.zxm.easymqttclient.R;
import com.zxm.easymqttclient.base.BaseActivity;
import com.zxm.easymqttclient.util.Constant;
import com.zxm.utils.core.DialogUtil;
import com.zxm.utils.core.PermissionChecker;

/**
 * Created by ZhangXinmin on 2018/11/19.
 * Copyright (c) 2018 . All rights reserved.
 * 建立连接
 */
public class NewConnectionActivity extends BaseActivity implements View.OnClickListener {

    private static final int REQUEST_EXTERNAL = 1001;

    //连接
    private TextInputEditText mClientIdEt;
    private TextInputEditText mServerEt;
    private TextInputEditText mPortEt;
    private CheckBox mSessionCb;
    private CheckBox mAutoSubSb;

    //订阅主题
    private TextInputEditText mSubscribeTopicEt;
    private int mSubQos;

    //发布
    private TextInputEditText mPublishTopicEt;
    private TextInputEditText mMessageEt;
    private int mPubQos;
    private CheckBox mRemainCb;


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
        if (!PermissionChecker.checkPersmission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            PermissionChecker.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_EXTERNAL);
        }
    }

    @Override
    protected void initViews() {
        //0.连接
        mClientIdEt = findViewById(R.id.et_client_id);
        mServerEt = findViewById(R.id.et_server);
        mPortEt = findViewById(R.id.et_port);
        mSessionCb = findViewById(R.id.cb_session);
        mAutoSubSb = findViewById(R.id.cb_auto_subscribe);
        //建立连接
        findViewById(R.id.btn_connect).setOnClickListener(this);
        //断开连接
        findViewById(R.id.btn_disconnect).setOnClickListener(this);

        //1.订阅主题
        mSubscribeTopicEt = findViewById(R.id.et_subscribe_topic);
        RadioGroup rg = findViewById(R.id.rg_subscribe);
        rg.setOnCheckedChangeListener((group, checkedId) -> {
            final RadioButton rb = group.findViewById(checkedId);
            final String target = rb.getText().toString();
            mSubQos = Integer.parseInt(target);
        });
        //订阅
        findViewById(R.id.btn_subscribe).setOnClickListener(this);

        //发布
        mPublishTopicEt = findViewById(R.id.et_publish_topic);
        mMessageEt = findViewById(R.id.et_publish_message);
        RadioGroup rg_publish = findViewById(R.id.rg_publish);
        rg_publish.setOnCheckedChangeListener((group, checkedId) -> {
            final RadioButton rb = group.findViewById(checkedId);
            final String target = rb.getText().toString();
            mPubQos = Integer.parseInt(target);
        });

        mRemainCb = findViewById(R.id.cb_retained);
        findViewById(R.id.btn_publish).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_connect:
                buildConnection();
                break;
            case R.id.btn_disconnect:
                disconnect();
                break;
            case R.id.btn_subscribe:
                subscribeTopic();
                break;
            case R.id.btn_publish:
                publishMessage();
                break;
        }
    }

    //创建连接
    private void buildConnection() {

        final String clientId = mClientIdEt.getEditableText().toString().trim() + System.currentTimeMillis();
        String host = mServerEt.getEditableText().toString().trim();
        String port = mPortEt.getEditableText().toString().trim();
        final boolean clearSession = mSessionCb.isChecked();

        if (TextUtils.isEmpty(host)) {
            host = Constant.MQTT_HOST;
        }
        if (TextUtils.isEmpty(port)) {
            port = Constant.MQTT_PORT;
        }

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

        MqttClientManager
                .getInstance()
                .connect(new MqttActionListener() {
                             @Override
                             public void onSuccess() {
                                 Toast.makeText(mContext, "Mqtt成功建立连接！", Toast.LENGTH_SHORT).show();
                                 Log.i(TAG, "Mqtt build connection success！");
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

                            @Override
                            public void connectComplete(boolean reconnect, String serverURI) {
                                super.connectComplete(reconnect, serverURI);
                                if (reconnect) {
                                    //重新订阅主题，否则收不到消息

                                }
                            }
                        });
    }

    private void subscribeTopic() {
        final String topic = mSubscribeTopicEt.getEditableText().toString().trim();
        if (TextUtils.isEmpty(topic)) {
            Toast.makeText(mContext, "请输入订阅Topic!", Toast.LENGTH_SHORT).show();
            return;
        }

        MqttClientManager.getInstance()
                .subscribe(topic, mSubQos, new MqttActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(mContext, "订阅成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Throwable exception) {
                        Toast.makeText(mContext, "订阅失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void publishMessage() {
        final String topic = mPublishTopicEt.getEditableText().toString().trim();
        final String msg = mMessageEt.getEditableText().toString().trim();
        final boolean retained = mRemainCb.isChecked();

        if (TextUtils.isEmpty(topic)) {
            Toast.makeText(mContext, "请输入发布Topic!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(msg)) {
            Toast.makeText(mContext, "请输入发布Message!", Toast.LENGTH_SHORT).show();
            return;
        }

        MqttClientManager.getInstance()
                .publish(topic, msg, mPubQos, retained, new MqttActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(mContext, "发布成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Throwable exception) {
                        Toast.makeText(mContext, "发布失败", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    DialogUtil.showForceDialog(mContext, "禁用存储权限将会影响应用部分功能~", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void disconnect() {
        MqttClientManager.getInstance().disconnect(new MqttActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(mContext, "成功断开连接！", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Throwable exception) {
                Toast.makeText(mContext, "断开连接失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
