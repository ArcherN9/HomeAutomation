package com.daksh.homeautomation.MainActivity

import com.daksh.homeautomation.MainActivity.Model.NodeModel

interface Listener {

    fun onReceived(body: MutableList<NodeModel>)

    /**
     * The method is called from the RecyclerViewAdapter to inform the activity that
     * an item on the Adapter has been interacted with
     */
    fun onSwitchExecuted(newStatus: Boolean, intPosition: Int, nodeId: String?)
}