package com.arankieskamp.sdmregressiontester

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.arankieskamp.sdmregressiontester.Regression.RegressionTester
import com.arankieskamp.sdmregressiontester.helpers.MQTTHelper
import com.arankieskamp.sdmregressiontester.models.MqttInput
import com.arankieskamp.sdmregressiontester.models.RegressionMessage
import com.arankieskamp.sdmregressiontester.ui.MqttMessageFragment
import com.arankieskamp.sdmregressiontester.ui.RegressionMessageFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAck
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish
import com.hivemq.client.mqtt.mqtt5.message.subscribe.suback.Mqtt5SubAck


class MainActivity : AppCompatActivity(), MqttMessageFragment.OnListFragmentInteractionListener,
    RegressionMessageFragment.OnListFragmentInteractionListener {

    override fun onRegressionTesterListFragmentInteraction(item: RegressionMessage.RegressionProblem?) {

    }

    override fun onMqttMessageListFragmentInteraction(regressionProblem: MqttInput.MqttMessage?) {

    }


    internal var mqttHelper: MQTTHelper = object : MQTTHelper() {
        override fun messageReceived(publish: Mqtt5Publish) {
            MqttInput.addItem(
                MqttInput.createMqttMessage(
                    publish.topic.toString(),
                    publish.payload.toString()
                )
            )
            updateMqttListView()
            try {
                RegressionTester.CheckMqttMessage(publish)
            } catch (e: RegressionTester.TopicStructureException) {
                RegressionMessage.addItem(
                    RegressionMessage.createRegressionProblem(
                        publish.topic.toString(),
                        publish.payload.toString(),
                        e.message
                    )
                )
                updateRegressionListView()
            }
        }

        override fun subscriptionComplete(
            subAck: Mqtt5SubAck,
            throwable: Throwable
        ) {
            createToast("Subscription successful")
        }

        override fun connectionCompleted(
            connAck: Mqtt5ConnAck,
            throwable: Throwable
        ) {
            createToast("Connection successful")
            subscribeToSelectedTopics()
        }
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)
        createToast(fragment.javaClass.name)
    }

    private fun updateMqttListView() {
        TODO("Update correct fragment listview")
//        if (mqttFragment.isVisible) {
//            mqttFragment.updateListView()
//        }
    }

    private fun updateRegressionListView() {
        TODO("Update correct fragment listview")
//        if (regressionFragment.isVisible) {
//            regressionFragment.updateListView()
//        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        startMqtt()
    }

    private fun createToast(message: String) {
        createToast(message, Toast.LENGTH_SHORT)
    }

    private fun createToast(message: String, duration: Int) {
        val refresh = Handler(Looper.getMainLooper())
        refresh.post { Toast.makeText(applicationContext, message, duration).show() }
    }

    private fun startMqtt() {
        val prefs = getSharedPreferences("pref_mqtt", Context.MODE_PRIVATE)
        val hostname = prefs.getString("hostname_text", "arankieskamp.com")
        val port = prefs.getInt("port_int", 1883)

        Log.w("Info", hostname!!)
        Log.w("Info", port.toString() + "")


        mqttHelper.init(hostname, port)

        val test = Thread(Runnable {
            Thread.sleep(5000)
            subscribeToSelectedTopics()
        }).start()
    }

    private fun subscribeToSelectedTopics() {
        mqttHelper.subscribeToTopic("23/#")
        createToast("Subscription successful")
    }
}
