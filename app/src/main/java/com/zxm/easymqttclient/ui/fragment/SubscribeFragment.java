package com.zxm.easymqttclient.ui.fragment;

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
import com.zxm.easymqttclient.util.DisplayUtil;
import com.zxm.easymqttclient.util.SPUtils;

/**
 * Created by ZhangXinmin on 2020/3/11.
 * Copyright (c) 2020 . All rights reserved.
 * Subscribing a topic~
 */
public class SubscribeFragment extends BaseFragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    //订阅主题
    private TextInputEditText mSubscribeTopicEt;
    private int mSubQos;
    //能否缓存
    private boolean mCacheEnable;

    public static SubscribeFragment newInstance() {
        return new SubscribeFragment();
    }

    @Override
    protected Object setRootLayout() {
        return R.layout.fragment_subscribe;
    }

    @Override
    public void initParamsAndValues() {
        mCacheEnable = SPUtils.getCacheState(mContext);
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

        //是否加载缓存
        final CheckBox loadCacheCb = rootView.findViewById(R.id.cb_load_cache);
        loadCacheCb.setOnCheckedChangeListener(this);

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
                        saveUserConfigure();
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

    private void saveUserConfigure() {
        if (mCacheEnable) {
            final String subTopic = mSubscribeTopicEt.getEditableText().toString().trim();
            if (!TextUtils.isEmpty(subTopic)) {
                SPUtils.setMqttSubscribeTopic(mContext, subTopic);
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_load_cache:

                if (isChecked){
                    final String subTopic = SPUtils.getMqttSubscribeTopic(mContext);


                    if (!TextUtils.isEmpty(subTopic)) {
                        mSubscribeTopicEt.setText(subTopic);
                    }
                }else {
                    mSubscribeTopicEt.getEditableText().clear();
                }

                break;
        }
    }
}
