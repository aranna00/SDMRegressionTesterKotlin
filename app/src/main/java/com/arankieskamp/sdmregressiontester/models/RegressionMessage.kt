package com.arankieskamp.sdmregressiontester.models

import java.util.*

object RegressionMessage {
    val ITEMS: MutableList<RegressionProblem> = ArrayList()

    val ITEM_MAP: MutableMap<String, RegressionProblem> = HashMap()

    fun addItem(item: RegressionProblem) {
        ITEMS.add(item)
        ITEM_MAP[item.id] = item
    }

    fun createRegressionProblem(
        topic: String,
        payload: String,
        exception: String?
    ): RegressionProblem {
        return createRegressionProblem(ITEMS.size.toString(), topic, payload, exception)
    }

    fun createRegressionProblem(
        messageNum: String,
        topic: String,
        payload: String,
        exception: String?
    ): RegressionProblem {
        return RegressionProblem(messageNum, topic, payload, exception)
    }

    class RegressionProblem(
        val id: String,
        val topic: String,
        val payload: String,
        val exception: String?
    )
}