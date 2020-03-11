package com.zxm.easymqttclient.fragment;

import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.coding.zxm.mqtt_master.client.MqttClientManager;
import com.coding.zxm.mqtt_master.client.listener.MqttActionListener;
import com.coding.zxm.mqtt_master.util.MqttDebuger;
import com.zxm.easymqttclient.R;
import com.zxm.easymqttclient.base.BaseFragment;
import com.zxm.easymqttclient.util.Constant;

/**
 * Created by ZhangXinmin on 2020/3/11.
 * Copyright (c) 2020 . All rights reserved.
 * Publishing the messages.
 */
public class PublishFragment extends BaseFragment implements
        View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    //发布
    private TextInputEditText mPublishTopicEt;
    private TextInputEditText mMessageEt;
    private int mPubQos;
    private boolean mIsRetained;

    public static PublishFragment newInstance() {
        return new PublishFragment();
    }

    @Override
    protected Object setRootLayout() {
        return R.layout.fragment_publish;
    }

    @Override
    public void initParamsAndValues() {

    }

    @Override
    protected void initViews(View rootView) {
        //2.发布
        mPublishTopicEt = rootView.findViewById(R.id.et_publish_topic);
        mMessageEt =  rootView.findViewById(R.id.et_publish_message);
        RadioGroup rg_publish =  rootView.findViewById(R.id.rg_publish);
        rg_publish.setOnCheckedChangeListener((group, checkedId) -> {
            final RadioButton rb = group.findViewById(checkedId);
            final String target = rb.getText().toString();
            mPubQos = Integer.parseInt(target);
        });

        final CheckBox remainCb =  rootView.findViewById(R.id.cb_retained);
        remainCb.setOnCheckedChangeListener(this);
        rootView.findViewById(R.id.tv_publish).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.et_publish_topic:
                publishMessage();
                break;
        }
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

        if (!MqttClientManager.getInstance().isConnected()) {
            Toast.makeText(mContext, getString(R.string.all_tips_refuse_other_operation), Toast.LENGTH_SHORT).show();
            return;
        }

        MqttClientManager.getInstance()
                .publish(topic, msg, mPubQos, mIsRetained, new MqttActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(mContext, "发布成功", Toast.LENGTH_SHORT).show();
                        MqttDebuger.i(tag, "publishMessage..message [: " + msg + "]..success!");
//                        addLogEvent(Constant.TAG_PUBLISHING, "Mqtt publish the message [" + msg + "] successfully!");
                    }

                    @Override
                    public void onFailure(Throwable exception) {
                        Toast.makeText(mContext, "发布失败", Toast.LENGTH_SHORT).show();
                        MqttDebuger.e(tag, "publishMessage..message : [" + msg + "]..failure!");
//                        addLogEvent(Constant.TAG_ERROR, "Mqtt publish the topic [" + topic + "] failied!");
                    }
                });

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_retained:
                mIsRetained = isChecked;
                break;
        }
    }
}
