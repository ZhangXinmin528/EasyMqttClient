package com.zxm.easymqttclient;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;

import com.zxm.easymqttclient.base.BaseActivity;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private RecyclerView mRecyclerView;

    private SwitchCompat mSwitchCompat;

    @Override
    protected Object setLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initParamsAndViews() {

    }

    @Override
    protected void initViews() {
        mSwitchCompat = findViewById(R.id.switch_log_print);
        findViewById(R.id.btn_add_client).setOnClickListener(this);

        mRecyclerView = findViewById(R.id.rv_main);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_client:

                break;

        }
    }
}
