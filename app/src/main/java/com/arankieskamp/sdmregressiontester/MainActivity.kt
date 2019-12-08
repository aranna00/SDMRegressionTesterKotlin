package com.arankieskamp.sdmregressiontester

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.arankieskamp.sdmregressiontester.helpers.MQTTHelper
import com.arankieskamp.sdmregressiontester.models.RegressionMessage
import com.arankieskamp.sdmregressiontester.ui.MqttMessageFragment
import com.arankieskamp.sdmregressiontester.ui.RegressionMessageFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), MqttMessageFragment.OnListFragmentInteractionListener,
    RegressionMessageFragment.OnListFragmentInteractionListener {

    override fun onRegressionTesterListFragmentInteraction(item: RegressionMessage.RegressionProblem?) {

    }

    override fun onMqttMessageListFragmentInteraction(regressionProblem: RegressionMessage.RegressionProblem?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    internal var mqttHelper: MQTTHelper? = null

    internal var mqttFragment = MqttMessageFragment()
    internal var regressionFragment = RegressionMessageFragment()

    private fun updateMqttListView() {
        if (mqttFragment.isVisible) {
            mqttFragment.updateListView()
        }
    }

    private fun updateRegressionListView() {
        if (regressionFragment.isVisible) {
            regressionFragment.updateListView()
        }
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
        val hostname = prefs.getString("hostname_text", "")
        val port = prefs.getInt("port_int", 0)

        Log.w("Info", hostname!!)
        Log.w("Info", port.toString() + "")

        mqttHelper = MQTTHelper.getInstance()
    }
}
