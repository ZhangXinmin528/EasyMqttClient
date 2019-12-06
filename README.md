# EasyMqttClient

<p align="center">
  <img alt="logo" src="https://github.com/ZhangXinmin528/EasyMqttClient/blob/master/app/src/main/assets/ic_launcher.png"/>
</p>

Introduction
---
MQTT stands for MQ Telemetry Transport. It is a publish/subscribe, extremely simple and lightweight messaging protocol, designed for constrained devices and low-bandwidth, high-latency or unreliable networks. The design principles are to minimise network bandwidth and device resource requirements whilst also attempting to ensure reliability and some degree of assurance of delivery. These principles also turn out to make the protocol ideal of the emerging “machine-to-machine” (M2M) or “Internet of Things” world of connected devices, and for mobile applications where bandwidth and battery power are at a premium.

This library is the secondary packaging based on eclipse's paho.mqtt.android, which is designed to help IoT developers get started quickly and develop reliable products.

Download
---
Download example apk from application market.
<p align="center">
  <img alt="logo" src="https://www.coolapk.com/qr/image?data=base64:aHR0cHM6Ly93d3cuY29vbGFway5jb20vYXBrL2NvbS56eG0uZWFzeW1xdHRjbGllbnQ_ZnJvbT1xcg~~&h=d42ee57c"/>
</p>

Usage
---

##### 1.Add aar(mqtt_service_1.0.3_release.aar) to your project.

##### 2.Confiure your clientId,host,port for your mqtt client.

		final MqttConfig.Builder builder = new MqttConfig.Builder(getApplicationContext());

        builder.setClientId(clientId)
                .setHost(host)
                .setPort(port)
                .setAutomaticReconnect(mIsAutoReconnect)
                .setCleanSession(mIsClearSession)
                .setKeepalive(20);

        if (!TextUtils.isEmpty(userName)) {
            builder.setUserName(userName);
        }

        if (!TextUtils.isEmpty(pwd)) {
            builder.setPassWord(port);
        }

        final MqttConfig config = builder.create();

If your mqtt server need username and password to accept yout connection,your should configure these information.

##### 3.Init your mqtt client.

		MqttClientManager.getInstance().init(config);

##### 4.Build your connection.

		 MqttClientManager
                .getInstance()
                .connect(new MqttActionListener() {
                             @Override
                             public void onSuccess() {
                                //build connection success...
                             }

                             @Override
                             public void onFailure(Throwable exception) {
                                //build connection failure
                             }
                         },
                        new SimpleConnectionMqttCallback() {
                            @Override
                            public void messageArrived(String topic, String message, int qos) throws Exception {
                              super.messageArrived(topic, message, qos);
								//receiving message from the mqtt server...
                            }

                            @Override
                            public void connectionLost(Throwable cause) {
                                super.connectionLost(cause);
                               //lost connection to the mqtt server...
                            }

                            @Override
                            public void connectComplete(boolean reconnect, String serverURI) {
                                super.connectComplete(reconnect, serverURI);
                                //do something after connection completed...
                            }
                        });

##### 5.Subscribe topics.
If you want to receive messages from the mqtt server,you should subscribe series of topics.

		MqttClientManager.getInstance()
                .subscribe(topic, mSubQos, new MqttActionListener() {
                    @Override
                    public void onSuccess() {
                        //subscribe topic success...
                    }

                    @Override
                    public void onFailure(Throwable exception) {
                       //subscribe topic failure...
                    }
                });
Please refer to the code in the App Module related Activity in the project.

##### 6.Publish messages.

		MqttClientManager.getInstance()
                .publish(topic, msg, mPubQos, mIsRetained, new MqttActionListener() {
                    @Override
                    public void onSuccess() {
                       //publish a message success...
                    }

                    @Override
                    public void onFailure(Throwable exception) {
                       publish a message failure...
                    }
                });

##### 7.Disconnect to the server.

		MqttClientManager.getInstance().disconnect(new MqttActionListener() {
            @Override
            public void onSuccess() {
               //disconnect to the server success...
            }

            @Override
            public void onFailure(Throwable exception) {
                //disconnect to the server failure...
            }
        });

Communication
---
Email : zhangxinmin528@sina.com

License
---

    Copyright 2019 ZhangXinmin528

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


The End
---
If you are interested in AndroidUtils, don't forget to STAR [EasyMqttClient](https://github.com/ZhangXinmin528/EasyMqttClient).

Thank you for reading ~^o^~