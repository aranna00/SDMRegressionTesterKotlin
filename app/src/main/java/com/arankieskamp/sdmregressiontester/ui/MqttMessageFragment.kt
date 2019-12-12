package com.arankieskamp.sdmregressiontester.ui

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arankieskamp.sdmregressiontester.MainActivity
import com.arankieskamp.sdmregressiontester.R
import com.arankieskamp.sdmregressiontester.models.MqttInput

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [MqttMessageFragment.OnListFragmentInteractionListener] interface.
 */
class MqttMessageFragment : Fragment() {

    // TODO: Customize parameters
    private var columnCount = 1

    private var listener: OnListFragmentInteractionListener? = null
    private var messageAdapter: MyMqttMessageRecyclerViewAdapter? = null
    internal var listView: RecyclerView.LayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    fun updateListView() {
        val refresh = Handler(Looper.getMainLooper())
        refresh.post {
            messageAdapter!!.notifyDataSetChanged()

            val sharedPref = PreferenceManager.getDefaultSharedPreferences(activity)
            val autoScroll = sharedPref.getBoolean("MQTTAutoScroll", true)

            if (autoScroll) {
                listView!!.scrollToPosition(messageAdapter!!.itemCount - 1)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mqttmessage_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = MyMqttMessageRecyclerViewAdapter(
                    MqttInput.ITEMS,
                    listener
                )

                listView = layoutManager
                messageAdapter = adapter as MyMqttMessageRecyclerViewAdapter
            }
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).setCurrentFragment(this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson
     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onMqttMessageListFragmentInteraction(mqttMessage: MqttInput.MqttMessage?)
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            MqttMessageFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}
