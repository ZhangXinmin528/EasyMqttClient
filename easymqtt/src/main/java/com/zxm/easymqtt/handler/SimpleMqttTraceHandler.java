package com.zxm.easymqtt.handler;

import com.zxm.easymqtt.util.Debugger;

import org.eclipse.paho.android.service.MqttTraceHandler;

/**
 * Created by ZhangXinmin on 2018/11/20.
 * Copyright (c) 2018 . All rights reserved.
 * Simple trace handling, pass the trace message to trace
 * callback.
 */
public class SimpleMqttTraceHandler implements MqttTraceHandler {

    @Override
    public void traceDebug(String tag, String message) {
        Debugger.d(tag, message);
    }

    @Override
    public void traceError(String tag, String message) {
        Debugger.e(tag, message);
    }

    @Override
    public void traceException(String tag, String message, Exception e) {
        Debugger.e(tag, "[message : " + message + "]--[exception : " + e.getMessage() + "]");
    }
}
