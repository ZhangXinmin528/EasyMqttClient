package com.zxm.easymqttclient.fragment;

import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.coding.zxm.mqtt_master.client.MqttClientManager;
import com.coding.zxm.mqtt_master.client.listener.MqttActionListener;
import com.zxm.easymqttclient.R;
import com.zxm.easymqttclient.base.BaseFragment;

/**
 * Created by ZhangXinmin on 2018/11/21.
 * Copyright (c) 2018 . All rights reserved.
 * Subscribe
 */
public class SubscribeFragment extends BaseFragment implements View.OnClickListener {

    private TextInputEditText mTopicEt;
    private int mQos;

    public static SubscribeFragment newInstance() {
        return new SubscribeFragment();
    }

    @Override
    protected Object setRootLayout() {
        return R.layout.fragment_subscribe;
    }

    @Override
    public void initParamsAndValues() {
        mQos = 1;
    }

    @Override
    protected void initViews(View rootView) {
        mTopicEt = rootView.findViewById(R.id.et_subscribe_topic);
        RadioGroup rg = rootView.findViewById(R.id.rg_subscribe);
        rg.setOnCheckedChangeListener((group, checkedId) -> {
            final RadioButton rb = group.findViewById(checkedId);
            final String target = rb.getText().toString();
            mQos = Integer.parseInt(target);
        });

        rootView.findViewById(R.id.btn_subscribe).setOnClickListener(this);
        rootView.findViewById(R.id.btn_disconnect).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_subscribe:
                subscribeTopic();
                break;
            case R.id.btn_disconnect:
                disconnect();
                break;
        }
    }

    private void subscribeTopic() {
        final String topic = mTopicEt.getEditableText().toString().trim();
        if (TextUtils.isEmpty(topic)) {
            Toast.makeText(mContext, "请输入订阅Topic!", Toast.LENGTH_SHORT).show();
            return;
        }

        MqttClientManager.getInstance()
                .subscribe(topic, mQos, new MqttActionListener() {
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
