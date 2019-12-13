package com.arankieskamp.sdmregressiontester

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
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

    private var mqttHelper: MQTTHelper = object : MQTTHelper() {
        override fun messageReceived(publish: Mqtt5Publish) {
            val newMessage = MqttInput.createMqttMessage(
                publish.topic.toString(),
                publish.payloadAsBytes.toString(Charsets.UTF_8)
            )
            try {
                RegressionTester.CheckMqttMessage(publish)
            } catch (e: RegressionTester.TopicStructureException) {
                newMessage.success = false
                RegressionMessage.addItem(
                    RegressionMessage.createRegressionProblem(
                        newMessage.id,
                        publish.topic.toString(),
                        publish.payloadAsBytes.toString(Charsets.UTF_8),
                        e.message
                    )
                )
                if (currentFragment is RegressionMessageFragment) {
                    (currentFragment as RegressionMessageFragment).updateListView()
                }
            }
            MqttInput.addItem(newMessage)
            if (currentFragment is MqttMessageFragment) {
                (currentFragment as MqttMessageFragment).updateListView()
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
    private var currentFragment: Fragment? = null

    override fun onRegressionTesterListFragmentInteraction(item: RegressionMessage.RegressionProblem?) {

    }

    override fun onMqttMessageListFragmentInteraction(mqttMessage: MqttInput.MqttMessage?) {

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.top_nav_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.nav_settings_btn) {
            startActivity(Intent(this, SettingsActivity::class.java))
            return true
        }

        return false
    }

    fun setCurrentFragment(fragment: Fragment) {
        currentFragment = fragment
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
                R.id.navigation_regression, R.id.navigation_regression
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

        Thread(Runnable {
            Thread.sleep(2500)
            subscribeToSelectedTopics()
        }).start()
    }

    private fun subscribeToSelectedTopics() {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val groupNumber = sharedPref.getString("GroupNumber", "23")
        mqttHelper.clearSubscribtions()
        mqttHelper.subscribeToTopic("$groupNumber/#")
//        createToast("Subscription successful")
    }
}
