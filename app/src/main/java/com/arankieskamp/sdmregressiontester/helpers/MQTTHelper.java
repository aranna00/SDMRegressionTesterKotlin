package com.arankieskamp.sdmregressiontester.helpers;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAck;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import com.hivemq.client.mqtt.mqtt5.message.subscribe.suback.Mqtt5SubAck;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;

public class MQTTHelper {
    public static Observable observable;
    private static MQTTHelper instance;
    final String clientId = "Group-23-RegressionTester";
    private Mqtt5AsyncClient mqttClient;
    private List<Mqtt5Publish> messages = new ArrayList<>();

    private String hostname;
    private int port;

    public static MQTTHelper getInstance() {
        if (instance == null) {
            createInstance();
        }

        return instance;
    }

    private synchronized static void createInstance() {
        if (instance == null) {
            instance = new MQTTHelper();
        }
    }

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
        observable = Observable.create((ObservableOnSubscribe) emitter -> {
            try {
                emitter.onNext(messages.get(0));
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }

    private void connect() {
        mqttClient.connectWith()
                .sessionExpiryInterval(1800)
                .simpleAuth()
                .username("")
                .applySimpleAuth()
                .send()
                .whenCompleteAsync(this::ConnectionCompleted);
    }

    public void subscribeToTopic(String subscriptionTopic) {
        mqttClient.subscribeWith()
                .topicFilter(subscriptionTopic)
                .callback(this::MessageRecieved)
                .send()
                .whenCompleteAsync(this::SubscriptionComplete);
    }


    public void MessageRecieved(Mqtt5Publish publish) {
        messages.add(publish);
    }

    public void SubscriptionComplete(Mqtt5SubAck subAck, Throwable throwable) {
    }

    public void ConnectionCompleted(Mqtt5ConnAck connAck, Throwable throwable) {
    }
}
