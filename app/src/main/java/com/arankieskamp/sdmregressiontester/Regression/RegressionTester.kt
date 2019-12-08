package com.arankieskamp.sdmregressiontester.Regression


import com.hivemq.client.mqtt.datatypes.MqttTopic
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish

object RegressionTester {

    private enum class LaneTypes {
        foot, cycle, motorised, vessel, track
    }

    private enum class ComponentTypes {
        traffic_light, warning_light, sensor, barrier, deck, boat_light, train_light
    }

    private var regressionTester: RegressionTester? = null

    @Throws(TopicStructureException::class)
    fun CheckMqttMessage(message: Mqtt5Publish) {
        val topic = message.topic
        val levels = topic.levels
        if (!checkTopicLengthIsWithinSpec(levels)) {
            throw TopicStructureException("Topic size outside specification", null)
        }
        checkFirstTopicLevel(levels[0])
        val laneType = checkSecondTopicLevel(levels[1])

        val direction = checkThirdTopicLevel(levels[2], laneType)

        val componentType = checkFourthTopicLevel(levels[3], laneType)

        checkFifthTopicLevel(levels[4], laneType, direction, componentType)

        checkPayload(message.payload.get().int, laneType, componentType)
    }

    private fun checkPayload(
        payload: Int,
        laneType: LaneTypes,
        componentType: ComponentTypes
    ) {
        if (payload in 0..3) {

        } else {
            throw TopicStructureException("Payload outside spec", null)
        }
    }

    private fun checkFifthTopicLevel(
        level: String,
        laneType: LaneTypes,
        direction: Boolean,
        componentType: ComponentTypes
    ): Int {
        val componentId: Int
        try {
            componentId = Integer.parseInt(level)
        } catch (e: NumberFormatException) {
            throw TopicStructureException("Fifth topic level is not an integer", e)
        }

        return componentId
    }

    private fun checkFourthTopicLevel(
        level: String,
        laneType: LaneTypes
    ): ComponentTypes {
        for (componentType in ComponentTypes.values()) {
            if (componentType.name == level) {
                if (componentType == ComponentTypes.boat_light || componentType == ComponentTypes.deck) {
                    if (laneType != LaneTypes.vessel) {
                        throw TopicStructureException(
                            componentType.name + " component used outside vessel lane",
                            null
                        )
                    }
                }

                if (componentType == ComponentTypes.train_light && laneType != LaneTypes.track) {
                    throw TopicStructureException(
                        "Train_light component used outside track lane",
                        null
                    )
                }

                if (
                    (componentType == ComponentTypes.warning_light ||
                            componentType == ComponentTypes.barrier ||
                            componentType == ComponentTypes.traffic_light) &&
                    (laneType == LaneTypes.track || laneType == LaneTypes.vessel)
                ) {
                    throw TopicStructureException(
                        componentType.name + " component used in" + laneType.name,
                        null
                    )
                }

                return componentType
            }
        }

        throw TopicStructureException("Fourth topic level is an invalid component", null)
    }

    private fun checkThirdTopicLevel(level: String, laneType: LaneTypes): Boolean {
        val directionInt: Int
        try {
            directionInt = Integer.parseInt(level)
        } catch (e: NumberFormatException) {
            throw TopicStructureException("Third topic level is not an integer", e)
        }

        when (laneType) {
            LaneTypes.foot -> if (directionInt in 0..7) {
                return true
            }
            LaneTypes.cycle -> if (directionInt in 0..5) {
                return true
            }
            LaneTypes.motorised -> if (directionInt in 0..9) {
                return true
            }
            LaneTypes.vessel -> if (directionInt in 0..2) {
                return true
            }
            LaneTypes.track -> if (directionInt in 0..2) {
                return true
            }
        }

        throw TopicStructureException("Direction outside lanetype bounds", null)
    }

    private fun checkMotorisedMessage(topic: MqttTopic) {
        topic.levels
    }

    fun getRegressionTester(): RegressionTester {
        if (regressionTester == null) {
            regressionTester = RegressionTester()
        }
        return regressionTester!!
    }

    private operator fun invoke(): RegressionTester? {
        return RegressionTester
    }

    @Throws(TopicStructureException::class)
    private fun checkSecondTopicLevel(level: String): LaneTypes {
        for (laneType in LaneTypes.values()) {
            if (laneType.name == level) {
                return laneType
            }
        }
        throw TopicStructureException("Second topic level is an invalid lane type", null)
    }

    @Throws(TopicStructureException::class)
    private fun checkFirstTopicLevel(level: String) {
        try {
            Integer.parseInt(level)
        } catch (e: NumberFormatException) {
            throw TopicStructureException("First topic level is not an integer", e)
        }
    }

    private fun checkTopicLengthIsWithinSpec(levels: List<String>): Boolean {
        return levels.size == 5
    }

    class TopicStructureException(message: String, cause: NumberFormatException?) :
        RegressionTestException(message, cause)

    open class RegressionTestException(message: String, cause: NumberFormatException?) :
        Exception(message, cause)
}
