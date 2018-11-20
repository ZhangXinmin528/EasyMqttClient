package com.zxm.easymqtt.listener;

/**
 * Created by ZhangXinmin on 2018/11/20.
 * Copyright (c) 2018 . All rights reserved.
 * Implementors of this interface will be notified when an asynchronous action completes.
 */
public interface MqttActionListener {
    /**
     * This method is invoked when an action has completed successfully.
     */
    void onSuccess();

    /**
     * This method is invoked when an action fails.
     * If a client is disconnected while an action is in progress
     * onFailure will be called. For connections
     * that use cleanSession set to false, any QoS 1 and 2 messages that
     * are in the process of being delivered will be delivered to the requested
     * quality of service next time the client connects.
     *
     * @param exception thrown by the action that has failed
     */
    void onFailure(Throwable exception);
}
