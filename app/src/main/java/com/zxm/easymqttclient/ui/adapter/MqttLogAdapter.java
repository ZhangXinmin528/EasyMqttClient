package com.zxm.easymqttclient.ui.adapter;

import android.graphics.Color;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxm.easymqttclient.R;
import com.zxm.easymqttclient.model.LogEntity;
import com.zxm.easymqttclient.util.Constant;
import com.zxm.easymqttclient.util.TimeUtil;

import java.util.List;

/**
 * Created by ZhangXinmin on 2019/12/5.
 * Copyright (c) 2019 . All rights reserved.
 */
public class MqttLogAdapter extends BaseQuickAdapter<LogEntity, BaseViewHolder> {

    public MqttLogAdapter(@Nullable List<LogEntity> data) {
        super(R.layout.layout_log_list_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, LogEntity item) {
        final String content = String.format("%s/%s : %s",
                TimeUtil.millis2String(item.getTimeMills(), TimeUtil.DEFAULT_FORMAT),
                item.getTag(), item.getMessage());
        if (item.getTag().equals(Constant.TAG_ERROR)) {
            helper.setTextColor(R.id.tv_log_info, Color.RED);
        } else {
            helper.setTextColor(R.id.tv_log_info, Color.WHITE);
        }
        helper.setText(R.id.tv_log_info, content);
    }
}
