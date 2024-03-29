package com.arankieskamp.sdmregressiontester.ui


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.arankieskamp.sdmregressiontester.R
import com.arankieskamp.sdmregressiontester.models.RegressionMessage
import com.arankieskamp.sdmregressiontester.ui.RegressionMessageFragment.OnListFragmentInteractionListener
import kotlinx.android.synthetic.main.fragment_regression_message.view.*

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class MyRegressionMessageRecyclerViewAdapter(
    private val mValues: List<RegressionMessage.RegressionProblem>,
    private val mListener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<MyRegressionMessageRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as RegressionMessage.RegressionProblem
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onRegressionTesterListFragmentInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_regression_message, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.mIdView.text = item.id
        holder.mContentView.text = item.payload
        holder.mTopicView.text = item.topic
        holder.mExceptionView.text = item.exception

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mIdView: TextView = mView.item_number
        val mContentView: TextView = mView.payload
        val mTopicView: TextView = mView.topic
        val mExceptionView: TextView = mView.exception

        override fun toString(): String {
            return super.toString() + " '" + mContentView.text + "'"
        }
    }
}
