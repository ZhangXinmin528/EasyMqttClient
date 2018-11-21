package com.zxm.easymqttclient.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.zxm.easymqttclient.model.TabInfo;

import java.util.List;

/**
 * Created by ZhangXinmin on 2018/11/21.
 * Copyright (c) 2018 . All rights reserved.
 */
public class ConnectionDetialAdapter extends FragmentPagerAdapter {

    private List<TabInfo> mDataList;

    public ConnectionDetialAdapter(FragmentManager fm, List<TabInfo> list) {
        super(fm);
        mDataList = list;
    }

    @Override
    public Fragment getItem(int position) {
        return mDataList.get(position).getFragment();
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mDataList.get(position).getTitle();
    }
}
