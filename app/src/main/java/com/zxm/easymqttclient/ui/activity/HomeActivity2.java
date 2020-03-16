package com.zxm.easymqttclient.ui.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zxm.easymqttclient.R;
import com.zxm.easymqttclient.base.BaseActivity;
import com.zxm.easymqttclient.ui.adapter.HomeTabAdapter;
import com.zxm.easymqttclient.ui.fragment.ConnectionFragment;
import com.zxm.easymqttclient.ui.fragment.PublishFragment;
import com.zxm.easymqttclient.ui.fragment.SubscribeFragment;
import com.zxm.easymqttclient.util.Constant;
import com.zxm.easymqttclient.util.DialogUtil;
import com.zxm.easymqttclient.widget.LogSheetDialog;
import com.zxm.utils.core.permission.PermissionChecker;

/**
 * Created by ZhangXinmin on 2020/3/11.
 * Copyright (c) 2020 . All rights reserved.
 */
public class HomeActivity2 extends BaseActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_PERMISSIONS = 1001;

    private static final String[] PERMISSIONS = new String[]{Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private TabLayout mTabLayout;
    private HomeTabAdapter mAdapter;

    private LogSheetDialog mLogDialog;

    //Receive log information.
    private BroadcastReceiver mLogReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                final String action = intent.getAction();
                if (!TextUtils.isEmpty(action)
                        && (Constant.ACTION_LOG_EVENT).equals(action)) {
                    final String tag = intent.getStringExtra(Constant.EXTRA_TAG);
                    final String msg = intent.getStringExtra(Constant.EXTRA_MSG);
                    if (mLogDialog != null) {
                        mLogDialog.addLogEvent(tag, msg);
                    }
                }
            }
        }
    };

    @Override
    protected Object setLayout() {
        return R.layout.activity_home2;
    }

    @Override
    protected void initParamsAndViews() {
        mAdapter = new HomeTabAdapter(getSupportFragmentManager());

        mAdapter.addItem("Connect", ConnectionFragment.newInstance());
        mAdapter.addItem("Subscribe", SubscribeFragment.newInstance());
        mAdapter.addItem("Publish", PublishFragment.newInstance());

        //检查位置权限
        checkPermissions();

        registerLogReceiver();

        mLogDialog = new LogSheetDialog(mContext);
    }

    @Override
    protected void initViews() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.navigation_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.setCheckedItem(R.id.navigation_item_connection);

        final View headerLayout = mNavigationView.getHeaderView(0);
        if (headerLayout != null) {

            final ImageView header = headerLayout.findViewById(R.id.iv_header);
            Glide.with(mContext)
                    .load(R.drawable.icon_default_header)
                    .into(header);
        }

        final Toolbar toolbar = findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("MqttClient");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        final ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(mAdapter);
        //TODO:参数为1,页面会重新创建，分析&解决方案？？
        viewPager.setOffscreenPageLimit(2);
        mTabLayout = findViewById(R.id.tablayout_home);
        mTabLayout.setupWithViewPager(viewPager);

        mDrawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {
                mNavigationView.clearFocus();
                mNavigationView.setCheckedItem(R.id.navigation_item_connection);
            }
        });

    }

    //register log event receiver
    private void registerLogReceiver() {
        if (mLogReceiver != null) {
            final IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Constant.ACTION_LOG_EVENT);
            LocalBroadcastManager.getInstance(mContext)
                    .registerReceiver(mLogReceiver, intentFilter);
        }
    }

    private void checkPermissions() {
        final String[] deniedPermissions = PermissionChecker.checkDeniedPermissions(mContext, PERMISSIONS);
        if (deniedPermissions != null && deniedPermissions.length > 0) {
            PermissionChecker.requestPermissions(this, deniedPermissions, REQUEST_PERMISSIONS);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.menu_log:
                mLogDialog.showLogDialog();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        menuItem.setChecked(true);
        mDrawerLayout.closeDrawers();
        switch (menuItem.getItemId()) {
            case R.id.navigation_item_connection:
                return true;
            case R.id.navigation_item_setting:
                final Intent setting = new Intent(mContext, SettingActivity.class);
                startActivity(setting);
                return true;
            case R.id.navigation_item_feedback:
                sendFeedback();
                return true;
            default:
                return false;
        }
    }

    private void sendFeedback() {
        final String[] email = new String[]{"zhangxinmin528@sina.com"};
        final String subject = "[" + getString(R.string.app_name) + "]-意见反馈!";
        final Intent feedback = new Intent(Intent.ACTION_SEND);
        feedback.setType("message/rfc822");
        feedback.putExtra(Intent.EXTRA_EMAIL, email);//接收人
        feedback.putExtra(Intent.EXTRA_CC, email);//抄送人
        feedback.putExtra(Intent.EXTRA_SUBJECT, subject);//主题
        startActivity(Intent.createChooser(feedback, "请选择邮箱"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    DialogUtil.showForceDialog(mContext, "禁用权限将会影响应用部分功能~", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        if (mLogReceiver != null) {
            LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mLogReceiver);
        }
        super.onDestroy();
    }
}
