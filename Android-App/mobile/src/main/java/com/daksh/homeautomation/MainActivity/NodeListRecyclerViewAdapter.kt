package com.daksh.homeautomation.MainActivity

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.daksh.homeautomation.R
import com.hcl.daksh.android_poc_camp.Dashboard.ContractMain
import com.hcl.daksh.android_poc_camp.Login.DB.EntityDevices
import kotlinx.android.synthetic.main.layout_nodelist.view.*

class NodeListRecyclerViewAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //The node list
    private var nodeList: MutableList<EntityDevices>? = null
    //Presenter for the associated activity
    private var presenter: ContractMain.Presenter? = null

    /**
     * Accepts the node list items from the activity
     */
    internal fun setNodeList(nodeList: MutableList<EntityDevices>?) {
        this@NodeListRecyclerViewAdapter.nodeList = nodeList
    }

    /**
     * Accepts the interface to pass on the recyclerView interactions to the activity
     */
    internal fun setInteractionlistener(presenter: ContractMain.Presenter) {
        this@NodeListRecyclerViewAdapter.presenter = presenter
    }

    /**
     * Returns the adapter items
     */
    internal fun getAdapterItems(): MutableList<EntityDevices>? = nodeList

    override fun getItemCount(): Int {
        nodeList?.run {
            return if(!isEmpty())
                size
            else
                0
        }

        return 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)
            = (holder as ViewHolder).bind(position, nodeList, presenter)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = ViewHolder(parent.inflateView())

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bind(intPosition: Int, nodeList: MutableList<EntityDevices>?, presenter: ContractMain.Presenter?){
            //Set the name of the node
            itemView.nodeName.text = nodeList?.get(intPosition)?.deviceName

            //Set the description of the node
            itemView.nodeDescription.text = nodeList?.get(intPosition)?.deviceLocation

            //Identify the type of node and setup appropriately
            if(nodeList?.get(intPosition)?.deviceType == 1) {

                //Hide the switch, its not required
                itemView.nodeToggle.visibility = View.VISIBLE
                itemView.nodeStatus.visibility = View.GONE
                //Set the status of the node
                itemView.nodeToggle.isChecked = nodeList[intPosition].isDeviceSwitchedOn
            } else if(nodeList?.get(intPosition)?.deviceType == 2) {

                //Hide the switch, its not required
                itemView.nodeToggle.visibility = View.GONE
                //Update the status with data received
                itemView.nodeStatus.visibility = View.VISIBLE
//                itemView.nodeStatus.text = nodeList[intPosition].nodeUpdatedAt
            }
            //Set the tap listener
            itemView.nodeContainer.setOnClickListener { _ ->

                itemView.nodeToggle.isChecked = !itemView.nodeToggle.isChecked

                //Inform the activity to pass on request to the controller to fetch updated data on the switch
                presenter?.toggleSwitch(itemView.nodeToggle.isChecked, nodeList?.get(intPosition)?._id)
            }
        }
    }

    // =========================== Extension methods =========================== //

    private fun ViewGroup.inflateView(): View =
            LayoutInflater.from(context).inflate(R.layout.layout_nodelist, this, false)
}