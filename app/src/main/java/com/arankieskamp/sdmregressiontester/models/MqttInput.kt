package com.arankieskamp.sdmregressiontester.models

import java.util.*
import kotlin.collections.ArrayList

object MqttInput {
    val ITEMS: MutableList<MqttMessage> = ArrayList()

    val ITEM_MAP: MutableMap<String, MqttMessage> = HashMap()

    fun addItem(item: MqttMessage) {
        ITEMS.add(item)
        ITEM_MAP[item.id] = item
    }

    fun createMqttMessage(topic: String, payload: String): MqttMessage {
        return MqttMessage(ITEMS.size.toString(), topic, payload, true)
    }

    fun clearMessages() {
        ITEMS.clear()
    }

    class MqttMessage(val id: String, val topic: String, val payload: String, var success: Boolean)
}