/*******************************************************************************
 * Copyright (c) 1999, 2014 IBM Corp.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution. 
 *
 * The Eclipse Public License is available at 
 *    http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 *   http://www.eclipse.org/org/documents/edl-v10.php.
 */
package com.zxm.easymqtt.client;

import android.content.Context;
import android.support.annotation.NonNull;

import org.eclipse.paho.android.service.MqttAndroidClient;

import java.util.HashMap;
import java.util.Map;

/**
 * <code>Connections</code> is a singleton class which stores all the connection objects
 * in one central place so they can be passed between activities using a client
 * handle
 */
public class Connections {

    /**
     * Singleton instance of <code>Connections</code>
     **/
    private static Connections instance = null;

    /**
     * List of {@link Connection} objects
     **/
    private HashMap<String, Connection> connections = null;

    /**
     * Create a Connections object
     *
     * @param context Applications context
     */
    private Connections(@NonNull Context context) {
        connections = new HashMap<>();
    }

    /**
     * Returns an already initialised instance of <code>Connections</code>, if Connections has yet to be created, it will
     * create and return that instance
     *
     * @param context The applications context used to create the <code>Connections</code> object if it is not already initialised
     * @return Connections instance
     */
    public synchronized static Connections getInstance(@NonNull Context context) {
        if (instance == null) {
            instance = new Connections(context);
        }

        return instance;
    }

    /**
     * Finds and returns a connection object that the given client handle points to
     *
     * @param handle The handle to the <code>Connection</code> to return
     * @return a connection associated with the client handle, <code>null</code> if one is not found
     */
    public Connection getConnection(@NonNull String handle) {
        return connections.get(handle);
    }

    /**
     * Adds a <code>Connection</code> object to the collection of connections associated with this object
     *
     * @param connection connection to add
     */
    public void addConnection(@NonNull Connection connection) {
        connections.put(connection.handle(), connection);
    }

    /**
     * Create a fully initialised <code>MqttAndroidClient</code> for the parameters given
     *
     * @param context   The Applications context
     * @param serverURI The ServerURI to connect to
     * @param clientId  The clientId for this client
     * @return new instance of MqttAndroidClient
     */
    public MqttAndroidClient createClient(@NonNull Context context, @NonNull String serverURI, @NonNull String clientId) {
        return new MqttAndroidClient(context, serverURI, clientId);
    }

    /**
     * Get all the connections associated with this <code>Connections</code> object.
     *
     * @return <code>Map</code> of connections
     */
    public Map<String, Connection> getConnections() {
        return connections;
    }

    /**
     * Removes a connection from the map of connections
     *
     * @param connection connection to be removed
     */
    public void removeConnection(@NonNull Connection connection) {
        connections.remove(connection.handle());
    }

}
