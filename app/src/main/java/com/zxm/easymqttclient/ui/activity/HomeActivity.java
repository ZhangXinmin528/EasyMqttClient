package com.zxm.easymqttclient.ui.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.coding.zxm.mqtt_master.client.MqttClientManager;
import com.coding.zxm.mqtt_master.client.MqttConfig;
import com.coding.zxm.mqtt_master.client.listener.MqttActionListener;
import com.coding.zxm.mqtt_master.client.listener.SimpleConnectionMqttCallback;
import com.coding.zxm.mqtt_master.util.MqttDebuger;
import com.zxm.easymqttclient.R;
import com.zxm.easymqttclient.ui.adapter.MqttLogAdapter;
import com.zxm.easymqttclient.base.BaseActivity;
import com.zxm.easymqttclient.model.LogEntity;
import com.zxm.easymqttclient.util.Constant;
import com.zxm.easymqttclient.util.DialogUtil;
import com.zxm.easymqttclient.util.PermissionChecker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhangXinmin on 2018/11/19.
 * Copyright (c) 2018 . All rights reserved.
 * 建立连接
 */
public class HomeActivity extends BaseActivity implements
        View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final int REQUEST_EXTERNAL = 1001;

    //连接
    private TextView mConnectTv;
    private TextView mExpansionTv;
    private TextInputEditText mClientIdEt;
    private TextInputEditText mServerEt;
    private TextInputEditText mPortEt;
    private boolean mIsClearSession;
    private boolean mIsAutoReconnect;
    private LinearLayoutCompat mExpansionLayout;
    private TextInputEditText mUserNameEt;
    private TextInputEditText mPwdEt;

    //订阅主题
    private TextInputEditText mSubscribeTopicEt;
    private int mSubQos;

    //发布
    private TextInputEditText mPublishTopicEt;
    private TextInputEditText mMessageEt;
    private int mPubQos;
    private boolean mIsRetained;

    private boolean mIsConnected;

    //日志
    private MqttLogAdapter mAdapter;
    private List<LogEntity> mLogList;
    private BottomSheetDialog mLogDialog;

    @Override
    protected Object setLayout() {
        return R.layout.activity_home;
    }

    @Override
    protected void initParamsAndViews() {
        checkStrogePermission();

        mIsAutoReconnect = true;
        mIsClearSession = false;
        mIsConnected = false;

        mLogList = new ArrayList<>();
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
        mConnectTv = findViewById(R.id.tv_connect);
        mConnectTv.setOnClickListener(this);

        mExpansionTv = findViewById(R.id.tv_connection_expansion);
        mExpansionTv.setOnClickListener(this);

        mExpansionLayout = findViewById(R.id.layout_home_connection_expansion);
        mExpansionLayout.setVisibility(View.GONE);

        mClientIdEt = findViewById(R.id.et_client_id);
        mServerEt = findViewById(R.id.et_server);
        mPortEt = findViewById(R.id.et_port);
        mUserNameEt = findViewById(R.id.et_username);
        mPwdEt = findViewById(R.id.et_password);

        final CheckBox sessionCb = findViewById(R.id.cb_session);
        sessionCb.setOnCheckedChangeListener(this);
        final CheckBox autoReconnectCb = findViewById(R.id.cb_auto_reconnect);
        autoReconnectCb.setOnCheckedChangeListener(this);

        //1.订阅主题
        mSubscribeTopicEt = findViewById(R.id.et_subscribe_topic);
        RadioGroup rg = findViewById(R.id.rg_subscribe);
        rg.setOnCheckedChangeListener((group, checkedId) -> {
            final RadioButton rb = group.findViewById(checkedId);
            final String target = rb.getText().toString();
            mSubQos = Integer.parseInt(target);
        });
        //订阅
        findViewById(R.id.tv_subscribe).setOnClickListener(this);

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
        findViewById(R.id.tv_publish).setOnClickListener(this);

        //底部日志
        final View rootView = LayoutInflater.from(mContext)
                .inflate(R.layout.layout_log_info, null);


        if (rootView != null) {
            //日志
            RecyclerView recyclerView = rootView.findViewById(R.id.rv_log);
            rootView.findViewById(R.id.tv_log_title).setOnClickListener(this);

            mAdapter = new MqttLogAdapter(mLogList);
            recyclerView.setAdapter(mAdapter);

            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));


            mLogDialog = new BottomSheetDialog(mContext, R.style.Theme_Design_BottomSheetDialog);
            mLogDialog.setContentView(rootView);

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //连接
            case R.id.tv_connect:
                if (!mIsConnected) {
                    buildConnection();
                } else {
                    disconnect();
                }
                break;
            case R.id.tv_connection_expansion:
                if (mExpansionLayout.isShown()) {
                    mExpansionLayout.setVisibility(View.GONE);
                    mExpansionTv.setText(R.string.all_expansion);
                } else {
                    mExpansionLayout.setVisibility(View.VISIBLE);
                    mExpansionTv.setText(R.string.all_shrink);
                }
                break;
            //订阅
            case R.id.tv_subscribe:
                subscribeTopic();
                break;
            //发布
            case R.id.tv_publish:
                publishMessage();
                break;
            case R.id.tv_log_title:
                mLogDialog.dismiss();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_log:
                Toast.makeText(mContext, "Count : " + mLogList.size(), Toast.LENGTH_SHORT).show();
                mLogDialog.show();
                mAdapter.notifyDataSetChanged();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //创建连接
    private void buildConnection() {
        final String clientId = mClientIdEt.getEditableText().toString().trim();

        final String host = mServerEt.getEditableText().toString().trim();
        final String port = mPortEt.getEditableText().toString().trim();
        final String userName = mUserNameEt.getEditableText().toString().trim();
        final String pwd = mPwdEt.getEditableText().toString().trim();

        if (TextUtils.isEmpty(host) || TextUtils.isEmpty(port) || TextUtils.isEmpty(clientId)) {
            Toast.makeText(mContext, getString(R.string.all_tips_refuse_build_connection), Toast.LENGTH_SHORT).show();
            return;
        }

        final MqttConfig.Builder builder = new MqttConfig.Builder(getApplicationContext());

        builder.setClientId(clientId)
                .setHost(host)
                .setPort(port)
                .setAutomaticReconnect(mIsAutoReconnect)
                .setCleanSession(mIsClearSession)
                .setKeepalive(20);

        if (!TextUtils.isEmpty(userName)) {
            builder.setUserName(userName);
        }

        if (!TextUtils.isEmpty(pwd)) {
            builder.setPassWord(port);
        }

        final MqttConfig config = builder.create();

        MqttClientManager.getInstance().init(config);

        MqttClientManager
                .getInstance()
                .connect(new MqttActionListener() {
                             @Override
                             public void onSuccess() {
                                 Toast.makeText(mContext, "Mqtt成功建立连接！", Toast.LENGTH_SHORT).show();
                                 MqttDebuger.i(TAG, "Mqtt build connection success!");
                                 addLogEvent(Constant.TAG_CONNECTION, "Mqtt build connection success!");
//                                 MqttDebuger.file(TAG, "Mqtt connection params : \n" +
//                                         MqttClientManager.getInstance().getMqttConfig().getConfigParams());
                             }

                             @Override
                             public void onFailure(Throwable exception) {
                                 Toast.makeText(mContext, "Mqtt建立连接失败！", Toast.LENGTH_SHORT).show();
                                 MqttDebuger.e(TAG, "Mqtt build connection failed!");
                                 addLogEvent(Constant.TAG_ERROR, "Mqtt build connection failed!");
                             }
                         },
                        new SimpleConnectionMqttCallback() {
                            @Override
                            public void messageArrived(String topic, String message, int qos) throws Exception {
                                super.messageArrived(topic, message, qos);
                                MqttDebuger.i(TAG, "messageArrived~");
                                MqttDebuger.json(message);
                                addLogEvent(Constant.TAG_MESSAGE, "Message of [" + topic + "] has arrived : " + message);
                            }

                            @Override
                            public void connectionLost(Throwable cause) {
                                super.connectionLost(cause);
                                mIsConnected = false;
                                switchConnectState();
                                clearMqttData();

                                if (cause == null) {
                                    MqttDebuger.e(TAG, "connectionLost..cause is null~");
                                    addLogEvent(Constant.TAG_ERROR, "Mqtt connection to the server is lost, while the cause is unknown!");
                                } else {
                                    MqttDebuger.e(TAG, "connectionLost..cause : " + cause.toString());
                                    addLogEvent(Constant.TAG_ERROR, "Mqtt connection to the server is lost, while the cause is " + cause.toString());
                                }
                            }

                            @Override
                            public void connectComplete(boolean reconnect, String serverURI) {
                                super.connectComplete(reconnect, serverURI);
                                addLogEvent(Constant.TAG_CONNECTION, "Mqtt connection to the server is completed successfully," +
                                        " while the params reconnect [" + reconnect + "] & serverURI [" + serverURI + "]!");
                                mIsConnected = true;
                                MqttDebuger.i(TAG, "connectComplete..reconnect:" + reconnect);
                                //重新订阅主题，否则收不到消息
                                subscribeTopic();
                                switchConnectState();
                            }
                        });
    }

    private void switchConnectState() {
        if (mIsConnected) {
            mConnectTv.setText(R.string.all_disconnect);
        } else {
            mConnectTv.setText(R.string.all_connection);
        }
    }

    private void subscribeTopic() {
        final String topic = mSubscribeTopicEt.getEditableText().toString().trim();
        if (TextUtils.isEmpty(topic)) {
            Toast.makeText(mContext, getString(R.string.all_tips_empty_topic), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!mIsConnected) {
            Toast.makeText(mContext, getString(R.string.all_tips_refuse_other_operation), Toast.LENGTH_SHORT).show();
            return;
        }

        MqttClientManager.getInstance()
                .subscribe(topic, mSubQos, new MqttActionListener() {
                    @Override
                    public void onSuccess() {
                        MqttDebuger.i(TAG, "subscribeTopic..topic : [" + topic + "]..success!");
                        addLogEvent(Constant.TAG_SUBSCRIBTION, "Mqtt subscribe the topic [" + topic + "] successfully!");
                    }

                    @Override
                    public void onFailure(Throwable exception) {
                        Toast.makeText(mContext, "订阅失败", Toast.LENGTH_SHORT).show();
                        MqttDebuger.e(TAG, "subscribeTopic..topic : [" + topic + "]..failure!");
                        addLogEvent(Constant.TAG_ERROR, "Mqtt subscribe the topic [" + topic + "] failied!");
                    }
                });
    }

    private void publishMessage() {
        final String topic = mPublishTopicEt.getEditableText().toString().trim();
        final String msg = mMessageEt.getEditableText().toString().trim();

        if (TextUtils.isEmpty(topic)) {
            Toast.makeText(mContext, getString(R.string.all_tips_empty_topic), Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(msg)) {
            Toast.makeText(mContext, "请输入发布Message!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!mIsConnected) {
            Toast.makeText(mContext, getString(R.string.all_tips_refuse_other_operation), Toast.LENGTH_SHORT).show();
            return;
        }

        MqttClientManager.getInstance()
                .publish(topic, msg, mPubQos, mIsRetained, new MqttActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(mContext, "发布成功", Toast.LENGTH_SHORT).show();
                        MqttDebuger.i(TAG, "publishMessage..message [: " + msg + "]..success!");
                        addLogEvent(Constant.TAG_PUBLISHING, "Mqtt publish the message [" + msg + "] successfully!");
                    }

                    @Override
                    public void onFailure(Throwable exception) {
                        Toast.makeText(mContext, "发布失败", Toast.LENGTH_SHORT).show();
                        MqttDebuger.e(TAG, "publishMessage..message : [" + msg + "]..failure!");
                        addLogEvent(Constant.TAG_ERROR, "Mqtt publish the topic [" + topic + "] failied!");
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
                MqttDebuger.i(TAG, "disconnect..success~");
                mIsConnected = false;
                switchConnectState();
                addLogEvent(Constant.TAG_DISCONNECTION, "Mqtt disconnect to the server successfully!");
                clearMqttData();
            }

            @Override
            public void onFailure(Throwable exception) {
                Toast.makeText(mContext, "断开连接失败", Toast.LENGTH_SHORT).show();
                MqttDebuger.e(TAG, "disconnect..cause : " + exception.toString());
                addLogEvent(Constant.TAG_ERROR, "Mqtt disconnect to the server has failied!");
            }
        });
    }

    private void clearMqttData() {
        if (!mIsConnected) {
            mClientIdEt.getEditableText().clear();
            mServerEt.getEditableText().clear();
            mPortEt.getEditableText().clear();

            mUserNameEt.getEditableText().clear();
            mPwdEt.getEditableText().clear();

            mSubscribeTopicEt.getEditableText().clear();

            mPublishTopicEt.getEditableText().clear();
            mMessageEt.getEditableText().clear();
        }
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
            case R.id.cb_retained:
                mIsRetained = isChecked;
                break;
        }
    }

    private void addLogEvent(@NonNull String tag, @NonNull String message) {
        if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(message))
            return;
        final LogEntity entity = new LogEntity();
        entity.setTag(tag);
        entity.setMessage(message);
        entity.setTimeMills(System.currentTimeMillis());
        mLogList.add(0, entity);
        MqttDebuger.e(TAG, "log count : " + mLogList.size());
        mAdapter.notifyDataSetChanged();
    }

}
