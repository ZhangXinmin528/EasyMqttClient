package com.zxm.easymqttclient.model;

import android.support.v4.app.Fragment;

import java.io.Serializable;

/**
 * Created by ZhangXinmin on 2018/11/21.
 * Copyright (c) 2018 . All rights reserved.
 */
public class TabInfo implements Serializable {
    private String title;
    private Fragment fragment;

    public TabInfo() {
    }

    public TabInfo(String title, Fragment fragment) {
        this.title = title;
        this.fragment = fragment;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public String toString() {
        return "TabInfo{" +
                "title='" + title + '\'' +
                ", fragment=" + fragment +
                '}';
    }
}
