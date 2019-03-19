package com.zxm.easymqttclient.fragment;

import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
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
 * Publish
 */
public class PublishFragment extends BaseFragment implements View.OnClickListener {

    private TextInputEditText mTopicEt;
    private TextInputEditText mMsgEt;
    private CheckBox mRetainedCb;
    private int mQos;

    public static PublishFragment newInstance() {
        return new PublishFragment();
    }

    @Override
    protected Object setRootLayout() {
        return R.layout.fragment_publish;
    }

    @Override
    public void initParamsAndValues() {
        mQos = 0;
    }

    @Override
    protected void initViews(View rootView) {
        mTopicEt = rootView.findViewById(R.id.et_publish_topic);
        mMsgEt = rootView.findViewById(R.id.et_publish_message);
        RadioGroup rg = rootView.findViewById(R.id.rg_publish);
        rg.setOnCheckedChangeListener((group, checkedId) -> {
            final RadioButton rb = group.findViewById(checkedId);
            final String target = rb.getText().toString();
            mQos = Integer.parseInt(target);
        });
        mRetainedCb = rootView.findViewById(R.id.cb_retained);

        rootView.findViewById(R.id.btn_publish).setOnClickListener(this);
        rootView.findViewById(R.id.btn_disconnect).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_publish:
                publishMessage();
                break;
            case R.id.btn_disconnect:
                disconnect();
                break;
        }
    }

    private void publishMessage() {
        final String topic = mTopicEt.getEditableText().toString().trim();
        final String msg = mMsgEt.getEditableText().toString().trim();
        final boolean retained = mRetainedCb.isChecked();

        if (TextUtils.isEmpty(topic)) {
            Toast.makeText(mContext, "请输入发布Topic!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(msg)) {
            Toast.makeText(mContext, "请输入发布Message!", Toast.LENGTH_SHORT).show();
            return;
        }

        MqttClientManager.getInstance()
                .publish(topic, msg, mQos, retained, new MqttActionListener() {
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
