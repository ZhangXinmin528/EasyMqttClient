package com.zxm.easymqttclient.ui.fragment;

import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.coding.zxm.mqtt_master.client.MqttClientManager;
import com.coding.zxm.mqtt_master.client.listener.MqttActionListener;
import com.coding.zxm.mqtt_master.util.MqttDebuger;
import com.zxm.easymqttclient.R;
import com.zxm.easymqttclient.base.BaseFragment;
import com.zxm.easymqttclient.util.Constant;
import com.zxm.easymqttclient.util.DisplayUtil;

/**
 * Created by ZhangXinmin on 2020/3/11.
 * Copyright (c) 2020 . All rights reserved.
 * Subscribing a topic~
 */
public class SubscribeFragment extends BaseFragment implements View.OnClickListener {

    //订阅主题
    private TextInputEditText mSubscribeTopicEt;
    private int mSubQos;

    public static SubscribeFragment newInstance() {
        return new SubscribeFragment();
    }

    @Override
    protected Object setRootLayout() {
        return R.layout.fragment_subscribe;
    }

    @Override
    public void initParamsAndValues() {

    }

    @Override
    protected void initViews(View rootView) {
        //1.订阅主题
        mSubscribeTopicEt = rootView.findViewById(R.id.et_subscribe_topic);
        RadioGroup rg = rootView.findViewById(R.id.rg_subscribe);
        rg.setOnCheckedChangeListener((group, checkedId) -> {
            final RadioButton rb = group.findViewById(checkedId);
            final String target = rb.getText().toString();
            mSubQos = Integer.parseInt(target);
        });
        //订阅
        rootView.findViewById(R.id.tv_subscribe).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_subscribe:
                subscribeTopic();
                break;
        }
    }

    private void subscribeTopic() {
        final String topic = mSubscribeTopicEt.getEditableText().toString().trim();
        if (TextUtils.isEmpty(topic)) {
            Toast.makeText(mContext, getString(R.string.all_tips_empty_topic), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!MqttClientManager.getInstance().isConnected()) {
            Toast.makeText(mContext, getString(R.string.all_tips_refuse_other_operation), Toast.LENGTH_SHORT).show();
            return;
        }

        MqttClientManager.getInstance()
                .subscribe(topic, mSubQos, new MqttActionListener() {
                    @Override
                    public void onSuccess() {
                        MqttDebuger.i(tag, "subscribeTopic..topic : [" + topic + "]..success!");
                        DisplayUtil.sendLogEvent(mContext, Constant.TAG_SUBSCRIBTION,
                                "Mqtt subscribe the topic [" + topic + "] successfully!");
                    }

                    @Override
                    public void onFailure(Throwable exception) {
                        Toast.makeText(mContext, "订阅失败", Toast.LENGTH_SHORT).show();
                        MqttDebuger.e(tag, "subscribeTopic..topic : [" + topic + "]..failure!");
                        DisplayUtil.sendLogEvent(mContext, Constant.TAG_ERROR,
                                "Mqtt subscribe the topic [" + topic + "] failied!");
                    }
                });
    }
}
