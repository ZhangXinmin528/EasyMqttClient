package com.zxm.easymqttclient.ui.fragment;

import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.coding.zxm.mqtt_master.client.MqttClientManager;
import com.coding.zxm.mqtt_master.client.MqttConfig;
import com.coding.zxm.mqtt_master.client.listener.MqttActionListener;
import com.coding.zxm.mqtt_master.client.listener.SimpleConnectionMqttCallback;
import com.coding.zxm.mqtt_master.util.MqttDebuger;
import com.zxm.easymqttclient.R;
import com.zxm.easymqttclient.base.BaseFragment;
import com.zxm.easymqttclient.util.Constant;
import com.zxm.easymqttclient.util.DisplayUtil;
import com.zxm.easymqttclient.util.SPUtils;

import java.util.Objects;

/**
 * Created by ZhangXinmin on 2020/3/11.
 * Copyright (c) 2020 . All rights reserved.
 * Building a mqtt connection.
 */
public class ConnectionFragment extends BaseFragment implements
        View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    //连接
    private TextView mConnectTv;
    private TextView mExpansionTv;
    private TextInputEditText mClientIdEt;
    private TextInputEditText mServerEt;
    private TextInputEditText mPortEt;
    private boolean mIsClearSession;
    private boolean mIsAutoReconnect;
    private int mKeepaliveInterval;
    private LinearLayoutCompat mExpansionLayout;
    private TextInputEditText mUserNameEt;
    private TextInputEditText mPwdEt;

    //能否缓存
    private boolean mCacheEnable;

    public static ConnectionFragment newInstance() {
        return new ConnectionFragment();
    }

    @Override
    protected Object setRootLayout() {
        return R.layout.fragment_connection;
    }

    @Override
    public void initParamsAndValues() {
        mCacheEnable = SPUtils.getCacheState(mContext);
        mIsAutoReconnect = true;
        mIsClearSession = false;

        mKeepaliveInterval = 60;
    }

    @Override
    protected void initViews(View rootView) {

        //0.连接
        mConnectTv = rootView.findViewById(R.id.tv_connect);
        mConnectTv.setOnClickListener(this);

        mExpansionTv = rootView.findViewById(R.id.tv_connection_expansion);
        mExpansionTv.setOnClickListener(this);

        mExpansionLayout = rootView.findViewById(R.id.layout_home_connection_expansion);
        mExpansionLayout.setVisibility(View.GONE);

        //ClientId
        mClientIdEt = rootView.findViewById(R.id.et_client_id);

        //Host
        mServerEt = rootView.findViewById(R.id.et_server);
        final String host = SPUtils.getMqttHost(mContext);
        if (!TextUtils.isEmpty(host) && mIsAutoloadingCache) {
            mServerEt.setText(host);
        }

        //Port
        mPortEt = rootView.findViewById(R.id.et_port);
        final String port = SPUtils.getMqttPort(mContext);
        if (!TextUtils.isEmpty(port) && mIsAutoloadingCache) {
            mPortEt.setText(port);
        }

        mUserNameEt = rootView.findViewById(R.id.et_username);

        mPwdEt = rootView.findViewById(R.id.et_password);

        final CheckBox sessionCb = rootView.findViewById(R.id.cb_session);
        sessionCb.setOnCheckedChangeListener(this);
        if (mIsAutoloadingCache) {
            sessionCb.setChecked(mIsClearSession);
        }

        final CheckBox autoReconnectCb = rootView.findViewById(R.id.cb_auto_reconnect);
        autoReconnectCb.setOnCheckedChangeListener(this);
        if (mIsAutoloadingCache) {
            autoReconnectCb.setChecked(mIsAutoReconnect);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //连接
            case R.id.tv_connect:
                if (!MqttClientManager.getInstance().isConnected()) {
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
        }
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

        if (getActivity() == null) {
            return;
        }

        final MqttConfig.Builder builder =
                new MqttConfig.Builder(
                        Objects.requireNonNull(getActivity()).getApplicationContext());

        builder.setClientId(clientId)
                .setHost(host)
                .setPort(port)
                .setAutomaticReconnect(mIsAutoReconnect)
                .setCleanSession(mIsClearSession)
                .setKeepalive(mKeepaliveInterval);

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
                                 MqttDebuger.i(tag, "Mqtt build connection success!");
                                 DisplayUtil.sendLogEvent(mContext, Constant.TAG_CONNECTION, "Mqtt build connection success!");
                             }

                             @Override
                             public void onFailure(Throwable exception) {
                                 Toast.makeText(mContext, "Mqtt建立连接失败！", Toast.LENGTH_SHORT).show();
                                 MqttDebuger.e(tag, "Mqtt build connection failed!");
                                 DisplayUtil.sendLogEvent(mContext, Constant.TAG_ERROR, "Mqtt build connection failed!");
                             }
                         },
                        new SimpleConnectionMqttCallback() {
                            @Override
                            public void messageArrived(String topic, String message, int qos) throws Exception {
                                super.messageArrived(topic, message, qos);
                                MqttDebuger.i(tag, "messageArrived~");
                                MqttDebuger.json(message);
                                DisplayUtil.sendLogEvent(mContext, Constant.TAG_MESSAGE,
                                        "Message of [" + topic + "] has arrived : " + message);
                            }

                            @Override
                            public void connectionLost(Throwable cause) {
                                super.connectionLost(cause);
                                //切换按钮状态
                                switchConnectState();
//                                clearMqttData();

                                if (cause == null) {
                                    MqttDebuger.e(tag, "connectionLost..cause is null~");
                                    DisplayUtil.sendLogEvent(mContext, Constant.TAG_ERROR,
                                            "Mqtt connection to the server is lost, while the cause is unknown!");
                                } else {
                                    MqttDebuger.e(tag, "connectionLost..cause : " + cause.toString());
                                    DisplayUtil.sendLogEvent(mContext, Constant.TAG_ERROR,
                                            "Mqtt connection to the server is lost, while the cause is " + cause.toString());
                                }
                            }

                            @Override
                            public void connectComplete(boolean reconnect, String serverURI) {
                                super.connectComplete(reconnect, serverURI);
                                DisplayUtil.sendLogEvent(mContext, Constant.TAG_CONNECTION, "Mqtt connection to the server is completed successfully," +
                                        " while the params reconnect [" + reconnect + "] & serverURI [" + serverURI + "]!");
                                MqttDebuger.i(tag, "connectComplete..reconnect:" + reconnect);
                                //TODO：连接成功才能订阅主题
                                switchConnectState();
                                //缓存用户配置
                                saveUserConfigure();
                            }
                        });
    }

    private void disconnect() {

        MqttClientManager.getInstance().disconnect(new MqttActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(mContext, "成功断开连接！", Toast.LENGTH_SHORT).show();
                MqttDebuger.i(tag, "disconnect..success~");
                switchConnectState();
                DisplayUtil.sendLogEvent(mContext, Constant.TAG_DISCONNECTION,
                        "Mqtt disconnect to the server successfully!");
                clearMqttData();
            }

            @Override
            public void onFailure(Throwable exception) {
                Toast.makeText(mContext, "断开连接失败", Toast.LENGTH_SHORT).show();
                MqttDebuger.e(tag, "disconnect..cause : " + exception.toString());
                DisplayUtil.sendLogEvent(mContext, Constant.TAG_ERROR,
                        "Mqtt disconnect to the server has failied!");
            }
        });
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

    private void switchConnectState() {
        if (MqttClientManager.getInstance().isConnected()) {
            mConnectTv.setText(R.string.all_disconnect);
        } else {
            mConnectTv.setText(R.string.all_connection);
        }
    }

    /**
     * 保存用户信息
     */
    private void saveUserConfigure() {
        if (mCacheEnable) {
            final String host = mServerEt.getEditableText().toString().trim();
            if (!TextUtils.isEmpty(host)) {
                SPUtils.setMqttHost(mContext, host);
            }

            final String port = mPortEt.getEditableText().toString().trim();
            if (!TextUtils.isEmpty(port)) {
                SPUtils.setMqttPort(mContext, port);
            }
        }
    }

    /**
     * 清除输入框信息
     */
    private void clearMqttData() {
        if (!MqttClientManager.getInstance().isConnected()) {
            mClientIdEt.getEditableText().clear();
            mServerEt.getEditableText().clear();
            mPortEt.getEditableText().clear();

            mUserNameEt.getEditableText().clear();
            mPwdEt.getEditableText().clear();
        }
    }
}
