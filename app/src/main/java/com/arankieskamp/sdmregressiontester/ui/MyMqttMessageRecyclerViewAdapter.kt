package com.arankieskamp.sdmregressiontester.ui


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.arankieskamp.sdmregressiontester.R
import com.arankieskamp.sdmregressiontester.models.MqttInput
import com.arankieskamp.sdmregressiontester.ui.MqttMessageFragment.OnListFragmentInteractionListener
import kotlinx.android.synthetic.main.fragment_mqtt_message.view.*

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class MyMqttMessageRecyclerViewAdapter(
    private val mValues: List<MqttInput.MqttMessage>,
    private val mListener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<MyMqttMessageRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as MqttInput.MqttMessage?
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onMqttMessageListFragmentInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_mqtt_message, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        if (item.success) {

            holder.mIdView.setBackgroundResource(R.color.colorSuccess)
        } else {
            holder.mIdView.setBackgroundResource(R.color.colorFail)
        }
        holder.mIdView.text = item.id
        holder.mPayloadView.text = item.payload
        holder.mTopicView.text = item.topic

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mIdView: TextView = mView.item_number
        val mPayloadView: TextView = mView.payload
        val mTopicView: TextView = mView.topic

        override fun toString(): String {
            return super.toString() + " '" + mPayloadView.text + "'"
        }
    }
}
