package com.zxm.easymqttclient.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhangXinmin on 2020/3/11.
 * Copyright (c) 2020 . All rights reserved.
 */
public class HomeTabAdapter extends FragmentPagerAdapter {

    private List<Fragment> mDataList;
    private List<String> mTitleList;


    public HomeTabAdapter(FragmentManager fm) {
        super(fm);
        mDataList = new ArrayList<>();
        mTitleList = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position) {
        return mDataList.get(position);
    }

    //添加Tab
    public void addItem(@NonNull String title, @NonNull Fragment fragment) {
        if (fragment != null && !TextUtils.isEmpty(title)) {
            if (mTitleList.contains(title)) {
                return;
            }
            mDataList.add(fragment);
            mTitleList.add(title);
        }
    }

    //移除Tab
    public void removeItem(int position) {
        if (position < 0 || mDataList.isEmpty() || position > getCount() - 1) {
            return;
        }
        mDataList.remove(position);
        mTitleList.remove(position);
    }

    @Override
    public int getCount() {
        if (mDataList != null && !mDataList.isEmpty()) {
            return mDataList.size();
        }
        return 0;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitleList.get(position);
    }
}
