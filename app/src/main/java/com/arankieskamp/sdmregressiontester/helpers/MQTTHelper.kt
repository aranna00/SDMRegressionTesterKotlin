package com.arankieskamp.sdmregressiontester.helpers

import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAck
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish
import com.hivemq.client.mqtt.mqtt5.message.subscribe.suback.Mqtt5SubAck
import java.util.*

abstract class MQTTHelper {
    private val clientId = "Group-23-RegressionTester"
    private val topicSubscriptions = ArrayList<String>()
    private var mqttClient: Mqtt5AsyncClient? = null
    private val messages = ArrayList<Mqtt5Publish>()

    private var hostname: String? = null
    private var port: Int = 0

    fun init(hostname: String, port: Int) {
        if (hostname != this.hostname || port != this.port || mqttClient == null) {
            this.hostname = hostname
            this.port = port
            if (mqttClient != null) {
                mqttClient!!.disconnect()
            }
            mqttClient = MqttClient.builder()
                .useMqttVersion5()
                //                .identifier(UUID.randomUUID().toString())
                .identifier(clientId)
                .serverHost(hostname)
                .serverPort(port)
                .buildAsync()
            connect()
        }
    }

    fun clearSubscribtions() {
        for (topic in topicSubscriptions) {
            mqttClient!!.unsubscribeWith()
                .topicFilter(topic)
                .send()
        }
    }

    private fun connect() {
        mqttClient!!.connectWith()
            .cleanStart(true)
            .sessionExpiryInterval(0)
            .noKeepAlive()
            .simpleAuth()
            .username("")
            .applySimpleAuth()
            .send()
            .whenCompleteAsync { connAck, throwable ->
                this.connectionCompleted(
                    connAck,
                    throwable
                )
            }
    }

    fun subscribeToTopic(subscriptionTopic: String) {
        mqttClient!!.subscribeWith()
            .topicFilter(subscriptionTopic)
            .callback { this.messageReceived(it) }
            .send()
            .whenCompleteAsync { subAck, throwable ->
                this.subscriptionComplete(
                    subAck,
                    throwable
                )
            }
        topicSubscriptions.add(subscriptionTopic)
    }


    abstract fun messageReceived(publish: Mqtt5Publish)

    abstract fun subscriptionComplete(subAck: Mqtt5SubAck, throwable: Throwable)

    abstract fun connectionCompleted(connAck: Mqtt5ConnAck, throwable: Throwable)
}
