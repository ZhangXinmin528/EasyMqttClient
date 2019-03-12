package com.zxm.easymqtt.listener;

import org.eclipse.paho.client.mqttv3.MqttCallback;

/**
 * Created by ZhangXinmin on 2018/11/27.
 * Copyright (c) 2018 . All rights reserved.
 * <p>
 * Enables an application to be notified when asynchronous
 * events related to the client occur.
 * Classes implementing this interface
 * can be registered on both types of client:
 * {@link org.eclipse.paho.client.mqttv3.IMqttClient#setCallback(MqttCallback)}
 * and {@link org.eclipse.paho.client.mqttv3.IMqttAsyncClient#setCallback(MqttCallback)}
 */
public interface MqttConnectionCallback {

    /**
     * This method is called when the connection to the server is lost.
     *
     * @param cause the reason behind the loss of connection.
     */
    void connectionLost(Throwable cause);

    /**
     * This method is called when a message arrives from the server.
     *
     * <p>
     * This method is invoked synchronously by the MQTT client. An
     * acknowledgment is not sent back to the server until this
     * method returns cleanly.</p>
     * <p>
     * If an implementation of this method throws an <code>Exception</code>, then the
     * client will be shut down.  When the client is next re-connected, any QoS
     * 1 or 2 messages will be redelivered by the server.</p>
     * <p>
     * Any additional messages which arrive while an
     * implementation of this method is running, will build up in memory, and
     * will then back up on the network.</p>
     * <p>
     * If an application needs to persist data, then it
     * should ensure the data is persisted prior to returning from this method, as
     * after returning from this method, the message is considered to have been
     * delivered, and will not be reproducible.</p>
     * <p>
     * It is possible to send a new message within an implementation of this callback
     * (for example, a response to this message), but the implementation must not
     * disconnect the client, as it will be impossible to send an acknowledgment for
     * the message being processed, and a deadlock will occur.</p>
     *
     * @param topic   name of the topic on the message was published to
     * @param message the actual message.
     * @param qos     the "quality of service" to use.
     * @throws Exception if a terminal error has occurred, and the client should be
     *                   shut down.
     */
    void messageArrived(String topic, String message, int qos) throws Exception;

    /**
     * Called when delivery for a message has been completed, and all
     * acknowledgments have been received. For QoS 0 messages it is
     * called once the message has been handed to the network for
     * delivery. For QoS 1 it is called when PUBACK is received and
     * for QoS 2 when PUBCOMP is received. The token will be the same
     * token as that returned when the message was published.
     */
    void deliveryComplete();
}
