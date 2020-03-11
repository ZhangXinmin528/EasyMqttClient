package com.zxm.easymqttclient.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.coding.zxm.mqtt_master.util.MqttDebuger;
import com.zxm.easymqttclient.R;
import com.zxm.easymqttclient.model.LogEntity;
import com.zxm.easymqttclient.ui.adapter.MqttLogAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhangXinmin on 2020/3/11.
 * Copyright (c) 2020 . All rights reserved.
 * Display log information.
 */
public class LogSheetDialog extends BottomSheetDialog implements View.OnClickListener {

    private String tag = "LogSheetDialog";

    private Context mContext;

    private MqttLogAdapter mAdapter;
    private List<LogEntity> mLogList;

    public LogSheetDialog(@NonNull Context context) {
        super(context, R.style.Theme_Design_BottomSheetDialog);
        mContext = context;

        mLogList = new ArrayList<>();

        initViews();
    }

    private void initViews() {
        if (mContext != null) {
            //底部日志
            final View rootView = LayoutInflater.from(mContext)
                    .inflate(R.layout.layout_log_info, null);

            if (rootView != null) {
                //日志
                RecyclerView recyclerView = rootView.findViewById(R.id.rv_log);
                rootView.findViewById(R.id.tv_log_title).setOnClickListener(this);

                mAdapter = new MqttLogAdapter(mLogList);
                recyclerView.setAdapter(mAdapter);

                recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                recyclerView.addItemDecoration(new DividerItemDecoration(mContext,
                        DividerItemDecoration.VERTICAL));

                setContentView(rootView);
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_log_title:
                dismiss();
                break;
        }
    }

    /**
     * 添加日志信息
     *
     * @param tag
     * @param message
     */
    public void addLogEvent(@NonNull String tag, @NonNull String message) {
        if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(message))
            return;
        if (mLogList.size() >= 500) {
            final List<LogEntity> temp = new ArrayList<>(mLogList.subList(0, 99));
            if (!temp.isEmpty()) {
                mLogList.clear();
                mLogList.addAll(temp);
            }
        }

        final LogEntity entity = new LogEntity();
        entity.setTag(tag);
        entity.setMessage(message);
        entity.setTimeMills(System.currentTimeMillis());
        mLogList.add(0, entity);
        MqttDebuger.d(tag, "log count : " + mLogList.size());
    }

    /**
     * 展示日志信息
     */
    public void showLogDialog() {
        if (mLogList.isEmpty()) {
            Toast.makeText(mContext, "暂没有日志信息~", Toast.LENGTH_SHORT).show();
            return;
        }
        mAdapter.notifyDataSetChanged();
    }
}
