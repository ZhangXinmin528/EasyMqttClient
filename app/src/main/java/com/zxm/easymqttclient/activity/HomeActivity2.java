package com.zxm.easymqttclient.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.zxm.easymqttclient.R;
import com.zxm.easymqttclient.adapter.HomeTabAdapter;
import com.zxm.easymqttclient.base.BaseActivity;
import com.zxm.easymqttclient.fragment.ConnectionFragment;
import com.zxm.easymqttclient.fragment.PublishFragment;
import com.zxm.easymqttclient.fragment.SubscribeFragment;
import com.zxm.easymqttclient.util.DialogUtil;
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
    }

    @Override
    protected void initViews() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.navigation_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        final Toolbar toolbar = findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        final ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(mAdapter);
        viewPager.setOffscreenPageLimit(1);
        mTabLayout = findViewById(R.id.tablayout_home);
        mTabLayout.setupWithViewPager(viewPager);

        mDrawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {
                Toast.makeText(mContext, "关了", Toast.LENGTH_SHORT).show();
//                mNavigationView.clearFocus();
//                mNavigationView.setCheckedItem(R.id.navigation_item_devices);
            }
        });
    }

    private void checkPermissions() {
        final String[] deniedPermissions = PermissionChecker.checkDeniedPermissions(mContext, PERMISSIONS);
        if (deniedPermissions != null && deniedPermissions.length > 0) {
            PermissionChecker.requestPermissions(this, deniedPermissions, REQUEST_PERMISSIONS);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            /*case R.id.menu_scan_start:
            case R.id.menu_scan_stop:
                return false;*/
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
            case R.id.navigation_item_devices_info:

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

    public void addTab(@NonNull String deviceName, @NonNull String macAddress) {
        if (!TextUtils.isEmpty(macAddress)) {
//            mAdapter.addItem(ConnectionFragment.newInstance(deviceName, macAddress),macAddress);
            mAdapter.notifyDataSetChanged();
            mTabLayout.getTabAt(3).select();
        }
    }

    public void removeTab() {
        if (mAdapter != null) {
            final int size = mAdapter.getCount();
            if (size == 4) {
                mAdapter.removeItem(size - 1);
                mAdapter.notifyDataSetChanged();
                mTabLayout.getTabAt(0).select();
            }
        }
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

}
