package com.zxm.easymqttclient.activity;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.zxm.easymqttclient.R;
import com.zxm.easymqttclient.adapter.ConnectionDetialAdapter;
import com.zxm.easymqttclient.base.BaseActivity;
import com.zxm.easymqttclient.fragment.PublishFragment;
import com.zxm.easymqttclient.fragment.SubscribeFragment;
import com.zxm.easymqttclient.model.TabInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhangXinmin on 2018/11/21.
 * Copyright (c) 2018 . All rights reserved.
 * Activity for subscribing the topic and publishing the message connected with the topic.
 */
public class ConnectionDetialActivity extends BaseActivity {
    private Context mContext;

    private List<TabInfo> mDataList;
    private ConnectionDetialAdapter mAdapter;

    @Override
    protected Object setLayout() {
        return R.layout.activity_connection_detial;
    }

    @Override
    protected void initParamsAndViews() {
        mContext = this;
        mDataList = new ArrayList<>();
        mDataList.add(new TabInfo("Subscribe",SubscribeFragment.newInstance()));
        mDataList.add(new TabInfo("Publish",PublishFragment.newInstance()));
        mAdapter = new ConnectionDetialAdapter(getSupportFragmentManager(), mDataList);
    }

    @Override
    protected void initViews() {
        ViewPager viewPager = findViewById(R.id.viewpager_deyial);
        viewPager.setAdapter(mAdapter);

        TabLayout tabLayout = findViewById(R.id.tablayout_detial);
        tabLayout.setupWithViewPager(viewPager);
    }
}
