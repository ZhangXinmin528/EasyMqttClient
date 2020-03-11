package com.zxm.easymqttclient.widget;

import android.graphics.Point;

/**
 * Created by ZhangXinmin on 2019/9/12.
 * Copyright (c) 2019 . All rights reserved.
 */
public class SpiderPoint extends Point {

    // x 方向加速度
    public int aX;

    // y 方向加速度
    public int aY;

    // 小球颜色
    public int color;

    // 小球半径
    public int r;

    // x 轴方向速度
    public float vX;

    // y 轴方向速度
    public float vY;

    // 点
    // public float x;
    // public float y;


    public SpiderPoint(int x, int y) {
        super(x, y);
    }

    public SpiderPoint() {
    }
}
