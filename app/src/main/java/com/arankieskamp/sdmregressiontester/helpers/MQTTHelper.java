package com.arankieskamp.sdmregressiontester.helpers;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAck;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import com.hivemq.client.mqtt.mqtt5.message.subscribe.suback.Mqtt5SubAck;

import java.util.ArrayList;
import java.util.List;

public abstract class MQTTHelper {
    final String clientId = "Group-23-RegressionTester";
    private Mqtt5AsyncClient mqttClient;
    private List<Mqtt5Publish> messages = new ArrayList<>();

    private String hostname;
    private int port;

    public void init(String hostname, int port) {
        if (!hostname.equals(this.hostname) || port != this.port || mqttClient == null) {
            this.hostname = hostname;
            this.port = port;
            if (mqttClient != null) {
                mqttClient.disconnect();
            }
            mqttClient = MqttClient.builder()
                    .useMqttVersion5()
//                .identifier(UUID.randomUUID().toString())
                    .identifier(clientId)
                    .serverHost(hostname)
                    .serverPort(port)
                    .buildAsync();
            connect();
        }
    }

    private void connect() {
        mqttClient.connectWith()
                .cleanStart(true)
                .sessionExpiryInterval(0)
                .noKeepAlive()
                .simpleAuth()
                .username("")
                .applySimpleAuth()
                .send()
                .whenCompleteAsync(this::connectionCompleted);
    }

    public void subscribeToTopic(String subscriptionTopic) {
        mqttClient.subscribeWith()
                .topicFilter(subscriptionTopic)
                .callback(this::messageReceived)
                .send()
                .whenCompleteAsync(this::subscriptionComplete);
    }


    public void messageReceived(Mqtt5Publish publish) {
        messages.add(publish);
    }

    public abstract void subscriptionComplete(Mqtt5SubAck subAck, Throwable throwable);

    public abstract void connectionCompleted(Mqtt5ConnAck connAck, Throwable throwable);
}
