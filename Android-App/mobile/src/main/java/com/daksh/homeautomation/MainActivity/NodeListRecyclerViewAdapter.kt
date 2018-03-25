package com.daksh.homeautomation.MainActivity

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.daksh.homeautomation.MainActivity.Model.NodeModel
import com.daksh.homeautomation.R
import kotlinx.android.synthetic.main.layout_nodelist.view.*

class NodeListRecyclerViewAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //The node list
    private var nodeList: MutableList<NodeModel>? = null

    //The interaction listener that informs the activity that an item has been interacted with
    private var interactionListener: Listener? = null

    /**
     * Accepts the node list items from the activity
     */
    internal fun setNodeList(nodeList: MutableList<NodeModel>) {
        this@NodeListRecyclerViewAdapter.nodeList = nodeList
    }

    /**
     * Accepts the interface to pass on the recyclerView interactions to the activity
     */
    internal fun setInteractionlistener(listener: Listener) {
        this@NodeListRecyclerViewAdapter.interactionListener = listener
    }

    /**
     * Returns the adapter items
     */
    internal fun getAdapterItems(): MutableList<NodeModel>? = nodeList

    override fun getItemCount(): Int {
        return if(nodeList != null || !nodeList?.isEmpty()!!)
            nodeList?.size!!
        else
            0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)
            = (holder as ViewHolder).bind(position, nodeList, interactionListener)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = ViewHolder(parent.inflateView())

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bind(intPosition: Int, nodeList: MutableList<NodeModel>?, interactionListener: Listener?){
            //Set the name of the node
            itemView.nodeName.text = nodeList?.get(intPosition)?.nodeName

            //Set the description of the node
            itemView.nodeDescription.text = nodeList?.get(intPosition)?.nodeDescription

            //Identify the type of node and setup appropriately
            if(nodeList?.get(intPosition)?.nodeType == 1) {

                //Hide the switch, its not required
                itemView.nodeToggle.visibility = View.VISIBLE
                itemView.nodeStatus.visibility = View.GONE
                //Set the status of the node
                itemView.nodeToggle.isChecked = nodeList[intPosition].isNodeTurnedOn!!
            } else if(nodeList?.get(intPosition)?.nodeType == 2) {

                //Hide the switch, its not required
                itemView.nodeToggle.visibility = View.GONE
                //Update the status with data received
                itemView.nodeStatus.visibility = View.VISIBLE
                itemView.nodeStatus.text = nodeList[intPosition].nodeUpdatedAt
            }
            //Set the tap listener
            itemView.nodeContainer.setOnClickListener { _ ->

                itemView.nodeToggle.isChecked = !itemView.nodeToggle.isChecked

                //Inform the activity to pass on request to the controller to fetch updated data on the switch
                interactionListener?.onSwitchExecuted(itemView.nodeToggle.isChecked, intPosition, nodeList?.get(intPosition)?.nodeId)
            }
        }
    }

    // =========================== Extension methods =========================== //

    private fun ViewGroup.inflateView(): View =
            LayoutInflater.from(context).inflate(R.layout.layout_nodelist, this, false)
}