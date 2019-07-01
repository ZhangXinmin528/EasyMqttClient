package com.zxm.easymqttclient.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.coding.zxm.mqtt_master.client.MqttClientManager;
import com.coding.zxm.mqtt_master.client.MqttConfig;
import com.coding.zxm.mqtt_master.client.listener.MqttActionListener;
import com.coding.zxm.mqtt_master.client.listener.SimpleConnectionMqttCallback;
import com.coding.zxm.mqtt_master.util.MLogger;
import com.zxm.easymqttclient.R;
import com.zxm.easymqttclient.base.BaseActivity;
import com.zxm.easymqttclient.polling.PollingSender;
import com.zxm.easymqttclient.util.Constant;
import com.zxm.easymqttclient.util.DialogUtil;
import com.zxm.easymqttclient.util.PermissionChecker;
import com.zxm.easymqttclient.util.TimeUtil;

/**
 * Created by ZhangXinmin on 2018/11/19.
 * Copyright (c) 2018 . All rights reserved.
 * 建立连接
 */
public class NewConnectionActivity extends BaseActivity implements
        View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final int REQUEST_EXTERNAL = 1001;

    //连接
    private TextInputEditText mClientIdEt;
    private TextInputEditText mServerEt;
    private TextInputEditText mPortEt;
    private boolean isClearSession;
    private boolean isAutoSubscribe;
    private boolean isAutoReconnect;

    //订阅主题
    private TextInputEditText mSubscribeTopicEt;
    private int mSubQos;

    //发布
    private TextInputEditText mPublishTopicEt;
    private TextInputEditText mMessageEt;
    private int mPubQos;
    private boolean isRetained;

    private PollingSender mPollingSender;
    private boolean isAutoDisconnect;

    @Override
    protected Object setLayout() {
        return R.layout.activity_new_connection;
    }

    @Override
    protected void initParamsAndViews() {
        checkStrogePermission();

        mPollingSender =
                new PollingSender(mContext, PollingSender.INTERVAL_FIVE_MINUTES, new PollingReceiver());

    }

    /**
     * check permission
     */
    private void checkStrogePermission() {
        if (!PermissionChecker.checkPersmission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            PermissionChecker.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_EXTERNAL);
        }
    }

    @Override
    protected void initViews() {
        //0.连接
        mClientIdEt = findViewById(R.id.et_client_id);
        mServerEt = findViewById(R.id.et_server);

        mPortEt = findViewById(R.id.et_port);

        final CheckBox sessionCb = findViewById(R.id.cb_session);
        sessionCb.setOnCheckedChangeListener(this);
        final CheckBox autoSubSb = findViewById(R.id.cb_auto_subscribe);
        autoSubSb.setOnCheckedChangeListener(this);
        isAutoSubscribe = true;
        final CheckBox autoReconnect = findViewById(R.id.cb_auto_reconnect);
        autoReconnect.setOnCheckedChangeListener(this);
//        isAutoReconnect = false;

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

        //2.发布
        mPublishTopicEt = findViewById(R.id.et_publish_topic);
        mMessageEt = findViewById(R.id.et_publish_message);
        RadioGroup rg_publish = findViewById(R.id.rg_publish);
        rg_publish.setOnCheckedChangeListener((group, checkedId) -> {
            final RadioButton rb = group.findViewById(checkedId);
            final String target = rb.getText().toString();
            mPubQos = Integer.parseInt(target);
        });

        final CheckBox remainCb = findViewById(R.id.cb_retained);
        remainCb.setOnCheckedChangeListener(this);
        findViewById(R.id.btn_publish).setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        if (mPollingSender != null &&
                !mPollingSender.isPollingStarted()) {
            mPollingSender.start();
        }
        super.onStart();
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
        final String inputValue = mClientIdEt.getEditableText().toString().trim();
        final String tempClientId = TextUtils.isEmpty(inputValue) ?
                "EasyMqttClient" :
                inputValue;

        final String clientId = tempClientId.contains("_") ?
                "EasyMqttClient_" + System.currentTimeMillis() :
                tempClientId + "_" + System.currentTimeMillis();

        mClientIdEt.setText(clientId);

        String host = mServerEt.getEditableText().toString().trim();
        String port = mPortEt.getEditableText().toString().trim();

        if (TextUtils.isEmpty(host)) {
            host = Constant.MQTT_HOST;
            mServerEt.setText(Constant.MQTT_HOST);
        }
        if (TextUtils.isEmpty(port)) {
            port = Constant.MQTT_PORT;
            mPortEt.setText(Constant.MQTT_PORT);
        }

        final MqttConfig config = new MqttConfig.Builder(getApplicationContext())
                .setClientId(clientId)
                .setHost(host)
                .setPort(port)
                .setAutomaticReconnect(isAutoReconnect)
                .setCleanSession(isClearSession)
                .setKeepalive(20)
                .setUserName(Constant.MQTT_NAME)
                .setPassWord(Constant.MQTT_PWD)
                .create();

        MqttClientManager.getInstance().init(config);

        MqttClientManager
                .getInstance()
                .connect(new MqttActionListener() {
                             @Override
                             public void onSuccess() {
                                 Toast.makeText(mContext, "Mqtt成功建立连接！", Toast.LENGTH_SHORT).show();
                                 MLogger.i(TAG, "Mqtt build connection success！");

                                 isAutoDisconnect = false;

                                 MLogger.file(TAG, "Mqtt connection params : \n" +
                                         MqttClientManager.getInstance().getMqttConfig().getConfigParams());
                             }

                             @Override
                             public void onFailure(Throwable exception) {
                                 Toast.makeText(mContext, "Mqtt建立连接失败！", Toast.LENGTH_SHORT).show();
                                 MLogger.e(TAG, "Mqtt build connection failed！");
                             }
                         },
                        new SimpleConnectionMqttCallback() {
                            @Override
                            public void messageArrived(String topic, String message, int qos) throws Exception {
                                super.messageArrived(topic, message, qos);
                                MLogger.i(TAG, "messageArrived~");
                                MLogger.json(message);
                            }

                            @Override
                            public void connectionLost(Throwable cause) {
                                super.connectionLost(cause);
                                if (cause == null) {
                                    MLogger.e(TAG, "connectionLost..cause is null~");
                                } else {
                                    MLogger.e(TAG, "connectionLost..cause : " + cause.toString());
                                }
                            }

                            @Override
                            public void connectComplete(boolean reconnect, String serverURI) {
                                super.connectComplete(reconnect, serverURI);
                                MLogger.i(TAG, "connectComplete..reconnect:" + reconnect);
                                if (isAutoSubscribe && !reconnect) {
                                    //重新订阅主题，否则收不到消息
                                    subscribeTopic();
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
                        MLogger.i(TAG, "subscribeTopic..topic : [" + topic + "]..success!");
                    }

                    @Override
                    public void onFailure(Throwable exception) {
                        Toast.makeText(mContext, "订阅失败", Toast.LENGTH_SHORT).show();
                        MLogger.e(TAG, "subscribeTopic..topic : [" + topic + "]..failure!");
                    }
                });
    }

    private void publishMessage() {
        final String topic = mPublishTopicEt.getEditableText().toString().trim();
        final String msg = mMessageEt.getEditableText().toString().trim();

        if (TextUtils.isEmpty(topic)) {
            Toast.makeText(mContext, "请输入发布Topic!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(msg)) {
            Toast.makeText(mContext, "请输入发布Message!", Toast.LENGTH_SHORT).show();
            return;
        }

        MqttClientManager.getInstance()
                .publish(topic, msg, mPubQos, isRetained, new MqttActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(mContext, "发布成功", Toast.LENGTH_SHORT).show();
                        MLogger.i(TAG, "publishMessage..message [: " + msg + "]..success!");
                    }

                    @Override
                    public void onFailure(Throwable exception) {
                        Toast.makeText(mContext, "发布失败", Toast.LENGTH_SHORT).show();
                        MLogger.e(TAG, "publishMessage..message : [" + msg + "]..failure!");
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
                MLogger.i(TAG, "disconnect..success~");
                isAutoDisconnect = true;
            }

            @Override
            public void onFailure(Throwable exception) {
                Toast.makeText(mContext, "断开连接失败", Toast.LENGTH_SHORT).show();
                MLogger.e(TAG, "disconnect..cause : " + exception.toString());
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_session:
                isClearSession = isChecked;
                break;
            case R.id.cb_auto_reconnect:
                isAutoReconnect = isChecked;
                break;
            case R.id.cb_auto_subscribe:
                isAutoSubscribe = isChecked;
                break;
            case R.id.cb_retained:
                isRetained = isChecked;
                break;
        }
    }

    private final class PollingReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                final String action = intent.getAction();
                MLogger.file(TAG, "PollingReceiver..onReceive : "
                        + TimeUtil.getNowTimeStamp(PollingSender.DATE_FORMAT));

                if (!TextUtils.isEmpty(action)) {
                    //进行下次定时任务
                    mPollingSender.schedule();
                    if (!MqttClientManager.getInstance().isConnected()
                            && !isAutoDisconnect) {

                        buildConnection();
                    }
                }
            }
        }
    }
}
